package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.event.QuestEvent
import cn.inrhor.questengine.api.event.TargetEvent
import cn.inrhor.questengine.api.quest.ControlFrame
import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.database.data.questData
import cn.inrhor.questengine.common.database.data.teamData
import cn.inrhor.questengine.common.quest.enum.ModeType
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.failQuest
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager.updateSortQuest
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.entity.Player
import taboolib.common.io.deepDelete
import taboolib.common.io.newFile
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.setObject

object QuestManager {

    /**
     * 注册的任务模块内容
     */
    private val questMap = mutableMapOf<String, QuestFrame>()

    val extendsQuest = mutableListOf<QuestFrame>()

    /**
     * 自动接受的任务模块内容
     */
    var autoQuestMap = mutableMapOf<String, QuestFrame>()

    fun getQuestMap() = questMap

    fun clearQuestMap() = questMap.clear()

    fun QuestFrame.waitRegister() {
        extendsQuest.add(this)
    }

    /**
     * 注册任务模块内容
     */
    fun QuestFrame.register() {
        questMap[id] = this
        target.forEach {
            it.loadNode()
        }
        if (accept.auto) {
            autoQuestMap[id] = this
        }
        updateSortQuest(group.sort)
        time.updateTime()
    }

    /**
     * 保存任务配置
     */
    fun QuestFrame.saveFile(create: Boolean = false) {
        val file = newFile(path, create)
        val yaml = Configuration.loadFromFile(file)
        yaml.setObject("quest", this)
        yaml.saveToFile(file)
    }

    /**
     * 保存任务配置
     */
    fun String.saveQuestFile() {
        getQuestFrame().saveFile()
    }

    /**
     * 删除任务配置
     */
    fun QuestFrame.delFile() {
        newFile(path).deepDelete()
        questMap.remove(id)
    }

    /**
     * 删除任务配置
     */
    fun String.delQuestFile() {
        getQuestFrame().delFile()
    }

    /**
     * @return 任务模块
     */
    fun String.getQuestFrame(): QuestFrame {
        return questMap[this]?: error("null quest frame: $this")
    }

    /**
     * @return 是否存在任务模块
     */
    fun String.existQuestFrame() = questMap.containsKey(this)

    /**
     * @return 玩家是否满足任务组模式
     * @param share 是否判断共享数据
     */
    fun QuestFrame.matchMode(player: Player, share: Boolean = true): Boolean {
        val mode = this.mode
        if (mode.type == ModeType.PERSONAL) return true
        val amount = mode.amount
        if (amount <= 1) return true
        val tData = player.teamData()?: return false
        if (amount >= TeamManager.getMemberAmount(tData)) {
            if (share) {
                if (mode.shareData) return true
            }else return true
        }
        return false
    }

    /**
     * @return 玩家是否满足任务组模式
     * @param share 是否判断共享数据
     */
    fun String.matchMode(player: Player, share: Boolean = true): Boolean {
        return getQuestFrame().matchMode(player, share)
    }

    /**
     * @return 任务组队伍模式
     */
    fun String.getQuestMode(): ModeType {
        return getQuestFrame().mode.type
    }

    /**
     * @return 控制模块
     */
    fun String.getControlFrame(questID: String): ControlFrame {
        questID.getQuestFrame().control.forEach { if (it.id == this) return it }
        error("null control frame: $this($questID)")
    }

    /**
     * 接受任务
     */
    fun Player.acceptQuest(quest: QuestFrame) {
        if (runEval(this, quest.accept.condition)) {
            getPlayerData().dataContainer.installQuest(quest)
            QuestEvent.Accept(this, quest).call()
            questData(quest.id).updateTime(this)
        }
    }

    /**
     * 接受任务
     */
    fun Player.acceptQuest(questID: String) {
        acceptQuest(questID.getQuestFrame())
    }

    /**
     * 放弃任务
     */
    fun Player.quitQuest(questID: String) {
        getPlayerData().dataContainer.unloadQuest(questID)
        QuestEvent.Quit(this, questID.getQuestFrame()).call()
    }

    /**
     * 完成任务
     */
    fun Player.finishQuest(questID: String) {
        getPlayerData().dataContainer.toggleQuest(questID, StateType.FINISH).finishTime(questID)
        QuestEvent.Finish(this, questID.getQuestFrame()).call()
    }

    /**
     * 重置任务
     */
    fun Player.resetQuest(questID: String) {
        val quest = questID.getQuestFrame()
        getPlayerData().dataContainer.installQuest(quest)
        QuestEvent.Reset(this, quest).call()
        questData(quest.id).updateTime(this)
    }

    /**
     * 任务失败
     */
    fun Player.failQuest(questID: String) {
        getPlayerData().dataContainer.toggleQuest(questID, StateType.FAILURE)
        QuestEvent.Fail(this, questID.getQuestFrame()).call()
    }

    /**
     * 追踪任务
     */
    fun Player.trackQuest(questID: String) {
        QuestEvent.Track(this, questID.getQuestFrame()).call()
    }

    /**
     * 完成目标
     */
    fun Player.finishTarget(targetData: TargetData, modeType: ModeType) {
        TargetEvent.Finish(this, targetData, modeType).call()
    }

    /**
     * @return 目标模块
     */
    fun String.getTargetFrame(questID: String): TargetFrame {
        questID.getQuestFrame().target.forEach {
            if (it.id == this) return it
        }
        error("null target frame: $this($questID)")
    }

}