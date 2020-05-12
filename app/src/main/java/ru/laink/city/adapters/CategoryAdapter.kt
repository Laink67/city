package ru.laink.city.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_item.view.*
import ru.laink.city.R
import ru.laink.city.models.Category

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(categoryView: View) : RecyclerView.ViewHolder(categoryView)

    // DiffUtil позволяет сравнивать различия между двумя списками, тем самым,
    // обновляя только те значения,которые были разными
    private val differCallBack = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

    // Асинхронный список отличий, который возьмёт наши два списка, сравнит их и вычислит различия
    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.category_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CategoryAdapter.CategoryViewHolder, position: Int) {
        val category = differ.currentList[position]

        holder.itemView.apply {
            category_title_text.text = category?.title

            setOnClickListener {
                onItemClickListener?.let { it(category!!) }
            }
        }
    }

    private var onItemClickListener: ((Category) -> Unit)? = null

    fun setOnItemClickListener(listener: (Category) -> Unit) {
        onItemClickListener = listener
    }

}