package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.common.quest.target.TPlayerChat.targetTrigger
import org.bukkit.event.player.PlayerCommandSendEvent

object TPlayerCommand: TargetExtend<PlayerCommandSendEvent>() {

    override val name = "player send command"

    override val isAsync = true

    init {
        event = PlayerCommandSendEvent::class
        tasker{
            val questData = QuestManager.getDoingQuest(player, true)?: return@tasker player
            val content = object : ConditionType("content") {
                override fun check(): Boolean {
                    return targetTrigger(player, name, "content", content, questData)
                }
            }
            val number = object: ConditionType("number") {
                override fun check(): Boolean {
                    return Schedule.isNumber(player, name, "number", questData)
                }
            }
            TargetManager.set(name, "content", content).set(name, "number", number)
            player
        }
        TargetManager.register(name, "content").register(name, "number")
    }

}