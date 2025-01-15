package com.example.gogoviet

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
//import com.example.gogoviet.ui.foryou.ForYouVideoScreen
import com.example.gogoviet.login.login
import com.example.gogoviet.login.signup
import com.example.gogoviet.ui.theme.GoGoVietTheme
import com.example.gogoviet.ui.theme.Poppins
import dagger.hilt.android.AndroidEntryPoint
import forgotPassword


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        lateinit var instance: MainActivity
        fun getAppResources(): Resources = instance.resources
        fun getContext(): Context = instance
        fun getActivity(): MainActivity = instance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        instance = this
        var startDestination = "home"

        if(intent.getStringExtra("screen") != null) {
            startDestination = intent.getStringExtra("screen")!!
        }

        enableEdgeToEdge()
        val authViewModel : AuthViewModel by viewModels()
        setContent {
            GoGoVietTheme {
                val navController = rememberNavController()
                Scaffold (
                    bottomBar = { Menu(navController) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    innerPadding -> NavigationHost(
                        navController,
                        this,
                        startDestination,
                        Modifier.padding(innerPadding),
                        authViewModel)
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun NavigationHost(
    navController: NavHostController,
    context: Context, startDestination: String,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    NavHost(navController, startDestination = startDestination, modifier = modifier) {
        composable("home") { HomeScreen(navController) }
        composable("explore") { ExploreScreen(context, authViewModel) }
        composable("video") { VideoScreen() }
        composable("saved") {
            if(authViewModel.authState.value is AuthState.Authenticated) {
                SavedScreen(authViewModel, context, navController)
            }
            else {
                navController.navigate("login")
            }
        }
        composable("account") { AccountScreen(modifier, navController,authViewModel) }
        composable("login") { login(modifier, navController,authViewModel) }
        composable("signup") { signup(modifier, navController,authViewModel) }
        composable("updateProfile") { userUpdate(modifier, navController,authViewModel) }
        composable("forgotPassword") { forgotPassword(modifier,navController,authViewModel) }
        composable("detail") { Detail(modifier, navController) }
    }
}

@Composable
fun Menu(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Column {
        HorizontalDivider(thickness = 1.dp)
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(93.dp)
                .background(Color.White)
                .padding(horizontal = 8.dp)
        ) {
            MenuItem(
                icon = ImageVector.vectorResource(id = R.drawable.ic_home),
                name = "Trang chủ",
                active = currentRoute == "home",
                onClick = { navController.navigate("home") }
            )
            MenuItem(
                icon = ImageVector.vectorResource(id = R.drawable.ic_map),
                name = "Khám phá",
                active = currentRoute == "explore",
                onClick = { navController.navigate("explore") }
            )
            MenuItem(
                icon = ImageVector.vectorResource(id = R.drawable.ic_video),
                name = "Video",
                active = currentRoute == "video",
                onClick = { navController.navigate("video") }
            )
            MenuItem(
                icon = ImageVector.vectorResource(id = R.drawable.ic_saved),
                name = "Đã lưu",
                active = currentRoute == "saved",
                onClick = { navController.navigate("saved") }
            )
            MenuItem(
                icon = ImageVector.vectorResource(id = R.drawable.ic_account),
                name = "Tài khoản",
                active = currentRoute == "account",
                onClick = { navController.navigate("account") }
            )
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, name: String, active: Boolean, onClick: () -> Unit) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
            .wrapContentWidth()
            .clickable { onClick() }
    ) {
        val colorId = if (active) R.color.teal1 else R.color.gray1

        Icon(
            imageVector = icon,
            contentDescription = name, 
            modifier = Modifier.size(24.dp, 24.dp),
            colorResource(id = colorId)
        )
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            text = name,
            style = TextStyle(
                fontSize = 10.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight(400)
            ),
            color = colorResource(id = colorId)
        )
    }
}