package ru.laink.city.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

class CategoriesFragment : Fragment(R.layout.categories_fragment) {

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private val TAG = "CategoriesFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = RequestDatabase.invoke(requireContext())
        val firebaseCategoryRepoImpl = FirebaseCategoryRepoImpl(db)
//        val categoryRepository =
//            CategoryRepository(RequestDatabase(requireContext()), FirebaseCategoryRepoImpl())
        val viewModelProviderFactory = CategoryViewModelFactory(firebaseCategoryRepoImpl)
        categoryViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(CategoryViewModel::class.java)

        setUpRecyclerView()

//        categoryAdapter.setOnItemClickListener {
//            val category =
//        }


        categoryViewModel.getLocalCategories().observe(viewLifecycleOwner, Observer { categories ->
            categoryAdapter.differ.submitList(categories)
        })
        categoryViewModel.categoriesAnswer.observe(
            viewLifecycleOwner,
            Observer { response ->
//                response?.apply {
//                    categoryAdapter.differ.submitList(response)
//                }
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
//                        response.data?.let { categoryResponse ->
//                            categoryAdapter.differ.submitList(categoryResponse)
//                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        Snackbar.make(requireView(), "Ошибка ${response.message}", 1000).show()
                        Log.e(TAG, "An error occured: ${response.message}")
                    }
                }
            })

/*
        categoryViewModel.eventNetworkError.observe(
            viewLifecycleOwner,
            Observer<Boolean> { isNetworkError ->
                if(isNetworkError){
                    hideProgressBar()
                }
            })
*/
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