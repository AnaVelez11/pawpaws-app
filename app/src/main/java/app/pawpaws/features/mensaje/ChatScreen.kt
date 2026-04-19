package app.pawpaws.features.mensaje

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pawpaws.R
import app.pawpaws.core.theme.AdminBgPage
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawGrayText
import app.pawpaws.core.theme.PawLinkBlue
import app.pawpaws.domain.model.models.BurbujaMensaje
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    onNavigateBack: () -> Unit,
    viewModel: MensajesViewModel
) {
    LaunchedEffect(chatId) { viewModel.abrirChat(chatId) }

    val chat         by viewModel.chatActual.collectAsState()
    val conversacion by viewModel.conversacion.collectAsState()
    var inputTexto   by remember { mutableStateOf("") }
    val listState    = rememberLazyListState()

    // Scroll al último mensaje al cargar o al enviar
    LaunchedEffect(conversacion.size) {
        if (conversacion.isNotEmpty()) {
            listState.animateScrollToItem(conversacion.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (chat?.fotoPerfil != null) {
                            AsyncImage(
                                model              = chat!!.fotoPerfil,
                                contentDescription = chat!!.nombre,
                                modifier           = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PawBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    chat?.nombre?.first()?.uppercase() ?: "?",
                                    color      = PawBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                chat?.nombre ?: "",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 15.sp,
                                color      = PawDarkText
                            )
                            Text(
                                stringResource(R.string.chat_online),
                                fontSize = 11.sp,
                                color    = Color(0xFF4CAF50)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.chat_back_description),
                            tint = PawDarkText
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Videocam, contentDescription = stringResource(R.string.chat_video_description), tint = PawDarkText)
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Phone, contentDescription = stringResource(R.string.chat_call_description), tint = PawDarkText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = AdminBgPage
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Mensajes
            LazyColumn(
                state          = listState,
                modifier       = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Separador de fecha
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.chat_today),
                            fontSize   = 11.sp,
                            color      = PawGrayText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                items(conversacion, key = { it.id }) { burbuja ->
                    BurbujaItem(burbuja = burbuja, fotoPerfil = chat?.fotoPerfil)
                }
            }

            // Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value         = inputTexto,
                    onValueChange = { inputTexto = it },
                    placeholder   = { Text(stringResource(R.string.chat_message_placeholder), color = PawGrayText, fontSize = 14.sp) },
                    modifier      = Modifier.weight(1f),
                    shape         = RoundedCornerShape(24.dp),
                    maxLines      = 4,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = PawBlue,
                        unfocusedBorderColor = Color(0xFFDDE3ED)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick  = {
                        viewModel.enviarMensaje(chatId, inputTexto)
                        inputTexto = ""
                    },
                    enabled  = inputTexto.isNotBlank(),
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (inputTexto.isNotBlank()) PawBlue else Color(0xFFDDE3ED))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(R.string.chat_send_description),
                        tint               = Color.White,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BurbujaItem(burbuja: BurbujaMensaje, fotoPerfil: String?) {
    Row(
        modifier            = Modifier.fillMaxWidth(),
        horizontalArrangement = if (burbuja.esMio) Arrangement.End else Arrangement.Start,
        verticalAlignment   = Alignment.Bottom
    ) {
        if (!burbuja.esMio) {
            if (fotoPerfil != null) {
                AsyncImage(
                    model              = fotoPerfil,
                    contentDescription = null,
                    modifier           = Modifier
                        .size(28.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(PawBlue.copy(alpha = 0.2f))
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Burbuja
        Column(
            horizontalAlignment = if (burbuja.esMio) Alignment.End else Alignment.Start,
            modifier            = Modifier.widthIn(max = 280.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart    = 18.dp,
                            topEnd      = 18.dp,
                            bottomStart = if (burbuja.esMio) 18.dp else 4.dp,
                            bottomEnd   = if (burbuja.esMio) 4.dp else 18.dp
                        )
                    )
                    .background(if (burbuja.esMio) PawBlue else Color.White)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text     = burbuja.texto,
                    fontSize = 14.sp,
                    color    = if (burbuja.esMio) Color.White else PawLinkBlue,
                    lineHeight = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text     = burbuja.hora,
                fontSize = 10.sp,
                color    = PawGrayText
            )
        }

        // Espaciado derecho para mensajes propios
        if (burbuja.esMio) {
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}