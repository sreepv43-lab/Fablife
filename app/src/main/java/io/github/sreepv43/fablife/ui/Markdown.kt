package io.github.sreepv43.fablife.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Replaces letter placeholders such as {name} and {wakeTime}. */
fun fillPlaceholders(text: String, values: Map<String, String>): String =
    values.entries.fold(text) { acc, (k, v) -> acc.replace("{$k}", v) }

private val inline = Regex("""\*\*(.+?)\*\*|\*(.+?)\*""")

/** Inline Markdown: **bold** and *italic*. */
fun inlineMarkdown(text: String): AnnotatedString = buildAnnotatedString {
    var last = 0
    for (m in inline.findAll(text)) {
        append(text.substring(last, m.range.first))
        val bold = m.groups[1]
        if (bold != null) withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(inlineMarkdown(bold.value)) }
        else withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(inlineMarkdown(m.groups[2]!!.value)) }
        last = m.range.last + 1
    }
    append(text.substring(last))
}

private sealed interface Block {
    data class Heading(val text: String) : Block
    data class Paragraph(val lines: List<String>) : Block
    data class Bullet(val marker: String, val text: String) : Block
    data class Table(val rows: List<List<String>>) : Block
    data class Quote(val text: String) : Block
}

private fun parse(markdown: String): List<Block> {
    val blocks = mutableListOf<Block>()
    val paragraph = mutableListOf<String>()
    val table = mutableListOf<List<String>>()
    fun flush() {
        if (paragraph.isNotEmpty()) blocks += Block.Paragraph(paragraph.toList()).also { paragraph.clear() }
        if (table.isNotEmpty()) blocks += Block.Table(table.toList()).also { table.clear() }
    }
    for (raw in markdown.lines()) {
        val line = raw.trimEnd()
        when {
            line.isBlank() -> flush()
            line.startsWith("|") -> {
                if (paragraph.isNotEmpty()) flush()
                if (!line.replace(Regex("[|\\-: ]"), "").isEmpty()) {
                    table += line.trim('|').split("|").map { it.trim() }
                }
            }
            line.startsWith("#") -> { flush(); blocks += Block.Heading(line.trimStart('#').trim()) }
            line.startsWith("> ") -> { flush(); blocks += Block.Quote(line.removePrefix("> ")) }
            line.startsWith("- ") -> { flush(); blocks += Block.Bullet("•", line.removePrefix("- ")) }
            Regex("""^\d+\. """).containsMatchIn(line) -> {
                flush()
                val marker = line.substringBefore(" ")
                blocks += Block.Bullet(marker, line.substringAfter(" "))
            }
            else -> { if (table.isNotEmpty()) flush(); paragraph += line }
        }
    }
    flush()
    return blocks
}

@Composable
fun MarkdownText(markdown: String, modifier: Modifier = Modifier) {
    val body = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, lineHeight = 28.sp)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        for (block in parse(markdown)) when (block) {
            is Block.Heading -> Text(inlineMarkdown(block.text), style = MaterialTheme.typography.titleLarge)
            is Block.Paragraph -> Text(inlineMarkdown(block.lines.joinToString("\n")), style = body)
            is Block.Bullet -> Row {
                Text(block.marker, style = body, modifier = Modifier.width(28.dp))
                Text(inlineMarkdown(block.text), style = body)
            }
            is Block.Quote -> Row(Modifier.height(IntrinsicSize.Min)) {
                Box(Modifier.width(4.dp).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
                Text(inlineMarkdown(block.text), style = body.copy(fontStyle = FontStyle.Italic), modifier = Modifier.padding(start = 12.dp))
            }
            is Block.Table -> Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceContainer).padding(12.dp),
            ) {
                block.rows.forEachIndexed { i, row ->
                    if (i > 0) HorizontalDivider(Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { cell ->
                            Text(
                                inlineMarkdown(cell),
                                style = if (i == 0) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}
