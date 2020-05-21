package ru.laink.city.ui.fragments.addRequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.categories_fragment.*
import ru.laink.city.R
import ru.laink.city.adapters.CategoryAdapter
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.FirebaseCategoryRepoImpl
import ru.laink.city.ui.factory.CategoryViewModelFactory
import ru.laink.city.ui.fragments.BaseFragment
import ru.laink.city.ui.viewmodels.CategoryViewModel
import ru.laink.city.util.Resource
import timber.log.Timber

class CategoriesFragment : BaseFragment() {

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private val args: CategoriesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.categories_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = RequestDatabase.invoke(requireContext())
        val firebaseCategoryRepoImpl = FirebaseCategoryRepoImpl(db)
        val viewModelProviderFactory =
            CategoryViewModelFactory(
                firebaseCategoryRepoImpl
            )
        categoryViewModel =
            ViewModelProviders.of(this, viewModelProviderFactory).get(CategoryViewModel::class.java)

        swipeRefresh()
        setUpRecyclerView()
        adapterItemOnClick()

        categoryViewModel.localCategories.observe(viewLifecycleOwner, Observer { categories ->
            categoryAdapter.differ.submitList(categories)
        })

        categoryViewModel.categoriesAnswer.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        swipe_categories.isRefreshing = false
                    }
                    is Resource.Loading -> {
                        swipe_categories.isRefreshing = true
                    }
                    is Resource.Error -> {
                        swipe_categories.isRefreshing = false
                        Snackbar.make(requireView(), "Ошибка ${response.message}", 2000).show()
                        Timber.d("An error occured: ${response.message}")
                    }
                }
            })

    }

    private fun setUpRecyclerView() {
        categoryAdapter = CategoryAdapter()

        category_recyclerview.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun adapterItemOnClick() {
        // По клику на категорию передать её на фрагмент добавления заявки
        categoryAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("latLng", args.latLng)
                putParcelable("category", it)
            }
            findNavController().navigate(
                R.id.action_categoriesFragment_to_add_messaage_dest,
                bundle
            )
        }
    }

    private fun swipeRefresh() {
        swipe_categories.setOnRefreshListener {
            categoryViewModel.getCategories()
        }
    }
}