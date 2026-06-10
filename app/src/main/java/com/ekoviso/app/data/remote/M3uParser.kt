package com.ekoviso.app.data.remote

import com.ekoviso.app.data.local.entity.ChannelEntity

object M3uParser {

    fun parse(content: String): List<ChannelEntity> {
        val channels = mutableListOf<ChannelEntity>()
        val lines = content.lines()

        var currentName = ""
        var currentGroup = ""
        var currentLogo = ""

        for (line in lines) {
            val trimmedLine = line.trim()
            when {
                trimmedLine.startsWith("#EXTINF:") -> {
                    currentName = extractAttribute(trimmedLine, "tvg-name")
                        ?: extractDisplayName(trimmedLine)
                        ?: "Sans nom"
                    currentGroup = extractAttribute(trimmedLine, "group-title") ?: "Non classé"
                    currentLogo = extractAttribute(trimmedLine, "tvg-logo") ?: ""
                }
                trimmedLine.startsWith("#EXTGRP:") -> {
                    currentGroup = trimmedLine.substringAfter(":").trim().ifEmpty { currentGroup }
                }
                trimmedLine.startsWith("http") -> {
                    if (currentName.isNotEmpty()) {
                        channels.add(
                            ChannelEntity(
                                name = currentName,
                                url = trimmedLine,
                                group = currentGroup,
                                logo = currentLogo
                            )
                        )
                        // Reset for next channel
                        currentName = ""
                        currentGroup = "Non classé"
                        currentLogo = ""
                    }
                }
            }
        }

        // Fallback for simple formats if nothing was parsed
        if (channels.isEmpty()) {
            val regex = Regex("#EXTINF:.*?,(.*?)\\r?\\n(https?://[^\\s]+)")
            regex.findAll(content).forEach { match ->
                channels.add(
                    ChannelEntity(
                        name = match.groupValues[1].trim().ifEmpty { "Sans nom" },
                        url = match.groupValues[2].trim(),
                        group = "Non classé",
                        logo = ""
                    )
                )
            }
        }

        return channels
    }

    private fun extractAttribute(line: String, attr: String): String? {
        val regex = Regex("$attr\\s*=\\s*\"([^\"]*)\"", RegexOption.IGNORE_CASE)
        return regex.find(line)?.groupValues?.get(1)?.takeIf { it.isNotBlank() }
    }

    private fun extractDisplayName(line: String): String? {
        val lastComma = line.lastIndexOf(',')
        if (lastComma >= 0 && lastComma < line.length - 1) {
            return line.substring(lastComma + 1).trim().takeIf { it.isNotBlank() }
        }
        return null
    }
}
