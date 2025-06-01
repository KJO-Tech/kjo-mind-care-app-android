package tech.kjo.kjo_mind_care.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import tech.kjo.kjo_mind_care.R

@Composable
fun ThemedPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val textColor = MaterialTheme.colorScheme.onSurface
    val backgroundColor = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(
                text = label,
                color = if (passwordVisible) primaryColor else labelColor
            )
        },
        visualTransformation = if (passwordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            cursorColor = primaryColor,
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            focusedLabelColor = primaryColor,
            unfocusedLabelColor = labelColor
        ),
        trailingIcon = {
            val iconRes = if (passwordVisible) {
                R.drawable.ic_visibility
            } else {
                R.drawable.ic_eye_off
            }
            val contentDescription = if (passwordVisible) {
                stringResource(id = R.string.hide_password)
            } else {
                stringResource(id = R.string.show_password)
            }
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                tint = primaryColor,
                modifier = Modifier.clickable {
                    passwordVisible = !passwordVisible
                }
            )
        }
    )
}