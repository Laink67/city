package ru.laink.city.ui.fragments

import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment

open class BaseFragment() : Fragment() {

    fun showProgressBar(progressBar: ProgressBar){
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgressBar(progressBar: ProgressBar){
        progressBar.visibility = View.INVISIBLE
    }

}