package tech.kjo.kjo_mind_care.ui.register.ui

import android.graphics.drawable.Icon
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Preview
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit
) {

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Atrás",
                tint = Color.Black,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onNavigateBack() }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF90A4AE))
        ) {
            //Imagen
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "KJO Mind Care")
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Crear una cuenta", modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Unanse a nuestra comunidad de bienestar mental",
            color = Color.Gray,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text(text = "Nombre completo") },
            placeholder = { Text(text = "Ingrese su nombre completo") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Correo") },
            placeholder = { Text(text = "Ingrese su correo") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Constraseña") },
            placeholder = { Text(text = "Ingrese su contraseña") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp), shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(text = "Confirmar Contraseña") },
            placeholder = { Text(text = "Confirme su contraseña") },
            singleLine = true,
            visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = { confirmVisible = !confirmVisible }
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = if (confirmVisible) "Ocultar" else "Mostrar"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))


        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(text = "Iniciar Sesion")
        }

    }

}