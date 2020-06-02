package ru.laink.city.ml

import android.content.Context
import android.text.TextUtils
import org.tensorflow.lite.Interpreter
import ru.laink.city.util.Constants
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

class HelperClassifier(val context: Context) {

    private var classifier = Classifier(context)
    private var interpreter: Interpreter? = null

    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(Constants.MODEL_FILENAME)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun createInterpreter() {
        interpreter = Interpreter(loadModelFile(), null)
        // Считывание JSON
        setVocabulary()
    }

    fun createClassificator(context: Context){
        classifier = Classifier(context)
    }

    private fun setVocabulary() {
        classifier.setVocab()
    }

    private fun classifySequence(sequence: IntArray): FloatArray {
        interpreter = Interpreter(loadModelFile(), null)
        val inputs: Array<FloatArray> = arrayOf(sequence.map { it.toFloat() }.toFloatArray())
        val outputs: Array<FloatArray> = arrayOf(floatArrayOf(0.0f))
        interpreter!!.run(inputs, outputs)
        return outputs[0]
    }

    fun classifyMessage(description: String): Int {
        var answer = 0
        val message = description.toLowerCase(Locale("ru")).trim()

        if (!TextUtils.isEmpty(message)) {
            val tokenizedMessage = classifier.tokenize(message)
            val paddedMessage = classifier.padSequence(tokenizedMessage)
            val results = classifySequence(paddedMessage)

            answer = if (results[0] > 0.6) -1 else 0
        } else {
            throw Exception("Empty fields")
        }

        return answer
    }

}