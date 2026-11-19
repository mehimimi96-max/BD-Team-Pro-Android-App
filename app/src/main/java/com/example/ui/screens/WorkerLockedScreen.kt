package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.AppConfigEntity
import com.example.data.WorkerEntity
import com.example.ui.components.ContactAdminButtons
import com.example.ui.components.LockedBadgeGraphic
import com.example.ui.components.PinkGradientBackground
import com.example.ui.components.PinkSurfaceCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.InkBlack
import com.example.ui.theme.InkBlackMuted
import com.example.ui.theme.InkBlackSecondary
import com.example.ui.theme.PinkCard
import com.example.ui.theme.PinkCardAlt
import com.example.ui.theme.StatusAmberBg
import com.example.ui.theme.StatusAmberInk

@Composable
fun WorkerLockedScreen(
    worker: WorkerEntity,
    config: AppConfigEntity,
    onSwitchToPortalGate: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    PinkGradientBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 540.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LockedBadgeGraphic()
                Spacer(modifier = Modifier.height(16.dp))

                StatusBadge(
                    text = "অ্যাকাউন্ট লক করা আছে",
                    backgroundColor = StatusAmberBg,
                    textColor = StatusAmberInk
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "স্বাগতম, ${worker.name}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = InkBlack,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "নিরাপত্তার জন্য নতুন অ্যাকাউন্ট প্রথমে লক থাকে। অ্যাডমিন আপনার অ্যাকাউন্ট আনলক করলে ভিডিও, বিজ্ঞাপন, গেম ও রেফার করে আয় শুরু করতে পারবেন।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkBlackSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                PinkSurfaceCard(containerColor = PinkCard) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "আপনার অ্যাকাউন্টের তথ্য",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = InkBlack
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("মোবাইল নম্বর:", color = InkBlackMuted)
                            Text(worker.phone, color = InkBlack, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("রেফার কোড:", color = InkBlackMuted)
                            Text(worker.referralCode, color = InkBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                PinkSurfaceCard(containerColor = PinkCardAlt) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "অ্যাকাউন্ট আনলক করতে অ্যাডমিনের সাথে যোগাযোগ করুন",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = InkBlack
                        )
                        ContactAdminButtons(
                            context = context,
                            whatsappNumber = config.whatsappNumber,
                            facebookUrl = config.facebookUrl
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onSwitchToPortalGate,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = PinkCard,
                            contentColor = InkBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("locked_switch_portal_button")
                    ) {
                        Icon(Icons.Filled.SwapHoriz, contentDescription = null, tint = InkBlack)
                        Spacer(modifier = Modifier.height(0.dp))
                        Text(" পোর্টাল পরিবর্তন", color = InkBlack, fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("locked_logout_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = InkBlack)
                        Text(" লগআউট", color = InkBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
