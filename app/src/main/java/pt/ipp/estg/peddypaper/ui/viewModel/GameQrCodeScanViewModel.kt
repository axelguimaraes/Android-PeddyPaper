package pt.ipp.estg.peddypaper.ui.viewModel

import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.barcode.*
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.ipp.estg.peddypaper.data.remote.firebase.Game
import pt.ipp.estg.peddypaper.data.remote.firebase.Question
import java.util.regex.Pattern

class GameQrCodeScanViewModel(application: Application) : AndroidViewModel(application) {

    // Firebase
    val db: FirebaseFirestore
    val collectionName: String

    // LiveData
    private var _code: MutableLiveData<String>
    private var _scanCode: MutableLiveData<String>
    private var _isScanned: MutableLiveData<Boolean>
    private var _manualCode: MutableLiveData<String>
    private var _check: MutableLiveData<String>

    private var _game: MutableLiveData<Game?>

    init {
        db = Firebase.firestore
        collectionName = "QUESTIONS"

        _code = MutableLiveData()
        _scanCode = MutableLiveData()
        _isScanned = MutableLiveData(false)
        _manualCode = MutableLiveData()
        _check = MutableLiveData("Reading")

        _game = MutableLiveData()
    }

    val code: LiveData<String> = _code
    val check: LiveData<String> = _check
    val manualCode: LiveData<String> = _manualCode
    val isScanned: LiveData<Boolean> = _isScanned
    val scanCode: LiveData<String> = _scanCode

    fun setScanCode(code: String) {
        _scanCode.postValue(code)
        _isScanned.value = true
        _code.postValue(code)

        _check.postValue("Scanned")
    }

    fun setManualCode(code: String) {
        _manualCode.postValue(code)
        _code.postValue(code)
    }

    fun checkCode() {
        val code = _code.value ?: ""
        if (code.isEmpty()) {
            _check.value = "Reading"
            return
        }

        // Regex para verificar se o código é alfanumérico
        val regex = "^[a-zA-Z0-9]*$"
        if (!Pattern.matches(regex, code)) {
            _check.value = "Error: Invalid Code Format"
            return
        }

        viewModelScope.launch {
            // Verifica se existe na coleção QUESTIONS
            val questionExists = db.collection(collectionName)
                .document(code)
                .get()
                .await()
                .exists()

            Log.e(TAG, "checkCode: $questionExists")

            if (!questionExists) {
                _check.value = "Error: Question Not Found"
                return@launch
            }

            // Verifica se a pergunta pertence ao jogo em questão
            val game = _game.value
            val existsInGame = game?.questions?.any { it.id == code } == true
            if (!existsInGame) {
                _check.value = "Error: Question Not in Game"
                return@launch
            }

            // Verifica se a pergunta já foi respondida
            val isAnswered = checkIfQuestionIsAnswered(code)
            if (!isAnswered) {
                _check.value = "Error: Question Already Answered"
                return@launch
            }

            _check.value = "Verified"
        }
    }

    private suspend fun checkIfQuestionIsAnswered(code: String): Boolean {
       var isAnswered = false

        db.collection("QUESTIONS")
            .document(code)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    try {
                        val question = document.toObject(Question::class.java)
                        isAnswered = question?.awnsred == true
                    } catch (e: Exception) {
                        Log.w(TAG, "Erro na desserialização: ", e)
                    }
                } else {
                    // Trate o caso em que o documento não é encontrado
                    Log.w(TAG, "Document not found")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
            .await()

        return isAnswered
    }

    fun resetScanCode() {
        _scanCode = MutableLiveData()
        _isScanned.postValue(false)
        _code = MutableLiveData()
    }

    fun resetManualCode() {
        _manualCode = MutableLiveData()
        _code = MutableLiveData()
    }

    fun resetCheck() {
        _check.postValue("Reading")

        resetManualCode()
        resetScanCode()
    }


    fun startCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
    ) {
        val cameraController = LifecycleCameraController(context)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val barcodeScanner = BarcodeScanning.getClient(options)

        val mlKitAnalyzer = MlKitAnalyzer(
            listOf(barcodeScanner),
            CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
            ContextCompat.getMainExecutor(context)
        ) { result ->

            val barcodeResult = result?.getValue(barcodeScanner)
            val scannedBarcodeValue = barcodeResult?.firstOrNull()?.rawValue ?: ""
            if(scannedBarcodeValue.isNotEmpty()){
                setScanCode(scannedBarcodeValue)
                cameraController.unbind()
            }
        }

        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(context),
            mlKitAnalyzer
        )

        cameraController.bindToLifecycle(lifecycleOwner)
        previewView.controller = cameraController
    }

    fun loadGame(gameId: String){
        viewModelScope.launch {
            val game = db.collection("GAMES").document(gameId).get().await().toObject(Game::class.java)
            _game.postValue(game)
        }
    }

}

