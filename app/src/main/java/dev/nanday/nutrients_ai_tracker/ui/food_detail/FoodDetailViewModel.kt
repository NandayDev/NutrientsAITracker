package dev.nanday.nutrients_ai_tracker.ui.food_detail

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.nanday.nutrients_ai_tracker.data.FoodRepository
import dev.nanday.nutrients_ai_tracker.data.OpenAiRepository
import dev.nanday.nutrients_ai_tracker.data.models.Food
import dev.nanday.nutrients_ai_tracker.data.remote.models.OpenAiFoodResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

interface IFoodDetailViewModel {
    val foodName: String
    val selectedImageBitmap: Bitmap?
    val selectedImageUri: Uri?
    val energyKcalInput: NumericInputState
    val fatInput: NumericInputState
    val saturatedFatInput: NumericInputState
    val carbohydratesInput: NumericInputState
    val sugarsInput: NumericInputState
    val fiberInput: NumericInputState
    val proteinInput: NumericInputState
    val saltInput: NumericInputState

    val isLoadingFromText: StateFlow<Boolean>
    val isLoadingFromPhoto: StateFlow<Boolean>

    fun onFoodNameChange(newName: String)
    fun onDownloadInfoFromAISelected()
    fun onImageSelected(uri: Uri, bitmap: Bitmap, byteArray: ByteArray? = null)
    fun onNutritionalTablePhotoSelected(bytes: ByteArray)

    fun onEnergyKcalChange(integer: String? = null, decimal: String? = null)
    fun onFatChange(integer: String? = null, decimal: String? = null)
    fun onSaturatedFatChange(integer: String? = null, decimal: String? = null)
    fun onCarbohydratesChange(integer: String? = null, decimal: String? = null)
    fun onSugarsChange(integer: String? = null, decimal: String? = null)
    fun onFiberChange(integer: String? = null, decimal: String? = null)
    fun onProteinChange(integer: String? = null, decimal: String? = null)
    fun onSaltChange(integer: String? = null, decimal: String? = null)

    fun saveFood(onSuccess: () -> Unit, onError: (String) -> Unit)
}

