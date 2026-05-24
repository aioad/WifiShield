package com.example.ui.screens

import android.content.Context
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SecurityStatus
import com.example.data.model.WifiScanResult
import com.example.ui.ScanState
import com.example.ui.WifiShieldViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// ==========================================
// 1. SPLASH SCREEN
// ==========================================
@Composable
fun SplashScreen(onNavigateToDashboard: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    val scaleFactor by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale"
    )

    var currentTerminalText by remember { mutableStateOf("BOOT_LOADER_CYBER_SHIELD: 0x3f9A2") }
    
    LaunchedEffect(Unit) {
        val diagnosticMsgs = listOf(
            "INITIALIZING SECURITY ENVELOPE...",
            "ACQUIRING HOST BROADCAST REVERSED DNS...",
            "CONSTRUCTING PACKET ENCRYPTION TABLE...",
            "PROBING PUBLIC AP EXCLUSION CHIPS...",
            "SHIELD ENGAGED. LAUNCHING DASHBOARD COMMANDS..."
        )
        for (msg in diagnosticMsgs) {
            delay(400)
            currentTerminalText = msg
        }
        delay(300)
        onNavigateToDashboard()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Decorative grid mesh drawing (Simulated background)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSpacing = 60f
            val alpha = 0.08f
            for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
                drawLine(
                    color = CyberBlue,
                    start = androidx.compose.ui.geometry.Offset(x.toFloat(), 0f),
                    end = androidx.compose.ui.geometry.Offset(x.toFloat(), size.height),
                    strokeWidth = 1f,
                    alpha = alpha
                )
            }
            for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
                drawLine(
                    color = CyberBlue,
                    start = androidx.compose.ui.geometry.Offset(0f, y.toFloat()),
                    end = androidx.compose.ui.geometry.Offset(size.width, y.toFloat()),
                    strokeWidth = 1f,
                    alpha = alpha
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Outer rotating radar circle
                Canvas(
                    modifier = Modifier
                        .size(180.dp)
                        .testTag("radar_canvas")
                ) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(Color.Transparent, CyberBlue.copy(alpha = 0.6f), CyberBlue)
                        ),
                        startAngle = rotationAngle,
                        sweepAngle = 180f,
                        useCenter = false,
                        style = Stroke(width = 6f, cap = StrokeCap.Round)
                    )
                }

                // Inner shield container pulsing
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .border(2.dp, CyberBlue, CircleShape)
                        .background(CyberSlate, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = "Shield Logo",
                        tint = CyberBlue,
                        modifier = Modifier
                            .size(60.dp)
                            .testTag("splash_logo")
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "WIFI SHIELD",
                color = Color.White,
                fontSize = 32.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "CYBERSECURITY & THREAT AUDITOR",
                color = CyberBlue,
                fontSize = 12.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Cyber loader terminal log
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberBorder, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberCard)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = CyberBlue,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = currentTerminalText,
                        color = CyberGreen,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.testTag("terminal_diagnostic_text")
                    )
                }
            }
        }
    }
}


