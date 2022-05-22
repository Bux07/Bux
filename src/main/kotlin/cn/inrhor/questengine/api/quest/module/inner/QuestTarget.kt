package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.api.ui.UiFrame
import cn.inrhor.questengine.utlis.variableReader


class QuestTarget(var id: String, var name: String, var reward: String,
                  var period: Int, var async: Boolean, var condition: List<String>,
                  var node: String, val ui: UiFrame
) {
    constructor():
            this("targetId", "targetName", "", 0, false, listOf(), "", UiFrame())

    fun nodeMeta(meta: String): List<String>? {
        node.variableReader().forEach {
            val sp = it.split(" ")
            if (sp[0].uppercase() == meta.uppercase()) {
                val l = sp.toMutableList()
                l.removeAt(0)
                return l.toList()
            }
        }
        return null
    }
}
