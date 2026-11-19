package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AppConfigEntity
import com.example.ui.components.ContactAdminButtons
import com.example.ui.components.HeroEarningBanner
import com.example.ui.components.PinkGradientBackground
import com.example.ui.components.PinkSurfaceCard
import com.example.ui.theme.InkBlack
import com.example.ui.theme.InkBlackMuted
import com.example.ui.theme.InkBlackSecondary
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkCard
import com.example.ui.theme.PinkCardAlt
import com.example.ui.theme.PinkPrimary

@Composable
fun PortalGateScreen(
    config: AppConfigEntity,
    onOpenWorkerPortal: () -> Unit,
    onOpenAdminPortal: () -> Unit
) {
    val context = LocalContext.current

    PinkGradientBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HeroEarningBanner(
                    title = "BD Earning Team Pro",
                    subtitle = "ভিডিও, বিজ্ঞাপন, গেম, লাইক/ফলো এবং রেফার করে আয় করুন — অ্যাডমিন ও ওয়ার্কারের আলাদা পোর্টাল।"
                )

                Text(
                    text = "পোর্টাল নির্বাচন করুন",
                    style = MaterialTheme.typography.titleLarge,
                    color = InkBlack,
                    fontWeight = FontWeight.Bold
                )

                // Worker Portal Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenWorkerPortal)
                        .testTag("open_worker_portal_card"),
                    shape = RoundedCornerShape(22.dp),
                    color = PinkCard,
                    border = BorderStroke(1.5.dp, PinkBorder.copy(alpha = 0.45f)),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = PinkPrimary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.WorkOutline,
                                    contentDescription = "Worker Portal",
                                    tint = InkBlack,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ওয়ার্কার অ্যাপ (Worker Portal)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InkBlack
                            )
                            Text(
                                text = "কাজ করুন, রেফার করুন ও টাকা তোলার আবেদন পাঠান",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkBlackSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "লিঙ্ক: bdearning://worker",
                                style = MaterialTheme.typography.labelSmall,
                                color = InkBlackMuted
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "প্রবেশ করুন",
                            tint = InkBlack
                        )
                    }
                }

                // Admin Portal Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenAdminPortal)
                        .testTag("open_admin_portal_card"),
                    shape = RoundedCornerShape(22.dp),
                    color = PinkCardAlt,
                    border = BorderStroke(1.5.dp, InkBlack.copy(alpha = 0.25f)),
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = InkBlack
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = "Admin Panel",
                                    tint = PinkCard,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "অ্যাডমিন প্যানেল (Admin Panel)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InkBlack
                            )
                            Text(
                                text = "ভিডিও/এড/গেম লিংক যোগ, ওয়ার্কার আনলক ও পেমেন্ট অনুমোদন",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkBlackSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "লিঙ্ক: bdearning://admin",
                                style = MaterialTheme.typography.labelSmall,
                                color = InkBlackMuted
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "প্রবেশ করুন",
                            tint = InkBlack
                        )
                    }
                }

                // Rules Summary Card
                PinkSurfaceCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "আয়ের নিয়মাবলি",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = InkBlack
                        )
                        RuleBulletRow(
                            icon = Icons.Filled.CheckCircle,
                            text = "প্রতিটি ভিডিও, বিজ্ঞাপন, গেম ও লাইক/ফলো কাজের সর্বোচ্চ পুরস্কার ১০ টাকা।"
                        )
                        RuleBulletRow(
                            icon = Icons.Filled.Groups,
                            text = "প্রতিটি ভেরিফায়েড রেফারে পাবেন ৫০ টাকা বোনাস।"
                        )
                        RuleBulletRow(
                            icon = Icons.Filled.CardGiftcard,
                            text = "টাকা তোলার জন্য কমপক্ষে ৩টি ভেরিফায়েড রেফার ও সর্বনিম্ন ১০০ টাকা ব্যালেন্স লাগবে।"
                        )
                    }
                }

                if (config.whatsappNumber.isNotBlank() || config.facebookUrl.isNotBlank()) {
                    PinkSurfaceCard {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "অ্যাডমিনের সাথে যোগাযোগ",
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
                }
            }
        }
    }
}

@Composable
private fun RuleBulletRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = InkBlack,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = InkBlackSecondary
        )
    }
}
