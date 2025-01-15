package com.example.gogoviet.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gogoviet.AuthState
import com.example.gogoviet.AuthViewModel
import com.example.gogoviet.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun login(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    (authState.value as AuthState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Đăng Nhập") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF223263)
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()                         // Fill the entire scaffold space
                    .verticalScroll(rememberScrollState()) // Allow vertical scrolling
                    .padding(paddingValues)                 // Respect the top bar (Scaffold) padding
                    .background(Color.White)
                    .padding(16.dp),                       // Additional screen padding
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gogovit11),
                    contentDescription = "Highlighted Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp)
                )

                // Email InputField
                InputField(
                    label = "Email",
                    hint = "Vui lòng nhập email",
                    value = email,
                    onValueChange = { email = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Password InputField with visibility toggle
                InputField(
                    label = "Mật Khẩu",
                    hint = "Vui lòng nhập mật khẩu",
                    value = password,
                    onValueChange = { password = it },
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Remember Me and Forgot Password Row
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xffeeeeee))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ghi nhớ đăng nhập",
                            color = Color(0xff707070),
                            style = TextStyle(fontSize = 12.sp)
                        )
                    }
                    Text(
                        text = "Quên mật khẩu?",
                        color = Color(0xff707070),
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.clickable {
                            // Handle "Forgot Password" action
                            navController.navigate("forgotPassword")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SubmitButton for Login
                SubmitButton(
                    text = "Đăng Nhập",
                    onClick = {
                        authViewModel.login(email, password)
                    },
                    enabled = authState.value != AuthState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Navigate to Signup
                Text(
                    text = buildAnnotatedString {
                        append("Bạn đã có một tài khoản chưa? ")
                        withStyle(style = SpanStyle(color = Color(0xff2061c3))) {
                            append("Đăng ký ở đây")
                        }
                    },
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("signup")
                        }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Divider with Text "Hoặc đăng nhập bằng"

            }

        })


}

@Composable
fun InputField(
    label: String,
    hint: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    isEnabled: Boolean = true
) {
    var passwordVisibility by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.Black,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = hint, color = Color.Gray) },
            visualTransformation = if (isPassword && !passwordVisibility) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Text(if (passwordVisibility) "👁️" else "🙈")
                    }
                }
            } else {
                null
            },
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5))
        )
    }
}

@Composable
fun SubmitButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff2061c3))
    ) {
        Text(
            text = text,
            color = Color.White,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun SocialLoginButton(iconId: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
        elevation = ButtonDefaults.elevatedButtonElevation(0.dp)
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = "Social Login Icon",
            modifier = Modifier.size(24.dp)
        )
    }
}

