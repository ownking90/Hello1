package com.smartmate.ai.data.remote

/**
 * Collection names for the cloud (Firestore) database backing accounts, tasks,
 * notes, reminders, and chat history. Local Room entities mirror these
 * collections 1:1 so a future [sync worker] can push/pull documents keyed by
 * entity id while the app works fully offline-first.
 *
 * Document shapes (one collection per table):
 * - users/{uid}                         -> profile: name, email, photoUrl
 * - users/{uid}/settings/preferences    -> themePreference, language, notificationsEnabled
 * - chats/{chatId}                      -> ownerId, title, createdAt, updatedAt
 * - chats/{chatId}/messages/{messageId} -> isFromUser, text, createdAt
 * - tasks/{taskId}                      -> ownerId, title, notes, dueAt, priority, category, isCompleted
 * - notes/{noteId}                      -> ownerId, title, content, isPinned, category, tags
 * - reminders/{reminderId}              -> ownerId, title, note, dateTime, repeatOption, isEnabled
 */
object FirestoreSchema {
    const val USERS = "users"
    const val SETTINGS_SUBCOLLECTION = "settings"
    const val CHATS = "chats"
    const val MESSAGES_SUBCOLLECTION = "messages"
    const val TASKS = "tasks"
    const val NOTES = "notes"
    const val REMINDERS = "reminders"
}
