package ru.laink.city.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.idea_fragment.*
import ru.laink.city.R
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.IdeaRepository
import ru.laink.city.models.idea.Idea
import ru.laink.city.ui.factory.SingleIdeaViewModelFactory
import ru.laink.city.ui.viewmodels.SingleIdeaViewModel
import ru.laink.city.util.Resource
import timber.log.Timber

open class SingleIdeaFragment : BaseFragment() {

    private lateinit var ideaViewModel: SingleIdeaViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.idea_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val db = RequestDatabase(requireContext())
        val ideaRepository = IdeaRepository(db)
        val viewModelProviderFactory =
            SingleIdeaViewModelFactory(
                ideaRepository
            )
        ideaViewModel =
            ViewModelProviders.of(this, viewModelProviderFactory)
                .get(SingleIdeaViewModel::class.java)

        submitClick()
        listenAddIdeaCallback()
    }

    private fun listenAddIdeaCallback() {
        ideaViewModel.ideaAnswer.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressBar(idea_progress)
                }
                is Resource.Success -> {
                    hideProgressBar(idea_progress)
                    Snackbar.make(requireView(), getString(R.string.success), 2000).show()
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    hideProgressBar(idea_progress)
                    Snackbar.make(requireView(), response.message.toString(), 2000).show()
                    Timber.d(response.message)
                }
            }
        })
    }

    private fun submitClick() {
        submit_idea_button.setOnClickListener {
            addIdea()
        }
    }

    private fun addIdea() {
        ideaViewModel.insert(
            Idea(
                title_add_idea_text.text.toString(),
                description_idea_add_text.text.toString()
            )
        )
    }
}