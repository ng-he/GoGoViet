package com.example.gogoviet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // Observe auth state
    val authState = authViewModel.authState.observeAsState()
    val userInfoState = authViewModel.userInfo.observeAsState(UserInfo())
    val userInfo = userInfoState.value
    // Reset state when navigating to this screen
    LaunchedEffect(Unit) {
        authViewModel.resetState() // Ensures state is reset only once when the screen loads
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Account Page") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (authState.value) {
                    is AuthState.Unauthenticated -> {
                        Text("Bạn chưa đăng nhập")
                        Button(
                            onClick = { navController.navigate("login") },
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                        ) {
                            Text("Login")
                        }
                    }
                    is AuthState.Authenticated -> {
                        Text("Bạn đã đăng nhập thành công!")
                        Spacer(modifier = Modifier.height(16.dp))

                        if (userInfo != null) {
                            AsyncImage(
                                model = userInfo.photoUrl.ifEmpty { R.drawable.default_avatar },
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Name: ${userInfo.name ?: "N/A"}")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { navController.navigate("updateProfile") }) {
                                Text("Edit Profile")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    // Logout logic in AuthViewModel
                                    authViewModel.signout()
                                    navController.navigate("login")
                                },
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth()
                            ) {
                                Text("Thoát")
                            }
                        } else {
                            Text("Loading user info...")
                        }

                    }
                    is AuthState.Error -> {

                        Text(
                            text = "Đã xảy ra lỗi: Hay đăng nhập lại",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { navController.navigate("login") }, // Navigate back to login
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Thử lại")
                        }
                    }
                    is AuthState.Loading -> {
                        Text("Đang tải...")
                    }
                    else -> {
                        Text("Trạng thái không xác định")
                    }
                }
            }
        }
    )
}
