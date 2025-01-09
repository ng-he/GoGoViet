package com.example.gogoviet.login

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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



    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()) // Enable vertical scroll
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with User Icon and Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.usericon2),
                    contentDescription = "User Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color(0xFF2061C3), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Thông tin cá nhân",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = Color(0xFF223263)
                )
            }

            // Profile Picture Section
            AsyncImage(
                model = imageUri ?: if (userInfo.photoUrl.isNotEmpty()) userInfo.photoUrl else R.drawable.default_avatar,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { launcher.launch("image/*") },
                enabled = !isUploading,
                modifier = Modifier
                    .height(40.dp)
            ) {
                Text(text = "Chọn ảnh đại diện")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Input Fields
            InputField(
                label = "Họ Và Tên",
                hint = "Nhập họ và tên",
                value = name,
                onValueChange = { name = it },
                isPassword = false
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false, // Email is non-editable
                textStyle = TextStyle(color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                label = "Địa chỉ",
                hint = "Nhập địa chỉ",
                value = address,
                onValueChange = { address = it },
                isPassword = false
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Gender Selection
            GenderDropdownMenu(
                genders = genders,
                selectedGender = gender,
                onGenderSelected = { gender = it },
                isEnabled = !isUploading
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { /* Handle cancel */ },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .height(50.dp),
                    border = BorderStroke(1.dp, Color(0xFF2061C3)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Hủy bỏ",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                        color = Color.Black
                    )
                }
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
                            authViewModel.uploadProfilePicture(
                                imageUriToUpload,
                                onSuccess = { photoUrl ->
                                    val updatedFieldsWithPhoto = updatedFields.toMutableMap()
                                    updatedFieldsWithPhoto["photoUrl"] = photoUrl
                                    authViewModel.updateUserInfo(
                                        updatedFieldsWithPhoto,
                                        onSuccess = {
                                            isUploading = false // End loading state
                                            Toast.makeText(
                                                context,
                                                "Profile updated successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.navigate("account")
                                        },
                                        onFailure = { error ->
                                            isUploading = false // End loading state
                                            Toast.makeText(
                                                context,
                                                "Update failed: $error",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                },
                                onFailure = { error ->
                                    isUploading = false // End loading state
                                    Toast.makeText(
                                        context,
                                        "Failed to upload image: $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        } else {
                            authViewModel.updateUserInfo(
                                updatedFields,
                                onSuccess = {
                                    isUploading = false // End loading state
                                    Toast.makeText(
                                        context,
                                        "Profile updated successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("account")
                                },
                                onFailure = { error ->
                                    isUploading = false // End loading state
                                    Toast.makeText(
                                        context,
                                        "Update failed: $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    },
                    enabled = !isUploading, // Disable the button when uploading
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2061C3))
                ) {
                    if (isUploading) {
                        Text("Đang lưu...")
                    } else {
                        Text(
                            text = "Lưu và thay đổi",
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

}
@Composable
fun GenderDropdownMenu(genders: List<String>,
                       selectedGender: String,
                       onGenderSelected: (String) -> Unit,
                       isEnabled: Boolean = true) {


    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedGender,
            onValueChange = {},
            label = { Text("Giới Tính") },
            readOnly = true,
            enabled = isEnabled,
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


