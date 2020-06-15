package ru.laink.city.ui.fragments

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.add_message_fragment.*
import ru.laink.city.models.request.Request
import ru.laink.city.ui.fragments.addRequest.AddMessageFragment

class EditMessageFragment : AddMessageFragment() {

    private val args: EditMessageFragmentArgs by navArgs()
    private lateinit var request: Request
    private var uri: Uri? = null

    override fun getCategoryId(): Int {
        return request.categoryId
    }

    override fun getLatLng(): LatLng {
        return LatLng(request.latitude, request.longitude)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        request = args.request
        super.onViewCreated(view, savedInstanceState)

        uri = request.uri?.toUri()

        if (uri != null) {
            Glide.with(this)
                .load(uri)
                .into(photo_image)
        }

        setDataInFields(request)
    }

    override fun upsert(categoryId: Int, latLng: LatLng) {
        requestViewModel.upsertRequest(getRequest(categoryId, latLng), bitmap!!, request.id)
    }

    override fun upsertRequest(categoryId: Int, latLng: LatLng) {
        bitmap = uri?.let { (photo_image.drawable as BitmapDrawable).bitmap }
        super.upsertRequest(categoryId, latLng)
    }

    private fun setDataInFields(request: Request) {
        title_edit_text.setText(request.titleRequest)
        date_edit_text.setText(request.date)
        description_edit_text.setText(request.description)
    }
}