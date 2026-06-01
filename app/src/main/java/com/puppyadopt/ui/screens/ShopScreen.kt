package com.puppyadopt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puppyadopt.data.*
import com.puppyadopt.ui.components.*
import com.puppyadopt.ui.theme.*

@Composable
fun ShopScreen(
    state: GameState,
    onHireEmployee: (EmployeeType) -> Unit,
    onDiamondHire: (EmployeeType) -> Unit,
    onDiamondExchange: () -> Unit,
    onBuyDiamondProb: () -> Unit,
    onBuyShopItem: (ShopItem) -> Unit,
    onUpgrade: (UpgradeType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ===== 钻石兑换 =====
        GameCard {
            CardTitle("💎 钻石操作")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    PinkButton(
                        text = "💎→⭐ (1→500)",
                        onClick = onDiamondExchange,
                        enabled = state.diamond >= 1 && !state.finished,
                        small = false
                    )
                    Text("消耗1钻石", fontSize = 10.sp, color = LightText, modifier = Modifier.padding(top = 4.dp))
                }
                if (!state.diamondProb) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        PinkButton(
                            text = "📈 大奖概率UP",
                            onClick = onBuyDiamondProb,
                            enabled = state.diamond >= 3 && !state.finished,
                            small = false
                        )
                        Text("消耗3钻石", fontSize = 10.sp, color = LightText, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }

        // ===== 员工招募 =====
        GameCard {
            CardTitle("👥 员工招募（声望雇佣）")

            val loversCost = EmployeeType.LOVER.baseCost * getHireMultiplier(state.lovers)
            val volunCost = EmployeeType.VOLUNTEER.baseCost * getHireMultiplier(state.volunteers)
            val keepersCost = EmployeeType.KEEPER.baseCost * getHireMultiplier(state.keepers)

            EmployeeRow(
                icon = "❤️", name = "爱护者", count = state.lovers,
                desc = "自动救助流浪狗（概率）",
                costDesc = "⭐${loversCost} ×${getHireMultiplier(state.lovers)}",
                onHire = { onHireEmployee(EmployeeType.LOVER) },
                hireEnabled = state.prestige >= loversCost && !state.finished
            )
            EmployeeRow(
                icon = "📣", name = "志愿者", count = state.volunteers,
                desc = "自动宣传获得声望",
                costDesc = "⭐${volunCost} ×${getHireMultiplier(state.volunteers)}",
                onHire = { onHireEmployee(EmployeeType.VOLUNTEER) },
                hireEnabled = state.prestige >= volunCost && !state.finished
            )
            EmployeeRow(
                icon = "🏠", name = "看守者", count = state.keepers,
                desc = "狗上限+3",
                costDesc = "⭐${keepersCost} ×${getHireMultiplier(state.keepers)}",
                onHire = { onHireEmployee(EmployeeType.KEEPER) },
                hireEnabled = state.prestige >= keepersCost && !state.finished
            )
        }

        // ===== 钻石招募（消耗1钻石随机选类型） =====
        GameCard {
            CardTitle("💎 钻石招募（1💎直接招募）")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                EmployeeType.entries.forEach { type ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GrayOutlinedButton(
                            text = "${type.icon}${type.label}",
                            onClick = { onDiamondHire(type) },
                            enabled = state.diamond >= 1 && !state.finished,
                            small = true
                        )
                    }
                }
            }
        }

        // ===== 等级升级 =====
        GameCard {
            CardTitle("⬆️ 等级升级")

            val rc = if (state.rescueLv < 6) RESCUE_COSTS[state.rescueLv] else 0L
            UpgradeRow(
                "🐾", "救助", state.rescueLv, 6, rc,
                state.prestige >= rc,
                { onUpgrade(UpgradeType.RESCUE) }
            )
            val pc = if (state.promoteLv < 6) PROMOTE_COSTS[state.promoteLv] else 0L
            UpgradeRow(
                "📢", "宣传", state.promoteLv, 6, pc,
                state.prestige >= pc,
                { onUpgrade(UpgradeType.PROMOTE) }
            )
            val pp = if (state.prestigeLv < 5) PRESTIGE_COSTS[state.prestigeLv] else 0L
            UpgradeRow(
                "⭐", "声望", state.prestigeLv, 5, pp,
                state.prestige >= pp,
                { onUpgrade(UpgradeType.PRESTIGE) }
            )
        }

        // ===== 商店道具 =====
        val canBuyLucky = !state.luckyBracelet && state.prestige >= 200
        val canBuyFood = !state.specialFood && state.prestige >= 50
        val canBuyBadge = !state.badge && state.prestige >= 300

        if (state.xiaoziActive) {
            GameCard {
                CardTitle("🛍️ 小紫商店")

                ShopItemRow("🌸", "小紫的幸运手链", "+20%大奖概率", "⭐200", canBuyLucky) {
                    onBuyShopItem(ShopItem.LUCKY_BRACELET)
                }
                ShopItemRow("🍖", "小紫的特制狗粮", "+2级效果15秒", "⭐50", canBuyFood) {
                    onBuyShopItem(ShopItem.SPECIAL_FOOD)
                }
                if (state.eSurprise) {
                    ShopItemRow("🏅", "小紫的纪念徽章", "每1000声望+50", "⭐300", canBuyBadge) {
                        onBuyShopItem(ShopItem.BADGE)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ShopItemRow(
    icon: String,
    name: String,
    desc: String,
    cost: String,
    canBuy: Boolean,
    onBuy: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "$icon $name", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = desc, fontSize = 11.sp, color = LightText, modifier = Modifier.padding(top = 2.dp))
            Text(text = cost, fontSize = 10.sp, color = DarkPink)
        }
        PinkButton(
            text = if (canBuy) "购买" else "已拥有",
            onClick = onBuy,
            enabled = canBuy,
            small = true
        )
    }
    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
}
