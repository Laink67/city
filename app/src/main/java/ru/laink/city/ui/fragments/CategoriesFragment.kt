package ru.laink.city.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.categories_fragment.*
import ru.laink.city.R
import ru.laink.city.adapters.CategoryAdapter
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.FirebaseCategoryRepoImpl
import ru.laink.city.ui.CategoryViewModelFactory
import ru.laink.city.ui.viewmodels.CategoryViewModel
import ru.laink.city.util.Resource
import timber.log.Timber

class CategoriesFragment : Fragment(R.layout.categories_fragment) {

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private val TAG = "CategoriesFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = RequestDatabase.invoke(requireContext())
        val firebaseCategoryRepoImpl = FirebaseCategoryRepoImpl(db)
        val viewModelProviderFactory = CategoryViewModelFactory(firebaseCategoryRepoImpl)
        categoryViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(CategoryViewModel::class.java)

        setUpRecyclerView()

        // По клику на категорию передать её на фрагмент добавления заявки
        categoryAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("category", it)
            }
            findNavController().navigate(
                R.id.action_categoriest_to_map_dest,
                bundle
            )
        }


        categoryViewModel.localCategories.observe(viewLifecycleOwner, Observer { categories ->
            categoryAdapter.differ.submitList(categories)
        })

        categoryViewModel.categoriesAnswer.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        Snackbar.make(requireView(), "Ошибка ${response.message}", 1000).show()
                        Timber.d("An error occured: ${response.message}")
                    }
                }
            })

    }

    private fun hideProgressBar() {
        category_progress_bar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        category_progress_bar.visibility = View.VISIBLE
    }


    private fun setUpRecyclerView() {
        categoryAdapter = CategoryAdapter()

        category_recyclerview.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}