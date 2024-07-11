package com.example.recipekeeper.utils

import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream

class AppendObjectOutputStream (outputStream: FileOutputStream) : ObjectOutputStream(outputStream) {
    @Throws(IOException::class)
    override fun writeStreamHeader() {
        // Do not write a header
        reset()
    }
}