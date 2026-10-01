package com.smartmate.ai.ai

import com.smartmate.ai.domain.AiToolType
import kotlinx.coroutines.delay

/**
 * Abstraction over whatever AI/LLM backend powers chat replies and the AI
 * Tools section. [LocalAiService] is a lightweight offline implementation so
 * the app is fully functional without any API key; swap in a real
 * implementation (OpenAI, Gemini, etc.) by providing another [AiService] to
 * the view models.
 */
interface AiService {
    suspend fun chatReply(conversationHistory: List<String>, userMessage: String): String
    suspend fun runTool(tool: AiToolType, input: String): String
    suspend fun generateDailyPlan(pendingTaskTitles: List<String>): String
}

class LocalAiService : AiService {

    override suspend fun chatReply(conversationHistory: List<String>, userMessage: String): String {
        simulateThinking()
        return when {
            userMessage.isBlank() -> "Could you tell me a bit more?"
            userMessage.trim().endsWith("?") ->
                "That's a great question! Here's a quick explanation of \"${userMessage.trim()}\": " +
                    "this is a placeholder AI response — connect a real AI provider in AiService to get full answers."
            else -> "Got it. Here's a summary/response for: \"${userMessage.trim()}\"."
        }
    }

    override suspend fun runTool(tool: AiToolType, input: String): String {
        simulateThinking()
        if (input.isBlank()) return "Please enter some text first."
        return when (tool) {
            AiToolType.SUMMARIZER -> "Summary: ${input.take(120)}${if (input.length > 120) "…" else ""}"
            AiToolType.GRAMMAR_FIXER -> "Corrected: ${input.trim().replaceFirstChar { it.uppercase() }}"
            AiToolType.EMAIL_WRITER -> "Subject: Quick note\n\nHi,\n\n$input\n\nBest regards"
            AiToolType.CAPTION_GENERATOR -> "✨ ${input.take(60)} #SmartMateAI"
            AiToolType.STUDY_NOTES -> "Key points:\n- ${input.take(80)}\n- Review & revise regularly"
            AiToolType.QUESTION_ANSWER -> "Answer: Based on \"$input\", here is a helpful explanation."
            AiToolType.TRANSLATOR -> "Translation: $input"
        }
    }

    override suspend fun generateDailyPlan(pendingTaskTitles: List<String>): String {
        simulateThinking()
        if (pendingTaskTitles.isEmpty()) return "You have no pending tasks today. Enjoy a light day! 🎉"
        return buildString {
            appendLine("Here's a suggested plan for today:")
            pendingTaskTitles.forEachIndexed { index, title ->
                appendLine("${index + 1}. $title")
            }
        }
    }

    private suspend fun simulateThinking() = delay(150)
}
