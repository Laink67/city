package ru.laink.city.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.laink.city.R
import ru.laink.city.firebase.FireBaseUserRepoImpl
import ru.laink.city.models.LoginResult
import ru.laink.city.ui.UserViewModel
import ru.laink.city.ui.UserViewModelFactory
import ru.laink.city.util.Constants.Companion.GOOGLE_SIGN_IN
import ru.laink.city.util.Resource

class LoginFragment : Fragment(R.layout.login_fragment) {

    lateinit var viewModel: UserViewModel
    val TAG = "LoginFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fireBaseUserRepoImpl = FireBaseUserRepoImpl()
        val viewModelProviderFactory = UserViewModelFactory(fireBaseUserRepoImpl)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(UserViewModel::class.java)

        viewModel.answer.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    Snackbar.make(requireView(), response.data.toString(), 500)
                    findNavController().navigate(R.id.action_login_to_home_dest)
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        view.findViewById<Button>(R.id.login_button)?.setOnClickListener {
            startSignInGoogle()
        }
    }

    private fun hideProgressBar() {
        login_progress.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        login_progress.visibility = View.VISIBLE
    }


    // Для отображения входа Google
    private fun startSignInGoogle() {
        // Создаём объект параметров входа в Google
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)

        val signInIntent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            var userToken: String? = null

            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

                if (account != null) userToken = account.idToken

            } catch (exception: Exception) {
                Log.d("LOGIN", exception.toString())
            }

            viewModel.onSignInResult(LoginResult(requestCode, userToken))

        }
    }
}