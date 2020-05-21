package ru.laink.city.ui.fragments.addRequest

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_message_fragment.*
import ru.laink.city.R
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.FirebaseRequestRepoImpl
import ru.laink.city.models.Category
import ru.laink.city.models.request.RequestFirebase
import ru.laink.city.ui.factory.RequestViewModelProviderFactory
import ru.laink.city.ui.fragments.BaseFragment
import ru.laink.city.ui.viewmodels.RequestsViewModel
import ru.laink.city.util.Constants.Companion.CAMERA_PERMISSION_CODE
import ru.laink.city.util.Constants.Companion.CAMERA_REQUEST_CODE
import ru.laink.city.util.Constants.Companion.GALLERY_REQUEST_CODE
import ru.laink.city.util.Resource
import java.text.SimpleDateFormat
import java.util.*

class AddMessageFragment : BaseFragment() {

    private lateinit var requestViewModel: RequestsViewModel
    private var bitmap: Bitmap? = null
    private lateinit var photoImage: ImageView
    private val args: AddMessageFragmentArgs by navArgs()

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
        val viewModelProviderFactory =
            RequestViewModelProviderFactory(
                requestRepository
            )
        requestViewModel =
            ViewModelProviders.of(this, viewModelProviderFactory).get(RequestsViewModel::class.java)

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
            upsertRequest(category, latLng)
        }

        // Прослушивание ответа добавленных
        requestViewModel.resultUpsert.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showProgressBar(add_request_progress)
                }
                is Resource.Success -> {
                    hideProgressBar(add_request_progress)
                    Snackbar.make(requireView(), "Заявка успешно добавлена", 1000).show()
                    // Переход к главному меню
                    findNavController().popBackStack(R.id.home_dest, true)
                }
                is Resource.Error -> {
                    hideProgressBar(add_request_progress)
                    Snackbar.make(requireView(), "Error: ${resource.message}", 1000).show()
                }
            }
        })
    }

    private fun upsertRequest(category: Category, latLng: LatLng) {
        if (bitmap == null) {
            Snackbar.make(requireView(), getString(R.string.add_photo), 2000).show()
        } else if (title_edit_text.text.toString() == ""
            || description_edit_text.text.toString() == ""
            || date_edit_text.text.toString() == ""
        ) {
            Snackbar.make(requireView(), getString(R.string.enter_empty_field), 2000).show()
        } else {
            requestViewModel.upsertRequest(getRequest(category.id, latLng), bitmap!!)
        }
    }

    private fun getRequest(categoryId: Int, latLng: LatLng): RequestFirebase {
        return RequestFirebase(
            title_edit_text.text.toString(),
            date_edit_text.text.toString(),
            description_edit_text.text.toString(),
            latLng.latitude,
            latLng.longitude,
            null,
            categoryId
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
                    val source =
                        ImageDecoder.createSource(requireContext().contentResolver, contentUri)
                    bitmap = ImageDecoder.decodeBitmap(source)
                    photoImage.setImageBitmap(bitmap)
                }
            }
        }
    }

}