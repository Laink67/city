package ru.laink.city.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_message_fragment.*
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.laink.city.R
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.FirebaseRequestRepoImpl
import ru.laink.city.models.Category
import ru.laink.city.models.Request
import ru.laink.city.ui.RequestViewModelProviderFactory
import ru.laink.city.ui.viewmodels.RequestsViewModel
import ru.laink.city.util.Constants.Companion.CAMERA_PERMISSION_CODE
import ru.laink.city.util.Constants.Companion.CAMERA_REQUEST_CODE
import ru.laink.city.util.Constants.Companion.GALLERY_REQUEST_CODE
import ru.laink.city.util.Resource
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddMessageFragment : Fragment() {

    lateinit var requestViewModel: RequestsViewModel
    private lateinit var bitmap: Bitmap
    private lateinit var photoImage: ImageView
    val args: AddMessageFragmentArgs by navArgs()

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
        val category = args.category
        val latLng = args.latLng

        val requestRepository = FirebaseRequestRepoImpl(RequestDatabase(requireContext()))
        val viewModelProviderFactory = RequestViewModelProviderFactory(requestRepository)
        requestViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(RequestsViewModel::class.java)

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

        view.findViewById<MaterialButton>(R.id.submit_button)?.setOnClickListener {
            requestViewModel.upsertRequest(getRequest(category, latLng), bitmap)
        }

        // Прослушивание ответа добавленных
        requestViewModel.result.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Success -> {
                    hideProgressBar()
                    Snackbar.make(requireView(), "Заявка успешно добавлена", 1000).show()
                    // Переход к следующему фрагменту
                }
                is Resource.Error -> {
                    hideProgressBar()
                    Snackbar.make(requireView(), "Error: ${resource.message}", 1000).show()
                }
            }
        })
    }

    private fun hideProgressBar() {
        add_request_progress.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        add_request_progress.visibility = View.VISIBLE
    }


    private fun getRequest(category: Category, latLng: LatLng): Request {
        return Request(
            title_edit_text.text.toString(),
            date_edit_text.text.toString(),
            description_edit_text.text.toString(),
            latLng,
            null,
            category
        )
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

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    bitmap = data?.extras?.get("data") as Bitmap
                    photoImage.setImageBitmap(bitmap)
                }
                GALLERY_REQUEST_CODE -> {
                    val contentUri = data?.data!!
//                    photoImage.setImageURI(contentUri)
                    val source =
                        ImageDecoder.createSource(requireContext().contentResolver, contentUri)
                    bitmap = ImageDecoder.decodeBitmap(source)
                    photoImage.setImageBitmap(bitmap)
                }
            }
        }
    }

}