package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.api.quest.TimeAddon
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.getControlFrame
import org.bukkit.entity.Player
import java.text.SimpleDateFormat
import java.util.*

data class QuestData(
    val id: String = "?",
    var target: MutableList<TargetData> = mutableListOf(),
    var state: StateType = StateType.DOING,
    val control: MutableList<String>,
    var time: Date = Date(), @Transient var end: Date? = null) {

    fun loadControl(player: Player) {
        control.forEach {
            val c = it.getControlFrame(id)
        }
    }

    fun TimeAddon.updateTime() {
        time = Date()
        val type = type
        val duration = duration
        if (type != TimeAddon.Type.ALWAYS) {
            val sp = duration.split(">")
            val a = sp[0].split(",")
            val b = sp[1].split(",")
            when (type) {
                TimeAddon.Type.DAY -> {
                    val ymdFormat = SimpleDateFormat("yyyy-MM-dd")
                    val ymd= ymdFormat.format(time)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    time = dateFormat.parse("$ymd ${a[0]}")
                    end = dateFormat.parse("$ymd ${b[0]}")
                }
                TimeAddon.Type.ALWAYS -> {}
                TimeAddon.Type.WEEKLY -> {
                    val cal1 = Calendar.getInstance()
                    cal1.set(Calendar.DAY_OF_WEEK, a[0].toInt()) // 当前周某一天，1是上周日，2是本周一
                    val cal2 = Calendar.getInstance()
                    cal2.set(Calendar.DAY_OF_WEEK, b[0].toInt())
                    val c = a[1].split(":") ;val d = b[1].split(":")
                    cal1.set(Calendar.HOUR, c[0].toInt());cal1.set(Calendar.MINUTE, c[1].toInt());cal1.set(Calendar.SECOND, c[2].toInt())
                    cal2.set(Calendar.HOUR, d[0].toInt());cal2.set(Calendar.MINUTE, d[1].toInt());cal2.set(Calendar.SECOND, d[2].toInt())
                    time = cal1.time
                    end = cal2.time
                }
                TimeAddon.Type.MONTHLY -> {
                    val cal1 = Calendar.getInstance()
                    val cal2 = Calendar.getInstance()
                    cal1.set(Calendar.DAY_OF_MONTH, a[0].toInt()) // 当前月的某一天
                    cal2.set(Calendar.DAY_OF_MONTH, b[0].toInt())
                    val c = a[1].split(":") ;val d = b[1].split(":")
                    cal1.set(Calendar.HOUR, c[0].toInt());cal1.set(Calendar.MINUTE, c[1].toInt());cal1.set(Calendar.SECOND, c[2].toInt())
                    cal2.set(Calendar.HOUR, d[0].toInt());cal2.set(Calendar.MINUTE, d[1].toInt());cal2.set(Calendar.SECOND, d[2].toInt())
                    time = cal1.time
                    end = cal2.time
                }
                TimeAddon.Type.YEARLY -> {
                    val cal1 = Calendar.getInstance()
                    cal1.set(Calendar.MONTH, a[0].toInt()) // 当前年某一月，0是一月
                    val cal2 = Calendar.getInstance()
                    cal2.set(Calendar.MONTH, b[0].toInt())
                    cal1.set(Calendar.DAY_OF_MONTH, a[1].toInt()) // 当前月的某一天
                    cal2.set(Calendar.DAY_OF_MONTH, b[1].toInt())
                    val c = a[2].split(":") ;val d = b[2].split(":")
                    cal1.set(Calendar.HOUR, c[0].toInt());cal1.set(Calendar.MINUTE, c[1].toInt());cal1.set(Calendar.SECOND, c[2].toInt())
                    cal2.set(Calendar.HOUR, d[0].toInt());cal2.set(Calendar.MINUTE, d[1].toInt());cal2.set(Calendar.SECOND, d[2].toInt())
                    time = cal1.time
                    end = cal2.time
                }
                TimeAddon.Type.CUSTOM -> {
                    val add = duration.lowercase().split(" ")
                    val cal = Calendar.getInstance()
                    val t = add[1].toInt()
                    when (add[0]) {
                        "s" -> {
                            cal.add(Calendar.SECOND, t)
                        }
                        "m" -> {
                            cal.add(Calendar.MINUTE, t)
                        }
                        "h" -> {
                            cal.add(Calendar.HOUR, t)
                        }
                    }
                    end = cal.time
                }
            }
        }
    }

    /**
     * @return 是否完成了所有目标
     */
    fun isFinishTarget(): Boolean {
        var finish = 0
        val targetSize = target.size
        target.forEach {
            if (it.state == StateType.FINISH) finish++
        }
        return finish >= targetSize
    }

}