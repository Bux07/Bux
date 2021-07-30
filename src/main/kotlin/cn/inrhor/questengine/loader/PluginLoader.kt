package cn.inrhor.questengine.loader

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.database.type.DatabaseManager
import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.common.packet.PacketManager
import cn.inrhor.questengine.common.quest.QuestFile
import cn.inrhor.questengine.utlis.public.UtilString
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.console
import taboolib.module.lang.sendLang
import java.io.File
import kotlin.system.measureTimeMillis

class PluginLoader {

    @Awake(LifeCycle.ENABLE)
    fun init() {
        Info.logoSend()
        doLoad()
    }

    private var reloading = false

    @Awake(LifeCycle.DISABLE)
    fun cancel() {
        Bukkit.getScheduler().cancelTasks(QuestEngine.plugin)
        clearMap()
    }

    private fun doLoad() {
        UtilString.updateLang().forEach {
            UpdateYaml.run("lang/$it.yml")
        }
        Bukkit.getScheduler().runTaskAsynchronously(QuestEngine.plugin, Runnable {
            val timeCost = measureTimeMillis {
                ItemManager.loadItem()
                DialogManager.loadDialog()
                PacketManager.loadPacket()
                QuestFile.loadDialog()
                UpdateYaml.run("team/chat.yml")
            }
            console().sendLang("LOADER.TIME_COST", UtilString.pluginTag, timeCost)
        })
        DatabaseManager.init()
    }

    fun doReload() {
        if (reloading) {
            throw RuntimeException("reloading")
        }
        reloading = true
        clearMap()
        doLoad()
        reloading = false
    }

    private fun clearMap() {
        DialogManager.clearMap()
        ItemManager.clearMap()
    }

}