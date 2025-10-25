package com.siagajiwa.siagajiwaid.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.siagajiwa.siagajiwaid.R
import com.siagajiwa.siagajiwaid.ui.theme.DarkLight
import com.siagajiwa.siagajiwaid.ui.theme.Primary
import com.siagajiwa.siagajiwaid.ui.theme.White
import com.siagajiwa.siagajiwaid.viewmodel.MediaType
import com.siagajiwa.siagajiwaid.viewmodel.MediaUiState
import com.siagajiwa.siagajiwaid.viewmodel.MediaViewModel

@Composable
fun StressManagementMaterialScreen(
    navController: NavHostController,
    viewModel: MediaViewModel = viewModel()
) {
    val mediaState by viewModel.stressMediaState.collectAsState()

    // Load media when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadStressMedia()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar Spacer
            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Bar
            StressNavigationBar(
                title = "Manajemen Stres",
                onBackClick = { navController.popBackStack() }
            )

            // Content based on state
            when (val state = mediaState) {
                is MediaUiState.Loading -> {
                    LoadingContent()
                }
                is MediaUiState.Success -> {
                    // Content - Vertical Scrollable Images from Supabase
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Add top spacing
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Display images from Supabase (ordered by 'order' column)
                        items(state.media) { mediaContent ->
                            AsyncImage(
                                model = mediaContent.link,
                                contentDescription = "Stress Management Material ${mediaContent.order}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(276f / 390f),
                                contentScale = ContentScale.Fit,
                                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                                error = painterResource(id = R.drawable.ic_launcher_foreground)
                            )
                        }

                        // Add bottom spacing
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
                is MediaUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.retryLoading(MediaType.STRESS) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StressNavigationBar(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onBackClick() },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow),
                contentDescription = "Back",
                tint = DarkLight,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkLight,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Primary,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Memuat konten...",
                fontSize = 16.sp,
                color = DarkLight
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 48.dp)
        ) {
            Text(
                text = "Gagal memuat konten",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkLight
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = DarkLight.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    text = "Coba Lagi",
                    color = White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StressManagementMaterialScreenPreview() {
    StressManagementMaterialScreen(navController = rememberNavController())
}
