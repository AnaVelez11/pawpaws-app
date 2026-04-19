package app.pawpaws.features.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.pawpaws.R
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawLinkBlue
import app.pawpaws.core.theme.PawOrange
import app.pawpaws.core.utils.RequestResult
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val nameState by viewModel.name.collectAsState()
    val emailState by viewModel.email.collectAsState()
    val passwordState by viewModel.password.collectAsState()
    val confirmPasswordState by viewModel.confirmPassword.collectAsState()
    val cityState by viewModel.city.collectAsState()
    val registerResult by viewModel.registerResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val locationNotImplementedMsg = stringResource(R.string.register_location_not_implemented)

    LaunchedEffect(registerResult) {
        when (registerResult) {
            is RequestResult.Success -> {
                snackbarHostState.showSnackbar("Cuenta creada correctamente")
                onRegisterSuccess()
            }
            is RequestResult.Error -> {
                snackbarHostState.showSnackbar(
                    (registerResult as RequestResult.Error).message
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
                    contentDescription = stringResource(R.string.register_cd_banner),
                    modifier = Modifier.height(35.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                IconButton(
                    onClick = { onNavigateBack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.register_cd_back)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = stringResource(R.string.register_title),
                    fontSize = 32.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = PawDarkText,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Nombre
                OutlinedTextField(
                    value = nameState.value,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = { Text(stringResource(R.string.register_label_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameState.error != null,
                    supportingText = {
                        nameState.error?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Email
                OutlinedTextField(
                    value = emailState.value,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text(stringResource(R.string.register_label_email)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailState.error != null,
                    supportingText = {
                        emailState.error?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Password
                OutlinedTextField(
                    value = passwordState.value,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text(stringResource(R.string.register_label_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordState.error != null,
                    supportingText = {
                        passwordState.error?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Confirm Password
                OutlinedTextField(
                    value = confirmPasswordState.value,
                    onValueChange = { viewModel.onConfirmPasswordChange(it) },
                    label = { Text(stringResource(R.string.register_label_confirm_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPasswordState.error != null,
                    supportingText = {
                        confirmPasswordState.error?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Ciudad o código postal
                OutlinedTextField(
                    value = cityState.value,
                    onValueChange = { viewModel.onCityChange(it) },
                    label = { Text(stringResource(R.string.register_label_city)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Link ubicación actual
                Text(
                    text = stringResource(R.string.register_link_location),
                    color = PawLinkBlue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch {
                                snackbarHostState.showSnackbar(locationNotImplementedMsg)
                            }
                        }
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Botón principal naranja
                val scope = rememberCoroutineScope()

                Button(
                    onClick = { viewModel.register() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PawOrange
                    )
                ) {
                    Text(
                        text = stringResource(R.string.register_button_text),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
