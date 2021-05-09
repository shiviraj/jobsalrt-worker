package com.jobsalrt.worker.security.crypto

import org.junit.jupiter.api.Test

class CryptoTest {
    private val crypto = Crypto()

    @Test
    fun name() {
//        val toString = AdminToken("raj.shiviraj@gmail.com").toString()
        val encrypt = crypto.encrypt("hello world")
        println(encrypt)
        val decryptWithAES = crypto.decrypt(encrypt)
        println(decryptWithAES)


    }
}
