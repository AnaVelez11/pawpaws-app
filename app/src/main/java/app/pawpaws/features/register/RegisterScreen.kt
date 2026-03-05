package app.pawpaws.features.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import app.pawpaws.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ArrowBack
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawLinkBlue
import app.pawpaws.core.theme.PawOrange
import app.pawpaws.core.utils.RequestResult

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

                IconButton(
                    onClick = { onNavigateBack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))


                Text(
                    text = "Crea tu cuenta",
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
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameState.error != null
                )

                nameState.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Email
                OutlinedTextField(
                    value = emailState.value,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailState.error != null
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Password
                OutlinedTextField(
                    value = passwordState.value,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordState.error != null
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Confirm Password
                OutlinedTextField(
                    value = confirmPasswordState.value,
                    onValueChange = { viewModel.onConfirmPasswordChange(it) },
                    label = { Text("Confirmar contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPasswordState.error != null
                )

                Spacer(modifier = Modifier.height(6.dp))

                //Ciudad o código postal
                OutlinedTextField(
                    value = cityState.value,
                    onValueChange = { viewModel.onCityChange(it) },
                    label = { Text("Busca tu ciudad o código postal") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                //Link ubicación actual
                Text(
                    text = "Usar mi ubicación actual",
                    color = PawLinkBlue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Función de ubicación no implementada aún"
                                )
                            }
                        }
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Botón principal naranja
                val scope = rememberCoroutineScope()

                Button(
                    onClick = {
                        viewModel.register()
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
                        text = "Crear cuenta   ➜",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}