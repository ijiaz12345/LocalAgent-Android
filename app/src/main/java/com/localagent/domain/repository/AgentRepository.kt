package com.localagent.domain.repository

import com.localagent.domain.model.AgentConnection
import com.localagent.domain.model.MissionResult

interface AgentRepository {
    suspend fun ping(): AgentConnection
    suspend fun runMission(prompt: String): MissionResult
}
