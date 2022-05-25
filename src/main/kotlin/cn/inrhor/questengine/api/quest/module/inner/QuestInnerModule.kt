package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import java.util.*

class QuestInnerModule(var id: String, var name: String,
                       var nextInnerQuestID: String,
                       var description: List<String>,
                       var reward: QuestReward,
                       var time: TimeFrame,
                       var control: MutableList<QuestControl>,
                       var target: MutableList<QuestTarget>) {
    constructor(): this("innerIDNull", "null inner name", "",listOf(), QuestReward(),
        TimeFrame(), mutableListOf(), mutableListOf())

    fun existTargetID(id: String): Boolean {
        target.forEach { if (it.id == id) return true }
        return false
    }

    fun delTarget(id: String) {
        val i = target.iterator()
        while (i.hasNext()) {
            val n = i.next()
            if (n.id== id) {
                i.remove()
                break
            }
        }
    }

    fun existControlID(id: String): Boolean {
        control.forEach { if (it.id == id) return true }

        return false
    }

    private fun acceptInner(player: Player, innerData: QuestInnerData) {
        if (innerData.state == QuestState.FAILURE) {
            QuestManager.acceptInnerQuest(player, innerData.questID, id, false)
        }
    }

    private fun timeout(player: Player, innerModule: QuestInnerModule, innerData: QuestInnerData) {
        if (innerData.state != QuestState.FAILURE) {
            innerData.state = QuestState.FAILURE
            runEval(player, innerModule.reward.fail)
        }
    }

    /**
     * 定时任务
     */
    fun timeAccept(player: Player, innerModule: QuestInnerModule, innerData: QuestInnerData) {
        if (time.type != TimeFrame.Type.ALWAYS) {
            innerData.updateTime(innerModule.time)
            submit(period = 20L, async = true) {
                if (innerData.end == null || !player.isOnline || time.type == TimeFrame.Type.ALWAYS) {
                    cancel();return@submit
                }else {
                    val now = Date()
                    if (!time.noTimeout(now, innerData.timeDate, innerData.end!!)) {
                        innerData.updateTime(innerModule.time)
                        if (time.noTimeout(now, innerData.timeDate, innerData.end!!)) {
                            acceptInner(player, innerData)
                            cancel()
                            return@submit
                        }else {
                            timeout(player, innerModule, innerData)
                        }
                    }
                }
            }
        }
    }

}