package com.example.gogoviet

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoGoVietTheme {
                Scaffold (
                    bottomBar = { Menu() },
                    modifier = Modifier.fillMaxSize()
                ) {
                        innerPadding -> Home(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Home(modifier: Modifier = Modifier) {
    Text(text = "Hello", modifier = modifier)
}

@Composable
fun Menu() {
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
            MenuItem(icon = ImageVector.vectorResource(id = R.drawable.ic_home), name = "Trang chủ")
            MenuItem(icon = ImageVector.vectorResource(id = R.drawable.ic_map), name = "Khám phá", active = true)
            MenuItem(icon = ImageVector.vectorResource(id = R.drawable.ic_video), name = "Video")
            MenuItem(icon = ImageVector.vectorResource(id = R.drawable.ic_saved), name = "Đã lưu")
            MenuItem(icon = ImageVector.vectorResource(id = R.drawable.ic_account), name = "Tài khoản")
        }
    }

}

@Composable
fun MenuItem(icon: ImageVector, name: String, active: Boolean = false) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
            .wrapContentWidth()
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