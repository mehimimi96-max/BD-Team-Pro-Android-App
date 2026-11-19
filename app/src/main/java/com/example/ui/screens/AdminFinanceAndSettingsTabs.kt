package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AppConfigEntity
import com.example.data.ReferralEntity
import com.example.data.ReferralStatus
import com.example.data.WithdrawalEntity
import com.example.data.WithdrawalStatus
import com.example.ui.components.PinkSurfaceCard
import com.example.ui.components.PortalLinkRow
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatDateTime
import com.example.ui.components.formatTaka
import com.example.ui.theme.InkBlack
import com.example.ui.theme.InkBlackMuted
import com.example.ui.theme.InkBlackSecondary
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkCard
import com.example.ui.theme.PinkCardAlt
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.StatusAmberBg
import com.example.ui.theme.StatusAmberInk
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusGreenInk
import com.example.ui.theme.StatusRedBg
import com.example.ui.theme.StatusRedInk

@Composable
fun AdminFinanceTab(
    referrals: List<ReferralEntity>,
    withdrawals: List<WithdrawalEntity>,
    onVerifyReferral: (Long) -> Unit,
    onRejectReferral: (Long) -> Unit,
    onApproveWithdrawal: (Long) -> Unit,
    onRejectWithdrawal: (Long, String) -> Unit
) {
    var subTab by rememberSaveable { mutableIntStateOf(0) } // 0 = Withdrawals, 1 = Referrals

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = subTab,
            containerColor = PinkCardAlt,
            contentColor = InkBlack
        ) {
            Tab(
                selected = subTab == 0,
                onClick = { subTab = 0 },
                modifier = Modifier.testTag("admin_subtab_withdrawals"),
                text = {
                    val pendingCount = withdrawals.count { it.withdrawalStatus() == WithdrawalStatus.PENDING }
                    Text("উত্তোলন ($pendingCount)", fontWeight = FontWeight.Bold, color = InkBlack)
                }
            )
            Tab(
                selected = subTab == 1,
                onClick = { subTab = 1 },
                modifier = Modifier.testTag("admin_subtab_referrals"),
                text = {
                    val pendingCount = referrals.count { it.referralStatus() == ReferralStatus.PENDING }
                    Text("রেফার যাচাই ($pendingCount)", fontWeight = FontWeight.Bold, color = InkBlack)
                }
            )
        }

        if (subTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                if (withdrawals.isEmpty()) {
                    item {
                        PinkSurfaceCard {
                            Text(
                                text = "এখনও কোনো টাকা তোলার আবেদন আসেনি।",
                                color = InkBlackSecondary,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                } else {
                    items(withdrawals, key = { it.id }) { item ->
                        val status = item.withdrawalStatus()
                        val (bg, ink) = when (status) {
                            WithdrawalStatus.APPROVED -> StatusGreenBg to StatusGreenInk
                            WithdrawalStatus.PENDING -> StatusAmberBg to StatusAmberInk
                            WithdrawalStatus.REJECTED -> StatusRedBg to StatusRedInk
                        }
                        PinkSurfaceCard {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${item.workerName} (${item.workerPhone})",
                                            fontWeight = FontWeight.Bold,
                                            color = InkBlack
                                        )
                                        Text(
                                            text = "${item.method}: ${item.accountNumber}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = InkBlackSecondary
                                        )
                                        Text(
                                            text = "ভেরিফায়েড রেফার: ${item.verifiedReferralsAtRequest}টি • ${formatDateTime(item.createdAt)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = InkBlackMuted
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        StatusBadge(text = status.labelBn, backgroundColor = bg, textColor = ink)
                                        Text(
                                            text = formatTaka(item.amount),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = InkBlack
                                        )
                                    }
                                }

                                if (status == WithdrawalStatus.PENDING) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = { onApproveWithdrawal(item.id) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = StatusGreenBg,
                                                contentColor = StatusGreenInk
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp)
                                                .testTag("admin_approve_withdrawal_${item.id}")
                                        ) {
                                            Icon(Icons.Filled.Check, contentDescription = null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("অনুমোদন", fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = { onRejectWithdrawal(item.id, "অ্যাডমিন বাতিল করেছেন") },
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = StatusRedBg,
                                                contentColor = StatusRedInk
                                            ),
                                            border = BorderStroke(1.dp, StatusRedInk.copy(alpha = 0.4f)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp)
                                                .testTag("admin_reject_withdrawal_${item.id}")
                                        ) {
                                            Icon(Icons.Filled.Close, contentDescription = null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("বাতিল", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                if (referrals.isEmpty()) {
                    item {
                        PinkSurfaceCard {
                            Text(
                                text = "এখনও কোনো রেফার আবেদন নেই।",
                                color = InkBlackSecondary,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                } else {
                    items(referrals, key = { it.id }) { ref ->
                        val status = ref.referralStatus()
                        val (bg, ink) = when (status) {
                            ReferralStatus.VERIFIED -> StatusGreenBg to StatusGreenInk
                            ReferralStatus.PENDING -> StatusAmberBg to StatusAmberInk
                            ReferralStatus.REJECTED -> StatusRedBg to StatusRedInk
                        }
                        PinkSurfaceCard {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "রেফারকারী: ${ref.referrerName} (${ref.referrerPhone})",
                                            fontWeight = FontWeight.Bold,
                                            color = InkBlack
                                        )
                                        Text(
                                            text = "নতুন কর্মী: ${ref.referredName} (${ref.referredPhone})",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = InkBlackSecondary
                                        )
                                        Text(
                                            text = formatDateTime(ref.createdAt),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = InkBlackMuted
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        StatusBadge(text = status.labelBn, backgroundColor = bg, textColor = ink)
                                        Text(formatTaka(ref.bonusAmount), fontWeight = FontWeight.Bold, color = InkBlack)
                                    }
                                }

                                if (status == ReferralStatus.PENDING) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = { onVerifyReferral(ref.id) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = StatusGreenBg,
                                                contentColor = StatusGreenInk
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp)
                                                .testTag("admin_verify_referral_${ref.id}")
                                        ) {
                                            Icon(Icons.Filled.Check, contentDescription = null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("ভেরিফাই (+৳৫০)", fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = { onRejectReferral(ref.id) },
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = StatusRedBg,
                                                contentColor = StatusRedInk
                                            ),
                                            border = BorderStroke(1.dp, StatusRedInk.copy(alpha = 0.4f)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp)
                                                .testTag("admin_reject_referral_${ref.id}")
                                        ) {
                                            Icon(Icons.Filled.Close, contentDescription = null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("বাতিল", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun AdminSettingsTab(
    config: AppConfigEntity,
    onSaveSettings: (whatsapp: String, facebook: String, newPassword: String?) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var whatsapp by rememberSaveable(config.whatsappNumber) { mutableStateOf(config.whatsappNumber) }
    var facebook by rememberSaveable(config.facebookUrl) { mutableStateOf(config.facebookUrl) }
    var newPassword by rememberSaveable { mutableStateOf("") }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = InkBlack,
        unfocusedTextColor = InkBlack,
        focusedBorderColor = PinkBorder,
        unfocusedBorderColor = InkBlack.copy(alpha = 0.35f),
        focusedLabelColor = InkBlack,
        unfocusedLabelColor = InkBlackSecondary,
        cursorColor = InkBlack
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            PinkSurfaceCard {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "অ্যাডমিন ও ওয়ার্কার পোর্টাল লিংক",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )
                    PortalLinkRow(
                        label = "ওয়ার্কার অ্যাপ লিংক (Worker Portal)",
                        link = stringResource(R.string.portal_worker_link),
                        context = context,
                        testTag = "copy_worker_portal_link_button"
                    )
                    PortalLinkRow(
                        label = "অ্যাডমিন প্যানেল লিংক (Admin Panel)",
                        link = stringResource(R.string.portal_admin_link),
                        context = context,
                        testTag = "copy_admin_portal_link_button"
                    )
                }
            }
        }

        item {
            PinkSurfaceCard {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "যোগাযোগ (WhatsApp ও Facebook) ও পাসওয়ার্ড",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )

                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("WhatsApp নম্বর") },
                        leadingIcon = { Icon(Icons.Filled.Chat, contentDescription = null, tint = InkBlack) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_settings_whatsapp_input")
                    )

                    OutlinedTextField(
                        value = facebook,
                        onValueChange = { facebook = it },
                        label = { Text("Facebook লিংক") },
                        leadingIcon = { Icon(Icons.Filled.Public, contentDescription = null, tint = InkBlack) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_settings_facebook_input")
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("নতুন পাসওয়ার্ড (পরিবর্তন করতে চাইলে)") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = InkBlack) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_settings_password_input")
                    )

                    Button(
                        onClick = {
                            onSaveSettings(whatsapp, facebook, newPassword.takeIf { it.isNotBlank() })
                            newPassword = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PinkPrimary,
                            contentColor = InkBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("admin_save_settings_button")
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = null, tint = InkBlack)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("সেটিংস সংরক্ষণ করুন", fontWeight = FontWeight.Bold, color = InkBlack)
                    }
                }
            }
        }

        item {
            Surface(
                color = PinkCardAlt,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PinkBorder.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = InkBlack)
                    Text(
                        text = stringResource(R.string.local_db_notice),
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkBlackSecondary
                    )
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onLogout,
                border = BorderStroke(1.2.dp, InkBlack),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admin_settings_logout_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = InkBlack)
                Spacer(modifier = Modifier.width(8.dp))
                Text("অ্যাডমিন লগআউট করুন", color = InkBlack, fontWeight = FontWeight.Bold)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
