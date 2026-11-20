package com.localagent.domain.model

data class AgentConnection(
    val connected: Boolean,
    val brain: String,
    val lessons: Int,
    val missions: Int
)

data class MissionResult(
    val output: String,
    val success: Boolean
)

enum class AgentErrorKind {
    NETWORK, SERVER, AUTH, VALIDATION, UNKNOWN
}

// استخدام sealed class لتصنيف الأخطاء بدقة
sealed class AgentError(
    val kind: AgentErrorKind,
    override val message: String,
    val retryable: Boolean
) : Exception(message) {
    class Network(msg: String = "تعذر الاتصال بالخادم") : 
        AgentError(AgentErrorKind.NETWORK, msg, retryable = true)
    class Server(msg: String = "خطأ مؤقت في الخادم") : 
        AgentError(AgentErrorKind.SERVER, msg, retryable = true)
    class Auth(msg: String = "انتهت صلاحية المصادقة") : 
        AgentError(AgentErrorKind.AUTH, msg, retryable = false)
    class Validation(msg: String) : 
        AgentError(AgentErrorKind.VALIDATION, msg, retryable = false)
    class Unknown(msg: String = "حدث خطأ غير متوقع") : 
        AgentError(AgentErrorKind.UNKNOWN, msg, retryable = false)
}
