package cn.inrhor.questengine.common.editor

import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.api.target.RegisterTarget
import cn.inrhor.questengine.api.target.TargetNode
import cn.inrhor.questengine.api.target.TargetNodeType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText

object EditorTarget {

    val editMeta = mutableListOf(
        "NAME", "REWARD", "REWARD_BOOLEAN", "ASYNC", "CONDITION")

   fun Player.editorTarget(questID: String, innerID: String, targetID: String) {
        val target = QuestManager.getTargetModule(questID, innerID, targetID)?: return
        val json = TellrawJson()
            .newLine()
            .append("   "+asLangText("EDITOR-EDIT-TARGET", questID, innerID, targetID))
            .newLine()
            .append("      "+asLangText("EDITOR-BACK"))
            .append("  "+asLangText("EDITOR-BACK-META"))
            .hoverText(asLangText("EDITOR-BACK-HOVER"))
            .runCommand("/qen eval editor target in list page 0 select $questID $innerID $targetID")
            .newLine()
            .newLine()
       if (target.name.uppercase().contains("TASK ")) editMeta.add("PERIOD")
       editMeta.forEach {
           val r = target.reward
           val rewardID = if (r.isEmpty()) " " else r.split(" ")[0]
           val b = if (r.isEmpty()) "NULL" else r.split(" ")[1].uppercase()
           if (it == "REWARD_BOOLEAN") {
               json.append("  "+asLangText("EDITOR-EDIT-TARGET-$it", asLangText("REWARD-BOOLEAN-META-$b")))
                   .hoverText(asLangText("EDITOR-EDIT-TARGET-BOOLEAN-META-HOVER"))
           }else {
               val t = "${target.async}".uppercase()
               json.append("      " + asLangText("EDITOR-EDIT-TARGET-$it",
                       target.name, rewardID, target.period, asLangText("ASYNC-BOOLEAN-META-$t")))
                   .append("  " + asLangText("EDITOR-EDIT-TARGET-META"))
               if (it == "ASYNC") {
                   json.hoverText(asLangText("EDITOR-EDIT-TARGET-BOOLEAN-META-HOVER"))
               }else {
                   json.hoverText(asLangText("EDITOR-EDIT-TARGET-META-HOVER"))
               }
           }
           when (it) {
               "CONDITION" -> {
                   json.runCommand("/qen eval editor target in edit "+it.lowercase()+" page 0 select $questID $innerID $targetID").newLine()
               }
               "REWARD" -> {
                   json.runCommand("/qen eval editor target in sel "+it.lowercase()+" page 0 select $questID $innerID $targetID")
               }
               else -> {
                   json.runCommand("/qen eval editor target in edit "+it.lowercase()+" select $questID $innerID $targetID").newLine()
               }
           }
       }
       RegisterTarget.getNodeList(target.name).forEach {
           val node = it.node
           val cmd = "/qen eval editor target in edit node to $'$node' select $questID $innerID $targetID"
           if (it.nodeType == TargetNodeType.LIST) {
               json.append("      "+asLangText("EDITOR-TARGET-LIST-NODE", asLangText("EDITOR-TARGET-LIST-NODE-${node.uppercase()}")))
                   .append("  "+asLangText("EDITOR-TARGET-NODE-META"))
                   .hoverText(asLangText("EDITOR-TARGET-NODE-META-HOVER"))
                   .runCommand(cmd)
                   .newLine()
           }else {
               val nodeMeta = target.nodeMeta(node)
               json.append("      "+asLangText("EDITOR-TARGET-NODE", asLangText("EDITOR-TARGET-NODE-${node.uppercase()}"), nodeMeta?.get(0)?: "NULL"))
                   .append("  "+asLangText("EDITOR-TARGET-NODE-META"))
               if (it.nodeType == TargetNodeType.BOOLEAN) {
                   json.hoverText(asLangText("EDITOR-TARGET-NODE-BOOLEAN-HOVER"))
               }else json.hoverText(asLangText("EDITOR-TARGET-NODE-META-HOVER"))
               json.runCommand(cmd).newLine()
           }
       }
        json.newLine().sendTo(adaptPlayer(this))
    }

    fun Player.editorTargetNode(questID: String, innerID: String, target: QuestTarget, targetNode: TargetNode) {
        val node = targetNode.node
        val meta = target.nodeMeta(node)
        when (targetNode.nodeType) {
            TargetNodeType.STRING -> {
                inputSign(arrayOf(node, asLangText("EDITOR-TARGET-NODE-PLAYER-STRING"))) {

                }
            }
            TargetNodeType.BOOLEAN -> {

            }
            TargetNodeType.INT -> {
                inputSign(arrayOf(node, asLangText("EDITOR-TARGET-NODE-PLAYER-INT"))) {

                }
            }
            TargetNodeType.DOUBLE -> {
                inputSign(arrayOf(node, asLangText("EDITOR-TARGET-NODE-PLAYER-DOUBLE"))) {

                }
            }
            else -> {

            }
        }
    }

}