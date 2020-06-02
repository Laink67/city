package ru.laink.city.ml

import android.content.Context
import org.json.JSONObject
import ru.laink.city.util.Constants.Companion.MAX_LEN
import ru.laink.city.util.Constants.Companion.VOCAB_FILENAME
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class Classifier(context: Context) {

    private var context: Context? = context
    private val filename: String = VOCAB_FILENAME
    private var maxlen: Int = MAX_LEN
    private var vocabData: HashMap<String, Int>? = null

    fun tokenize(message: String): IntArray {
        val parts: List<String> = message.split(" ")
        val tokenizedMessage = IntArray(maxlen) { 0 }
        for (part in parts) {
            if (part.trim() != "") {
                var index: Int? = 0

                index = if (vocabData!![part] == null) {
                    0
                } else {
                    vocabData!![part]
                }

                tokenizedMessage[index!!]++
            }
        }
        return tokenizedMessage
    }

    fun padSequence(sequence: IntArray): IntArray {
        return when {
            sequence.size > maxlen -> {
                sequence.sliceArray(0..maxlen)
            }
            sequence.size < maxlen -> {
                val array = ArrayList<Int>()
                array.addAll(sequence.asList())
                for (i in array.size until maxlen) {
                    array.add(0)
                }
                array.toIntArray()
            }
            else -> {
                sequence
            }
        }
    }

    fun setVocab() {
        this.vocabData = getJsonData()
    }

    private fun getJsonData(): HashMap<String, Int> {
        val jsonObject = JSONObject(loadJSONFromAsset())
        val iterator: Iterator<String> = jsonObject.keys()
        val data = HashMap<String, Int>()

        while (iterator.hasNext()) {
            val key = iterator.next()
            data[key] = jsonObject.get(key) as Int
        }

        return data
    }

    private fun loadJSONFromAsset(): String {
        var json = ""

        try {
            val inputStream = context!!.assets.open(filename)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return json
    }

}