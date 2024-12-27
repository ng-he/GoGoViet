package com.example.gogoviet

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gogoviet.ui.theme.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            GoGoVietTheme {
                val navController = rememberNavController()
                Scaffold (
                    bottomBar = { Menu(navController) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    innerPadding -> NavigationHost(navController, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "home", modifier = modifier) {
        composable("home") { HomeScreen() }
        composable("explore") { ExploreScreen() }
        composable("video") { VideoScreen() }
        composable("saved") { SavedScreen() }
        composable("account") { AccountScreen() }
    }
}

@Composable
fun HomeScreen() {
    ScreenContent("Home Screen")
}

@Composable
fun ExploreScreen() {
    ScreenContent("Explore Screen")
}

@Composable
fun VideoScreen() {
    ScreenContent("Video Screen")
}

@Composable
fun SavedScreen() {
    ScreenContent("Saved Screen")
}

@Composable
fun AccountScreen() {
    ScreenContent("Account Screen")
}

@Composable
fun ScreenContent(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = TextStyle(fontSize = 24.sp))
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