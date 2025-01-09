@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gogoviet.login

import android.preference.PreferenceActivity
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import okhttp3.internal.http2.Header

@Composable
fun signup(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    // Quản lý trạng thái nhập liệu
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current

    // Xử lý trạng thái auth
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> navController.navigate("home") {
                popUpTo("signup") { inclusive = true }
            }
            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    (authState as AuthState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> Unit
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Đăng Ký") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF223263)
                )
            )
        },
        content = { paddingValues ->
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFFFFF))
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 16.dp)
                ) {

                    Spacer(modifier = Modifier.height(16.dp))
                    NgK(
                        username = username,
                        onUsernameChange = { username = it },
                        email = email,
                        onEmailChange = { email = it },
                        password = password,
                        onPasswordChange = { password = it },
                        onSignupClick = {
                            authViewModel.signup(email, password, username)
                        },
                        isLoading = authState == AuthState.Loading,
                        navController = navController
                    )
                }

            }
        })
}
@Composable
fun NgK(
    username: String,
    onUsernameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onSignupClick: () -> Unit,
    isLoading: Boolean,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        // Sử dụng InputField cho Họ và Tên
        InputField(
            label = "Họ và tên",
            hint = "Tên của bạn, ví dụ: NguyenA",
            value = username,
            onValueChange = onUsernameChange
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Sử dụng InputField cho Email
        InputField(
            label = "Email",
            hint = "Email của bạn, ví dụ: NguyenA@gmail.com",
            value = email,
            onValueChange = onEmailChange
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Sử dụng InputField cho Mật khẩu với tính năng hiển thị/ẩn
        InputField(
            label = "Mật khẩu",
            hint = "Ít nhất 8 ký tự",
            value = password,
            onValueChange = onPasswordChange,
            isPassword = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Nút Đăng ký
        SubmitButton(
            text = "Đăng ký",
            onClick = onSignupClick,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nút Điều hướng đến Đăng nhập
        Text(
            text = buildAnnotatedString {
                append("Bạn đã có một tài khoản? ")
                withStyle(style = SpanStyle(color = Color(0xff2061c3))) {
                    append("Đăng nhập ở đây")
                }
            },
            style = TextStyle(fontSize = 12.sp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Divider với văn bản "Hoặc đăng ký bằng"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(color = Color(0xffadadad), modifier = Modifier.weight(1f))
            Text(
                text = "Hoặc đăng ký bằng(Coming soon)",
                color = Color(0xff707070),
                style = TextStyle(fontSize = 13.sp),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Divider(color = Color(0xffadadad), modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Nút Đăng ký bằng phương thức xã hội

    }
}





