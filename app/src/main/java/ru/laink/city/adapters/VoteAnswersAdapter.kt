package ru.laink.city.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.vote_answer_item.view.*
import ru.laink.city.R
import ru.laink.city.models.vote.VoteAnswer
import ru.laink.city.util.Constants.Companion.TWO_SIGNS_FORMAT
import kotlin.math.roundToInt

class VoteAnswersAdapter :
    RecyclerView.Adapter<VoteAnswersAdapter.AnswerViewHolder>() {

    private val answerList = mutableListOf<VoteAnswer>()
    var selectedText: String? = null
    private var selectedItem: Int = -1

    inner class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        return AnswerViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.vote_answer_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return answerList.size
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val answer = answerList[position]
        val percent = round2((answer.count.toDouble() / sum().toDouble()) * 100)
        val percentAnswer = "${answer.count} (${TWO_SIGNS_FORMAT.format(percent)} %)"

        holder.itemView.apply {
            answer_percent_vote.text = percentAnswer
            radio_button_answer.text = answer.name
            vote_progress.progress = percent.toInt()
            radio_button_answer.isChecked = position == selectedItem

            radio_button_answer.setOnClickListener {
                selectedText = radio_button_answer.text.toString()
                selectedItem = position
                notifyDataSetChanged()
            }
        }
    }

    private fun round2(number: Double): Double {
        return (number * 100.0).roundToInt() / 100.0
    }

    fun update(list: List<VoteAnswer>) {
        answerList.clear()
        answerList.addAll(list)
        notifyDataSetChanged()
    }

    private fun sum(): Int {
        var sum = 0
        answerList.forEach { answer ->
            sum += answer.count
        }
        return sum
    }

}