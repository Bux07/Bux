package cn.inrhor.questengine.common.edit

import cn.inrhor.questengine.common.edit.list.EditorInnerList
import cn.inrhor.questengine.common.edit.list.EditorListModule
import cn.inrhor.questengine.common.edit.list.EditorOfList
import cn.inrhor.questengine.common.edit.list.EditorQuestList
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.platform.util.asLangText

object EditorList {
    /**
     * 可视化 - 任务列表
     */
    fun Player.editorListQuest(page: Int = 0) {
        val list = QuestManager.questMap.values.toMutableList()
        EditorQuestList(this, asLangText("EDITOR-LIST-QUEST"))
            .list(page, 7, list, true, "EDITOR-LIST-QUEST-INFO", "qen editor quest list",
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-EDIT"),
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-EDIT-META",
                    "EDITOR-LIST-QUEST-EDIT-HOVER", "/qen editor quest edit"),
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-DEL-META",
                    "EDITOR-LIST-QUEST-DEL-HOVER", "/qen editor quest del"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorListInner(questID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        EditorInnerList(this, questModule, asLangText("EDITOR-LIST-INNER", questID))
            .list(page, 7, questModule.innerQuestList, true, "EDITOR-LIST-INNER-INFO", "qen editor quest list",
                EditorListModule.EditorButton("EDITOR-LIST-INNER-EDIT"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-EDIT-META",
                    "EDITOR-LIST-INNER-EDIT-HOVER", "/qen editor inner edit $questID"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-DEL-META",
                    "EDITOR-LIST-INNER-DEL-HOVER", "/qen editor inner del $questID"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorStartInner(questID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        EditorInnerList(this, questModule, asLangText("EDITOR-EDIT-QUEST-INNER-START", questID))
            .list(page, 7, questModule.innerQuestList, true, "EDITOR-EDIT-INNER-LIST",
                "qen editor quest edit innerlist",
                EditorListModule.EditorButton("EDITOR-EDIT-QUEST-START-STATE"),
                EditorListModule.EditorButton("EDITOR-EDIT-QUEST-START-STATE-META",
                    "EDITOR-EDIT-QUEST-START-STATE-HOVER", "/qen editor quest change start $questID"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorAcceptCondition(questID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        listEditDel(this, questID, questModule.accept.condition,
            "ACCEPT", "CONDITION", "acceptcondition", page)
    }

    fun Player.editorFailCondition(questID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        listEditDel(this, questID, questModule.failure.condition,
            "FAIL", "CONDITION","failcondition", page)
    }

    fun Player.editorFailScript(questID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        listEditDel(this, questID, questModule.failure.script,
            "FAIL", "SCRIPT","failscript", page)
    }

    fun listEditDel(player: Player, questID: String, list: List<String>, node: String, meta: String, cmd: String, page: Int = 0) {
        EditorOfList(player, player.asLangText("EDITOR-$node-$meta-LIST", questID))
            .list(page, 3, list, true, "EDITOR-$meta-LIST",
                "qen editor quest edit $cmd $questID",
                EditorListModule.EditorButton("EDITOR-$meta-RETURN"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-META", "EDITOR-LIST-HOVER",
                    "/qen editor quest change $cmd $questID [0]"))
            .json.sendTo(adaptPlayer(player))
    }

    fun Player.editorNextInner(questID: String, innerID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        EditorInnerList(this, questModule, asLangText("EDITOR-EDIT-INNER-NEXT", questID, innerID))
            .list(page, 7, questModule.innerQuestList, true, "EDITOR-EDIT-INNER-LIST",
                "qen editor inner edit nextinner $questID",
                EditorListModule.EditorButton("EDITOR-EDIT-INNER-NEXT-CHOOSE"),
                EditorListModule.EditorButton("EDITOR-EDIT-INNER-NEXT-META",
                    "EDITOR-EDIT-INNER-NEXT-HOVER", "/qen editor inner change nextinner $questID"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorInnerDesc(questID: String, innerID: String, page: Int = 0) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        EditorOfList(this, asLangText("EDITOR-EDIT-INNER-NOTE", questID, innerID), empty = "  ")
            .add(asLangText("EDITOR-LIST-INNER-DESC-ADD"),
                EditorListModule.EditorButton(asLangText("EDITOR-LIST-INNER-DESC-ADD-META"),
                    asLangText("EDITOR-LIST-INNER-DESC-ADD-HOVER"), "/qen editor inner change desc add $questID $innerID [0]"))
            .list(page, 5, inner.description, true, "EDITOR-LIST-INNER-NOTE-LIST", "qen editor inner edit desc $questID $innerID",
                EditorListModule.EditorButton("EDITOR-LIST-INNER-NOTE-ADD"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-NOTE-ADD-META",
                    "EDITOR-LIST-INNER-NOTE-ADD-HOVER", "/qen editor inner change desc add $questID $innerID [0]"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-NOTE-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-NOTE-DEL-META",
                    "EDITOR-LIST-INNER-NOTE-DEL-HOVER", "/qen editor inner change desc del $questID $innerID [0]"))
            .json.sendTo(adaptPlayer(this))
    }
}