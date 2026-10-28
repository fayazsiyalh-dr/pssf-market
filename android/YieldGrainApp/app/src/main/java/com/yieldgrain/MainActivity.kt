package com.yieldgrain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YieldGrainTheme {
                YieldGrainApp()
            }
        }
    }
}

@Composable
fun YieldGrainApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Home", "Rates", "Inventory", "Resale")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("YieldGrain")
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { },
                        label = { Text(title) }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                0 -> HomeScreen()
                1 -> RatesScreen()
                2 -> InventoryScreen()
                else -> ResaleScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Customer dashboard", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Owned wheat: 650 maunds")
                    Text("Warehouse status: Active")
                    Text("Next storage fee: Rs. 1,300")
                }
            }
        }
        item {
            Card {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Latest activity")
                    Text("Procurement request approved")
                    Text("Resale request under review")
                }
            }
        }
    }
}

@Composable
fun RatesScreen() {
    val rates = listOf(
        Rate("Wheat", "Rs. 3,300 / maund", "Last updated 08:00 PM PST"),
        Rate("Wheat premium", "Rs. 3,450 / maund", "Procurement rate"),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(rates) { rate ->
            Card {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(rate.name, fontWeight = FontWeight.Bold)
                    Text(rate.price)
                    Text(rate.meta)
                }
            }
        }
    }
}

@Composable
fun InventoryScreen() {
    val inventory = listOf(
        InventoryLot("Lot 101", "540 maunds", "Storage: Karachi", "Verified"),
        InventoryLot("Lot 102", "210 maunds", "Storage: Lahore", "Awaiting storage update"),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(inventory) { item ->
            Card {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(item.name, fontWeight = FontWeight.Bold)
                    Text(item.quantity)
                    Text(item.location)
                    Text(item.status)
                }
            }
        }
    }
}

@Composable
fun ResaleScreen() {
    val resale = listOf(
        ResaleRequest("Request #11", "Sold: 120 maunds", "Brokerage: Rs. 600"),
        ResaleRequest("Request #12", "Sold: 85 maunds", "Awaiting admin approval"),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(resale) { item ->
            Card {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(item.number, fontWeight = FontWeight.Bold)
                    Text(item.summary)
                    Text(item.status)
                }
            }
        }
    }
}

@Composable
fun YieldGrainTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}

data class Rate(val name: String, val price: String, val meta: String)

data class InventoryLot(val name: String, val quantity: String, val location: String, val status: String)

data class ResaleRequest(val number: String, val summary: String, val status: String)

fun formatPkrs(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
    format.currency = Currency.getInstance("PKR")
    return format.format(amount)
}
