package cn.inrhor.questengine.common.dialog.animation.item

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @param item 物品内容
 * @param delay 延迟播放
 */
class ItemDialogPlay(var holoID: Int, val item: ItemStack, val delay: Int) {

    constructor(item: ItemStack, delay: Int) :
            this(0, item, delay)

}