package cn.inrhor.questengine.common.database

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.type.DatabaseLocal
import cn.inrhor.questengine.common.database.type.DatabaseManager
import cn.inrhor.questengine.common.database.type.DatabaseSQL
import cn.inrhor.questengine.common.database.type.DatabaseType
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.*
import taboolib.common.platform.function.*
import java.util.*

abstract class Database {

    /**
     * 为玩家拉取数据
     */
    abstract fun pull(player: Player)

    /**
     * 为玩家上载数据
     */
    abstract fun push(player: Player)

    /**
     * 得到玩家已存储的内部任务
     */
    abstract fun getInnerQuestData(player: Player, questUUID: UUID, innerQuestID: String): QuestData?

    /**
     * 清除任务数据，并清除内部任务和目标任务
     */
    abstract fun removeQuest(player: Player, questData: GroupData)

    /**
     * 清除内部任务数据，并清除其目标数据
     */
    open fun removeInnerQuest(player: Player, questUUID: UUID) {}

    /**
     * 清除控制模块数据
     */
    abstract fun removeControl(player: Player, controlID: String)

    open fun createQuest(player: Player, questUUID: UUID, questData: GroupData) {}

    companion object {

        lateinit var database: Database

        fun initDatabase() {
            database = when (DatabaseManager.type) {
                DatabaseType.MYSQL -> DatabaseSQL()
                else -> DatabaseLocal()
            }
        }

        @SubscribeEvent
        fun join(ev: PlayerJoinEvent) {
            val uuid = ev.player.uniqueId
            val pData = PlayerData(uuid)
            DataStorage.addPlayerData(uuid, pData)
            database.pull(ev.player)
        }

        @SubscribeEvent
        fun quit(ev: PlayerQuitEvent) {
            database.push(ev.player)
            val uuid = ev.player.uniqueId
            DataStorage.removePlayerData(uuid)
        }

        @Awake(LifeCycle.DISABLE)
        fun cancel() {
            pushAll()
        }

        @Awake(LifeCycle.ACTIVE)
        fun updateDatabase() {
            submit(async = true, period = 100L) {
                pushAll()
            }
        }

        private fun pushAll() {
            Bukkit.getOnlinePlayers().forEach {
                database.push(it)
            }
        }

    }

}