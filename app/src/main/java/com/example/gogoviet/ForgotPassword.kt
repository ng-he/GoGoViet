import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gogoviet.AuthState
import com.example.gogoviet.AuthViewModel
import com.example.gogoviet.UserInfo
import com.example.gogoviet.login.InputField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun forgotPassword(
    modifier: Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    // Observe state changes for success/error feedback
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.ResetEmailSent -> {
                // Show success message
                Toast.makeText(
                    context,
                    "Reset email sent. Check your inbox!",
                    Toast.LENGTH_SHORT
                ).show()
                // Optionally navigate back
                navController.popBackStack()
            }
            is AuthState.Error -> {
                val errorMsg = (authState.value as AuthState.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
            else -> { /* no-op */ }
        }
    }

    // UI: Scaffold with a top bar and content
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Quên Mật Khẩu") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF223263)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nhập email đã đăng ký để nhận link đặt lại mật khẩu:",
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Simple TextField for email input
            InputField(
                label = "Email",
                hint = "Vui lòng nhập email",
                value = email,
                onValueChange = { email = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Reset Password Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    onClick = {
                        // Check if email is empty before calling forgotPassword
                        if (email.isBlank()) {
                            Toast.makeText(
                                context,
                                "Vui lòng nhập email.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            authViewModel.forgotPassword(email)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff2061c3)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "Gửi",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { navController.navigate("account") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(BorderStroke(1.dp, Color(0xff1e232c)), RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "Trờ về",
                        color = Color(0xff1e232c),
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
