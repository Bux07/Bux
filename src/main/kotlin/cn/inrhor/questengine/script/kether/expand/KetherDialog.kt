package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.dialog.DialogManager
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherDialog {

    class SendDialog(val dialogID: String, val location: ParsedAction<*>): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                val p: Player = player.cast()
                DialogManager.sendDialog(p, dialogID, it)
            }
        }
    }

    class EndDialog(val dialogID: String): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
            DialogManager.endHoloDialog(player.cast(), dialogID)
            return CompletableFuture.completedFuture(null)
        }
    }

    /*
     * Dialog send [dialogID] where [location]
     * Dialog end [dialogID]
     */
    internal object Parser {
        @KetherParser(["dialog"])
        fun parser() = scriptParser {
            it.mark()
            when (it.expects("send", "end")) {
                "send" -> {
                    val dialogID = it.nextToken()
                    it.mark()
                    it.expect("where")
                    SendDialog(dialogID, it.next(ArgTypes.ACTION))
                }
                "end" -> EndDialog(it.nextToken())
                else -> error("unknown Dialog")
            }
        }
    }

}