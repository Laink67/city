package ru.laink.city.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.ideas_fragment.*
import ru.laink.city.R
import ru.laink.city.adapters.IdeaAdapter
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.IdeaRepository
import ru.laink.city.ui.factory.IdeaViewModelFactory
import ru.laink.city.ui.viewmodels.IdeasViewModel
import ru.laink.city.util.Resource

class IdeasFragment : Fragment(R.layout.ideas_fragment) {

    private lateinit var ideasViewModel: IdeasViewModel
    private lateinit var ideaAdapter: IdeaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val db = RequestDatabase(requireContext())
        val ideaRepository = IdeaRepository(db)
        val viewModelProviderFactory =
            IdeaViewModelFactory(
                ideaRepository
            )
        ideasViewModel =
            ViewModelProviders.of(this, viewModelProviderFactory).get(IdeasViewModel::class.java)

        setUpRecyclerView()
        ideasViewModel.localIdeas.observe(viewLifecycleOwner, Observer { ideas ->
            ideaAdapter.differ.submitList(ideas)
        })

        addButtonClick()
        listenIdeas()
    }

    private fun addButtonClick() {
        add_idea_button.setOnClickListener {
            findNavController().navigate(R.id.action_ideas_to_single_idea)
        }
    }

    private fun listenIdeas() {
        ideasViewModel.ideaAnswer.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Loading -> {
                    swipe_ideas.isRefreshing = true
                }
                is Resource.Success -> {
                    swipe_ideas.isRefreshing = false
                }
                is Resource.Error -> {
                    swipe_ideas.isRefreshing = true
                    Snackbar.make(requireView(), response.message.toString(), 2000)
                }
            }
        })
    }

    private fun setUpRecyclerView() {
        ideaAdapter = IdeaAdapter()

        ideas_recycler.apply {
            adapter = ideaAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}