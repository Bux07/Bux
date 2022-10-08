package cn.inrhor.questengine.server

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.Database.Companion.database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.type.DatabaseManager
import cn.inrhor.questengine.common.dialog.DialogFile
import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.common.quest.QuestFile
import cn.inrhor.questengine.common.quest.group.GroupFile
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.utlis.UtilString
import cn.inrhor.questengine.utlis.file.releaseFile
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.*
import taboolib.module.lang.sendLang
import taboolib.module.nms.MinecraftVersion
import kotlin.system.measureTimeMillis

object PluginLoader {

    fun loadTask(logo: Boolean = true) {
        if (logo) {
            ConsoleMsg.logo()
        }
        val version = MinecraftVersion.major
        ConsoleMsg.loadSend(version)
        if (version <= 3) {
            disablePlugin()
            return
        }
        UtilString.updateLang().forEach {
            UpdateYaml.run("lang/$it.yml")
        }
        val timeCost = measureTimeMillis {
            ItemManager.loadItem()
            DialogFile.loadDialog()
            QuestFile.loadQuest()
            GroupFile.load()
            releaseFile("team/chat.yml")
        }
        console().sendLang("LOADER-TIME_COST", UtilString.pluginTag, timeCost)
        DatabaseManager.init()
        Bukkit.getOnlinePlayers().forEach {
            Database.playerPull(it)
        }
    }

    fun unloadTask() {
        ConsoleMsg.logo()
        Bukkit.getScheduler().cancelTasks(QuestEngine.plugin)
        Bukkit.getOnlinePlayers().forEach {
            val data = it.getPlayerData()
            data.dialogData.dialogMap.values.forEach { d -> d.end() }
            data.navData.values.forEach { n -> n.stop() }
            database.push(it)
            DataStorage.removePlayerData(it.uniqueId)
        }
        clearMap()
    }

    @Awake(LifeCycle.ENABLE)
    fun init() {
        loadTask()
    }

    @Awake(LifeCycle.DISABLE)
    fun cancel() {
        unloadTask()
    }

    private fun clearMap() {
        DialogManager.clearMap()
        ItemManager.clearMap()
        QuestManager.clearQuestMap()
    }

}