package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.data.AppConfigEntity
import com.example.ui.components.PinkGradientBackground
import com.example.ui.components.PinkSurfaceCard
import com.example.ui.theme.InkBlack
import com.example.ui.theme.InkBlackSecondary
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkCard
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.StatusRedBg
import com.example.ui.theme.StatusRedInk
import com.example.ui.viewmodel.AuthUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAuthScreen(
    config: AppConfigEntity,
    authState: AuthUiState,
    onSetupAdmin: (name: String, phone: String, password: String, whatsapp: String, facebook: String) -> Unit,
    onLoginAdmin: (phone: String, password: String) -> Unit,
    onClearAuthState: () -> Unit,
    onBack: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var whatsapp by rememberSaveable { mutableStateOf("") }
    var facebook by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onClearAuthState()
    }

    val isSetupMode = !config.adminSetupCompleted

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
                        text = if (isSetupMode) "অ্যাডমিন সেটআপ" else "অ্যাডমিন লগইন",
                        color = InkBlack,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("admin_auth_back_button")
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
                        Icon(
                            imageVector = Icons.Filled.AdminPanelSettings,
                            contentDescription = null,
                            tint = InkBlack,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = if (isSetupMode) {
                                "প্রথমবার অ্যাডমিন অ্যাকাউন্ট তৈরি করুন"
                            } else {
                                "অ্যাডমিন প্যানেলে প্রবেশ করুন"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = InkBlack,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isSetupMode) {
                                "আপনার অ্যাডমিন লগইন তথ্য এবং কর্মীদের যোগাযোগের জন্য WhatsApp ও Facebook লিংক সেট করুন।"
                            } else {
                                "আপনার অ্যাডমিন ফোন নম্বর এবং পাসওয়ার্ড দিয়ে লগইন করুন।"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkBlackSecondary
                        )

                        val fieldColors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = InkBlack,
                            unfocusedTextColor = InkBlack,
                            focusedBorderColor = PinkBorder,
                            unfocusedBorderColor = InkBlack.copy(alpha = 0.35f),
                            focusedLabelColor = InkBlack,
                            unfocusedLabelColor = InkBlackSecondary,
                            cursorColor = InkBlack
                        )

                        if (isSetupMode) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("অ্যাডমিনের নাম") },
                                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = InkBlack) },
                                singleLine = true,
                                colors = fieldColors,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_setup_name_input")
                            )
                        }

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("ফোন নম্বর") },
                            leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = InkBlack) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = fieldColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_phone_input")
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
                                .testTag("admin_password_input")
                        )

                        if (isSetupMode) {
                            OutlinedTextField(
                                value = whatsapp,
                                onValueChange = { whatsapp = it },
                                label = { Text("WhatsApp নম্বর (যেমন: 017xxxxxxxx)") },
                                leadingIcon = { Icon(Icons.Filled.Chat, contentDescription = null, tint = InkBlack) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                colors = fieldColors,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_setup_whatsapp_input")
                            )

                            OutlinedTextField(
                                value = facebook,
                                onValueChange = { facebook = it },
                                label = { Text("Facebook লিংক (যেমন: https://fb.com/...)") },
                                leadingIcon = { Icon(Icons.Filled.Public, contentDescription = null, tint = InkBlack) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                                singleLine = true,
                                colors = fieldColors,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_setup_facebook_input")
                            )
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
                                if (isSetupMode) {
                                    onSetupAdmin(name, phone, password, whatsapp, facebook)
                                } else {
                                    onLoginAdmin(phone, password)
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
                                .testTag("admin_submit_button")
                        ) {
                            if (authState is AuthUiState.Loading) {
                                CircularProgressIndicator(
                                    color = InkBlack,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (isSetupMode) "অ্যাডমিন সেটআপ সম্পন্ন করুন" else "অ্যাডমিন লগইন করুন",
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
