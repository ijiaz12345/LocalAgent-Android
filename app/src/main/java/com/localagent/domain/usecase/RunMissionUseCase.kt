package com.localagent.domain.usecase

import com.localagent.domain.model.MissionResult
import com.localagent.domain.repository.AgentRepository
import javax.inject.Inject

class RunMissionUseCase @Inject constructor(
    private val repository: AgentRepository
) {
    suspend operator fun invoke(prompt: String): MissionResult {
        val cleaned = prompt.trim()
        require(cleaned.isNotEmpty()) { "اكتب وصف المهمة أولاً" }
        return repository.runMission(cleaned)
    }
}
