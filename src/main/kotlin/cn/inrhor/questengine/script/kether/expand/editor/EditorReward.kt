package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.common.editor.EditorList.editorFinishReward
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.script.kether.*
import cn.inrhor.questengine.utlis.newLineList
import taboolib.common.util.addSafely
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText
import java.util.concurrent.CompletableFuture

class EditorReward(val ui: ActionEditor.RewardUi, vararg var variable: String, val page: Int = 0) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = frame.player()
        val questID = frame.selectQuestID()
        val innerID = frame.selectInnerID()
        when (ui) {
            ActionEditor.RewardUi.LIST -> {
                sender.editorFinishReward(questID, innerID, page)
            }
            ActionEditor.RewardUi.DEL -> {
                val inner = QuestManager.getInnerModule(questID, innerID)?: return frameVoid()
                inner.finish = inner.finish.newLineList().removeAt(variable[0].toInt())
                QuestManager.saveFile(questID, innerID)
                sender.editorFinishReward(questID, innerID)
            }
            ActionEditor.RewardUi.ADD -> {
                sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-EVAL"))) {
                    val inner = QuestManager.getInnerModule(questID, innerID)?: return@inputSign
                    val list = inner.finish.newLineList()
                    val index = if (variable[0]=="{head}") 0 else variable[0].toInt()+1
                    list.addSafely(index, it[1], "")
                    inner.finish = list.joinToString("\n")
                    QuestManager.saveFile(questID, innerID)
                    sender.editorFinishReward(questID, innerID)
                }
            }
        }
        return frameVoid()
    }
}