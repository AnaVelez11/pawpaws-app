package app.pawpaws.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pawpaws.R
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawOrange
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawGrayText


@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
        ) {

            // Fondo azul
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PawBlue)
            )

            // Logo centrado
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.home_logo_description),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(400.dp),
                contentScale = ContentScale.Fit
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .align(Alignment.BottomCenter)
            ) {

                val path = Path().apply {

                    moveTo(0f, size.height * 0.3f)

                    cubicTo(
                        size.width * 0.25f,
                        size.height * 1.2f,
                        size.width * 0.75f,
                        size.height * 0.2f,
                        size.width,
                        size.height * 0.8f
                    )

                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }

                drawPath(
                    path = path,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column {

                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = stringResource(R.string.home_welcome),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = PawDarkText
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.home_description),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PawGrayText
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Button(
                    onClick = { onNavigateToLogin() },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PawOrange
                    ),
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = stringResource(R.string.home_arrow_description),
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}