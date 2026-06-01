package com.puppyadopt.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class GameViewModel : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    // Toast 消息
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // 所有计时器 Job
    private var timerJob: Job? = null
    private var buffTimerJob: Job? = null

    // 当前游戏时间计数
    private var currentGameSeconds = 0L

    // ===================== 初始化 & 计时器 =====================

    fun startGame() {
        val s = _state.value.copy(
            startTime = System.currentTimeMillis(),
            dogs = 3
        )
        _state.value = s
        startTimers()
    }

    fun loadGame(savedState: GameState) {
        _state.value = savedState
        if (savedState.startTime > 0 && !savedState.finished) {
            currentGameSeconds = if (savedState.startTime > 0) {
                (System.currentTimeMillis() - savedState.startTime) / 1000
            } else 0
            startTimers()
        }
    }

    fun resetGame() {
        stopTimers()
        _state.value = GameState()
        currentGameSeconds = 0
    }

    private fun startTimers() {
        stopTimers()

        // 主计时器：每秒减少Buff/CD
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                tick()
            }
        }
    }

    private fun stopTimers() {
        timerJob?.cancel()
        buffTimerJob?.cancel()
        timerJob = null
        buffTimerJob = null
    }

    private fun tick() {
        val s = _state.value
        if (s.finished) return

        currentGameSeconds++

        var newState = s

        // 减少Buff秒数
        newState = newState.copy(
            goldSec = maxOf(0, newState.goldSec - 1),
            foodSec = maxOf(0, newState.foodSec - 1),
            warmSec = maxOf(0, newState.warmSec - 1),
            soulSec = maxOf(0, newState.soulSec - 1),
            firmHeartSec = maxOf(0, newState.firmHeartSec - 1),
            chatRedSec = maxOf(0, newState.chatRedSec - 1),
            loverSec = maxOf(0, newState.loverSec - 1),
            volunSec = maxOf(0, newState.volunSec - 1),

            // 减少CD
            chatCd = maxOf(0, newState.chatCd - 1),
            goldCd = maxOf(0, newState.goldCd - 1),
            lotteryCd = maxOf(0, newState.lotteryCd - 1)
        )

        // 员工自动操作
        if (newState.lovers > 0 && newState.dogs < newState.dogMax) {
            newState = autoRescueByEmployees(newState)
        }
        if (newState.volunteers > 0 && newState.dogs > 0) {
            newState = autoPromoteByEmployees(newState)
        }

        _state.value = newState
    }

    // ===================== 事件系统 =====================

    private fun triggerEvent(event: PuppyEvent) {
        val s = _state.value
        var ns = s.copy(eventLock = true, currentEvent = event)

        when (event) {
            PuppyEvent.MEET -> {
                ns = ns.copy(
                    prestige = ns.prestige + 50,
                    diamond = ns.diamond + 1,
                    xiaoziActive = true,
                    eMeet = true
                )
            }
            PuppyEvent.VISIT -> {
                ns = ns.copy(
                    dogs = maxOf(ns.dogs, ns.dogs + 1),
                    warmSec = 60,
                    eVisit = true
                )
            }
            PuppyEvent.CLINIC -> {
                ns = ns.copy(
                    prestige = ns.prestige + 300,
                    communityActive = true,
                    eClinic = true
                )
            }
            PuppyEvent.TALK -> {
                ns = ns.copy(
                    lotteryFree = true,
                    eTalk = true
                )
            }
            PuppyEvent.CHOICE -> {
                ns = ns.copy(
                    eChoice = true
                )
            }
            PuppyEvent.SURPRISE -> {
                val bonus = maxOf(1, ns.dogs) * 20L
                ns = ns.copy(
                    prestige = ns.prestige + bonus,
                    dogs = minOf(ns.dogMax, ns.dogs + 2),
                    eSurprise = true
                )
            }
            PuppyEvent.SOUL -> {
                ns = ns.copy(
                    prestige = ns.prestige + 2000,
                    diamond = ns.diamond + 3,
                    eSoul = true
                )
            }
            PuppyEvent.LIFE -> {
                ns = ns.copy(
                    eLife = true,
                    finished = true,
                    finishTime = currentGameSeconds
                )
            }
        }

        _state.value = ns
    }

    /** 检查是否触发剧情事件 */
    private fun checkAndTriggerEvents(state: GameState): GameState {
        if (state.finished || state.eventLock) return state
        if (state.startTime == 0L) return state

        val events = PuppyEvent.entries
        for (event in events) {
            val alreadyTriggered = when (event) {
                PuppyEvent.MEET -> state.eMeet
                PuppyEvent.VISIT -> state.eVisit
                PuppyEvent.CLINIC -> state.eClinic
                PuppyEvent.TALK -> state.eTalk
                PuppyEvent.CHOICE -> state.eChoice
                PuppyEvent.SURPRISE -> state.eSurprise
                PuppyEvent.SOUL -> state.eSoul
                PuppyEvent.LIFE -> state.eLife
            }
            if (!alreadyTriggered && state.prestige >= event.prestigeThreshold) {
                triggerEvent(event)
                return _state.value // 返回已更新的状态
            }
        }
        return state
    }

    /** 处理事件按钮点击 */
    fun onEventButtonClick(buttonIndex: Int) {
        val s = _state.value
        val event = s.currentEvent ?: return

        var ns = s.copy(eventLock = false, currentEvent = null)

        when (event) {
            PuppyEvent.CHOICE -> {
                if (buttonIndex == 0) {
                    // 支持她 - 消耗3钻石
                    if (ns.diamond >= 3) {
                        ns = ns.copy(
                            diamond = ns.diamond - 3,
                            fulltimeActive = true
                        )
                        showToast("💝 小紫全职协助已激活！")
                    }
                } else {
                    // 慎重考虑
                    ns = ns.copy(
                        prestige = ns.prestige + 500,
                        diamond = ns.diamond + 1,
                        firmHeart = true,
                        firmHeartSec = 120
                    )
                    showToast("💝 坚定的心激活120秒！")
                }
            }
            else -> {
                // 其他事件只是确认
            }
        }

        _state.value = ns
    }

    // ===================== 游戏操作 =====================

    /** 救助按钮 */
    fun doRescue() {
        val s = _state.value
        if (s.finished || s.eventLock) return

        var ns = s.copy(
            startTime = if (s.startTime == 0L) System.currentTimeMillis() else s.startTime,
            rescClk = s.rescClk + 1
        )

        val needed = getRescueClicks(ns)
        if (ns.rescClk >= needed) {
            if (ns.dogs < ns.dogMax) {
                ns = ns.copy(dogs = ns.dogs + 1)
            }
            ns = ns.copy(rescClk = 0)
        }

        ns = checkAndTriggerEvents(ns)
        _state.value = ns
    }

    /** 宣传按钮 */
    fun doPromote() {
        val s = _state.value
        if (s.finished || s.eventLock || s.dogs <= 0) return

        var ns = s.copy(
            startTime = if (s.startTime == 0L) System.currentTimeMillis() else s.startTime,
            proClk = s.proClk + 1
        )

        val needed = getPromoteClicks(ns)
        if (ns.proClk >= needed) {
            // 温暖效果有20%几率不消耗狗
            if (!(ns.warmSec > 0 && Random.nextInt(1, 101) <= 20)) {
                ns = ns.copy(dogs = ns.dogs - 1)
            }
            val gained = calcPrestige(ns)
            ns = ns.copy(
                prestige = ns.prestige + gained,
                totalPrestige = ns.totalPrestige + gained,
                proClk = 0
            )
            // 徽章奖励
            if (ns.badge) {
                val thousand = (ns.totalPrestige / 1000).toInt()
                if (thousand > ns.lastBadgeThousand) {
                    ns = ns.copy(
                        prestige = ns.prestige + 50,
                        lastBadgeThousand = thousand
                    )
                }
            }
        }

        ns = checkAndTriggerEvents(ns)
        _state.value = ns
    }

    /** 与小紫聊天 */
    fun doChat() {
        val s = _state.value
        if (s.chatCd > 0 || !s.xiaoziActive) return

        var ns = s.copy(chatCd = 60)
        val p = Random.nextInt(1, 101)

        when {
            p <= 80 -> {
                val g = Random.nextLong(5L, 16L)
                ns = ns.copy(prestige = ns.prestige + g)
                showToast("💬 聊得很开心 +${g}声望")
            }
            p <= 95 -> {
                val g = Random.nextLong(20L, 31L)
                ns = ns.copy(prestige = ns.prestige + g, chatRedSec = 30)
                showToast("💬 小紫很开心！+${g}声望，减点击30秒")
            }
            else -> {
                if (ns.dogs > 0) {
                    val gained = calcPrestige(ns)
                    ns = ns.copy(
                        prestige = ns.prestige + gained,
                        totalPrestige = ns.totalPrestige + gained
                    )
                    showToast("💬 小紫超开心！获得${gained}声望")
                } else {
                    showToast("💬 小紫笑了笑")
                }
            }
        }
        _state.value = ns
    }

    /** 雇佣员工 */
    fun hireEmployee(type: EmployeeType) {
        val s = _state.value
        val mult = getHireMultiplier(
            when (type) {
                EmployeeType.LOVER -> s.lovers
                EmployeeType.VOLUNTEER -> s.volunteers
                EmployeeType.KEEPER -> s.keepers
            }
        )
        val cost = type.baseCost * mult.toLong()
        if (s.prestige < cost) return

        var ns = s.copy(prestige = s.prestige - cost)
        when (type) {
            EmployeeType.LOVER -> {
                ns = ns.copy(lovers = ns.lovers + 1)
                showToast("❤️ 爱护者 +1")
            }
            EmployeeType.VOLUNTEER -> {
                ns = ns.copy(volunteers = ns.volunteers + 1)
                showToast("📣 志愿者 +1")
            }
            EmployeeType.KEEPER -> {
                ns = ns.copy(keepers = ns.keepers + 1, dogMax = ns.dogMax + 3)
                showToast("🏠 看守者 +1，上限+3")
            }
        }
        _state.value = ns
    }

    /** 钻石招募（消耗1钻石随机选） */
    fun diamondHire(type: EmployeeType) {
        val s = _state.value
        if (s.diamond < 1) return
        var ns = s.copy(diamond = s.diamond - 1)
        when (type) {
            EmployeeType.LOVER -> {
                ns = ns.copy(lovers = ns.lovers + 1)
                showToast("💎 爱护者招募成功！")
            }
            EmployeeType.VOLUNTEER -> {
                ns = ns.copy(volunteers = ns.volunteers + 1)
                showToast("💎 志愿者招募成功！")
            }
            EmployeeType.KEEPER -> {
                ns = ns.copy(keepers = ns.keepers + 1, dogMax = ns.dogMax + 3)
                showToast("💎 看守者招募成功！")
            }
        }
        _state.value = ns
    }

    /** 钻石兑换声望 */
    fun diamondExchange() {
        val s = _state.value
        if (s.diamond < 1) return
        _state.value = s.copy(
            diamond = s.diamond - 1,
            prestige = s.prestige + 500
        )
        showToast("💎 +500声望")
    }

    /** 购买提升大奖概率 */
    fun buyDiamondProb() {
        val s = _state.value
        if (s.diamond < 3 || s.diamondProb) return
        _state.value = s.copy(
            diamond = s.diamond - 3,
            diamondProb = true,
            lotteryWinRate = s.lotteryWinRate + 10
        )
        showToast("💎 大奖概率提升至${s.lotteryWinRate + 10}%")
    }

    /** 购买商店道具 */
    fun buyShopItem(item: ShopItem) {
        val s = _state.value
        when (item) {
            ShopItem.LUCKY_BRACELET -> {
                if (s.luckyBracelet || s.prestige < 200) return
                _state.value = s.copy(
                    prestige = s.prestige - 200,
                    luckyBracelet = true,
                    lotteryWinRate = s.lotteryWinRate + 20
                )
                showToast("🌸 幸运手链 +20%大奖概率")
            }
            ShopItem.SPECIAL_FOOD -> {
                if (s.specialFood || s.prestige < 50) return
                _state.value = s.copy(
                    prestige = s.prestige - 50,
                    specialFood = true,
                    foodSec = 15
                )
                showToast("🍖 特制狗粮15秒！")
            }
            ShopItem.BADGE -> {
                if (s.badge || s.prestige < 300) return
                _state.value = s.copy(
                    prestige = s.prestige - 300,
                    badge = true,
                    lastBadgeThousand = (s.totalPrestige / 1000).toInt()
                )
                showToast("🏅 纪念徽章已购买！")
            }
        }
    }

    /** 升级 */
    fun upgrade(type: UpgradeType) {
        val s = _state.value
        var ns = s
        when (type) {
            UpgradeType.RESCUE -> {
                if (ns.rescueLv >= 6) return
                val cost = RESCUE_COSTS[ns.rescueLv]
                if (ns.prestige < cost) return
                ns = ns.copy(prestige = ns.prestige - cost, rescueLv = ns.rescueLv + 1)
                showToast("🐾 救助 Lv.${ns.rescueLv}")
            }
            UpgradeType.PROMOTE -> {
                if (ns.promoteLv >= 6) return
                val cost = PROMOTE_COSTS[ns.promoteLv]
                if (ns.prestige < cost) return
                ns = ns.copy(prestige = ns.prestige - cost, promoteLv = ns.promoteLv + 1)
                showToast("📢 宣传 Lv.${ns.promoteLv}")
            }
            UpgradeType.PRESTIGE -> {
                if (ns.prestigeLv >= 5) return
                val cost = PRESTIGE_COSTS[ns.prestigeLv]
                if (ns.prestige < cost) return
                ns = ns.copy(prestige = ns.prestige - cost, prestigeLv = ns.prestigeLv + 1)
                showToast("⭐ 声望 Lv.${ns.prestigeLv}")
            }
        }
        _state.value = ns
    }

    /** 使用黄金体验 */
    fun useGoldenExperience() {
        val s = _state.value
        if (s.prestige < 300 || s.goldCd > 0) return
        _state.value = s.copy(
            prestige = s.prestige - 300,
            goldCd = 60,
            goldSec = 5
        )
        showToast("✨ 黄金体验5秒！")
    }

    /** 使用彩票 */
    fun useLottery() {
        val s = _state.value
        var free = false
        if (s.lotteryFree && Random.nextInt(1, 101) <= 30) free = true
        if (!free) {
            if (s.prestige < 100 || s.lotteryCd > 0) return
        }
        val won = Random.nextInt(1, 101) <= s.lotteryWinRate
        val gained = if (won) Random.nextLong(100L, 1001L) else Random.nextLong(10L, 101L)

        val ns = if (free) s else s.copy(prestige = s.prestige - 100, lotteryCd = 60)
        _state.value = ns.copy(
            prestige = ns.prestige + gained,
            totalPrestige = ns.totalPrestige + gained
        )
        showToast(if (won) "🎰 中大奖！+${gained}声望" else "🎰 中了${gained}声望")
    }

    /** 领取小紫的日记 */
    fun claimDiary() {
        val s = _state.value
        if (s.diaryFreeUsed) return
        _state.value = s.copy(
            prestige = s.prestige + 500,
            diaryFreeUsed = true
        )
        showToast("📕 +500声望")
    }

    /** 小紫的祝福 */
    fun claimBlessing() {
        val s = _state.value
        if (s.soulSec > 0) return
        _state.value = s.copy(soulSec = 30)
        showToast("💫 祝福30秒！")
    }

    /** 消除 Toast */
    fun clearToast() {
        _toastMessage.value = null
    }

    private fun showToast(msg: String) {
        _toastMessage.value = msg
        viewModelScope.launch {
            delay(2000L)
            _toastMessage.value = null
        }
    }

    // ===================== 辅助计算函数 =====================

    private fun getRescueClicks(s: GameState): Int {
        val effectiveLv = if (s.foodSec > 0) minOf(6, s.rescueLv + 2) else s.rescueLv
        val base = BASE_CLICKS[effectiveLv.coerceIn(0, 6)]
        return if (s.chatRedSec > 0) maxOf(1, base - 1) else base
    }

    private fun getPromoteClicks(s: GameState): Int {
        val effectiveLv = if (s.foodSec > 0) minOf(6, s.promoteLv + 2) else s.promoteLv
        val base = BASE_CLICKS[effectiveLv.coerceIn(0, 6)]
        return if (s.chatRedSec > 0) maxOf(1, base - 1) else base
    }

    private fun calcPrestige(s: GameState): Long {
        val effectiveLv = if (s.goldSec > 0) 6 else s.prestigeLv
        val base = when (effectiveLv) {
            1 -> Random.nextLong(10L, 21L)
            2 -> Random.nextLong(20L, 31L)
            3 -> Random.nextLong(30L, 51L)
            4 -> Random.nextLong(50L, 81L)
            else -> Random.nextLong(80L, 141L)
        }
        val bonus = if (s.communityActive) 5L else 0L
        var total = base + bonus
        if (s.soulSec > 0) total *= 2
        return total
    }

    private fun autoRescueByEmployees(s: GameState): GameState {
        // 每个爱护者每10秒自动救助一次（在tick中每1秒处理时用概率模拟）
        val chance = s.lovers * 10
        if (Random.nextInt(1, 101) <= chance) {
            return s.copy(dogs = minOf(s.dogMax, s.dogs + 1))
        }
        return s
    }

    private fun autoPromoteByEmployees(s: GameState): GameState {
        // 每个志愿者每15秒自动宣传一次
        val chance = (s.volunteers * 100) / 15
        if (Random.nextInt(1, 101) <= chance && s.dogs > 0) {
            val gained = calcPrestige(s)
            return s.copy(
                dogs = s.dogs - 1,
                prestige = s.prestige + gained,
                totalPrestige = s.totalPrestige + gained
            )
        }
        return s
    }

    fun getGameTimeSeconds(): Long = currentGameSeconds

    override fun onCleared() {
        super.onCleared()
        stopTimers()
    }
}
