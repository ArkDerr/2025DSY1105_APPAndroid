package cl.daeriquelme.appduoc_profe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.tooling.preview.Preview
import cl.daeriquelme.appduoc_profe.ui.theme.AppDuoc_ProfeTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun oldPrincipalScreenold2(
    onLogout: () -> Unit = {}
) {
    val user = FirebaseAuth.getInstance().currentUser
    val saludo = "Hola ${user?.email ?: "usuario"}"

    // Estado para controlar el menú desplegable
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Principal") },
                actions = {
                    // Botón de tres puntitos
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menú")
                    }

                    // DropdownMenu desplegable
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Perfil") },
                            onClick = {
                                expanded = false
                                // Aquí podrías navegar a una pantalla de Perfil
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Info, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Configuración") },
                            onClick = {
                                expanded = false
                                // Aquí podrías navegar a Configuración
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Settings, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                expanded = false
                                FirebaseAuth.getInstance().signOut()
                                onLogout()
                            }
                        )
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(saludo, style = MaterialTheme.typography.headlineSmall)
            Text("Bienvenido a tu pantalla principal.")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun oldPrincipalScreenold2Preview() {
    AppDuoc_ProfeTheme {
        oldPrincipalScreenold2(onLogout = {})
    }
}
