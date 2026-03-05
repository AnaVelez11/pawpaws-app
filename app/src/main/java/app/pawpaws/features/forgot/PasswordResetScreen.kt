package app.pawpaws.features.forgot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch

import app.pawpaws.R
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawOrange

@Composable
fun PasswordResetScreen(
    onNavigateBack: () -> Unit,
    onPasswordResetSuccess: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 5 dígitos
    var digit1 by remember { mutableStateOf("") }
    var digit2 by remember { mutableStateOf("") }
    var digit3 by remember { mutableStateOf("") }
    var digit4 by remember { mutableStateOf("") }
    var digit5 by remember { mutableStateOf("") }

    var newPassword by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            //Banner
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
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Restablecer contraseña",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = PawDarkText,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Ingresa el código de 5 dígitos enviado a tu correo",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                //Código OTP
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    listOf(
                        digit1 to { value: String -> digit1 = value },
                        digit2 to { value: String -> digit2 = value },
                        digit3 to { value: String -> digit3 = value },
                        digit4 to { value: String -> digit4 = value },
                        digit5 to { value: String -> digit5 = value }
                    ).forEach { (digit, onChange) ->

                        OutlinedTextField(
                            value = digit,
                            onValueChange = {
                                if (it.length <= 1 && it.all { char -> char.isDigit() }) {
                                    onChange(it)
                                }
                            },
                            modifier = Modifier.width(55.dp),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Nueva contraseña
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Botón confirmar
                Button(
                    onClick = {
                        scope.launch {

                            val fullCode = digit1 + digit2 + digit3 + digit4 + digit5

                            when {
                                fullCode.length != 5 -> {
                                    snackbarHostState.showSnackbar(
                                        "Ingresa el código completo"
                                    )
                                }

                                newPassword.length < 6 -> {
                                    snackbarHostState.showSnackbar(
                                        "La contraseña debe tener mínimo 6 caracteres"
                                    )
                                }

                                else -> {
                                    snackbarHostState.showSnackbar(
                                        "Contraseña actualizada correctamente"
                                    )
                                    onPasswordResetSuccess()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PawOrange
                    )
                ) {
                    Text("Confirmar cambio")
                }
            }
        }
    }
}