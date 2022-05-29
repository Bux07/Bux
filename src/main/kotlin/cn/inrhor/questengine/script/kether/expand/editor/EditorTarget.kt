package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.api.target.RegisterTarget
import cn.inrhor.questengine.common.editor.EditorList.editorNodeList
import cn.inrhor.questengine.common.editor.EditorList.editorTargetCondition
import cn.inrhor.questengine.common.editor.EditorList.editorTargetList
import cn.inrhor.questengine.common.editor.EditorList.selectReward
import cn.inrhor.questengine.common.editor.EditorList.selectTargetList
import cn.inrhor.questengine.common.editor.EditorTarget.editorTarget
import cn.inrhor.questengine.common.editor.EditorTarget.editorTargetNode
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.script.kether.*
import cn.inrhor.questengine.utlis.UtilString
import cn.inrhor.questengine.utlis.removeAt
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText
import taboolib.platform.util.sendLang
import java.util.concurrent.CompletableFuture

class EditorTarget(val ui: ActionEditor.TargetUi, vararg val variable: String, val page: Int = 0) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = frame.player()
        val questID = frame.selectQuestID()
        val innerID = frame.selectInnerID()
        when (ui) {
            ActionEditor.TargetUi.CHANGE -> {
                val targetID = frame.selectTargetID()
                when (variable[0]) {
                    "node" -> {
                        val target = QuestManager.getTargetModule(questID, innerID, targetID)?: return frameVoid()
                        val change = variable[1]
                        val meta = target.nodeMeta(change)?: mutableListOf()
                        meta.removeAt(variable[2].toInt())
                        target.reloadNode(change, meta)
                        QuestManager.saveFile(questID, innerID)
                        sender.editorNodeList(questID, innerID, target, change)
                    }
                    "name" -> {
                        val target = QuestManager.getTargetModule(questID, innerID, targetID)?: return frameVoid()
                        target.name = variable[1]
                        target.node = ""
                        QuestManager.saveFile(questID, innerID)
                        sender.editorTarget(questID, innerID, targetID)
                    }
                    "condition" -> {
                        val target = QuestManager.getTargetModule(questID, innerID, targetID)?: return frameVoid()
                        target.condition = target.condition.removeAt(variable[1].toInt())
                        QuestManager.saveFile(questID, innerID)
                        sender.editorTarget(questID, innerID, targetID)
                    }
                }
            }
            ActionEditor.TargetUi.LIST -> {
                sender.editorTargetList(questID, innerID, page)
            }
            ActionEditor.TargetUi.EDIT -> {
                val targetID = frame.selectTargetID()
                when (variable[0]) {
                    "name" -> {
                        sender.selectTargetList(questID, innerID, targetID)
                    }
                    "reward_boolean" -> {
                        val target = QuestManager.getTargetModule(questID, innerID, targetID)?: return frameVoid()
                        val r = target.reward
                        if (r.isEmpty()) return frameVoid()
                        val sp = r.split(" ")
                        val b = sp[1].toBoolean()
                        target.reward = sp[0]+(!b)
                        sender.editorTarget(questID, innerID, targetID)
                        QuestManager.saveFile(questID, innerID)
                    }
                    "async" -> {
                        val target = QuestManager.getTargetModule(questID, innerID, targetID)?: return frameVoid()
                        val a = target.async
                        target.async = !a
                        sender.editorTarget(questID, innerID, targetID)
                        QuestManager.saveFile(questID, innerID)
                    }
                    "condition" -> {
                        sender.editorTargetCondition(questID, innerID, targetID, page)
                    }
                    "node" -> {
                        val target = QuestManager.getTargetModule(questID, innerID, targetID)?: return frameVoid()
                        val node = RegisterTarget.getNode(target.name, variable[1])?: return frameVoid()
                        sender.editorTargetNode(questID, innerID, target, node)
                    }
                    else -> {
                        sender.editorTarget(questID,innerID,targetID)
                    }
                }
            }
            ActionEditor.TargetUi.DEL -> {
                val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return frameVoid()
                inner.delTarget(frame.selectTargetID())
                QuestManager.saveFile(questID, innerID)
                sender.editorTargetList(questID, innerID)
            }
            ActionEditor.TargetUi.ADD -> {
                sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-TARGET-ID"))) {
                    val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return@inputSign
                    val id = it[1]
                    if (inner.existTargetID(id)) {
                        sender.sendLang("EXIST-TARGET-ID", UtilString.pluginTag, id)
                        return@inputSign
                    }
                    val target = QuestTarget()
                    target.id = id
                    inner.target.add(target)
                    QuestManager.saveFile(questID, innerID)
                    sender.selectTargetList(questID, innerID, id)
                }
            }
            ActionEditor.TargetUi.SEL -> {
                val targetID = frame.selectTargetID()
                when (variable[0]) {
                    "node" -> {
                        val target = QuestManager.getTargetModule(questID, innerID, targetID)?: return frameVoid()
                        val node = RegisterTarget.getNode(target.name, variable[1])?: return frameVoid()
                        sender.editorTargetNode(questID, innerID, target, node)
                    }
                    "list" -> {
                        sender.selectTargetList(questID, innerID, targetID, page)
                    }
                    "reward" -> {
                        sender.selectReward(questID, innerID, targetID, page)
                    }
                }
            }
        }
        return frameVoid()
    }
}