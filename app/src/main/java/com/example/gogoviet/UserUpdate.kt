import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

import com.example.gogoviet.AuthViewModel
import com.example.gogoviet.R
import com.example.gogoviet.UserInfo

@Composable
fun userUpdate(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val userInfoState by authViewModel.userInfo.observeAsState(UserInfo())
    val userInfo = userInfoState

    var name by remember { mutableStateOf(userInfo.name) }
    var email by remember { mutableStateOf(userInfo.email) }
    var address by remember { mutableStateOf(userInfo.address) }
    var gender by remember { mutableStateOf(userInfo.gender) }

    val genders = listOf("Male", "Female", "Other")
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Local loading state for the "Save" operation
    var isUploading by remember { mutableStateOf(false) }

    // Update text field values when userInfo changes
    LaunchedEffect(userInfo) {
        name = userInfo.name
        address = userInfo.address
        gender = userInfo.gender
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Update Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        AsyncImage(
            model = imageUri ?: userInfo.photoUrl.ifEmpty { R.drawable.default_avatar },
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
        Button(onClick = { launcher.launch("image/*") }, enabled = !isUploading) {
            Text("Select Profile Picture")
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isUploading // Disable when uploading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false // Email is non-editable
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isUploading // Disable when uploading
        )

        Spacer(modifier = Modifier.height(16.dp))

        GenderDropdownMenu(
            genders = genders,
            selectedGender = gender,
            onGenderSelected = { gender = it },
            isEnabled = !isUploading // Disable when uploading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isUploading = true // Start loading state
                val updatedFields = mapOf(
                    "name" to name,
                    "address" to address,
                    "gender" to gender
                )
                val imageUriToUpload = imageUri

                if (imageUriToUpload != null) {
                    authViewModel.uploadProfilePicture(imageUriToUpload,
                        onSuccess = { photoUrl ->
                            val updatedFieldsWithPhoto = updatedFields.toMutableMap()
                            updatedFieldsWithPhoto["photoUrl"] = photoUrl
                            authViewModel.updateUserInfo(updatedFieldsWithPhoto,
                                onSuccess = {
                                    isUploading = false // End loading state
                                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("account")
                                },
                                onFailure = { error ->
                                    isUploading = false // End loading state
                                    Toast.makeText(context, "Update failed: $error", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onFailure = { error ->
                            isUploading = false // End loading state
                            Toast.makeText(context, "Failed to upload image: $error", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    authViewModel.updateUserInfo(updatedFields,
                        onSuccess = {
                            isUploading = false // End loading state
                            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            navController.navigate("account")
                        },
                        onFailure = { error ->
                            isUploading = false // End loading state
                            Toast.makeText(context, "Update failed: $error", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            enabled = !isUploading // Disable the button when uploading
        ) {
            if (isUploading) {
                Text("Saving...") // Show loading state
            } else {
                Text("Save")
            }
        }
    }
}

@Composable
fun GenderDropdownMenu(
    genders: List<String>,
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    isEnabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedGender,
            onValueChange = {}, // No manual input, only dropdown selection
            label = { Text("Gender") },
            readOnly = true,
            enabled = isEnabled, // Disable dropdown when uploading
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { if (isEnabled) expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            genders.forEach { gender ->
                DropdownMenuItem(
                    onClick = {
                        onGenderSelected(gender)
                        expanded = false
                    },
                    text = { Text(text = gender) }
                )
            }
        }
    }
}
