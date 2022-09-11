package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.TagsData
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.database.data.storage
import cn.inrhor.questengine.common.database.data.tagsData
import org.bukkit.entity.Player
import taboolib.common.io.newFile
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.getObject
import taboolib.module.configuration.Configuration.Companion.setObject
import java.io.File
import java.util.*

class DatabaseLocal: Database() {

    private fun UUID.playerFile(): File {
        val data = newFile(QuestEngine.plugin.dataFolder, "data", folder = true)
        return newFile(data, "$this.yml")
    }

    fun UUID.getLocal(): Configuration {
        return Configuration.loadFromFile(playerFile())
    }

    override fun removeQuest(player: Player, questID: String) {
        val uuid = player.uniqueId
        val data = uuid.getLocal()
        data["quest.$questID"] = null
        data.saveToFile(uuid.playerFile())
    }

    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val data = uuid.getLocal()
        val pData = player.getPlayerData()
        val questDataMap = pData.dataContainer.quest
        if (data.contains("quest")) {
            data.getConfigurationSection("quest")?.getKeys(false)?.forEach {
                val questData = data.getObject<QuestData>("quest.$it", false)
                questData.target.forEach { e -> e.load(player) }
                questDataMap[it] = questData
                questData.updateTime(player)
            }
        }
        pData.dataContainer.tags = TagsData(data.getStringList("tags").toMutableSet())
        pData.dataContainer.storage = data.getObject("storage", false)
    }

    override fun push(player: Player) {
        val uuid = player.uniqueId
        val data = uuid.getLocal()
        val dataContainer = player.getPlayerData().dataContainer
        val q = dataContainer.quest
        data.getConfigurationSection("quest")?.getKeys(false)?.forEach {
            if (!q.containsKey(it)) {
                data["quest.$it"] = null
            }
        }
        q.forEach { (t, u) ->
            data.setObject("quest.$t", u)
        }
        data.setObject("tags", dataContainer.tags)
        data.setObject("storage", dataContainer.storage)
        data.saveToFile(uuid.playerFile())
    }
}