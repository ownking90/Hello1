package com.smartmate.ai.domain

/** Priority levels for a task, ordered Low -> High for sorting purposes. */
enum class Priority { LOW, MEDIUM, HIGH }

/** Built-in task/note categories. Custom categories can be added later. */
enum class Category { STUDY, WORK, PERSONAL, OTHER }

/** How often a reminder should repeat once triggered. */
enum class RepeatOption { ONCE, DAILY, WEEKLY, MONTHLY }

/** App-wide theme preference, independent of the system setting. */
enum class ThemePreference { SYSTEM, LIGHT, DARK }

/** Supported in-app languages. */
enum class AppLanguage(val code: String) { ENGLISH("en"), HINDI("hi") }

/** The AI tool types available in the AI Tools section. */
enum class AiToolType {
    SUMMARIZER,
    GRAMMAR_FIXER,
    EMAIL_WRITER,
    CAPTION_GENERATOR,
    STUDY_NOTES,
    QUESTION_ANSWER,
    TRANSLATOR
}

/** Simple sealed result wrapper used by repositories/view models for UI state. */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
