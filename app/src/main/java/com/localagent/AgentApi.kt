package com.localagent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AgentStatus(val ok: Boolean, val brain: String, val model: String, val lessonCount: Int, val missionCount: Int, val bashEnabled: Boolean, val autoPush: Boolean)
data class MissionResult(val ok: Boolean, val text: String)

object AgentApi {
    private const val BASE = "http://127.0.0.1:8765"
    suspend fun status(): AgentStatus = withContext(Dispatchers.IO) {
        val c = URL("$BASE/api/status").openConnection() as HttpURLConnection
        c.connectTimeout = 3000; c.readTimeout = 5000
        val j = JSONObject(c.inputStream.bufferedReader().use { it.readText() })
        AgentStatus(j.optBoolean("ok"), j.optString("brain"), j.optString("model"), j.optInt("lesson_count"), j.optInt("mission_count"), j.optBoolean("bash_enabled"), j.optBoolean("auto_push"))
    }
    suspend fun run(mission: String): MissionResult = withContext(Dispatchers.IO) {
        val c = URL("$BASE/api/mission").openConnection() as HttpURLConnection
        c.requestMethod = "POST"; c.doOutput = true
        c.setRequestProperty("Content-Type", "application/json")
        c.connectTimeout = 5000; c.readTimeout = 600_000
        c.outputStream.use { it.write(JSONObject().put("mission", mission).toString().toByteArray()) }
        val stream = if (c.responseCode in 200..299) c.inputStream else c.errorStream
        val j = JSONObject(stream.bufferedReader().use { it.readText() })
        MissionResult(j.optBoolean("ok"), j.optString("result").ifEmpty { j.optString("error") })
    }
}
