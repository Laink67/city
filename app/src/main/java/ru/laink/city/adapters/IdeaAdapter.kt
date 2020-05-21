package ru.laink.city.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.idea_item.view.*
import ru.laink.city.R
import ru.laink.city.models.idea.Idea
import ru.laink.city.util.Constants.Companion.STATUS_DONE
import ru.laink.city.util.Constants.Companion.STATUS_IN_DEVELOPING
import ru.laink.city.util.Constants.Companion.STATUS_REJECTED

class IdeaAdapter : RecyclerView.Adapter<IdeaAdapter.IdeaViewHolder>() {

    inner class IdeaViewHolder(ideaView: View) : RecyclerView.ViewHolder(ideaView)

    // DiffUtil позволяет сравнивать различия между двумя списками, тем самым,
    // обновляя только те значения,которые были разными
    private val differCallBack = object : DiffUtil.ItemCallback<Idea>() {
        override fun areItemsTheSame(oldItem: Idea, newItem: Idea): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Idea, newItem: Idea): Boolean {
            return oldItem == newItem
        }
    }

    // Асинхронный список отличий, который возьмёт наши два списка, сравнит их и вычислит различия
    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IdeaAdapter.IdeaViewHolder {
        return IdeaViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.idea_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: IdeaAdapter.IdeaViewHolder, position: Int) {
        val idea = differ.currentList[position]

        holder.itemView.apply {
            idea_title_text.text = idea.title
            idea_description_text.text = idea.description

            bulb_image.setColorFilter(
                when (idea.status) {
                    STATUS_DONE -> {
                        resources.getColor(R.color.pastelGreen2, null)
                    }
                    STATUS_REJECTED -> {
                        resources.getColor(R.color.colorRed, null)
                    }
                    STATUS_IN_DEVELOPING -> {
                        resources.getColor(R.color.yellow, null)
                    }
                    else -> {
                        resources.getColor(R.color.colorPrimary, null)
                    }
                }
            )

            setOnClickListener {
                onItemClickListener?.let { it(idea!!) }
            }
        }
    }

    private var onItemClickListener: ((Idea) -> Unit)? = null

    fun setOnItemClickListener(listener: (Idea) -> Unit) {
        onItemClickListener = listener
    }

}