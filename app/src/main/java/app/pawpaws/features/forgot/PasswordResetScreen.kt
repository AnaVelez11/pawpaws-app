package app.pawpaws.features.forgot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun PasswordResetScreen(
    onNavigateBack: () -> Unit,
    onPasswordResetSuccess: () -> Unit,
    viewModel: PasswordResetViewModel = viewModel()
) {
    val digit1 by viewModel.digit1.collectAsState()
    val digit2 by viewModel.digit2.collectAsState()
    val digit3 by viewModel.digit3.collectAsState()
    val digit4 by viewModel.digit4.collectAsState()
    val digit5 by viewModel.digit5.collectAsState()
    val newPassword by viewModel.newPassword.collectAsState()
    val resetResult by viewModel.resetResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = resetResult is RequestResult.Loading

    // Reaccionar al resultado
    LaunchedEffect(resetResult) {
        when (val result = resetResult) {
            is RequestResult.Success -> {
                snackbarHostState.showSnackbar("Contraseña actualizada correctamente")
                viewModel.resetResult()
                onPasswordResetSuccess()
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

            // Banner
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

                // Flecha
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.passwordreset_back)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.passwordreset_title),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = PawDarkText,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(R.string.passwordreset_description),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Código OTP — 5 cajas
                val digits = listOf(digit1, digit2, digit3, digit4, digit5)

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        digits.forEachIndexed { index, field ->
                            OutlinedTextField(
                                value = field.value,
                                onValueChange = { viewModel.onDigitChange(index, it) },
                                modifier = Modifier.width(55.dp),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                shape = RoundedCornerShape(8.dp),
                                isError = index == 0 && digit1.error != null,
                                enabled = !isLoading
                            )
                        }
                    }
                    // Error del código (se almacena en digit1)
                    digit1.error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Nueva contraseña
                OutlinedTextField(
                    value = newPassword.value,
                    onValueChange = { viewModel.onNewPasswordChange(it) },
                    label = { Text(stringResource(R.string.passwordreset_new_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    isError = newPassword.error != null,
                    supportingText = {
                        newPassword.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Botón confirmar
                Button(
                    onClick = { viewModel.confirmReset() },
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
                        Text(stringResource(R.string.passwordreset_confirm_button))
                    }
                }
            }
        }
    }
}