package ru.laink.city

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_message_fragment.*
import ru.laink.city.util.Constants.Companion.CAMERA_PERMISSION_CODE
import ru.laink.city.util.Constants.Companion.CAMERA_REQUEST_CODE
import ru.laink.city.util.Constants.Companion.GALLERY_REQUEST_CODE
import java.io.File
import java.io.IOException
import java.security.Permission
import java.text.SimpleDateFormat
import java.util.*

class AddMessageFragment : Fragment() {

    private lateinit var photoImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_message_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoImage = view.findViewById(R.id.photo_image)

        // Для форматирования даты в виде dd/MM/yyyy
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("ru"))

        // Для отображения выбора даты через MaterialDatePicker
        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        // Создание диалога для отображения выбора фото
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage("Выбирите способ получения фото")
            .setTitle("Добавить фото?")
            .setNegativeButton("Галерея") { _, _ ->
                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(gallery, GALLERY_REQUEST_CODE)
            }
            .setPositiveButton("Камера") { _, _ ->
                askCameraPermission()
            }
            .setNegativeButtonIcon(
                resources.getDrawable(
                    R.drawable.ic_gallery,
                    requireContext().theme
                )
            )
            .setPositiveButtonIcon(
                resources.getDrawable(
                    R.drawable.ic_camera_color,
                    context?.theme
                )
            )

        // При нажатии по картинке открывается диалог с выбором способа получения фото
        photoImage.setOnClickListener {
            dialog.show()
        }

        // При нажатии на date_image_button показывается MaterialDatePicker
        view.findViewById<MaterialButton>(R.id.date_button)?.setOnClickListener {
            datePicker.show(childFragmentManager, "DATE_PICKER")
        }

        // Выбранная дата помещается в date_edit_text
        datePicker.addOnPositiveButtonClickListener {
            date_edit_text.setText(simpleDateFormat.format(Date(it)))
        }
    }

    private fun openCamera() {
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(camera, CAMERA_REQUEST_CODE)
    }

    // Запрос на доступ к камере
    private fun askCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Snackbar.make(
                        requireView(),
                        "Разрешение не было предоставлено",
                        500
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val image = data?.extras?.get("data") as Bitmap
                    photoImage.setImageBitmap(image)
                }
                GALLERY_REQUEST_CODE -> {
                    val contentUri = data?.data
                    photoImage.setImageURI(contentUri)
                }
            }
        }
    }

}