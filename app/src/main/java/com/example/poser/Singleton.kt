package com.example.poser

import android.graphics.Bitmap
import java.util.Collections.singleton

object Singleton {
    private var myImage: Bitmap? = null
    private var singleton: Singleton? = null

    @ExperimentalStdlibApi
    fun singleton(): Singleton?= null?.let { it {} }

    fun getMyImage(): Bitmap? {
        return myImage
    }

    fun setMyImage(myImage: Bitmap?) {
        this.myImage = myImage
    }

    @ExperimentalStdlibApi
    fun getInstance():Singleton? {
        if (singleton == null) {
            singleton =
                singleton()
        }
        return singleton
    }

}