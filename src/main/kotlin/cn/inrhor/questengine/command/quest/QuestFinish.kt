package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender


class QuestFinish {

    fun onCommand(sender: CommandSender, args: Array<out String>) {
        val questID = args[1]

        val player = Bukkit.getPlayer(args[2]) ?: return run { TLocale.sendTo(sender, "PLAYER_NOT_ONLINE") }
        val uuid = player.uniqueId

        val questData = QuestManager.getQuestData(uuid, questID)?: return run {
            TLocale.sendTo(sender, "QUEST.NULL_QUEST_DATA", questID) }

        QuestManager.endQuest(player, questData, QuestState.FINISH, false)

        return
    }

}