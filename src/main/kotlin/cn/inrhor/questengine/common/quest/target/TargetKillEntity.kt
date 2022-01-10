package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.script.kether.evalBoolean
import cn.inrhor.questengine.utlis.subAfter
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent

object TargetKillEntity: TargetExtend<EntityDeathEvent>() {

    override val name = "player kill entity"


    init {
        event = EntityDeathEvent::class
        tasker{
            val player = entity.killer?: return@tasker null
            val questData = QuestManager.getDoingQuest(player)?: return@tasker player
            if (!QuestManager.matchQuestMode(questData)) return@tasker player
            val innerData = questData.questInnerData
            val targetData = QuestManager.getDoingTarget(player, name)?: return@tasker player
            val innerTarget = targetData.questTarget
            val typeEntity = object: ConditionType("entity") {
                override fun check(): Boolean {
                    return checkEntity(innerTarget, entityType)
                }
            }
            val condition = object: ConditionType("condition") {
                override fun check(): Boolean {
                    return checkCondition(player, innerTarget, entity, droppedExp)
                }
            }
            val number = object: ConditionType("number") {
                override fun check(): Boolean {
                    return Schedule.isNumber(player, name, "number", questData, innerData, innerTarget)
                }
            }
            TargetManager.set(name, "entity", typeEntity)
            TargetManager.set(name, "condition", condition)
            TargetManager.set(name, "number", number)
            player
        }
        TargetManager.register(name, "entity", "entity")
        TargetManager.register(name, "check", "check")
        TargetManager.register(name, "condition", mutableListOf("condition"))
        TargetManager.register(name, "number", "number")
    }

    fun checkEntity(target: QuestTarget, type: EntityType): Boolean {
        val condition = target.condition["entity"]?: return false
        return when (condition.uppercase()) {
            "PLAYER" -> type == EntityType.PLAYER
            else -> type != EntityType.PLAYER
        }
    }

    fun checkCondition(player: Player, target: QuestTarget, entity: Entity, dropExp: Int): Boolean {
        val condition = target.conditionList["condition"]?: return false
        val check = target.condition["check"]?: return false
        val checkNumber = check.toInt()
        var i = 0
        condition.forEach {
            if (!checkNumber(checkNumber, i)) return true
            val s = it.lowercase()
            if (s.startsWith("entity name")) {
                val get = it.subAfter("@")
                if (!matchName(player, entity, get)) return false
            }else if (s.startsWith("eval ")) {
                val get = it.subAfter("@").replace("dropExp", dropExp.toString(), true)
                if (!evalBoolean(player, get)) return false
            }
            i++
        }
        return true
    }

    fun matchName(player: Player, entity: Entity, str: String): Boolean {
        if (evalBoolean(player, "strMatch type $str *'${entity.name}'")) return true
        if (evalBoolean(player, "strMatch type $str *'${entity.customName}'")) return true
        return false
    }

    fun checkNumber(check: Int, forEach: Int): Boolean {
        if (check <= 0 || check >= forEach) return true
        return false
    }

}