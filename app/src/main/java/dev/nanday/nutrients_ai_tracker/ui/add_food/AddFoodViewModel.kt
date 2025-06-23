package dev.nanday.nutrients_ai_tracker.ui.add_food

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.nanday.nutrients_ai_tracker.data.FoodRepository
import dev.nanday.nutrients_ai_tracker.data.OpenAiRepository
import dev.nanday.nutrients_ai_tracker.data.models.Food
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

interface IAddFoodViewModel {
    val foodName: String
    val selectedImageBitmap: Bitmap?
    val selectedImageUri: Uri?
    val energyKjInput: NumericInputState
    val energyKcalInput: NumericInputState
    val fatInput: NumericInputState
    val saturatedFatInput: NumericInputState
    val carbohydratesInput: NumericInputState
    val sugarsInput: NumericInputState
    val fiberInput: NumericInputState
    val proteinInput: NumericInputState
    val saltInput: NumericInputState

    fun onFoodNameChange(newName: String)
    fun onImageSelected(uri: Uri?, bitmap: Bitmap?)
    fun onNutritionalTablePhotoSelected(bytes: ByteArray)

    fun onEnergyKjChange(integer: String? = null, decimal: String? = null)
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
class AddFoodViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val foodRepository: FoodRepository,
    private val openAiRepository: OpenAiRepository
) : ViewModel(), IAddFoodViewModel {

    override var foodName by mutableStateOf("")
        private set

    override var selectedImageBitmap by mutableStateOf<Bitmap?>(null)
        private set
    override var selectedImageUri by mutableStateOf<Uri?>(null) // To hold URI from picker
        private set

    override var energyKjInput by mutableStateOf(NumericInputState())
        private set
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

    override fun onFoodNameChange(newName: String) {
        foodName = newName
    }

    override fun onImageSelected(uri: Uri?, bitmap: Bitmap?) {
        selectedImageUri = uri
        selectedImageBitmap = bitmap
    }

    override fun onNutritionalTablePhotoSelected(bytes: ByteArray) {
        viewModelScope.launch {
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)!!
            val response = openAiRepository.getFoodFromImage(base64)
            if (response != null) {
                onEnergyKjChange(
                    integer = response.energyKj.toInt().toString(),
                    decimal = ((response.energyKj * 10).toInt() % 10).toString()
                )

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

    // Specific updaters for each numeric field
    override fun onEnergyKjChange(integer: String?, decimal: String?) =
        onNumericInputChange(energyKjInput, { energyKjInput = it }, integer, decimal)

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
            val photoByteArray = selectedImageUri?.let { uri ->
                processAndCompressImage(uri)
            }

            // Validate and convert numeric inputs, handling potential nulls from toDouble()
            // For simplicity, we'll use 0.0 if a field is not filled,
            // but you might want more robust validation.
            val foodToSave = Food(
                // id will be generated by the database (autoGenerate = true)
                name = foodName.trim(),
                photo = photoByteArray,
                energyKj = energyKjInput.toDouble() ?: 0.0,
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
                foodRepository.insertFood(foodToSave)
                onSuccess()
            } catch (e: Exception) {
                // Log the exception e
                onError("Errore durante il salvataggio dell'alimento: ${e.message}")
            }
        }
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
