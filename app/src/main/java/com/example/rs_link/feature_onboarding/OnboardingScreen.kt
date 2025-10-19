package com.example.rs_link.feature_onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class OnboardingSlide(val title: String, val description: String)
val slides = listOf(
    OnboardingSlide("Welcome to RSLink", "The ultimate platform for resource sharing and communication."),
    OnboardingSlide("Collaborate Safely", "Secure messaging and private group creation features."),
    OnboardingSlide("Ready to Go!", "Complete the sign-in step to unlock the dashboard.")
)

@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val currentPage = pagerState.currentPage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {

        // 1. Main Pager Area
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            // Use the SlideContent Composable for modular page design
            SlideContent(slide = slides[page])
        }

        // 2. Indicator Dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(slides.size) { iteration ->
                val color = if (currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(10.dp)
                        .background(color, MaterialTheme.shapes.extraSmall)
                )
            }
        }

        // 3. Action Button Logic
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            val isLastPage = currentPage == slides.size - 1
            Button(
                onClick = {
                    if (isLastPage) {
                        viewModel.completeOnboarding() // Trigger persistence & navigation
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(currentPage + 1)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLastPage) "Get Started!" else "Next")
            }
        }
    }
}

// --- Individual Page Content (The 'Slide Fragment' replacement) ---
@Composable
fun SlideContent(slide: OnboardingSlide) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Placeholder for an illustration/icon
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium)
        )
        Spacer(Modifier.height(32.dp))
        Text(slide.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text(slide.description, style = MaterialTheme.typography.bodyLarge)
    }
}


// ----------------------------------------------------
// THE PREVIEW FUNCTION STARTS HERE
// ----------------------------------------------------

@Preview(showBackground = true) // This annotation tells the IDE to render this function
@Composable
fun SlideContentPreview() {
    // IMPORTANT: You must wrap your content in your app's main theme
    // to see the correct colors, fonts, and shapes.
    // Example: YourAppNameTheme {
    SlideContent(
        slide = OnboardingSlide(
            title = "Collaborate Safely",
            description = "Secure messaging and private group creation features."
        )
    )
    // }
}