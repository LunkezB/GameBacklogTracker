package com.example.gamebacklogtracker.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.gamebacklogtracker.domain.model.GameStatus
import com.example.gamebacklogtracker.domain.model.GameWithUserData
import java.net.URLEncoder
import androidx.core.net.toUri

object IntentUtils {

    fun shareGame(context: Context, item: GameWithUserData) {
        val text = buildString {
            append("I'm tracking ")
            append(item.displayTitle)
            append(" in Game Backlog Tracker.")
            append("\nStatus: ")
            append(statusLabel(item.resolvedStatus))

            if (item.userData?.note?.isNotBlank() == true) {
                append("\nNote: ")
                append(item.userData.note)
            }

            if (item.userData?.hoursPlayed != null) {
                append("\nHours played: ")
                append(item.userData.hoursPlayed)
            }
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "Game update: ${item.displayTitle}")
        }

        context.startActivity(
            Intent.createChooser(intent, "Share game")
        )
    }

    fun openGameLinkOrSearch(context: Context, item: GameWithUserData) {
        val rawUrl = item.game.sourceUrl?.takeIf { it.isNotBlank() }
        val finalUri = if (rawUrl != null) {
            rawUrl.toUri()
        } else {
            val query = URLEncoder.encode("${item.displayTitle} Nintendo Switch", "UTF-8")
            "https://www.google.com/search?q=$query".toUri()
        }

        val intent = Intent(Intent.ACTION_VIEW, finalUri)
        context.startActivity(intent)
    }

    private fun statusLabel(status: GameStatus): String {
        return when (status) {
            GameStatus.NONE -> "None"
            GameStatus.BACKLOG -> "Backlog"
            GameStatus.PLAYING -> "Playing"
            GameStatus.COMPLETED -> "Completed"
        }
    }
}