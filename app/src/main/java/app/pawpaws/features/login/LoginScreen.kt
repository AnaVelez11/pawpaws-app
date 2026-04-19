package app.pawpaws.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.pawpaws.R
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawLinkBlue
import app.pawpaws.core.theme.PawOrange
import app.pawpaws.core.utils.RequestResult
import app.pawpaws.domain.model.enums.Rol

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: (Rol) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val emailState    by viewModel.email.collectAsState()
    val passwordState by viewModel.password.collectAsState()
    val loginResult   by viewModel.loginResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = loginResult is RequestResult.Loading

    LaunchedEffect(loginResult) {
        when (val result = loginResult) {
            is RequestResult.Success -> {
                onLoginSuccess(result.data)
                viewModel.resetResult()
            }
            is RequestResult.Error -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetResult()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(PawBlue),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bannerpaws),
                    contentDescription = stringResource(R.string.login_banner_description),
                    modifier = Modifier.height(35.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = stringResource(R.string.login_title),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = PawDarkText,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = emailState.value,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text(stringResource(R.string.login_email_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    isError = emailState.error != null,
                    supportingText = {
                        emailState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = passwordState.value,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text(stringResource(R.string.login_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    isError = passwordState.error != null,
                    supportingText = {
                        passwordState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.login_forgot_password),
                    color = PawLinkBlue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToForgotPassword() },
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PawOrange),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.login_button_text), fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.login_no_account), color = PawDarkText)
                    Text(
                        text = stringResource(R.string.login_register),
                        color = PawLinkBlue,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { onNavigateToRegister() }
                    )
                }
            }
        }
    }
}