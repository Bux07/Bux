package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.itemTrigger
import cn.inrhor.questengine.api.target.util.TriggerUtils.numberTrigger
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.TargetManager
import org.bukkit.event.enchantment.EnchantItemEvent

object TEnchantItem: TargetExtend<EnchantItemEvent>() {

    override val name = "enchant item"

    init {
        event = EnchantItemEvent::class
        tasker{
            val questData = QuestManager.getDoingQuest(enchanter, true)?: return@tasker enchanter
            val itemContent = itemTrigger(questData, name, item)
            val cost = numberTrigger(questData, name, "cost", expLevelCost.toDouble())
            TargetManager.set(name, "item", itemContent).set(name, "cost", cost)
            enchanter
        }
        TargetManager.register(name, "item")
    }

}