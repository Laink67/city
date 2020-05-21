package ru.laink.city.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import ru.laink.city.R
import ru.laink.city.firebase.FirebaseUserRepoImpl
import ru.laink.city.ui.factory.UserViewModelFactory
import ru.laink.city.ui.viewmodels.UserViewModel
import ru.laink.city.util.Resource

class HomeFragment : Fragment(R.layout.home_fragment) {

    lateinit var userViewModel: UserViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fireBaseUserRepoImpl = FirebaseUserRepoImpl()
        val viewModelProviderFactory =
            UserViewModelFactory(fireBaseUserRepoImpl)
        userViewModel =
            ViewModelProviders.of(this, viewModelProviderFactory).get(UserViewModel::class.java)

        view.findViewById<ImageView>(R.id.map_image)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_home_dest_to_map, null)
        )

        view.findViewById<FloatingActionButton>(R.id.add_fab)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_home_dest_to_addMap, null)
        )

        view.findViewById<ImageView>(R.id.sign_out_image)?.setOnClickListener {
            signOut()
        }
        view.findViewById<ImageView>(R.id.messages_image)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_home_dest_to_ownRequestsFragment)
        )
        view.findViewById<ImageView>(R.id.voting_image)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_home_dest_to_voting)
        )
        view.findViewById<ImageView>(R.id.idea_image)?.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeDestToIdeas())
        }
    }

    private fun signOut() {
        val resource = userViewModel.signOut()
        if (resource is Resource.Success) {
//            findNavController().popBackStack()
            findNavController().navigateUp()/*navigate(R.id.action_home_dest_to_login_dest)*/
        } else {
            Snackbar.make(requireView(), "Ошибка: ${resource.message}", 1000).show()
        }
    }

}