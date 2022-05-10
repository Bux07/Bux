package cn.inrhor.questengine.common.edit

import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

object EditorInner {

    val editInnerMeta = listOf(
        "NAME", "NEXTINNER", "DESC")

    fun Player.editorInner(questID: String, innerID: String) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        val json = TellrawJson()
            .newLine()
            .append("   "+asLangText("EDITOR-EDIT-INNER", questID, innerID))
            .newLine().newLine()
        editInnerMeta.forEach {
            json.append("      "+asLangText("EDITOR-EDIT-INNER-$it",
                inner.name, inner.nextInnerQuestID))
                .append("  "+asLangText("EDITOR-EDIT-INNER-META"))
                .hoverText(asLangText("EDITOR-EDIT-INNER-META-HOVER"))
                .runCommand("/qen eval editor inner in edit "+it.lowercase()+" page 0 select $questID $innerID")
                .newLine()
        }
        listOf("TARGET", "REWARD", "FAIL").forEach {
            json.append("      "+asLangText("EDITOR-EDIT-INNER-$it",
                inner.name, inner.nextInnerQuestID))
                .append("  "+asLangText("EDITOR-EDIT-INNER-META"))
                .hoverText(asLangText("EDITOR-EDIT-INNER-META-HOVER"))
                .runCommand("/qen eval editor inner in "+it.lowercase()+" page 0 select $questID $innerID")
                .newLine()
        }
        json.newLine().sendTo(adaptPlayer(this))
    }

}