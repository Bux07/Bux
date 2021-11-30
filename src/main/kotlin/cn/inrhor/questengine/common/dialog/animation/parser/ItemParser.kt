package cn.inrhor.questengine.common.dialog.animation.parser

import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.script.kether.eval

/**
 * 注册对话传递物品列表，此类做解析并存储
 *
 * 允许 dialog & reply 使用
 */
class ItemParser(private val itemContents: MutableList<String>) {

    /**
     * 列表包含的物品内容
     * 每一行仅一个物品
     */
    val dialogItemList = mutableListOf<ItemDialogPlay>()

    fun init(dialogID: String) {
        for (line in 0 until this.itemContents.size) {
            val script = this.itemContents[line]
            val holoID = HoloIDManager.generate(dialogID, line, "item")
            val itemID = HoloIDManager.generate(dialogID, line, "itemStack")
            HoloIDManager.addEntityID(holoID)
            HoloIDManager.addEntityID(itemID)
            /*if (script.uppercase().startsWith("ITEMWRITE")) {
                val dialogItem = eval(script) as ItemDialogPlay
                dialogItem.holoID = holoID
                dialogItem.itemID = itemID
                dialogItemList.add(dialogItem)
            }else {
                val item = ItemManager.get(script)
                val dialogItem = ItemDialogPlay(holoID, itemID, item, ItemDialogPlay.Type.SUSPEND, 0)
                dialogItemList.add(dialogItem)
            }*/
        }
    }

    /**
     * 根据索引获取对话物品
     */
    fun getDialogItem(index: Int): ItemDialogPlay? {
        if (dialogItemList.size > index) return dialogItemList[index]
        return null
    }

    /**
     * 获取物品列表
     */
    /*fun getDialogItemList(): MutableList<ItemStack> {
        val itemList = mutableListOf<ItemStack>()
        dialogItemList.forEach { itemList.add(it.item) }
        return itemList
    }*/
}