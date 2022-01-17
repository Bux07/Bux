package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.toState
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherQuest {

    class DoQuest(val type: Type, val questID: String): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
            if (type == Type.ACCEPT) {
                QuestManager.acceptQuest(player.cast(), questID)
            }else if (type == Type.QUIT) {
                QuestManager.quitQuest(player.cast(), questID)
            }else if (type == Type.FINISH) {
                QuestManager.endQuest(player.cast(), questID, QuestState.FINISH, false)
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    enum class Type {
        ACCEPT, QUIT, FINISH, STATE
    }

    class ChangeQuest(val type: Type, val questID: String, val state: String): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
            QuestManager.setQuestState(player.cast(), questID, state.toState())
            return CompletableFuture.completedFuture(null)
        }
    }

    /*
     * Quest accept/quit/finish [questID]
     * Quest state [questID] [state]
     */
    internal object Parser {
        @KetherParser(["quest"])
        fun parser() = scriptParser {
            it.mark()
            when (it.expects("accept", "quit", "finish", "state")) {
                "accept" -> DoQuest(Type.ACCEPT, it.nextToken())
                "quit" -> DoQuest(Type.QUIT, it.nextToken())
                "finish" -> DoQuest(Type.FINISH, it.nextToken())
                "state" -> ChangeQuest(Type.STATE, it.nextToken(), it.nextToken())
                else -> error("unknown Quest")
            }
        }
    }

}