// ==========================================
// 2. DASHBOARD HOME
// ==========================================
@Composable
fun DashboardScreen(
    viewModel: WifiShieldViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scanResult by viewModel.scanResult.collectAsState()
    val scanState by viewModel.scanState.collectAsState()
    val isVpnActive by viewModel.isVpnActiveFlow.collectAsState()
    val autoVpn by viewModel.autoVpnEnabled.collectAsState()
    val countdown by viewModel.warningCountdown.collectAsState()
    val aiAdvisoryState by viewModel.aiAdvisoryState.collectAsState()
    val aiChatState by viewModel.aiChatState.collectAsState()

    // Activity Result Launcher to prepare & trigger Android VPN permissions window
    val vpnPrepareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                viewModel.engageVpn(context)
            }
        }
    )

    // Function to trigger VPN securely checking runtime permissions setup
    val triggerVpnActivation = {
        if (isVpnActive) {
            viewModel.stopVpn(context)
        } else {
            val vpnIntent = VpnService.prepare(context)
            if (vpnIntent != null) {
                vpnPrepareLauncher.launch(vpnIntent)
            } else {
                viewModel.engageVpn(context)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initNotificationHelper(context)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // 1. Sleek Header (Elegant Dark Design Theme Spec)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isVpnActive) "SHIELD ACTIVE" else "MONITORING ACTIVE",
                        color = if (isVpnActive) CyberBlue else CyberOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "WiFi Shield",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.5).sp
                    )
                }
                
                // Rotated decorative neon badge matching HTML spec branding
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(2.dp, CyberBlue, RoundedCornerShape(4.dp))
                            .rotate(45f)
                    )
                }
            }
        }

        // 2. Core Protection Top Status Badge (Updated layout)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    if (isVpnActive) CyberGreen else if (scanResult?.status == SecurityStatus.UNSAFE) CyberRed else CyberOrange,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isVpnActive) "FULL VPN ENCRYPTION ACTIVE" else "MONITORING WI-FI SAFETY FACTORS",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (isVpnActive) CyberGreen.copy(alpha = 0.15f) else CyberBlue.copy(alpha = 0.15f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isVpnActive) "PROTECTED" else "AUDITING",
                            color = if (isVpnActive) CyberGreen else CyberBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Custom Radial Status Canvas
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                val score = scanResult?.score ?: 100
                val accentColor = when {
                    isVpnActive -> CyberGreen
                    score <= 30 -> CyberRed
                    score <= 70 -> CyberOrange
                    else -> CyberBlue
                }

                val scaleProgress = remember { Animatable(1f) }
                LaunchedEffect(scanResult, isVpnActive) {
                    scaleProgress.animateTo(
                        targetValue = 1.05f,
                        animationSpec = repeatable(
                            iterations = 3,
                            animation = tween(800, easing = EaseInOutSine),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    scaleProgress.snapTo(1f)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .clickable { viewModel.triggerDeepScan(context) }
                            .testTag("animated_radar_centerpiece"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Halo Rings
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = accentColor,
                                radius = (size.minDimension / 2f) * scaleProgress.value,
                                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round),
                                alpha = 0.15f
                            )
                            drawCircle(
                                color = accentColor,
                                radius = size.minDimension / 2.3f,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                                alpha = 0.35f
                            )
                        }

                        // Central core circle containing Gradient and central text values (Matches HTML div class="shadow-...")
                        Card(
                            modifier = Modifier
                                .size(130.dp)
                                .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(Color(0xFF1C1C1C), Color(0xFF090909))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$score",
                                        color = accentColor,
                                        fontSize = 42.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.testTag("dashboard_score_value")
                                    )
                                    Text(
                                        text = "Risk Score".uppercase(),
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 1.5.sp
                                    )
                                    
                                    // Custom Pill status indicator
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .background(accentColor.copy(alpha = 0.2f), RoundedCornerShape(100.dp))
                                            .padding(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isVpnActive) "SECURE" else scanResult?.statusText?.uppercase() ?: "SAFE",
                                            color = accentColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4. Countdown Progress Mini Bar (Direct translation of HTML Countdown)
                    val count = countdown
                    if (count != null && count > 0) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .background(CyberRed, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Auto-VPN: ${count}s",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.triggerDeepScan(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .width(200.dp)
                            .testTag("trigger_scan_button")
                    ) {
                        Icon(imageVector = Icons.Filled.NetworkCheck, contentDescription = null, tint = CyberBlack)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Scan Wi-Fi", color = CyberBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Security Loader Progress
        scanState.let { state ->
            if (state is ScanState.Scanning) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberBlue.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberSlate)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "DEEP WI-FI AUDIT ACTIVE",
                                color = CyberBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state.status,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(4.dp),
                                color = CyberBlue,
                                trackColor = CyberCard
                            )
                        }
                    }
                }
            }
        }

        // 5. Glassmorphism 2x2 Telemetry Grid (Direct translation of HTML layout pattern)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "VULNERABILITY TELEMETRY",
                    color = CyberBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // CELL 1: Network
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp)
                            .border(1.dp, CyberBorder, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberCard)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "NETWORK",
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = scanResult?.ssid ?: "Disconnected",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    modifier = Modifier.testTag("grid_metric_network_ssid")
                                )
                            }
                            Text(
                                text = if (scanResult?.status == SecurityStatus.UNSAFE) "● Critical Risk" else if (scanResult?.status == SecurityStatus.RISKY) "● High Risk" else "● Protected Secure",
                                color = if (scanResult?.status == SecurityStatus.UNSAFE) CyberRed else if (scanResult?.status == SecurityStatus.RISKY) CyberOrange else CyberGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // CELL 2: Encryption
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp)
                            .border(1.dp, CyberBorder, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberCard)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "ENCRYPTION",
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = scanResult?.encryptionType ?: "None",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    modifier = Modifier.testTag("grid_metric_encryption")
                                )
                            }
                            Text(
                                text = if (scanResult?.isOpen == true) "Insecure" else "Fully Protected",
                                color = if (scanResult?.isOpen == true) CyberRed else CyberGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // CELL 3: VPN Status
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp)
                            .border(1.dp, CyberBorder, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberCard)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "VPN STATUS",
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isVpnActive) "Protected" else "Inactive",
                                    color = if (isVpnActive) CyberGreen else CyberBlue,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    modifier = Modifier.testTag("grid_metric_vpn")
                                )
                            }
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(2.dp))
                                ) {
                                    val progressFraction = if (isVpnActive) 1f else 0.33f
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(if (isVpnActive) 1f else 0.15f)
                                            .fillMaxHeight()
                                            .background(if (isVpnActive) CyberGreen else CyberBlue, RoundedCornerShape(2.dp))
                                    )
                                }
                            }
                        }
                    }

                    // CELL 4: DNS Configuration
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp)
                            .border(1.dp, CyberBorder, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberCard)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "DNS LEAK STATUS",
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (scanResult?.suspiciousDns == true) "Poisoned Leak" else "Protected",
                                    color = if (scanResult?.suspiciousDns == true) CyberRed else CyberGreen,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    modifier = Modifier.testTag("grid_metric_dns")
                                )
                            }
                            Text(
                                text = scanResult?.dnsInfo ?: "Cloudflare v2",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // 6. Critical Warning Action Card (Direct translation of HTML layout pattern)
        val showCriticalWarning = (scanResult != null && (scanResult?.status == SecurityStatus.UNSAFE || scanResult?.status == SecurityStatus.RISKY))
        if (showCriticalWarning && !isVpnActive) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberRed.copy(alpha = 0.4f), RoundedCornerShape(32.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(CyberRed.copy(alpha = 0.2f), CyberRed.copy(alpha = 0.05f))
                                )
                            )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(CyberRed, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "!",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Suspicious Activity Detected",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Possible MITM attack or Evil Twin detected. Encrypting traffic now.",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.stopVpn(context)
                                        viewModel.refreshActiveConnection(context)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                ) {
                                    Text(
                                        text = "DISCONNECT",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Button(
                                    onClick = { triggerVpnActivation() },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberRed, contentColor = Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                ) {
                                    Text(
                                        text = "PROTECT ME",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Interactive VPN Controller block (Adaptive 24dp card layout)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isVpnActive) CyberGreen.copy(alpha = 0.5f) else CyberBorder,
                        RoundedCornerShape(24.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = CyberCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isVpnActive) Icons.Filled.LockOpen else Icons.Filled.Lock,
                                contentDescription = null,
                                tint = if (isVpnActive) CyberGreen else CyberBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "In-App VPN Protection",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isVpnActive) "Safe traffic tunnel active" else "Unencrypted traffic vulnerable",
                                    color = if (isVpnActive) CyberGreen else Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = isVpnActive,
                            onCheckedChange = { triggerVpnActivation() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberBlack,
                                checkedTrackColor = CyberGreen,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = CyberSlate
                            ),
                            modifier = Modifier.testTag("in_app_vpn_toggle")
                        )
                    }

                    if (isVpnActive) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberGreen.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                .border(1.dp, CyberGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "Virtual IP: 10.8.0.2", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text(text = "Encryption: AES-256-GCM", color = CyberGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Proxying all TCP and UDP connection segments securely.", color = Color.LightGray, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // 8. Comprehensive Threat Indicators Grid
        scanResult?.let { result ->
            item {
                Column {
                    Text(
                        text = "VULNERABILITY ASSESSMENT FACTORS",
                        color = CyberBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberBorder, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberCard)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            CyberIndicatorRow(title = "Open Wi-Fi Risk (No Encryption)", flagged = result.isOpen)
                            CyberIndicatorRow(title = "Weak / Outdated WEP protocols", flagged = result.weakEncryption)
                            CyberIndicatorRow(title = "Rogue DNS / Poison Configuration", flagged = result.suspiciousDns)
                            CyberIndicatorRow(title = "Unsecured Captive Portal Intercept", flagged = result.captivePortalDetected)
                            CyberIndicatorRow(title = "MITM Attacks (Downgrades)", flagged = result.mitmIndicator)
                            CyberIndicatorRow(title = "Evil Twin (SSID cloning spoof)", flagged = result.evilTwinDetected)
                            CyberIndicatorRow(title = "ARP Network Poisoning Detection", flagged = result.arpSpoofingDetected)
                            CyberIndicatorRow(title = "Suspicious Local Gateway Broadcasts", flagged = result.suspiciousGateway)
                        }
                    }
                }
            }

            item {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "GEMINI CYBER ADVISOR",
                        color = CyberBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberBorder, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberCard)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Psychology,
                                        contentDescription = null,
                                        tint = CyberBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "AI Threat Intelligence",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "On-demand network risk diagnostics",
                                            color = Color.Gray,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .background(CyberBlue.copy(alpha = 0.15f), RoundedCornerShape(100.dp))
                                        .border(1.dp, CyberBlue, RoundedCornerShape(100.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "AI MODEL",
                                        color = CyberBlue,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            when (val state = aiAdvisoryState) {
                                is com.example.ui.AiAdvisoryState.Idle -> {
                                    Text(
                                        text = "Run a live Gemini intelligence assessment of the current scan scores, ports, encryption factors, and rogue configuration metrics to receive tailored safety ratings.",
                                        color = Color.LightGray,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { viewModel.analyzeNetworkWithAi(result) },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue, contentColor = CyberBlack),
                                        modifier = Modifier.fillMaxWidth().testTag("ai_run_assessment_button"),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Audit Network with AI", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                is com.example.ui.AiAdvisoryState.Loading -> {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(color = CyberBlue, modifier = Modifier.size(32.dp))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Consulting Gemini AI security brain...",
                                            color = CyberBlue,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                is com.example.ui.AiAdvisoryState.Success -> {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
                                                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                                .padding(12.dp)
                                        ) {
                                            Text(
                                                text = state.advice,
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                lineHeight = 18.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = { viewModel.analyzeNetworkWithAi(result) },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberSlate, contentColor = Color.White),
                                            border = BorderStroke(1.dp, CyberBorder),
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Refresh Report", fontSize = 11.sp)
                                        }
                                    }
                                }
                                is com.example.ui.AiAdvisoryState.Error -> {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = state.errorMessage,
                                            color = CyberRed,
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = { viewModel.analyzeNetworkWithAi(result) },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberRed, contentColor = Color.White),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Retry Assessment", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            Divider(color = CyberBorder, modifier = Modifier.padding(vertical = 16.dp))

                            // Interactive Ask-AI Console Row
                            Text(
                                text = "ASK WI-FI SHIELD AI ASSISTANT",
                                color = CyberBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            var queryText by remember { mutableStateOf("") }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = queryText,
                                    onValueChange = { queryText = it },
                                    placeholder = { Text("e.g. What is automatic VPN?", fontSize = 12.sp, color = Color.Gray) },
                                    modifier = Modifier.weight(1f).testTag("ai_chat_query_input"),
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyberBlue,
                                        unfocusedBorderColor = CyberBorder,
                                        cursorColor = CyberBlue
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Button(
                                    onClick = {
                                        if (queryText.isNotBlank()) {
                                            viewModel.askAiQuestion(queryText, result)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberBlue, contentColor = CyberBlack),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(52.dp).testTag("ai_send_query_button"),
                                    contentPadding = PaddingValues(horizontal = 14.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                                }
                            }

                            val chatState = aiChatState
                            if (chatState !is com.example.ui.AiChatState.Idle) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, CyberBorder, RoundedCornerShape(16.dp)),
                                    colors = CardDefaults.cardColors(containerColor = CyberBlack)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "AI RESPONSE CONSOLE",
                                                color = CyberGreen,
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp
                                            )
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "Clear",
                                                tint = Color.Gray,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable { viewModel.resetAiChat() }
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        when (chatState) {
                                            is com.example.ui.AiChatState.Loading -> {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    CircularProgressIndicator(color = CyberGreen, modifier = Modifier.size(14.dp), strokeWidth = 1.5.dp)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Decrypting AI counselor reply...",
                                                        color = CyberGreen,
                                                        fontSize = 11.sp,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }
                                            }
                                            is com.example.ui.AiChatState.Success -> {
                                                Text(
                                                    text = chatState.response,
                                                    color = Color.LightGray,
                                                    fontSize = 11.sp,
                                                    lineHeight = 16.sp
                                                )
                                            }
                                            is com.example.ui.AiChatState.Error -> {
                                                Text(
                                                    text = chatState.errorMessage,
                                                    color = CyberRed,
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                            else -> {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricRow(label: String, value: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = label, color = Color.LightGray, fontSize = 13.sp)
        }
        Text(
            text = value,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun CyberIndicatorRow(title: String, flagged: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        )
        Box(
            modifier = Modifier
                .background(
                    if (flagged) CyberRed.copy(alpha = 0.15f) else CyberGreen.copy(alpha = 0.15f),
                    RoundedCornerShape(4.dp)
                )
                .border(
                    1.dp,
                    if (flagged) CyberRed.copy(alpha = 0.5f) else CyberGreen.copy(alpha = 0.5f),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (flagged) Icons.Filled.Warning else Icons.Filled.Check,
                    contentDescription = null,
                    tint = if (flagged) CyberRed else CyberGreen,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = if (flagged) "THREAT_FLAGGED" else "STATUS_SECURE",
                    color = if (flagged) CyberRed else CyberGreen,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// ==========================================
// 3. EXCLUSIONS MODULE (TRUSTED NETWORKS)
// ==========================================
@Composable
fun TrustedNetworksScreen(
    viewModel: WifiShieldViewModel,
    modifier: Modifier = Modifier
) {
    val trustedNetworks by viewModel.trustedNetworks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var inputSsid by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Home") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "SAVED SECURE NETWORKS",
                color = CyberBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "Trusted networks bypass scanning checks and skip threatening active alert modal countdown screens.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (trustedNetworks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(CyberCard, RoundedCornerShape(8.dp))
                        .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Filled.VerifiedUser, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "No Excluded Networks Registered", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Register safe home/work nodes to whitelist coordinates.",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(trustedNetworks) { network ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = CyberCard)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (network.type) {
                                            "Home" -> Icons.Filled.Home
                                            "Office" -> Icons.Filled.Work
                                            "College" -> Icons.Filled.School
                                            else -> Icons.Filled.Wifi
                                        },
                                        contentDescription = null,
                                        tint = CyberBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(text = network.ssid, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(text = "Domain Type: ${network.type}", color = CyberGreen, fontSize = 11.sp)
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.removeTrustedNetwork(network.id, network.ssid) },
                                    modifier = Modifier.testTag("delete_trusted_${network.ssid}")
                                ) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete network", tint = CyberRed)
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = CyberBlue,
            contentColor = CyberBlack,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp)
                .testTag("register_whitelist_fab")
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Register Secure Node")
        }

        if (showAddDialog) {
            Dialog(onDismissRequest = { showAddDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberBlue, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberSlate)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "REGISTER TRUSTED NODE",
                            color = CyberBlue,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(text = "Wi-Fi SSID name (exact match)", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                        OutlinedTextField(
                            value = inputSsid,
                            onValueChange = { inputSsid = it },
                            placeholder = { Text("E.g., Home_Net", color = Color.Gray) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberBlue,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("whitelist_input_ssid")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Assigned Location Mode", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Home", "Office", "College", "Other").forEach { type ->
                                FilterChip(
                                    selected = selectedType == type,
                                    onClick = { selectedType = type },
                                    label = { Text(type) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberBlue,
                                        selectedLabelColor = CyberBlack,
                                        containerColor = CyberCard,
                                        labelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showAddDialog = false }) {
                                Text("Discard", color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (inputSsid.isNotBlank()) {
                                        viewModel.addTrustedNetwork(inputSsid, selectedType)
                                        inputSsid = ""
                                        showAddDialog = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                                modifier = Modifier.testTag("save_trusted_network_button")
                            ) {
                                Text("Add Node", color = CyberBlack, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 4. HISTORY LOGS MODULE
// ==========================================
@Composable
fun HistoryLogsScreen(
    viewModel: WifiShieldViewModel,
    modifier: Modifier = Modifier
) {
    val history by viewModel.networkHistory.collectAsState()
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AUDIT SCAN LOGGER",
                    color = CyberBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Text(text = "Security assessment history trace logs.", color = Color.Gray, fontSize = 12.sp)
            }

            if (history.isNotEmpty()) {
                TextButton(
                    onClick = { viewModel.clearScanHistory() },
                    modifier = Modifier.testTag("clear_history_logs_button")
                ) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null, tint = CyberRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Purge Logs", color = CyberRed, fontSize = 12.sp)
                }
            }
        }

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(CyberCard, RoundedCornerShape(8.dp))
                    .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Filled.History, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "No Session Artifact Logs Recorded", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Your connection security assessments will log entries locally.",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(history) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                when (record.securityStatus) {
                                    "UNSAFE" -> CyberRed.copy(alpha = 0.4f)
                                    "RISKY" -> CyberOrange.copy(alpha = 0.4f)
                                    else -> CyberBorder
                                },
                                RoundedCornerShape(8.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = CyberCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                when (record.securityStatus) {
                                                    "UNSAFE" -> CyberRed
                                                    "RISKY" -> CyberOrange
                                                    else -> CyberGreen
                                                },
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = record.ssid,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                Text(
                                    text = "${record.securityScore}/100",
                                    color = when (record.securityStatus) {
                                        "UNSAFE" -> CyberRed
                                        "RISKY" -> CyberOrange
                                        else -> CyberGreen
                                    },
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Date: ${sdf.format(Date(record.timestamp))}",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )

                                if (record.vpnUsed) {
                                    Box(
                                        modifier = Modifier
                                            .background(CyberGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("VPN_SHIELD", color = CyberGreen, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 5. SYSTEM SETTINGS MODULE
// ==========================================
@Composable
fun SettingsScreen(
    viewModel: WifiShieldViewModel,
    modifier: Modifier = Modifier
) {
    val autoVpn by viewModel.autoVpnEnabled.collectAsState()
    val notifications by viewModel.notificationsEnabled.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SHIELD PROTOCOL PARAMETERS",
            color = CyberBlue,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                
                // Switch row - Auto VPN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Auto VPN Enforcement", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Engages VPN automatically when unsafe nodes are connected.", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = autoVpn,
                        onCheckedChange = { viewModel.setAutoVpn(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberBlack,
                            checkedTrackColor = CyberGreen,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = CyberSlate
                        ),
                        modifier = Modifier.testTag("setting_auto_vpn_toggle")
                    )
                }

                HorizontalDivider(color = CyberBorder, modifier = Modifier.padding(vertical = 12.dp))

                // Switch row - Notification alerts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Security Notification Alerts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Pushes localized system alarms on critical Wi-Fi threats.", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = notifications,
                        onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberBlack,
                            checkedTrackColor = CyberGreen,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = CyberSlate
                        ),
                        modifier = Modifier.testTag("setting_notifications_toggle")
                    )
                }

                HorizontalDivider(color = CyberBorder, modifier = Modifier.padding(vertical = 12.dp))

                // System Dark Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Cyber Dark Interface", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Forces glassmorphic deep space visual matrix layouts.", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberBlack,
                            checkedTrackColor = CyberGreen,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = CyberSlate
                        )
                    )
                }
            }
        }

        // Info / educational cybersecurity segment
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSlate)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🔒 TECHNICAL RISK NOTES",
                    color = CyberOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "No scanning engine guarantees absolute safety. Our scoring computes the relative risk assessment by scanning local open wireless access headers, packet injection footprints, DNS latency, and MAC cloning signatures.",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}


// ==========================================
// 6. WARNING FULL SCREEN MODAL DIALOGUE
// ==========================================
@Composable
fun DangerWarningModal(
    viewModel: WifiShieldViewModel
) {
    val showModal by viewModel.showWarningModal.collectAsState()
    val countdown by viewModel.warningCountdown.collectAsState()
    val scanResult by viewModel.scanResult.collectAsState()
    val context = LocalContext.current

    if (showModal) {
        Dialog(
            onDismissRequest = { viewModel.dismissWarningModal() }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, CyberRed, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberBlack)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Threat Detected Flag",
                        tint = CyberRed,
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("threat_detected_warning_icon")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "UNSAFE WI-FI DETECTED",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Connection: ${scanResult?.ssid ?: "Unknown Source"}",
                        color = CyberOrange,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Text(
                        text = "This unsecured network does not encrypt transmissions. Sniffers may capture passwords, bank tokens, and personal credentials.",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Animated Warning Countdown Clock
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .border(2.dp, CyberRed.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${countdown ?: 0}",
                                color = CyberRed,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("countdown_timer_text")
                            )
                            Text(
                                text = "SECONDS",
                                color = Color.Gray,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Automatically engaging VPN protection afterward...",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = { viewModel.startVpnAction(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("engage_vpn_immediate_button")
                    ) {
                        Text(
                            text = "ENABLE SHIELD PROTECTION",
                            color = CyberBlack,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.stayConnectedAction() },
                            border = BorderStroke(1.dp, CyberBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("stay_connected_anyway_button")
                        ) {
                            Text(text = "Stay Connected", color = Color.LightGray, fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { 
                                viewModel.dismissWarningModal()
                                // Simulating WiFi disconnect by refreshing state cleanly
                                viewModel.refreshActiveConnection(context)
                            },
                            border = BorderStroke(1.dp, CyberRed.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("disconnect_unsecured_wifi_button")
                        ) {
                            Text(text = "Disconnect SSID", color = CyberRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
