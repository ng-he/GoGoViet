package com.example.s2travel

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.gogoviet.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF66CCFF))
            .verticalScroll(rememberScrollState())

    ) {
        HomeContent()
    }
}

@Composable
private fun HomeContent() {
    val spacing = 16.dp

    SearchBarWithNotification(modifier = Modifier.padding(16.dp))
    Spacer(modifier = Modifier.height(spacing))
    ImageSlider()
    Spacer(modifier = Modifier.height(spacing))

    Spacer(modifier = Modifier.height(spacing))
    LocationSection()
    FlightDeals()
    Spacer(modifier = Modifier.height(spacing))
    LocationSection2()
    FlashSaleHotels()
    Spacer(modifier = Modifier.height(spacing))
    HotelDeals()
}

@Preview
@Composable
fun SearchBarWithNotification(modifier: Modifier = Modifier) {
    var searchText by remember { mutableStateOf("") }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .border(1.dp, Color.Gray, RoundedCornerShape(28.dp))
            ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.leading),
            contentDescription = "Search Icon",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
                .padding(start = 10.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = searchText,
            onValueChange = { searchText = it },
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            ),
            modifier = Modifier.weight(1f),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    Text(
                        text = "Search",
                        style = TextStyle(fontSize = 16.sp, color = Color.Gray)
                    )
                }
                innerTextField()
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(id = R.drawable.styleoutlined),
            contentDescription = "Notification Icon",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ImageSlider(modifier: Modifier = Modifier) {
    val images = listOf(R.drawable.h1, R.drawable.h2, R.drawable.h3)
    val coroutineScope = rememberCoroutineScope()
    val imageIndex = remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(id = images[imageIndex.value]),
            contentDescription = "Slider Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        coroutineScope.launch {
            while (true) {
                delay(8000)
                imageIndex.value = (imageIndex.value + 1) % images.size
            }
        }
    }
}

@Composable
fun LocationSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Hà Nội, Hải Phòng, Sapa",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            ),
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        LocationChips()
    }
}
@Composable
fun LocationSection2(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Nha Trang , Quy Nhơn , Đà Lạt ",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            ),
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        LocationChips()
    }
}

@Composable
fun LocationChips() {
    val chips = listOf(
        "Địa điểm nổi bật",
        "Nhà Hàng",
        "Cafe",
        "Địa điểm vui chơi"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        chips.forEach { chip ->
            InputChip(
                label = { Text(text = chip) },
                selected = false,
                onClick = {},
                shape = RoundedCornerShape(24.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun FlightDeals(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Flight Deals",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(3) {
                    FlightDealCard(
                        city = "TP HCM - Hà Nội",
                        priceOld = "800.000 VND",
                        priceNew = "500.000 VND"
                    )
                }
            }
        }
    }
}

@Composable
fun FlashSaleHotels(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Flash sale khách sạn nội địa",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val cities = listOf("Hồ Chí Minh", "Hà Nội", "Đà Lạt")
                items(cities) { city ->
                    FlashSaleHotelCard(
                        city = city,
                        date = "15 tháng 2 2024",
                        price = "500.000 VND"
                    )
                }
            }
        }
    }
}

@Composable
fun HotelDeals(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "HOTEL DEALS",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(2) {
                    HotelDealCard(
                        rating = 5,
                        reviewCount = 86,
                        description = "Khách sạn Rex Sài Gòn",
                        price = "450.000 VND"
                    )
                }
            }
        }
    }
}

@Composable
fun FlightDealCard(city: String, priceOld: String, priceNew: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ahi1),
                contentDescription = "Flight Deal",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = city,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = priceOld,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                )
            )
            Text(
                text = priceNew,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun FlashSaleHotelCard(city: String, date: String, price: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.hn),
                contentDescription = "Hotel",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = city,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date,
                style = TextStyle(fontSize = 12.sp, color = Color.Gray)
            )
            Text(
                text = price,
                style = TextStyle(fontSize = 14.sp, color = Color.Red, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun HotelDealCard(rating: Int, reviewCount: Int, description: String, price: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.dn),
                contentDescription = "Hotel",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            RatingBar(rating = rating, reviewCount = reviewCount)
            Text(
                text = description,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                text = price,
                style = TextStyle(fontSize = 14.sp, color = Color.Red, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun RatingBar(rating: Int, reviewCount: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(rating) {
            Icon(
                painter = painterResource(id = R.drawable.s1),
                contentDescription = "Star",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = "($reviewCount)",
            style = TextStyle(fontSize = 12.sp, color = Color.Gray),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
