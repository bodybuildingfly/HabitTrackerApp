package com.example.habittrackerapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.RichTextState

@OptIn(ExperimentalRichTextApi::class)
@Composable
fun RichTextStyleRow(
    modifier: Modifier = Modifier,
    state: RichTextState,
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
//        item {
//            RichTextStyleButton(
//                onClick = {
//                    state.addParagraphStyle(
//                        ParagraphStyle(
//                            textAlign = TextAlign.Left,
//                        )
//                    )
//                },
//                isSelected = state.currentParagraphStyle.textAlign == TextAlign.Left,
//                icon = Icons.AutoMirrored.Outlined.FormatAlignLeft
//            )
//        }
//
//        item {
//            RichTextStyleButton(
//                onClick = {
//                    state.addParagraphStyle(
//                        ParagraphStyle(
//                            textAlign = TextAlign.Center
//                        )
//                    )
//                },
//                isSelected = state.currentParagraphStyle.textAlign == TextAlign.Center,
//                icon = Icons.Outlined.FormatAlignCenter
//            )
//        }
//
//        item {
//            RichTextStyleButton(
//                onClick = {
//                    state.addParagraphStyle(
//                        ParagraphStyle(
//                            textAlign = TextAlign.Right
//                        )
//                    )
//                },
//                isSelected = state.currentParagraphStyle.textAlign == TextAlign.Right,
//                icon = Icons.AutoMirrored.Outlined.FormatAlignRight
//            )
//        }

        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                isSelected = state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = Icons.Outlined.FormatBold
            )
        }

        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            fontStyle = FontStyle.Italic
                        )
                    )
                },
                isSelected = state.currentSpanStyle.fontStyle == FontStyle.Italic,
                icon = Icons.Outlined.FormatItalic
            )
        }

        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                },
                isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
                icon = Icons.Outlined.FormatUnderlined
            )
        }

//        item {
//            RichTextStyleButton(
//                onClick = {
//                    state.toggleSpanStyle(
//                        SpanStyle(
//                            textDecoration = TextDecoration.LineThrough
//                        )
//                    )
//                },
//                isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
//                icon = Icons.Outlined.FormatStrikethrough
//            )
//        }

        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            fontSize = 28.sp
                        )
                    )
                },
                isSelected = state.currentSpanStyle.fontSize == 28.sp,
                icon = Icons.Outlined.FormatSize
            )
        }

//        item {
//            RichTextStyleButton(
//                onClick = {
//                    state.toggleSpanStyle(
//                        SpanStyle(
//                            color = Color.Red
//                        )
//                    )
//                },
//                isSelected = state.currentSpanStyle.color == Color.Red,
//                icon = Icons.Filled.Circle,
//                tint = Color.Red
//            )
//        }
//
//        item {
//            RichTextStyleButton(
//                onClick = {
//                    state.toggleSpanStyle(
//                        SpanStyle(
//                            background = Color.Yellow
//                        )
//                    )
//                },
//                isSelected = state.currentSpanStyle.background == Color.Yellow,
//                icon = Icons.Outlined.Circle,
//                tint = Color.Yellow
//            )
//        }

        item {
            Box(
                Modifier
                    .height(24.dp)
                    .width(1.dp)
                    .background(Color(0xFF393B3D))
            )
        }

        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleUnorderedList()
                },
                isSelected = state.isUnorderedList,
                icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
            )
        }

        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleOrderedList()
                },
                isSelected = state.isOrderedList,
                icon = Icons.Outlined.FormatListNumbered,
            )
        }

        item {
            Box(
                Modifier
                    .height(24.dp)
                    .width(1.dp)
                    .background(Color(0xFF393B3D))
            )
        }

//        item {
//            RichTextStyleButton(
//                onClick = {
//                    state.addRichSpan(SpellCheck)
//                },
//                isSelected = state.currentRichSpanStyle is SpellCheck,
//                icon = Icons.Outlined.Spellcheck,
//            )
//        }
//
//        item {
//            RichTextStyleButton(
//                onClick = {
//                    state.toggleCodeSpan()
//                },
//                isSelected = state.isCodeSpan,
//                icon = Icons.Outlined.Code,
//            )
//        }
    }
}