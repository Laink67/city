package ru.laink.city.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.voting_list_item.view.*
import ru.laink.city.R
import ru.laink.city.models.vote.Vote

class VotingAdapter : RecyclerView.Adapter<VotingAdapter.VotingViewHolder>() {

    inner class VotingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Vote>() {
        override fun areItemsTheSame(oldItem: Vote, newItem: Vote): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Vote, newItem: Vote): Boolean {
            return oldItem == newItem
        }

    }

    // Асинхронный список отличий, который возьмёт наши два списка, сравнит их и вычислит различия
    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VotingAdapter.VotingViewHolder {
        return VotingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.voting_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: VotingViewHolder, position: Int) {
        val vote = differ.currentList[position]

        holder.itemView.apply {
            recycler_title_text.text = vote.title

            setOnClickListener {
                onItemClickListener?.let { it(vote) }
            }
        }

    }

    private var onItemClickListener: ((Vote) -> Unit)? = null

    fun setOnItemClickListener(listener: (Vote) -> Unit) {
        onItemClickListener = listener
    }

}