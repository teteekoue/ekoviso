package com.ekoviso.app.data.remote

import com.ekoviso.app.data.local.entity.ChannelEntity

object M3uParser {

    fun parse(content: String): List<ChannelEntity> {
        val channels = mutableListOf<ChannelEntity>()
        val lines = content.lines()

        var i = 0
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("#EXTINF:")) {
                val name = extractAttribute(line, "tvg-name") 
                    ?: extractDisplayName(line)
                    ?: "Sans nom"
                val group = extractAttribute(line, "group-title") ?: "Non classé"
                val logo = extractAttribute(line, "tvg-logo") ?: ""
                val url = if (i + 1 < lines.size) lines[i + 1].trim() else ""
                
                if (url.startsWith("http")) {
                    channels.add(
                        ChannelEntity(
                            name = name,
                            url = url,
                            group = group,
                            logo = logo
                        )
                    )
                }
                i += 2
            } else {
                i++
            }
        }
        
        // Fallback : pattern simple #EXTINF:...titre\nURL
        if (channels.isEmpty()) {
            val regex = Regex("#EXTINF:.*?,(.*?)\\n(https?://[^\\s]+)")
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
        val regex = Regex("$attr=\"([^\"]*)\"")
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
