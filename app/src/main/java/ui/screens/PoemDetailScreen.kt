package com.kavyakanaja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kavyakanaja.app.AppLanguage
import com.kavyakanaja.app.LanguageManager
import com.kavyakanaja.app.data.model.Poem
import com.kavyakanaja.app.data.model.WordMeaning
import com.kavyakanaja.app.viewmodel.PoemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoemDetailScreen(
    poemId: Int,
    navController: NavController,
    viewModel: PoemViewModel = hiltViewModel(),
    languageManager: LanguageManager
) {
    val uiState by viewModel.uiState.collectAsState()
    val language by languageManager.language.collectAsState()
    val poem = viewModel.getPoemById(poemId) ?: return

    LaunchedEffect(poem) {
        viewModel.selectPoem(poem)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(poem.titleKannada, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleBookmark(poem) }) {
                        val isBookmarked = uiState.bookmarks.any { it.poemId == poem.id }
                        Icon(
                            if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(poem.poet, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text(poem.era, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)

            Spacer(Modifier.height(24.dp))

            TappableVerse(
                poem = poem,
                showKannada = language == AppLanguage.KANNADA,
                onWordTap = { viewModel.selectWord(it) }
            )

            Spacer(Modifier.height(24.dp))

            AudioPlayerCard(
                isPlaying = uiState.isAudioPlaying,
                onToggle = { viewModel.toggleAudio(poem) }
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = if (language == AppLanguage.KANNADA) "ಭಾವಾರ್ಥ" else "Meaning",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (language == AppLanguage.KANNADA) poem.bhavartha else poem.verseEnglish,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(32.dp))
        }
    }

    uiState.selectedWord?.let { word ->
        WordMeaningBottomSheet(
            word = word,
            onDismiss = { viewModel.selectWord(null) }
        )
    }
}

@Composable
fun TappableVerse(
    poem: Poem,
    showKannada: Boolean,
    onWordTap: (WordMeaning) -> Unit
) {
    val text = if (showKannada) poem.verse else poem.verseEnglish
    val words = text.split(" ")
    val annotated = buildAnnotatedString {
        words.forEachIndexed { i, word ->
            val meaning = poem.wordMeanings.find {
                word.contains(it.word, ignoreCase = true)
            }
            if (meaning != null) {
                pushStringAnnotation("WORD", meaning.word)
                withStyle(SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )) { append(word) }
                pop()
            } else {
                append(word)
            }
            if (i < words.size - 1) append(" ")
        }
    }
    
    @Suppress("DEPRECATION")
    ClickableText(
        text = annotated,
        style = MaterialTheme.typography.headlineSmall.copy(
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        onClick = { offset ->
            annotated.getStringAnnotations("WORD", offset, offset).firstOrNull()?.let { ann ->
                poem.wordMeanings.find { it.word == ann.item }?.let { onWordTap(it) }
            }
        }
    )
}

@Composable
fun AudioPlayerCard(
    isPlaying: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FilledIconButton(
                onClick = onToggle,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Stop" else "Play",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Column {
                Text(
                    if (isPlaying) "ಕೇಳಿಸುತ್ತಿದೆ..." else "ಕವಿತೆ ಕೇಳಿ",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    if (isPlaying) "Tap to stop recitation"
                    else "Tap to hear recitation",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            if (isPlaying) {
                Spacer(Modifier.weight(1f))
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordMeaningBottomSheet(word: WordMeaning, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(24.dp).padding(bottom = 32.dp)) {
            Text(word.word, style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary)
            Text(word.transliteration, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(16.dp))
            InfoRow("Meaning", word.meaning)
            InfoRow("English Equivalent", word.englishEquivalent)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text("$label: ", style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
