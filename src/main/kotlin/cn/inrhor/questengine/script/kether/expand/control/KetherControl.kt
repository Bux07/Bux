package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.database.data.DataStorage
import org.bukkit.entity.Player
import taboolib.library.kether.*
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherControl {

    class WaitTime(val time: Int, val questID: String, val mainQuestID: String): QuestAction<Void>() {
        override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
            WaitRun.run(frame, time, questID, mainQuestID)
            return CompletableFuture.completedFuture(null)
        }
    }

    object WaitRun {
        fun run(frame: QuestContext.Frame, time: Int, questID: String, mainQuestID: String) {
            val player = frame.script().sender as? Player ?: error("unknown player")
            val pData = DataStorage.getPlayerData(player)
            val cMap = pData.controlList
            val id = QuestManager.generateControlID(questID, mainQuestID)
            if (cMap.containsKey(id)) {
                val cData = cMap[id]?: return
                cData.waitTime = time
            }
        }
    }

    /**
     * wait type time to questID mainQuestID
     */
    companion object {
        @KetherParser(["wait"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            when (it.expects("s", "minute")) {
                "s" -> {
                    try {
                        val time = it.nextInt()*20
                        it.mark()
                        it.expects("to")
                        WaitTime(time, it.nextToken(), it.nextToken())
                    } catch (ex: Exception) {
                        error("error script wait")
                    }
                }
                "minute" -> {
                    try {
                        val time = it.nextInt()*1200
                        it.mark()
                        it.expects("to")
                        WaitTime(time, it.nextToken(), it.nextToken())
                    } catch (ex: Exception) {
                        error("error script wait")
                    }
                }
                else -> error("unknown type")
            }
        }
    }

}