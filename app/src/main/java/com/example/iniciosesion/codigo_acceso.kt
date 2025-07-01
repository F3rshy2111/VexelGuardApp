package com.example.iniciosesion

//Libreria generadora de codigos QR
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.iniciosesion.com.example.iniciosesion.roleManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.EnumMap
import kotlin.math.floor
import java.lang.System.currentTimeMillis
import java.nio.ByteBuffer
import java.security.GeneralSecurityException
import java.util.concurrent.Executor
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow



// Duración total del contador en milisegundos (por ejemplo, 10 segundos)
private val COUNTDOWN_TIME_MILLIS: Long = 30000 // 10 segundos
// Intervalo de actualización del contador en milisegundos (por ejemplo, cada segundo)
private val COUNTDOWN_INTERVAL_MILLIS: Long = 1000 // 1 segundo

class codigo_acceso :  navDrawer() {

    private val db = Firebase.firestore

    //private lateinit var etInput: EditText
    private lateinit var btnGenerate: Button
    private lateinit var ivQRCode: ImageView
    private lateinit var countdownTextView: TextView
    private lateinit var countDownTimer: CountDownTimer

    // Definimos los colores aquí
    private val ENABLED_BACKGROUND_COLOR = Color.parseColor("#00000000") // Púrpura Material Design
    private val DISABLED_BACKGROUND_COLOR = Color.parseColor("#00000000") // Gris claro
    private val ENABLED_TEXT_COLOR = Color.parseColor("#001B2E") // Blanco
    private val DISABLED_TEXT_COLOR = Color.parseColor("#D3D3D3") // Gris oscuro

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefsManager = SharedPreferencesManager(this)
        val obtenerPermisos = obtener_permisos(this)
        val userId = prefsManager.getUserId()
        val master = prefsManager.getMasterTOTP()
        val expireAt = prefsManager.getCodigoExpiraEn()
        val tiempoRestante = expireAt - System.currentTimeMillis()

