package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ui.components.PinkGradientBackground
import com.example.ui.components.PinkSurfaceCard
import com.example.ui.theme.InkBlack
import com.example.ui.theme.InkBlackSecondary
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkCard
import com.example.ui.theme.PinkCardAlt
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.StatusRedBg
import com.example.ui.theme.StatusRedInk
import com.example.ui.viewmodel.AuthUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerAuthScreen(
    authState: AuthUiState,
    onLogin: (phone: String, password: String) -> Unit,
    onRegister: (name: String, phone: String, password: String, referralCode: String) -> Unit,
    onClearAuthState: () -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) } // 0 = Login, 1 = Register
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var referralCode by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onClearAuthState()
    }

    PinkGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "ওয়ার্কার পোর্টাল",
                        color = InkBlack,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("worker_auth_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "পিছনে যান",
                            tint = InkBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Box(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                PinkSurfaceCard {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = PinkCardAlt,
                            contentColor = InkBlack
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = {
                                    selectedTab = 0
                                    onClearAuthState()
                                },
                                modifier = Modifier.testTag("worker_login_tab"),
                                text = {
                                    Text(
                                        text = "লগইন",
                                        fontWeight = FontWeight.Bold,
                                        color = InkBlack
                                    )
                                }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = {
                                    selectedTab = 1
                                    onClearAuthState()
                                },
                                modifier = Modifier.testTag("worker_register_tab"),
                                text = {
                                    Text(
                                        text = "রেজিস্ট্রেশন",
                                        fontWeight = FontWeight.Bold,
                                        color = InkBlack
                                    )
                                }
                            )
                        }

                        val fieldColors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = InkBlack,
                            unfocusedTextColor = InkBlack,
                            focusedBorderColor = PinkBorder,
                            unfocusedBorderColor = InkBlack.copy(alpha = 0.35f),
                            focusedLabelColor = InkBlack,
                            unfocusedLabelColor = InkBlackSecondary,
                            cursorColor = InkBlack
                        )

                        if (selectedTab == 1) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("আপনার নাম") },
                                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = InkBlack) },
                                singleLine = true,
                                colors = fieldColors,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("worker_register_name_input")
                            )
                        }

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("মোবাইল নম্বর") },
                            leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = InkBlack) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = fieldColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("worker_phone_input")
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("পাসওয়ার্ড") },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = InkBlack) },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = "পাসওয়ার্ড দেখুন",
                                        tint = InkBlack
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            colors = fieldColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("worker_password_input")
                        )

                        if (selectedTab == 1) {
                            OutlinedTextField(
                                value = referralCode,
                                onValueChange = { referralCode = it.uppercase() },
                                label = { Text("রেফার কোড (ঐচ্ছিক)") },
                                leadingIcon = { Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = InkBlack) },
                                singleLine = true,
                                colors = fieldColors,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("worker_referral_code_input")
                            )

                            Surface(
                                color = PinkCardAlt,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, PinkBorder.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = null,
                                        tint = InkBlack,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "রেজিস্ট্রেশনের পর নতুন অ্যাকাউন্ট লক থাকবে। অ্যাডমিন আনলক করলে কাজ শুরু করতে পারবেন।",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = InkBlackSecondary
                                    )
                                }
                            }
                        }

                        if (authState is AuthUiState.Error) {
                            Surface(
                                color = StatusRedBg,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = authState.message,
                                    color = StatusRedInk,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                if (selectedTab == 0) {
                                    onLogin(phone, password)
                                } else {
                                    onRegister(name, phone, password, referralCode)
                                }
                            },
                            enabled = authState !is AuthUiState.Loading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PinkPrimary,
                                contentColor = InkBlack
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("worker_auth_submit_button")
                        ) {
                            if (authState is AuthUiState.Loading) {
                                CircularProgressIndicator(
                                    color = InkBlack,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (selectedTab == 0) "লগইন করুন" else "রেজিস্ট্রেশন করুন",
                                    fontWeight = FontWeight.Bold,
                                    color = InkBlack
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
