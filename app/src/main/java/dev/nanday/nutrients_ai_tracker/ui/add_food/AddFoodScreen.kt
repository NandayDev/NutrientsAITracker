package dev.nanday.nutrients_ai_tracker.ui.add_food

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    // Inject ViewModel using Hilt or pass it manually
    viewModel: IAddFoodViewModel = hiltViewModel<AddFoodViewModel>(), // Example using default ViewModel provider
    onFoodAdded: () -> Unit, // Callback when food is successfully added
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var productPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var nutritionTablePhotoUri by remember { mutableStateOf<Uri?>(null) }

    // --- Image Picker ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            val uri = productPhotoUri ?: return@rememberLauncherForActivityResult
            if (success) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                viewModel.onImageSelected(uri, bitmap)
            }
        }
    )

    val nutritionTablePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            val uri = nutritionTablePhotoUri ?: return@rememberLauncherForActivityResult
            if (success) {
                // Usa l'uri salvato
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: throw IllegalArgumentException("Impossibile leggere l'immagine")
                viewModel.onNutritionalTablePhotoSelected(bytes)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aggiungi Alimento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.saveFood(
                        onSuccess = {
                            // Potentially show a Snackbar or Toast here
                            onFoodAdded() // Navigate back or to another screen
                        },
                        onError = { errorMessage ->
                            // Show Snackbar or Toast with errorMessage
                            // For simplicity, printing to console for now
                            println("Error saving food: $errorMessage")
                            // You would typically show a Snackbar here
                        }
                    )
                },
                icon = { Icon(Icons.Filled.Save, "Salva alimento") }, // Choose an appropriate icon
                text = { Text("Salva") }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Food Name ---
            OutlinedTextField(
                value = viewModel.foodName,
                onValueChange = { viewModel.onFoodNameChange(it) },
                label = { Text("Nome alimento") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            // --- Image Selector ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        MaterialTheme.shapes.medium
                    )
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        val uri = createImageFileUri(context)
                        productPhotoUri = uri
                        photoPickerLauncher.launch(uri)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.selectedImageBitmap != null) {
                    Image(
                        bitmap = viewModel.selectedImageBitmap!!.asImageBitmap(),
                        contentDescription = "Foto alimento selezionata",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.PhotoCamera,
                            contentDescription = "Seleziona foto",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("Tocca per selezionare una foto", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp)) // Spacing after image picker

            IconButton(
                modifier = Modifier.width(150.dp),
                onClick = {
                    val uri = createImageFileUri(context)
                    nutritionTablePhotoUri = uri
                    nutritionTablePickerLauncher.launch(uri)
                }) {
                Row {
                    Icon(
                        Icons.Filled.PhotoCamera,
                        contentDescription = "Seleziona foto",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 10.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Decodifica tabella nutrizionale",
                        modifier = Modifier.weight(1F),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp)) // Spacing after image picker

            // --- Nutrient Fields ---
            NutrientInputRow("Energia (kJ)", viewModel.energyKjInput, viewModel::onEnergyKjChange)
            NutrientInputRow("Energia (kcal)", viewModel.energyKcalInput, viewModel::onEnergyKcalChange)
            NutrientInputRow("Grassi totali (g)", viewModel.fatInput, viewModel::onFatChange)
            NutrientInputRow("Grassi saturi (g)", viewModel.saturatedFatInput, viewModel::onSaturatedFatChange)
            NutrientInputRow("Carboidrati (g)", viewModel.carbohydratesInput, viewModel::onCarbohydratesChange)
            NutrientInputRow("Zuccheri (g)", viewModel.sugarsInput, viewModel::onSugarsChange)
            NutrientInputRow("Fibre (g)", viewModel.fiberInput, viewModel::onFiberChange)
            NutrientInputRow("Proteine (g)", viewModel.proteinInput, viewModel::onProteinChange)
            NutrientInputRow("Sale (g)", viewModel.saltInput, viewModel::onSaltChange)

            Spacer(Modifier.height(72.dp)) // Space for the FAB
        }
    }
}

@Composable
private fun NutrientInputRow(
    label: String,
    value: NumericInputState,
    onValueChange: (String?, String?) -> Unit // integerPart, decimalPart
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = value.integerPart,
                onValueChange = { onValueChange(it, null) },
                label = { Text("Intero") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.weight(2f),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
            )
            Text(
                ",", style = MaterialTheme.typography.headlineSmall, modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(bottom = 8.dp)
            ) // Align with TextField baseline
            OutlinedTextField(
                value = value.decimalPart,
                onValueChange = { onValueChange(null, it) },
                label = { Text("Decimale") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next // Consider ImeAction.Done for the last field
                ),
                modifier = Modifier.weight(1f),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
            )
        }
    }
}

private fun createImageFileUri(context: Context): Uri {
    val imageFile = File.createTempFile(
        "photo_", ".jpg",
        context.cacheDir
    ).apply { createNewFile() }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

@Preview
@Composable
private fun Preview() = AddFoodScreen(
    viewModel = object : IAddFoodViewModel {
        override val foodName: String = ""
        override val selectedImageBitmap: Bitmap? = null
        override val selectedImageUri: Uri? = null
        override val energyKjInput = NumericInputState()
        override val energyKcalInput = NumericInputState()
        override val fatInput = NumericInputState()
        override val saturatedFatInput = NumericInputState()
        override val carbohydratesInput = NumericInputState()
        override val sugarsInput = NumericInputState()
        override val fiberInput = NumericInputState()
        override val proteinInput = NumericInputState()
        override val saltInput = NumericInputState()

        override fun onFoodNameChange(newName: String) {
        }

        override fun onImageSelected(uri: Uri?, bitmap: Bitmap?) {
        }

        override fun onNutritionalTablePhotoSelected(bytes: ByteArray) {
        }

        override fun onEnergyKjChange(integer: String?, decimal: String?) {
        }

        override fun onEnergyKcalChange(integer: String?, decimal: String?) {
        }

        override fun onFatChange(integer: String?, decimal: String?) {
        }

        override fun onSaturatedFatChange(integer: String?, decimal: String?) {
        }

        override fun onCarbohydratesChange(integer: String?, decimal: String?) {
        }

        override fun onSugarsChange(integer: String?, decimal: String?) {
        }

        override fun onFiberChange(integer: String?, decimal: String?) {
        }

        override fun onProteinChange(integer: String?, decimal: String?) {
        }

        override fun onSaltChange(integer: String?, decimal: String?) {
        }

        override fun saveFood(onSuccess: () -> Unit, onError: (String) -> Unit) {
        }

    },
    onFoodAdded = {}
) { }