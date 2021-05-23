package com.jobsalrt.worker.security.crypto

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import org.springframework.stereotype.Component
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Component
class Crypto {
    private val secretKey = System.getenv("CRYPTO_SECRET_KEY")

    fun encrypt(strToEncrypt: String): String? {
        Security.addProvider(BouncyCastleProvider())
        try {
            val keyBytes = secretKey.toByteArray(charset("UTF8"))
            val secretKeySpec = SecretKeySpec(keyBytes, "AES")
            val input = strToEncrypt.toByteArray(charset("UTF8"))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)

                val cipherText = ByteArray(cipher.getOutputSize(input.size))
                var ctLength = cipher.update(
                    input, 0, input.size,
                    cipherText, 0
                )
                ctLength += cipher.doFinal(cipherText, ctLength)
                return String(Base64.encode(cipherText))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun decrypt(strToDecrypt: String?): String? {
        Security.addProvider(BouncyCastleProvider())

        try {
            val keyBytes = secretKey.toByteArray(charset("UTF8"))
            val secretKeySpec = SecretKeySpec(keyBytes, "AES")
            val input = Base64.decode(strToDecrypt?.trim()?.toByteArray(charset("UTF8")))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)

                val plainText = ByteArray(cipher.getOutputSize(input.size))
                var ptLength = cipher.update(input, 0, input.size, plainText, 0)
                ptLength += cipher.doFinal(plainText, ptLength)
                val decryptedString = String(plainText)
                return decryptedString.trim()
            }

        } catch (e: Exception) {
            return null
        }
    }
}
