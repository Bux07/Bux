package cn.inrhor.questengine.utlis.ui

import cn.inrhor.questengine.api.ui.AddonFrame
import cn.inrhor.questengine.utlis.copy
import cn.inrhor.questengine.utlis.toJsonStr
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.platform.compat.replacePlaceholder

/**
 * 高度自定义 JSON 内容
 *
 * 文字组件
 */
class TextComponent(
    var text: MutableList<String> = mutableListOf(),
    var hover: MutableList<String> = mutableListOf(),
    var condition: String = "",
    var command: String = "",
    var type: BuilderFrame.Type = BuilderFrame.Type.CUSTOM
) {

    constructor(addon: AddonFrame, type: BuilderFrame.Type = BuilderFrame.Type.CUSTOM):
            this(addon.text.toMutableList(), addon.hover.toMutableList(), addon.condition, addon.command, type)

    fun copy(): TextComponent {
        return TextComponent(text.copy(), hover.copy(), condition, command, type)
    }

    fun build(player: Player?): TellrawJson {
        val json = TellrawJson().append(text(player).toJsonStr())
        if (hover.isNotEmpty()) json.hoverText(hover(player).toJsonStr())
        if (command.isNotEmpty()) json.runCommand(command(player))
        return json
    }

    fun text(player: Player?): MutableList<String> {
        player?: return text
        return text.replacePlaceholder(player).toMutableList()
    }

    fun hover(player: Player?): MutableList<String> {
        player?: return hover
        return hover.replacePlaceholder(player).toMutableList()
    }

    fun condition(player: Player?): String {
        player?: return condition
        return condition.replacePlaceholder(player)
    }

    fun command(player: Player?): String {
        player?: return command
        return command.replace("{{player}}", player.name)
            .replacePlaceholder(player)
    }

}

inline fun textComponent(component: TextComponent.() -> Unit = {}): TextComponent {
    return TextComponent().also(component)
}