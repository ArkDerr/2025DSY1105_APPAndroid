package cl.daeriquelme.appduoc_profe.ui.register

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.daeriquelme.appduoc_profe.data.media.ImageCompressor
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarseScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    vm: RegistrarseViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()
    val ctx = LocalContext.current

    // ----- Galería -----
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // Comprimir desde el Uri de galería
            val file = runCatching {
                ImageCompressor.compressToTempFile(ctx, uri)
            }.getOrElse {
                Toast.makeText(ctx, "Error al procesar imagen: ${it.message}", Toast.LENGTH_SHORT).show()
                null
            }
            vm.onImagenFile(file)
        }
    }

    // ----- Cámara -----
    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { ok ->
        val u = pendingUri
        pendingUri = null
        if (!ok || u == null) {
            Toast.makeText(ctx, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        val file = runCatching {
            ImageCompressor.compressToTempFile(ctx, u)
        }.getOrElse {
            Toast.makeText(ctx, "Error al procesar imagen: ${it.message}", Toast.LENGTH_SHORT).show()
            null
        }
        vm.onImagenFile(file)
        Toast.makeText(ctx, "Foto agregada", Toast.LENGTH_SHORT).show()
    }

    fun createCameraUri(context: Context): Uri? {
        val name = "reg_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
        }
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        )
    }

    // Navegación cuando termina OK
    LaunchedEffect(ui.ok) { if (ui.ok) onRegistered() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } }
            )
        }
    ) { inner ->
        val scroll = rememberScrollState()

        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(scroll)          // permite desplazar la pantalla
                .imePadding()                    // evita que el teclado tape contenido
                .navigationBarsPadding()         // respeta las barras del sistema
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = ui.rut, onValueChange = vm::onRut,
                label = { Text("RUT") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ui.nombre, onValueChange = vm::onNombre,
                label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ui.rol, onValueChange = vm::onRol,
                label = { Text("Rol (id)") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ui.email, onValueChange = vm::onEmail,
                label = { Text("Email") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ui.password, onValueChange = vm::onPass,
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            // Imagen: cámara / galería
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        val dest = createCameraUri(ctx)
                        if (dest == null) {
                            Toast.makeText(ctx, "No se pudo crear destino de cámara", Toast.LENGTH_SHORT).show()
                            return@OutlinedButton
                        }
                        pendingUri = dest
                        takePictureLauncher.launch(dest)
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Tomar foto (cámara)") }

                OutlinedButton(
                    onClick = {
                        pickImage.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (ui.imagenFile == null) "Elegir de galería" else "Cambiar imagen")
                }
            }

            // Info de imagen seleccionada
            Text(
                text = ui.imagenFile?.let { "Imagen: ${it.length() / 1024} KB" } ?: "Sin imagen"
            )

            Button(
                onClick = vm::registrar,
                enabled = !ui.loading,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (ui.loading) "Guardando..." else "Registrar") }

            ui.msg?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
    }
}
