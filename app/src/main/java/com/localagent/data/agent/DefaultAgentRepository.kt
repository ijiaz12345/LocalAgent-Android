package com.localagent.data.agent

import com.localagent.domain.model.AgentConnection
import com.localagent.domain.model.AgentError
import com.localagent.domain.model.MissionResult
import com.localagent.domain.repository.AgentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAgentRepository @Inject constructor() : AgentRepository {

    override suspend fun ping(): AgentConnection = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://127.0.0.1:8765/api/status")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
            }
            val response = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(response)
            AgentConnection(
                connected = json.optBoolean("ok", false),
                brain = json.optString("brain", "Unknown"),
                lessons = json.optInt("lesson_count", 0),
                missions = json.optInt("mission_count", 0)
            )
        } catch (e: SocketTimeoutException) {
            throw AgentError.Network("انتهت مهلة الاتصال بالخادم")
        } catch (e: IOException) {
            throw AgentError.Network("تعذر الاتصال. تأكد من تشغيل server.py")
        } catch (e: Exception) {
            throw AgentError.Unknown("خطأ غير متوقع: ${e.message}")
        }
    }

    override suspend fun runMission(prompt: String): MissionResult = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://127.0.0.1:8765/api/mission")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10000
                readTimeout = 120000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }
            val body = JSONObject().put("mission", prompt).toString()
            OutputStreamWriter(conn.outputStream).use { it.write(body) }
            
            val responseCode = conn.responseCode
            if (responseCode in 500..599) {
                throw AgentError.Server("الخادم يواجه مشكلة ($responseCode)")
            }
            
            val response = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(response)
            MissionResult(
                output = json.optString("output", json.optString("answer", response)),
                success = json.optBoolean("ok", false)
            )
        } catch (e: SocketTimeoutException) {
            throw AgentError.Network("انتهت مهلة تنفيذ المهمة")
        } catch (e: AgentError) {
            throw e
        } catch (e: IOException) {
            throw AgentError.Network("انقطع الاتصال أثناء التنفيذ")
        } catch (e: Exception) {
            throw AgentError.Unknown("فشل التنفيذ: ${e.message}")
        }
    }
}
