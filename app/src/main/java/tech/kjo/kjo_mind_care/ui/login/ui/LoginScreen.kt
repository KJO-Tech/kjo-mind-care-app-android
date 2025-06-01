package tech.kjo.kjo_mind_care.ui.login.ui

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import tech.kjo.kjo_mind_care.ui.theme.backgroundDark
import kotlin.math.sin

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF90A4AE))
        ) {
            //Image
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "KJO Mind Care"
        )
        Text(
            text = "Bienestar mental a su alcance",
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Bienvenido de nuevo", modifier = Modifier.fillMaxWidth())

        Text(
            text = "Inicie sesion para continuar con su cuenta",
            color = Color.Gray,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            placeholder = { Text(text = "Ingrese su correo") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Contraseña") },
            placeholder = { Text(text = "Ingrese su contraseña") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                    Icon(
//                        imageVector = Icons.Default.
//                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp)
        )
        Text(
            text = "¿Olvidaste tu Contraseña?",
            color = Color(0xFF388E3C),
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
                .clickable {}
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
//            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4E8E63))
        ) {
            Text(
                text = "Iniciar Sesion"
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "¿No tienes cuenta?")
            Text(
                text = "Registrate",
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }

}
