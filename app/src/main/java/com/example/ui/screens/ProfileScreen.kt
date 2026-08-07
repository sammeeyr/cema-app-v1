package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.CemaLogoGraphic
import com.example.ui.viewmodel.CemaTab
import com.example.ui.viewmodel.CemaViewModel

@Composable
fun ProfileScreen(
    viewModel: CemaViewModel,
    onNavigateTab: (CemaTab) -> Unit
) {
    val context = LocalContext.current
    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val userPhone by viewModel.userPhone.collectAsState()
    val churchUnit by viewModel.churchUnit.collectAsState()
    val location by viewModel.userLocation.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val notifications by viewModel.notificationsEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // CEMA Official Brand Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CemaLogoGraphic(size = 130.dp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Profile Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Google Sign-In Active",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Church Profile Details
        Text(
            text = "Church Membership Details",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileDetailRow(icon = Icons.Outlined.Phone, label = "Phone", value = userPhone)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                ProfileDetailRow(icon = Icons.Outlined.Church, label = "Church Unit", value = churchUnit)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                ProfileDetailRow(icon = Icons.Outlined.LocationOn, label = "Location", value = location)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Preferences
        Text(
            text = "Preferences & Controls",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Dark Mode", style = MaterialTheme.typography.bodyMedium)
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.isDarkMode.value = it }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Notifications", style = MaterialTheme.typography.bodyMedium)
                    }
                    Switch(
                        checked = notifications,
                        onCheckedChange = { viewModel.notificationsEnabled.value = it }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateTab(CemaTab.SETTINGS) }
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Settings & Downloads", style = MaterialTheme.typography.bodyMedium)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        
        val isUserSignedIn by viewModel.isUserSignedIn.collectAsState()
        
        if (isUserSignedIn) {
            OutlinedButton(
                onClick = {
                    viewModel.isUserSignedIn.value = false
                    viewModel.userName.value = "Guest"
                    viewModel.userEmail.value = ""
                    Toast.makeText(context, "Signed out of Google account", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        } else {
            Button(
                onClick = {
                    viewModel.signInWithGoogle(context)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign In with Google")
            }
        }

    }
}

@Composable
fun ProfileDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