        btnGenerate = findViewById(R.id.btnGenerar)
        ivQRCode = findViewById(R.id.ivQR)
        countdownTextView = findViewById(R.id.tv_countdown)

        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)

                if (userId != null && master != null) {
                    btnGenerate.isEnabled = false
                    updateButtonColors()
                    startCountdownTimer()
                    val expiraEn = System.currentTimeMillis() + COUNTDOWN_TIME_MILLIS
                    SharedPreferencesManager(applicationContext).setCodigoExpiraEn(expiraEn)
                    Log.d("OTP",master)
                    val otpDecod = master.let { it1 -> generateTotpCode(it1) }
                    SharedPreferencesManager(applicationContext).setOtpTemporal(otpDecod ?: "")
                    mostrarQR("${userId}//${otpDecod}")
                } else {
                    Toast.makeText(applicationContext, "ID o TOTP inválidos", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, "Error: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Huella no reconocida", Toast.LENGTH_SHORT).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Verificación biométrica")
            .setSubtitle("Confirma tu identidad para continuar")
            .setNegativeButtonText("Cancelar")
            .build()

        btnGenerate.setOnClickListener {
            val permisoHorario = obtenerPermisos.permisoConcedido()
            val roleManager = roleManager(this)
            val rol = (roleManager.isAdmin() || roleManager.isInvitado())

            if(userId != null && permisoHorario && rol){
                if (canAuthenticate()) {
                    biometricPrompt.authenticate(promptInfo)
                } else {
                    Toast.makeText(this, "La autenticación biométrica no está disponible", Toast.LENGTH_LONG).show()
                }
            } else{
                Toast.makeText(this,"Código no generado. Inténtelo mas tarde.",Toast.LENGTH_SHORT).show()
            }
        }

        if (tiempoRestante > 0) {
            btnGenerate.isEnabled = false
            updateButtonColors()
            startCountdownTimer(tiempoRestante)

            val otpGuardado = prefsManager.getOtpTemporal()
            if (otpGuardado != null) {
                mostrarQR("${userId}//${otpGuardado}")
            } else {
                // Si por algún motivo el OTP no está, puedes invalidar
                prefsManager.clearCodigoExpiraEn()
                countdownTextView.text = "00:00"
                btnGenerate.isEnabled = true
                updateButtonColors()
            }
        } else {
            prefsManager.clearCodigoExpiraEn()
            countdownTextView.text = "00:00"
            btnGenerate.isEnabled = true
            updateButtonColors()
        }
    }

    // Proporciona el ID del layout específico de HomeActivity
    override fun getLayoutResId(): Int {
        return R.layout.activity_codigo_acceso // Asegúrate de tener este layout
    }

    private fun generarQR(text: String) : Bitmap? {
        try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)

            val width = 400
            val height = 400
            val codeColor = Color.parseColor("#001B2E") // Color del código (píxeles "negros" de la matriz)
            val backgroundColor = Color.parseColor("#EFE9F4") // Color del fondo (píxeles "blancos" de la matriz)

            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            // Puedes ajustar la corrección de errores si lo necesitas (L, M, Q, H)
            // hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

            val bitMatrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints)

            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    pixels[y * width + x] = if (bitMatrix.get(x, y)) codeColor else backgroundColor
                }
            }
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun startCountdownTimer() {
        // Crear una nueva instancia de CountDownTimer
        countDownTimer = object : CountDownTimer(COUNTDOWN_TIME_MILLIS, COUNTDOWN_INTERVAL_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                // Se llama en cada intervalo del temporizador
                // Actualiza el TextView con el tiempo restante
                val secondsRemaining = millisUntilFinished / 1000
                val minutesRemaining = (floor(secondsRemaining.toDouble() / 60)).toInt()
                val segundosRestantes = (secondsRemaining - (minutesRemaining * 60)).toInt()
                var segundos = ""

                if(segundosRestantes<10 ){
                    if ( minutesRemaining == 0){
                        countdownTextView.setTextColor(Color.parseColor("#8F0000"))
                    }
                    segundos = "0"+segundosRestantes
                } else{
                    segundos = segundosRestantes.toString()
                }
                var minutos = minutesRemaining.toString()

                countdownTextView.text = "0${minutos}:${segundos}"

            }

            override fun onFinish() {
                // Se llama cuando el temporizador ha terminado
                countdownTextView.text = "00:00"
                countdownTextView.setTextColor(Color.parseColor("#001B2E"))
                btnGenerate.isEnabled = true // Habilita el botón de nuevo
                updateButtonColors()
                ivQRCode.setImageResource(R.drawable.vexel_logo)
            }
        }.start() // Inicia el temporizador
    }

    private fun startCountdownTimer(duracionRestante: Long = COUNTDOWN_TIME_MILLIS) {
        countDownTimer = object : CountDownTimer(duracionRestante, COUNTDOWN_INTERVAL_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                // Se llama en cada intervalo del temporizador
                // Actualiza el TextView con el tiempo restante
                val secondsRemaining = millisUntilFinished / 1000
                val minutesRemaining = (floor(secondsRemaining.toDouble() / 60)).toInt()
                val segundosRestantes = (secondsRemaining - (minutesRemaining * 60)).toInt()
                var segundos = ""

                if(segundosRestantes<10 ){
                    if ( minutesRemaining == 0){
                        countdownTextView.setTextColor(Color.parseColor("#8F0000"))
                    }
                    segundos = "0"+segundosRestantes
                } else{
                    segundos = segundosRestantes.toString()
                }
                var minutos = minutesRemaining.toString()

                countdownTextView.text = "0${minutos}:${segundos}"
            }

            override fun onFinish() {
                countdownTextView.text = "00:00"
                countdownTextView.setTextColor(Color.parseColor("#001B2E"))
                btnGenerate.isEnabled = true
                updateButtonColors()
                ivQRCode.setImageResource(R.drawable.vexel_logo)

                SharedPreferencesManager(this@codigo_acceso).clearCodigoExpiraEn()
            }
        }.start()
    }


    private fun updateButtonColors() {
        if (btnGenerate.isEnabled) {
            // Si el botón está habilitado
            btnGenerate.backgroundTintList = ColorStateList.valueOf(ENABLED_BACKGROUND_COLOR)
            btnGenerate.setTextColor(ENABLED_TEXT_COLOR)
        } else {
            // Si el botón está deshabilitado
            btnGenerate.backgroundTintList = ColorStateList.valueOf(DISABLED_BACKGROUND_COLOR)
            btnGenerate.setTextColor(DISABLED_TEXT_COLOR)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Es importante cancelar el temporizador cuando la actividad se destruye
        // para evitar fugas de memoria y errores.
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }

    private fun mostrarQR(text: String) {
        Log.d("Prueba QR", text)
        val qrBitmap = generarQR(text)

        if (qrBitmap != null) {
            ivQRCode.setImageBitmap(qrBitmap)
        } else {
            // Manejar el error, por ejemplo, mostrar un Toast
            Toast.makeText(this, "Error al generar el QR", Toast.LENGTH_SHORT).show()
        }
    }

    fun generateTotpCode(secretoBase32: String, intervalo: Long = 30, digitos: Int = 6): String {
        try {
            // Decodifica el secreto en base32
            val secretBytes = decodeBase32(secretoBase32)

            // Tiempo actual dividido por el intervalo
            val tiempo = currentTimeMillis() / 1000L / intervalo

            // Empaqueta el tiempo en 8 bytes (big endian)
            val buffer = ByteBuffer.allocate(8).putLong(tiempo)
            val msg = buffer.array()

            // HMAC-SHA1
            val mac = Mac.getInstance("HmacSHA1")
            val keySpec = SecretKeySpec(secretBytes, "HmacSHA1")
            mac.init(keySpec)
            val hash = mac.doFinal(msg)

            // Offset dinámico
            val offset = hash.last().toInt() and 0x0F
            val parte = hash.sliceArray(offset until offset + 4)

            // Extrae los 31 bits más bajos
            val numero = ByteBuffer.wrap(parte).int and 0x7FFFFFFF

            // Calcula el OTP
            val otp = numero % (10.0.pow(digitos)).toInt()

            // Rellena con ceros a la izquierda si es necesario
            return otp.toString().padStart(digitos, '0')

        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
            return ""
        }
    }

    fun decodeBase32(base32: String): ByteArray {
        val base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val cleaned = base32.uppercase().replace("=", "")
        val output = ArrayList<Byte>()
        var buffer = 0
        var bitsLeft = 0

        for (char in cleaned) {
            val valChar = base32Chars.indexOf(char)
            if (valChar == -1) throw IllegalArgumentException("Carácter inválido en Base32: $char")

            buffer = (buffer shl 5) or valChar
            bitsLeft += 5

            if (bitsLeft >= 8) {
                bitsLeft -= 8
                output.add((buffer shr bitsLeft).toByte())
                buffer = buffer and ((1 shl bitsLeft) - 1)
            }
        }

        return output.toByteArray()
    }

    private fun canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

}
