package app.pawpaws.features.forgot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.pawpaws.R
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawOrange
import app.pawpaws.core.utils.RequestResult

@Composable
fun PasswordRecoveryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReset: () -> Unit,
    viewModel: PasswordRecoveryViewModel = viewModel()
) {
    val email by viewModel.email.collectAsState()
    val recoveryResult by viewModel.recoveryResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = recoveryResult is RequestResult.Loading

    // Reaccionar al resultado
    LaunchedEffect(recoveryResult) {
        when (val result = recoveryResult) {
            is RequestResult.Success -> {
                snackbarHostState.showSnackbar("Link de recuperación enviado")
                viewModel.resetResult()
                onNavigateToReset()
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

            // Banner superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(PawBlue),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bannerpaws),
                    contentDescription = "Banner PawPaws",
                    modifier = Modifier.height(35.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {

                // Flecha volver
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.passwordrecovery_back)
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))

                // Título
                Text(
                    text = stringResource(R.string.passwordrecovery_title),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PawDarkText,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Campo email
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text(stringResource(R.string.passwordrecovery_email_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    isError = email.error != null,
                    supportingText = {
                        email.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Botón enviar link
                Button(
                    onClick = { viewModel.sendRecoveryLink() },
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
                        Text(text = stringResource(R.string.passwordrecovery_send_button), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}