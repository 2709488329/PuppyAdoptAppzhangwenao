package com.puppyadopt.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puppyadopt.data.*
import com.puppyadopt.ui.components.*

private val navItems = listOf(
    BottomNavItem("home", "救助站", "🏠"),
    BottomNavItem("shop", "商店", "🛍️"),
    BottomNavItem("story", "小紫", "🌸"),
    BottomNavItem("settings", "设置", "⚙️")
)

@Composable
fun MainGameScreen(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    var selectedTab by remember { mutableStateOf("home") }
    var showResetDialog by remember { mutableStateOf(false) }

    val rescueClicks = getRescueClickNeeded(state)
    val promoteClicks = getPromoteClickNeeded(state)

    Scaffold(
        bottomBar = {
            GameBottomBar(
                items = navItems,
                selectedRoute = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                "home" -> HomeScreen(
                    state = state,
                    gameSeconds = viewModel.getGameTimeSeconds(),
                    onRescue = { viewModel.doRescue() },
                    onPromote = { viewModel.doPromote() },
                    showRescueClicks = state.rescClk,
                    showPromoteClicks = state.proClk,
                    rescueNeeded = rescueClicks,
                    promoteNeeded = promoteClicks
                )
                "shop" -> ShopScreen(
                    state = state,
                    onHireEmployee = { viewModel.hireEmployee(it) },
                    onDiamondHire = { viewModel.diamondHire(it) },
                    onDiamondExchange = { viewModel.diamondExchange() },
                    onBuyDiamondProb = { viewModel.buyDiamondProb() },
                    onBuyShopItem = { viewModel.buyShopItem(it) },
                    onUpgrade = { viewModel.upgrade(it) }
                )
                "story" -> StoryScreen(state = state)
                "settings" -> SettingsScreen(
                    onResetGame = { showResetDialog = true },
                    gameSeconds = viewModel.getGameTimeSeconds()
                )
            }

            // Toast
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                GameToast(message = toastMessage)
            }

            // 剧情事件
            if (state.eventLock && state.currentEvent != null) {
                val event = state.currentEvent!!
                GameDialog(
                    title = event.title,
                    message = event.message,
                    buttons = event.buttons.mapIndexed { index, _ ->
                        Pair(event.buttons[index].text) {
                            viewModel.onEventButtonClick(index)
                        }
                    },
                    onDismiss = { }
                )
            }

            // 重置确认
            if (showResetDialog) {
                GameDialog(
                    title = "🔄 确认重置",
                    message = "所有游戏进度将被清除，包括声望、钻石、员工、剧情进度等。此操作不可撤销！",
                    buttons = listOf(
                        Pair("✅ 确认重置") {
                            viewModel.resetGame()
                            showResetDialog = false
                        },
                        Pair("❌ 取消") { showResetDialog = false }
                    ),
                    onDismiss = { showResetDialog = false }
                )
            }

            // 结局
            if (state.finished) {
                FinishScreen(
                    totalPrestige = state.totalPrestige,
                    gameSeconds = viewModel.getGameTimeSeconds(),
                    onReset = { viewModel.resetGame() }
                )
            }

            // 欢迎页（未开始时）
            if (state.startTime == 0L && !state.finished && !state.eventLock) {
                WelcomeOverlay(onStart = { viewModel.startGame() })
            }
        }
    }
}

@Composable
private fun WelcomeOverlay(onStart: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GameCard(modifier = Modifier.padding(40.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "🐾", fontSize = 60.sp)
                Text(
                    text = "领养小奶狗",
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = "帮助流浪狗找到温暖的家\n招募员工 · 经营救助站 · 与小紫一起努力",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                PinkButton(text = "🐶 开始救助", onClick = onStart)
            }
        }
    }
}

private fun getRescueClickNeeded(state: GameState): Int {
    val lv = if (state.foodSec > 0) minOf(6, state.rescueLv + 2) else state.rescueLv
    val base = BASE_CLICKS[lv.coerceIn(0, 6)]
    return if (state.chatRedSec > 0) maxOf(1, base - 1) else base
}

private fun getPromoteClickNeeded(state: GameState): Int {
    val lv = if (state.foodSec > 0) minOf(6, state.promoteLv + 2) else state.promoteLv
    val base = BASE_CLICKS[lv.coerceIn(0, 6)]
    return if (state.chatRedSec > 0) maxOf(1, base - 1) else base
}
