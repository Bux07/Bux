package cn.inrhor.questengine.api.dialog

/**
 * 回复属性模块
 */
data class ReplyModule(val replyID: String) {

    val condition = mutableListOf<String>()
    val content = mutableListOf<String>()
    val script = mutableListOf<String>()

}