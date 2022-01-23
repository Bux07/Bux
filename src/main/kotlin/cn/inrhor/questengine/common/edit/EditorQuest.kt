package cn.inrhor.questengine.common.edit

import cn.inrhor.questengine.api.quest.module.main.QuestModule
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

object EditorQuest {

    val editQuestMeta = listOf(
        "NAME", "START", "SORT", "MODETYPE", "MODEAMOUNT", "SHAREDATA")

    fun Player.editorQuest(questID: String) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        val json = TellrawJson()
            .newLine()
            .append("   "+asLangText("EDITOR-EDIT-QUEST", questID))
            .newLine().newLine()
        editQuestMeta.forEach {
            json.append("      "+asLangText("EDITOR-EDIT-QUEST-$it",
                questModule.name, questModule.startInnerQuestID, questModule.sort,
                questModule.mode.modeTypeLang(this), questModule.mode.amount, questModule.mode.shareData))
                .append("  "+asLangText("EDITOR-EDIT-QUEST-META"))
                .hoverText(asLangText("EDITOR-EDIT-QUEST-META-HOVER"))
                .runCommand("/qen editor quest edit "+it.lowercase()+" $questID")
                .newLine()
        }
        json.newLine().sendTo(adaptPlayer(this))
    }

}