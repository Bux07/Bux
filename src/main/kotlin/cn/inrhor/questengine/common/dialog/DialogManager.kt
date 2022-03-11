package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.dialog.theme.chat.DialogChat
import cn.inrhor.questengine.common.dialog.theme.hologram.core.DialogHologram
import cn.inrhor.questengine.script.kether.runEval
import cn.inrhor.questengine.script.kether.runEvalSet

import cn.inrhor.questengine.utlis.UtilString
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.module.lang.sendLang


object DialogManager {
    /**
     * 成功注册的对话模块
     */
    private val dialogMap = mutableMapOf<String, DialogModule>()

    /**
     * 注册对话
     */
    fun register(dialogID: String, dialogModule: DialogModule) {
        if (exist(dialogID)) {
            console().sendLang("DIALOG-EXIST_DIALOG_ID", UtilString.pluginTag, dialogID)
            return
        }
        dialogMap[dialogID] = dialogModule
    }

    /**
     * 删除对话
     */
    fun remove(dialogID: String) {dialogMap.remove(dialogID)}

    /**
     * 对话ID 是否存在
     */
    fun exist(dialogID: String) = dialogMap.contains(dialogID)

    /**
     * 获取对话对象
     */
    fun get(dialogID: String) = dialogMap[dialogID]

    fun getMap() = dialogMap

    /**
     * 清空对话对象Map
     */
    fun clearMap() = dialogMap.clear()

    /**
     * 获取玩家是否进行相同对话ID的对话
     */
    fun hasDialog(players: MutableSet<Player>, dialogID: String): Boolean {
        players.forEach {
            if (hasDialog(it, dialogID)) return true
        }
        return false
    }

    fun hasDialog(player: Player, dialogID: String): Boolean {
        DataStorage.getPlayerData(player).dialogData.dialogMap[dialogID]?: return false
        return true
    }

    /**
     * 根据NPCID并判断玩家集合是否满足条件，返回dialogModule
     */
    fun returnNpcDialog(players: MutableSet<Player>, npcID: String): DialogModule? {
        dialogMap.values.forEach {
            if (!it.npcIDs.contains(npcID)) return@forEach
            if (runEvalSet(players, it.condition)) {
                val dialogID = it.dialogID
                if (!hasDialog(players, dialogID)) return it
            }
        }
        return null
    }

    fun sendDialog(players: MutableSet<Player>, npcLoc: Location, npcID: String) {
        val dialogModule = returnNpcDialog(players, npcID) ?: return
        if (dialogModule.type == "holo") {
            sendDialogHolo(players, dialogModule, npcLoc)
        }else sendDialogChat(players, dialogModule)
    }

    fun sendDialog(player: Player, dialogID: String, loc: Location = player.location) {
        if (hasDialog(player, dialogID)) return
        val dialogModule = get(dialogID)?: return
        if (dialogModule.type == "holo") {
            sendDialogHolo(player, dialogModule, loc)
        }else sendDialogChat(mutableSetOf(player), dialogModule)
    }

    private fun sendDialogChat(players: MutableSet<Player>, dialogModule: DialogModule) {
        DialogChat(dialogModule, players).play()
    }

    private fun sendDialogHolo(players: MutableSet<Player>, dialogModule: DialogModule, npcLoc: Location) {
        val dialog = DialogHologram(dialogModule, npcLoc, players)
        dialog.play()
        spaceDialogHolo(dialogModule, dialog)
    }

    private fun sendDialogHolo(player: Player, dialogModule: DialogModule, location: Location = player.location) {
        val holoDialog = DialogHologram(dialogModule, location, mutableSetOf(player))
        holoDialog.play()
        spaceDialogHolo(dialogModule, holoDialog)
    }

    fun spaceDialogHolo(dialogModule: DialogModule, holoDialog: DialogHologram) {
        val space = dialogModule.space
        if (!space.enable) return
        val id = dialogModule.dialogID
        submit(async = true, period = 5L) {
            val viewers = holoDialog.viewers
            if (viewers.isEmpty()) {
                cancel(); return@submit
            }
            if (!checkSpace(viewers, space.condition, holoDialog.npcLoc)) {
                endHoloDialog(viewers.first(), id)
                cancel()
                return@submit
            }
        }
    }

    fun checkSpace(players: MutableSet<Player>, condition: List<String>, loc: Location): Boolean {
        players.forEach {
            condition.forEach { cd ->
                val shell = if (cd.lowercase().startsWith("spacerange"))  cd+
                        " where location *"+loc.world?.name+
                        " *"+loc.x+" *"+loc.y+" *"+loc.z else cd
                if (!runEval(it, shell)) return false
            }
        }
        return true
    }

    fun endHoloDialog(player: Player, dialogID: String) {
        val pDate = DataStorage.getPlayerData(player)
        pDate.dialogData.endHoloDialog(dialogID)
    }
}