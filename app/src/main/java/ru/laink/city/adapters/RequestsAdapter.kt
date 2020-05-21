package ru.laink.city.adapters

import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.request_item_preview.view.*
import ru.laink.city.R
import ru.laink.city.models.request.Request
import ru.laink.city.util.Constants.Companion.STATUS_DONE
import ru.laink.city.util.Constants.Companion.STATUS_IN_DEVELOPING
import ru.laink.city.util.Constants.Companion.STATUS_REJECTED

class RequestsAdapter(private val geocoder: Geocoder) :
    RecyclerView.Adapter<RequestsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // DiffUtil позволяет сравнивать различия между двумя списками, тем самым,
    // обновляя только те значения,которые были разными
    private val differCallBack = object : DiffUtil.ItemCallback<Request>() {
        override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem == newItem
        }
    }

    // Асинхронный список отличий, который возьмёт наши два списка, сравнит их и вычислит различия
    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.request_item_preview,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val request = differ.currentList[position]

        holder.itemView.apply {

            if (request.uri != null) {
                Glide.with(this)
                    .load(request.uri.toUri())
                    .into(request_image)
            } else {
                request_image.setImageResource(R.drawable.ic_camera)
            }

            title_text.text = request.title
            location_text.text =
                geocoder.getFromLocation(
                    request.latitude,
                    request.longitude,
                    1
                )[0].getAddressLine(0)
            date_text.text = request.date
            description_text.text = request.description

            when (request.type) {
                STATUS_DONE -> {
                    request_item_constraint.setBackgroundColor(
                        resources.getColor(
                            R.color.pastelGreen2,
                            null
                        )
                    )
                }
                STATUS_IN_DEVELOPING -> {
                    request_item_constraint.setBackgroundColor(
                        resources.getColor(
                            R.color.pastelYellow,
                            null
                        )
                    )
                }
                STATUS_REJECTED -> {
                    request_item_constraint.setBackgroundColor(
                        resources.getColor(
                            R.color.colorRed,
                            null
                        )
                    )
                }
            }

            setOnClickListener {
                onItemClickListener?.let { it(request) }
            }
        }
    }

    private var onItemClickListener: ((Request) -> Unit)? = null

    fun setOnItemClickListener(listener: (Request) -> Unit) {
        onItemClickListener = listener
    }

}