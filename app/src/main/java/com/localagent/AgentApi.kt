package com.localagent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AgentStatus(
    val ok: Boolean,
    val brain: String,
    val model: String,
    val lessonCount: Int,
    val missionCount: Int,
    val bashEnabled: Boolean,
    val autoPush: Boolean,
)

data class MissionResult(val ok: Boolean, val text: String)

object AgentApi {
    private const val BASE = "http://127.0.0.1:8765"

    suspend fun status(): AgentStatus = withContext(Dispatchers.IO) {
        val c = URL("$BASE/api/status").openConnection() as HttpURLConnection
        c.connectTimeout = 3000; c.readTimeout = 5000
        val txt = c.inputStream.bufferedReader().use { it.readText() }
        val j = JSONObject(txt)
        AgentStatus(
            ok = j.optBoolean("ok"),
            brain = j.optString("brain"),
            model = j.optString("model"),
            lessonCount = j.optInt("lesson_count"),
            missionCount = j.optInt("mission_count"),
            bashEnabled = j.optBoolean("bash_enabled"),
            autoPush = j.optBoolean("auto_push"),
        )
    }

    suspend fun run(mission: String): MissionResult = withContext(Dispatchers.IO) {
        val c = URL("$BASE/api/mission").openConnection() as HttpURLConnection
        c.requestMethod = "POST"
        c.doOutput = true
        c.setRequestProperty("Content-Type", "application/json")
        c.connectTimeout = 5000
        c.readTimeout = 600_000
        c.outputStream.use { it.write(JSONObject().put("mission", mission)
            .toString().toByteArray()) }
        val stream = if (c.responseCode in 200..299) c.inputStream else c.errorStream
        val txt = stream.bufferedReader().use { it.readText() }
        val j = JSONObject(txt)
        MissionResult(
            ok = j.optBoolean("ok"),
            text = j.optString("result").ifEmpty { j.optString("error") }
        )
    }
}
