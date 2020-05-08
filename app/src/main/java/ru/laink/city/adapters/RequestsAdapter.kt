package ru.laink.city.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.request_item_preview.view.*
import ru.laink.city.R
import ru.laink.city.models.Request

class RequestsAdapter : RecyclerView.Adapter<RequestsAdapter.ItemViewHolder>() {

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
            title_text.text = request.title
            location_text.text = request.location
            date_text.text = request.date
            description_text.text = request.description

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