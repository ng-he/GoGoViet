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
fun userUpdate(modifier: Modifier = Modifier,
               navController: NavController,
               authViewModel: AuthViewModel
) {
    val userInfoState = authViewModel.userInfo.observeAsState(UserInfo())
    val userInfo = userInfoState.value
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
            model = imageUri ?: authViewModel.userInfo.value?.photoUrl ?: R.drawable.default_avatar,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Profile Picture")
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false // Email is usually non-editable
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        GenderDropdownMenu(
            genders = genders,
            selectedGender = gender,
            onGenderSelected = { gender = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val updatedFields = mapOf(
                "name" to name,
                "address" to address,
                "gender" to gender
            )
            val imageUriToUpload = imageUri // Capture the current imageUri

            // First, handle profile picture upload if imageUri is selected
            if (imageUriToUpload != null) {
                authViewModel.uploadProfilePicture(imageUriToUpload,
                    onSuccess = { photoUrl ->
                        // Add the photoUrl to updatedFields and update Firestore
                        val updatedFieldsWithPhoto = updatedFields.toMutableMap()
                        updatedFieldsWithPhoto["photoUrl"] = photoUrl
                        authViewModel.updateUserInfo(updatedFieldsWithPhoto,
                            onSuccess = {
                                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                navController.navigate("account") // Navigate back
                            },
                            onFailure = { error ->
                                Toast.makeText(context, "Update failed: $error", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onFailure = { error ->
                        Toast.makeText(context, "Failed to upload image: $error", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                // If no image is selected, update only text fields
                authViewModel.updateUserInfo(updatedFields,
                    onSuccess = {
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        navController.navigate("account") // Navigate back
                    },
                    onFailure = { error ->
                        Toast.makeText(context, "Update failed: $error", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }) {
            Text("Save")
        }
    }
}
@Composable
fun GenderDropdownMenu(
    genders: List<String>,
    selectedGender: String,
    onGenderSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // State to control menu visibility

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedGender,
            onValueChange = {}, // No manual input, only dropdown selection
            label = { Text("Gender") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
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
                    text = { Text(text = gender) } // Updated text parameter
                )
            }
        }
    }
}
@Composable
fun UpdateProfilePicture(onImageSelected: (Uri) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    Button(onClick = { launcher.launch("image/*") }) {
        Text("Change Profile Picture")
    }
}