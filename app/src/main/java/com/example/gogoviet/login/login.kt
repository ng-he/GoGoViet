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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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

@Composable
fun login(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message,Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.gogovit11),
                contentDescription = "Highlighted Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
            )

            // Sử dụng InputField cho Email
            InputField(
                label = "Email",
                hint = "Vui lòng nhập email",
                value = email,
                onValueChange = { email = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Sử dụng InputField cho Mật khẩu với tính năng hiển thị/ẩn
            InputField(
                label = "Mật Khẩu",
                hint = "Vui lòng nhập mật khẩu",
                value = password,
                onValueChange = { password = it },
                isPassword = true
            )
            Spacer(modifier = Modifier.height(16.dp))

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
                    text = "Quên mật khẩu ?",
                    color = Color(0xff707070),
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.clickable {
                        // Thêm hành động khi người dùng nhấn "Quên mật khẩu"
                        // Ví dụ: navController.navigate("forgotPassword")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sử dụng SubmitButton cho nút Đăng Nhập
            SubmitButton(
                text = "Đăng Nhập",
                onClick = {
                    authViewModel.login(email, password)
                },
                enabled = authState.value != AuthState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = buildAnnotatedString {
                    append("Bạn đã có một tài khoản chưa? ")
                    withStyle(SpanStyle(color = Color(0xff2061c3))) {
                        append("Đăng ký ở đây")
                    }
                },
                style = TextStyle(fontSize = 12.sp),
                modifier = Modifier.clickable {
                    navController.navigate("signup")
                }
            )



        }
    }
}
@Composable
fun InputField(
    label: String,
    hint: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    var passwordVisibility by remember { mutableStateOf(false) }

    Column {
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
//                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
//                        Icon(
//                            imageVector = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
//                            contentDescription = if (passwordVisibility) "Ẩn mật khẩu" else "Hiển thị mật khẩu"
//                        )
//                    }
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Text(if (passwordVisibility) "👁️" else "🙈")
                    }
                }
            } else {
                null
            },
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

