package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.TaskCategory
import com.example.ui.theme.FacebookBlue
import com.example.ui.theme.InkBlack
import com.example.ui.theme.InkBlackMuted
import com.example.ui.theme.InkBlackSecondary
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PinkBg
import com.example.ui.theme.PinkBgDeep
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkCard
import com.example.ui.theme.PinkCardAlt
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.WhatsAppGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PinkGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PinkBg, PinkBgDeep)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0x33FF80AB),
                radius = size.width * 0.42f,
                center = Offset(size.width * 0.9f, size.height * 0.08f)
            )
            drawCircle(
                color = Color(0x26FFFFFF),
                radius = size.width * 0.35f,
                center = Offset(size.width * 0.1f, size.height * 0.85f)
            )
        }
        content()
    }
}

@Composable
fun PinkSurfaceCard(
    modifier: Modifier = Modifier,
    containerColor: Color = PinkCard,
    borderColor: Color = PinkBorder.copy(alpha = 0.28f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = InkBlack
        ),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        content = content
    )
}

@Composable
fun HeroEarningBanner(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = PinkPrimary,
        border = BorderStroke(1.5.dp, InkBlack.copy(alpha = 0.16f)),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFF8BBD0), Color(0xFFF48FB1), Color(0xFFEC407A))
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = InkBlack,
                        shape = RoundedCornerShape(50),
                    ) {
                        Text(
                            text = "BD EARNING TEAM PRO",
                            color = PinkCard,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = InkBlack,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkBlackSecondary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PinkCard.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountBalanceWallet,
                        contentDescription = "BD Earning Wallet",
                        tint = InkBlack,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LockedBadgeGraphic(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(104.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(PinkPrimary, PinkBgDeep)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = InkBlack
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Account Locked",
                    tint = PinkCard,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun ContactAdminButtons(
    context: Context,
    whatsappNumber: String,
    facebookUrl: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = { openWhatsAppSafe(context, whatsappNumber) },
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("contact_whatsapp_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = PinkPrimary,
                contentColor = InkBlack
            ),
            border = BorderStroke(1.dp, WhatsAppGreen),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Chat,
                contentDescription = "WhatsApp",
                tint = InkBlack,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "WhatsApp",
                color = InkBlack,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        OutlinedButton(
            onClick = { openFacebookSafe(context, facebookUrl) },
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("contact_facebook_button"),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = PinkCard,
                contentColor = InkBlack
            ),
            border = BorderStroke(1.2.dp, FacebookBlue),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Public,
                contentDescription = "Facebook",
                tint = InkBlack,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Facebook",
                color = InkBlack,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PortalLinkRow(
    label: String,
    link: String,
    context: Context,
    testTag: String
) {
    Surface(
        color = PinkCardAlt,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, PinkBorder.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = InkBlackMuted
                )
                Text(
                    text = link,
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkBlack,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                onClick = { copyToClipboard(context, label, link) },
                modifier = Modifier.testTag(testTag)
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "লিংক কপি করুন",
                    tint = InkBlack
                )
            }
        }
    }
}

fun categoryIcon(category: TaskCategory): ImageVector = when (category) {
    TaskCategory.VIDEO -> Icons.Filled.PlayCircleFilled
    TaskCategory.AD -> Icons.Filled.Public
    TaskCategory.GAME -> Icons.Filled.SportsEsports
    TaskCategory.SOCIAL -> Icons.Filled.ThumbUp
}

fun formatTaka(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        "৳${amount.toInt()}"
    } else {
        String.format(Locale.US, "৳%.2f", amount)
    }
}

fun formatDateTime(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(millis))
}

fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    Toast.makeText(context, "$label কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
}

fun openUrlSafe(context: Context, rawUrl: String) {
    val url = rawUrl.trim()
    if (url.isEmpty()) {
        Toast.makeText(context, "লিংক পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
        return
    }
    val normalized = if (url.startsWith("http://", true) || url.startsWith("https://", true)) {
        url
    } else {
        "https://$url"
    }
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalized)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "লিংক খোলার জন্য কোনো ব্রাউজার পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
    }
}

fun openWhatsAppSafe(context: Context, rawNumber: String) {
    val digits = rawNumber.filter { it.isDigit() }
    if (digits.isEmpty()) {
        Toast.makeText(context, "অ্যাডমিন এখনও WhatsApp নম্বর যোগ করেননি", Toast.LENGTH_SHORT).show()
        return
    }
    val formatted = if (digits.startsWith("01") && digits.length == 11) "88$digits" else digits
    openUrlSafe(context, "https://wa.me/$formatted")
}

fun openFacebookSafe(context: Context, rawUrl: String) {
    if (rawUrl.isBlank()) {
        Toast.makeText(context, "অ্যাডমিন এখনও Facebook লিংক যোগ করেননি", Toast.LENGTH_SHORT).show()
        return
    }
    openUrlSafe(context, rawUrl)
}
