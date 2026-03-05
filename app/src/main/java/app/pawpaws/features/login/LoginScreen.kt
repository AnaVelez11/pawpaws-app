package app.pawpaws.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import app.pawpaws.R
import app.pawpaws.core.theme.PawOrange
import app.pawpaws.core.theme.PawGrayText
import app.pawpaws.core.theme.PawLinkBlue
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.viewmodel.compose.viewModel
import app.pawpaws.core.utils.RequestResult


@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel : LoginViewModel = viewModel()
) {

    val emailState by viewModel.email.collectAsState()
    val passwordState by viewModel.password.collectAsState()
    val loginResult by viewModel.loginResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginResult) {

        when (loginResult) {

            is RequestResult.Success -> {
                onLoginSuccess()
            }

            is RequestResult.Error -> {
                snackbarHostState.showSnackbar(
                    (loginResult as RequestResult.Error).message
                )
            }

            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            Image(
                painter = painterResource(id = R.drawable.logopaws),
                contentDescription = "Logo",
                modifier = Modifier.size(220.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                isError = emailState.error != null,
            )
            emailState.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = passwordState.error != null
            )
            passwordState.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            ClickableText(
                modifier = Modifier.align(Alignment.End),
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = PawLinkBlue,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("¿Olvidaste tu contraseña?")
                    }
                },
                onClick = {
                    onNavigateToForgotPassword()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.login()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PawOrange
                )
            ) {
                Text(
                    text = "Iniciar sesión   ➜",
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "--------¿Nuevo en PawPaws?---------",
                fontSize = 14.sp,
                color = PawGrayText
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = { onNavigateToRegister() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PawOrange
                )
            ) {
                Text(
                    text = "Crear cuenta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,

                    )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Texto legal
            Text(
                text = "Al continuar, acepta nuestros Términos de Servicio y Política de Privacidad",
                fontSize = 12.sp,
                color = PawGrayText,
                textAlign = TextAlign.Center
            )
        }
    }
}