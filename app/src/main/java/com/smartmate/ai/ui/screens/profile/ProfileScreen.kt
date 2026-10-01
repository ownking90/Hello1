package com.smartmate.ai.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartmate.ai.R
import com.smartmate.ai.data.AppContainer

/**
 * Combined profile/account screen: shows sign-in forms when logged out, and
 * account details + logout + settings entry point when logged in.
 */
@Composable
fun ProfileScreen(container: AppContainer, onOpenSettings: () -> Unit) {
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory(container))
    val state by viewModel.uiState.collectAsState()
    var isSignUpMode by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (state.user != null) {
            Text(stringResource(R.string.signed_in_as), style = MaterialTheme.typography.titleMedium)
            Text(state.user?.displayName ?: state.user?.email ?: stringResource(R.string.default_user_label))
            state.user?.email?.let { Text(it) }
            Button(onClick = onOpenSettings) { Text(stringResource(R.string.settings)) }
            OutlinedButton(onClick = viewModel::logout) { Text(stringResource(R.string.logout)) }
        } else {
            var name by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            Text(stringResource(if (isSignUpMode) R.string.sign_up else R.string.log_in), style = MaterialTheme.typography.headlineMedium)

            if (!state.isCloudAuthAvailable) {
                Text(stringResource(R.string.cloud_sign_in_unavailable))
            }

            if (isSignUpMode) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.full_name)) }, modifier = Modifier.fillMaxWidth())
            }
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.email)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            state.errorMessage?.let { Text(it) }

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(onClick = {
                    if (isSignUpMode) viewModel.signUp(name, email, password) else viewModel.login(email, password)
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(if (isSignUpMode) R.string.sign_up else R.string.log_in))
                }
                OutlinedButton(
                    onClick = { viewModel.loginWithGoogle("demo-id-token") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.continue_with_google)) }
                TextButton(onClick = { isSignUpMode = !isSignUpMode }) {
                    Text(if (isSignUpMode) "Already have an account? Log In" else "New here? Sign Up")
                }
            }
        }
    }
}
