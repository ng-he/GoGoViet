package com.example.gogoviet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        topBar = { /* Optional: Add a TopAppBar */ },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues) // Apply scaffold's padding
            ) {
                when (authState.value) {
                    is AuthState.Unauthenticated -> {
                        unAuthScreen(navController = navController)
                    }
                    is AuthState.Authenticated -> {
                        authedscreen(
                            navController = navController,
                            userInfo = userInfo,
                            authViewModel = authViewModel
                        )
                    }
                    is AuthState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Đã xảy ra lỗi: Hãy đăng nhập lại",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Button(
                                onClick = { navController.navigate("login") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Thử lại")
                            }
                        }
                    }
                    is AuthState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Đang tải...")
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Trạng thái không xác định")
                        }
                    }
                }
            }
        }
    )
}
@Composable
fun unAuthScreen(modifier: Modifier = Modifier,navController: NavController) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        // Header Image Section
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
                .fillMaxWidth()
                .height(256.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.gogovit11),
                contentDescription = "Highlighted Image",
                modifier = Modifier.fillMaxSize()
            )
        }

        // Login and Register Buttons Section
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 300.dp) // Đặt ngay dưới ảnh
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = {
                    navController.navigate("login")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff2061c3)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "Đăng Nhập",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { navController.navigate("signup")},
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(BorderStroke(1.dp, Color(0xff1e232c)), RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "Đăng Ký",
                    color = Color(0xff1e232c),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
            }
        }

        // Hotline Section
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Liên hệ hotline 1900",
                color = Color(0xff223263),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun authedscreen(modifier: Modifier = Modifier, navController: NavController,userInfo: UserInfo,authViewModel: AuthViewModel) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Header Section: Avatar and Name
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = if (userInfo.photoUrl.isNotEmpty()) userInfo.photoUrl else R.drawable.default_avatar,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            }



            Text(
                text = "Name: ${userInfo.name ?: "N/A"}",
                color = Color.Black,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(1f)
            )
        }

        // "Tài Khoản" Section Title
        Text(
            text = "Tài Khoản",
            color = Color(0xff223263),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )


        // "Thông tin cá nhân" Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("updateProfile") }
                .padding(vertical = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.usericon2),
                contentDescription = "User Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Thông tin cá nhân",
                color = Color(0xff223263),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
        }







        // Other Sections
        val sections = listOf(
            Triple(R.drawable.solarticketsalelinear, "Mã giảm giá", null),
            Triple(R.drawable.uilsetting, "Cài đặt") {
                // Handle Settings Click
            },
            Triple(R.drawable.wpffaq, "Trung tâm hỗ trợ", null),
            Triple(R.drawable.vector, "Liên hệ hotline", null),
            Triple(R.drawable.iconparklogout, "Đăng xuất") {
                authViewModel.signout()
                navController.navigate("account")
            }
        )

        sections.forEach { (iconRes, text, onClick) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick?.invoke() }
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "$text Icon",
                    modifier = Modifier.size(24.dp),
                    tint = when (text) {
                        "Cài đặt", "Trung tâm hỗ trợ", "Liên hệ hotline" -> Color(0xff40bfff)
                        else -> Color.Unspecified
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    color = Color(0xff223263),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }

}