@HiltViewModel
class FoodDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val foodRepository: FoodRepository,
    private val openAiRepository: OpenAiRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), IFoodDetailViewModel {

    private val foodId = savedStateHandle.get<Long>("foodId")

    override var foodName by mutableStateOf("")
        private set

    override var selectedImageBitmap by mutableStateOf<Bitmap?>(null)
        private set
    override var selectedImageUri by mutableStateOf<Uri?>(null) // To hold URI from picker
        private set

    @Volatile
    private var selectedImageByteArray: ByteArray? = null

    override var energyKcalInput by mutableStateOf(NumericInputState())
        private set
    override var fatInput by mutableStateOf(NumericInputState())
        private set
    override var saturatedFatInput by mutableStateOf(NumericInputState())
        private set
    override var carbohydratesInput by mutableStateOf(NumericInputState())
        private set
    override var sugarsInput by mutableStateOf(NumericInputState())
        private set
    override var fiberInput by mutableStateOf(NumericInputState())
        private set
    override var proteinInput by mutableStateOf(NumericInputState())
        private set
    override var saltInput by mutableStateOf(NumericInputState()) // Assuming salt also has int/decimal
        private set

    override val isLoadingFromText = MutableStateFlow(false)
    override val isLoadingFromPhoto = MutableStateFlow(false)

    init {
        loadFoodFromDatabase()
    }

    override fun onFoodNameChange(newName: String) {
        foodName = newName
    }

    override fun onDownloadInfoFromAISelected() {
        viewModelScope.launch {
            isLoadingFromText.value = true
            val response = openAiRepository.getFoodFromText(foodName)
            updateFoodInfoFromOpenAiResponse(response)
            isLoadingFromText.value = false
        }
    }

    override fun onImageSelected(uri: Uri, bitmap: Bitmap, byteArray: ByteArray?) {
        selectedImageUri = uri
        selectedImageBitmap = bitmap
        if (byteArray == null) {
            viewModelScope.launch {
                selectedImageByteArray = processAndCompressImage(uri)
            }
        } else {
            selectedImageByteArray = byteArray
        }
    }

    override fun onNutritionalTablePhotoSelected(bytes: ByteArray) {
        viewModelScope.launch {
            isLoadingFromPhoto.value = true
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)!!
            val response = openAiRepository.getFoodFromImage(base64)
            updateFoodInfoFromOpenAiResponse(response)
            isLoadingFromPhoto.value = false
        }
    }

    override fun onEnergyKcalChange(integer: String?, decimal: String?) =
        onNumericInputChange(energyKcalInput, { energyKcalInput = it }, integer, decimal)

    override fun onFatChange(integer: String?, decimal: String?) =
        onNumericInputChange(fatInput, { fatInput = it }, integer, decimal)

    override fun onSaturatedFatChange(integer: String?, decimal: String?) =
        onNumericInputChange(saturatedFatInput, { saturatedFatInput = it }, integer, decimal)

    override fun onCarbohydratesChange(integer: String?, decimal: String?) =
        onNumericInputChange(carbohydratesInput, { carbohydratesInput = it }, integer, decimal)

    override fun onSugarsChange(integer: String?, decimal: String?) =
        onNumericInputChange(sugarsInput, { sugarsInput = it }, integer, decimal)

    override fun onFiberChange(integer: String?, decimal: String?) =
        onNumericInputChange(fiberInput, { fiberInput = it }, integer, decimal)

    override fun onProteinChange(integer: String?, decimal: String?) =
        onNumericInputChange(proteinInput, { proteinInput = it }, integer, decimal)

    override fun onSaltChange(integer: String?, decimal: String?) =
        onNumericInputChange(saltInput, { saltInput = it }, integer, decimal)


    override fun saveFood(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (foodName.isBlank()) {
            onError("Il nome dell'alimento è obbligatorio.")
            return
        }

        viewModelScope.launch {
            // Validate and convert numeric inputs, handling potential nulls from toDouble()
            // For simplicity, we'll use 0.0 if a field is not filled,
            // but you might want more robust validation.
            val foodToSave = Food(
                id = foodId ?: 0L,
                name = foodName.trim(),
                photo = selectedImageByteArray,
                energyKj = energyKcalInput.toDouble()?.let { it * 4184.0 } ?: 0.0,
                energyKcal = energyKcalInput.toDouble() ?: 0.0,
                fat = fatInput.toDouble() ?: 0.0,
                saturatedFat = saturatedFatInput.toDouble() ?: 0.0,
                carbohydrates = carbohydratesInput.toDouble() ?: 0.0,
                sugars = sugarsInput.toDouble() ?: 0.0,
                fiber = fiberInput.toDouble() ?: 0.0,
                protein = proteinInput.toDouble() ?: 0.0,
                salt = saltInput.toDouble() ?: 0.0
            )
            try {
                if (foodId == null) {
                    foodRepository.insertFood(foodToSave)
                } else {
                    foodRepository.updateFood(foodToSave)
                }
                onSuccess()
            } catch (e: Exception) {
                // Log the exception e
                onError("Errore durante il salvataggio dell'alimento: ${e.message}")
            }
        }
    }

    private fun updateFoodInfoFromOpenAiResponse(response: OpenAiFoodResponse?) {
        if (response != null) {
            onEnergyKcalChange(
                integer = response.energyKcal.toInt().toString(),
                decimal = ((response.energyKcal * 10).toInt() % 10).toString()
            )

            onFatChange(
                integer = response.fat.toInt().toString(),
                decimal = ((response.fat * 10).toInt() % 10).toString()
            )

            onSaturatedFatChange(
                integer = response.saturatedFat.toInt().toString(),
                decimal = ((response.saturatedFat * 10).toInt() % 10).toString()
            )

            onCarbohydratesChange(
                integer = response.carbohydrates.toInt().toString(),
                decimal = ((response.carbohydrates * 10).toInt() % 10).toString()
            )

            onSugarsChange(
                integer = response.sugars.toInt().toString(),
                decimal = ((response.sugars * 10).toInt() % 10).toString()
            )

            onFiberChange(
                integer = response.fiber.toInt().toString(),
                decimal = ((response.fiber * 10).toInt() % 10).toString()
            )

            onProteinChange(
                integer = response.protein.toInt().toString(),
                decimal = ((response.protein * 10).toInt() % 10).toString()
            )

            onSaltChange(
                integer = response.salt.toInt().toString(),
                decimal = ((response.salt * 10).toInt() % 10).toString()
            )
        }
    }

    private fun onNumericInputChange(
        currentValue: NumericInputState,
        onValueChange: (NumericInputState) -> Unit,
        newInteger: String? = null,
        newDecimal: String? = null
    ) {
        val intInput = newInteger?.filter { it.isDigit() } ?: currentValue.integerPart
        val decInput = newDecimal?.filter { it.isDigit() } ?: currentValue.decimalPart
        onValueChange(NumericInputState(intInput, decInput))
    }

    private fun loadFoodFromDatabase() {
        viewModelScope.launch {
            if (foodId != null) {
                val food = foodRepository.getFoodById(foodId)
                if (food != null) {
                    onFoodNameChange(food.name)
                    food.photo?.let { byteArray ->
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                        val uri = createTempFileForImage(bitmap)
                        onImageSelected(uri, bitmap, byteArray)
                    }
                    onEnergyKcalChange(
                        integer = food.energyKcal.toInt().toString(),
                        decimal = ((food.energyKcal * 10).toInt() % 10).toString()
                    )
                    onFatChange(
                        integer = food.fat.toInt().toString(),
                        decimal = ((food.fat * 10).toInt() % 10).toString()
                    )
                    onSaturatedFatChange(
                        integer = food.saturatedFat.toInt().toString(),
                        decimal = ((food.saturatedFat * 10).toInt() % 10).toString()
                    )
                    onCarbohydratesChange(
                        integer = food.carbohydrates.toInt().toString(),
                        decimal = ((food.carbohydrates * 10).toInt() % 10).toString()
                    )
                    onSugarsChange(
                        integer = food.sugars.toInt().toString(),
                        decimal = ((food.sugars * 10).toInt() % 10).toString()
                    )
                    onFiberChange(
                        integer = food.fiber.toInt().toString(),
                        decimal = ((food.fiber * 10).toInt() % 10).toString()
                    )
                    onProteinChange(
                        integer = food.protein.toInt().toString(),
                        decimal = ((food.protein * 10).toInt() % 10).toString()
                    )
                    onSaltChange(
                        integer = food.salt.toInt().toString(),
                        decimal = ((food.salt * 10).toInt() % 10).toString()
                    )
                }
            }
        }
    }

    private fun createTempFileForImage(bitmap: Bitmap): Uri {
        val tempFile = File.createTempFile("loaded_image_", ".jpg", context.cacheDir)
        val fos = FileOutputStream(tempFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    private suspend fun processAndCompressImage(imageUri: Uri): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    // 1. Decodifica il bitmap
                    var originalBitmap = BitmapFactory.decodeStream(inputStream)

                    // 2. Leggi l'orientamento EXIF e ruota il bitmap
                    context.contentResolver.openInputStream(imageUri)?.use { exifInputStream ->
                        originalBitmap = rotateImageIfRequired(originalBitmap, exifInputStream)
                    }

                    // 3. Ridimensiona (opzionale ma consigliato)
                    val scaledBitmap = scaleBitmap(originalBitmap, 800) // Esempio: larghezza max 800px

                    // 4. Comprimi
                    val outputStream = ByteArrayOutputStream()
                    var quality = 90
                    val maxSizeInBytes = 1 * 1024 * 1024 // 1MB

                    do {
                        outputStream.reset()
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                        if (outputStream.size() <= maxSizeInBytes || quality <= 10) {
                            break
                        }
                        quality -= 10
                    } while (quality > 0)

                    return@withContext outputStream.toByteArray()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // TODO: Gestisci l'eccezione
            }
            return@withContext null
        }
    }

    // Funzione helper per ridimensionare il Bitmap mantenendo le proporzioni
    private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int): Bitmap {
        if (bitmap.width <= maxWidth) {
            return bitmap // Non c'è bisogno di ridimensionare se è già più piccolo o uguale alla larghezza massima
        }
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val targetHeight = (maxWidth / aspectRatio).toInt()
        return Bitmap.createScaledBitmap(bitmap, maxWidth, targetHeight, true)
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(bitmap: Bitmap, inputStream: InputStream): Bitmap {
        val exifInterface = ExifInterface(inputStream)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            // Altri casi per flip orizzontale/verticale se necessario
            // ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1.0f, 1.0f)
            // ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1.0f, -1.0f)
            else -> return bitmap // Nessuna rotazione necessaria
        }

        return try {
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
            // Libera la memoria del bitmap originale se non è più necessario e diverso dal ruotato
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }
            rotatedBitmap
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            // Se c'è un OutOfMemoryError durante la creazione del bitmap ruotato,
            // restituisci il bitmap originale per evitare crash.
            // Potresti voler loggare questo errore.
            bitmap
        }
    }
}

// Helper data class to manage integer and decimal parts for numeric inputs
data class NumericInputState(
    val integerPart: String = "",
    val decimalPart: String = ""
) {
    fun toDouble(): Double? {
        return if (integerPart.isBlank() && decimalPart.isBlank()) {
            null // Or 0.0 if you prefer a default
        } else {
            "${integerPart.ifBlank { "0" }}.${decimalPart.ifBlank { "0" }}".toDoubleOrNull()
        }
    }
}
