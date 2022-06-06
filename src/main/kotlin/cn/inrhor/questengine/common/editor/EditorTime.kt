package cn.inrhor.questengine.common.editor

import cn.inrhor.questengine.api.quest.module.TimeFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

object EditorTime {

    fun Player.editTime(questID: String, innerID: String) {
        val inner = QuestManager.getInnerModule(questID, innerID)?: return
        val time = inner.time
        val json = TellrawJson()
            .newLine()
            .append("   "+asLangText("EDITOR-EDIT-TIME", questID, innerID))
            .newLine()
            .append("      "+asLangText("EDITOR-BACK"))
            .append("  "+asLangText("EDITOR-BACK-META"))
            .hoverText(asLangText("EDITOR-BACK-HOVER"))
            .runCommand("/qen eval quest select $questID inner select $innerID editor inner in edit home")
            .newLine()
            .newLine()
            .append("      "+asLangText("EDITOR-EDIT-TIME-TYPE",
                asLangText("EDITOR-EDIT-TIME-TYPE-${time.type}")))
            .append("  "+asLangText("EDITOR-EDIT-TIME-META"))
            .hoverText(asLangText("EDITOR-EDIT-TIME-META-HOVER"))
            .runCommand("/qen eval quest select $questID inner select $innerID editor time in edit type").newLine()
            .append("      "+asLangText("EDITOR-EDIT-TIME-DUR", time.langTime(this)))
            if (time.type != TimeFrame.Type.ALWAYS) {
                json.append("  "+asLangText("EDITOR-EDIT-TIME-META"))
                    .hoverText(asLangText("EDITOR-EDIT-TIME-META-HOVER"))
                    .runCommand("/qen eval quest select $questID inner select $innerID editor time in edit ${time.type}")
            }
            json.newLine().sendTo(adaptPlayer(this))
    }

    fun Player.selectTimeType(questID: String, innerID: String) {
        val inner = QuestManager.getInnerModule(questID, innerID)?: return
        val json = TellrawJson()
            .newLine()
            .append("   "+asLangText("EDITOR-EDIT-TIME-SELECT", questID, innerID))
            .newLine()
            .append("      "+asLangText("EDITOR-BACK"))
            .append("  "+asLangText("EDITOR-BACK-META"))
            .hoverText(asLangText("EDITOR-BACK-HOVER"))
            .runCommand("/qen eval quest select $questID inner select $innerID  editor inner in edit time")
            .newLine()
            .newLine()
        listOf("ALWAYS", "DAY", "WEEKLY", "MONTHLY", "YEARLY", "CUSTOM").forEach {
            json.append("      "+asLangText("EDITOR-EDIT-TIME-TYPE-$it"))
            if (inner.time.type != TimeFrame.Type.valueOf(it)) {
                json.append("  "+asLangText("EDITOR-EDIT-TIME-SELECT_1"))
                    .hoverText(asLangText("EDITOR-EDIT-TIME-SELECT-HOVER"))
                    .runCommand("/qen eval quest select $questID inner select $innerID  editor time in change type to ${it.lowercase()}")
                    .newLine()
            }else {
                json.append("  "+asLangText("EDITOR-EDIT-TIME-SELECT_2")).newLine()
            }
        }
        json.newLine().sendTo(adaptPlayer(this))
    }

}