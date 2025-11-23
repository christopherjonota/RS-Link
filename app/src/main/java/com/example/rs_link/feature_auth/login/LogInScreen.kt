package com.example.rs_link.feature_auth.login

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.rs_link.R
import com.example.rs_link.feature_auth.registration.LabeledTextField

@Composable
fun ForgotPassword(onNavigateForgotPassword: () -> Unit){
    val listener: LinkInteractionListener = LinkInteractionListener{ link ->
        if (link is LinkAnnotation.Clickable){
            onNavigateForgotPassword
        }
    }

    Text(
        text = buildAnnotatedString {
            withLink(
                link = LinkAnnotation.Clickable(
                    linkInteractionListener = listener,
                    tag = "FORGOT_PASSWORD_TAG",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            textDecoration = TextDecoration.None,
                        )
                    ),

                    )
            ){ // <-- 2. The required trailing lambda (the 'block') for withLink starts here
                append("Forgot Password?") // <-- 3. This is the content inside the link
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
        textAlign = TextAlign.End,
        style = MaterialTheme.typography.labelMedium
    )
}

// This will be the login form that serve as the content inside the bottom sheet
@Composable
fun LoginForm(
   viewModel: SignInViewModel = hiltViewModel(),
   onLoginSuccess: () -> Unit,
   onNavigateToRegistration: () -> Unit,
    onClose: () -> Unit)
{
    val state by viewModel.uiState.collectAsState() // holds the value of the state e.g. email, password, etc.

    val emailRequester = remember { BringIntoViewRequester() }
    val passwordRequester = remember { BringIntoViewRequester() }

    // 1. Create state for the Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // 2. Watch for Errors and show Snackbar
    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            snackbarHostState.showSnackbar(
                message = state.errorMessage!!,
                actionLabel = "Dismiss" // Optional button on the snackbar
            )
            // 3. Tell ViewModel we showed it, so it clears the state
            viewModel.onErrorShown()
        }
    }
    // 1. Watch for Success
    LaunchedEffect(state.isLoginSuccess) {
        if (state.isLoginSuccess) {
            Log.e("haha","Success login")
            onLoginSuccess()
        }
    }
    // container of the login form
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to RS Link!",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Track. Alert. Ride with Confidence",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(24.dp))
        LabeledTextField(
            label = "Email",
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            placeholder = "Enter your email",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            errorText = state.emailError
        )
        LabeledTextField(
            label = "Password",
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = "Enter your password",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            errorText = state.passwordError
        )
        ForgotPassword {  }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = viewModel::login,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
            enabled = !state.isLoading
        ) {
            Text(
                text = "Log in",
                style = MaterialTheme.typography.labelLarge)
        }

        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "or",
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Button(
            onClick = onClose, // Trigger the lambda to close the sheet
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
            colors = ButtonDefaults.outlinedButtonColors(Color.Transparent),
        ) {
            Image(
                painter = painterResource(R.drawable.google_logo),
                contentDescription = "Google Logo",
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Sign up with Google",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}