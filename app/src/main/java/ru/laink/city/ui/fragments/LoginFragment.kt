package ru.laink.city.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
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
import ru.laink.city.R
import ru.laink.city.firebase.FirebaseUserRepoImpl
import ru.laink.city.models.LoginResult
import ru.laink.city.models.User
import ru.laink.city.ui.viewmodels.UserViewModel
import ru.laink.city.ui.UserViewModelFactory
import ru.laink.city.util.Constants.Companion.GOOGLE_SIGN_IN
import ru.laink.city.util.Resource

class LoginFragment : Fragment(R.layout.login_fragment) {

    lateinit var viewModel: UserViewModel
    private val TAG = "LoginFragment"

    override fun onStart() {
        super.onStart()

        email_edit_text.setText("")
        password_edit_text.setText("")

        // Проверка, что пользователь уже вошёл
        if (getCurrentUser() is Resource.Success) {
            findNavController().navigate(R.id.action_login_to_home_dest)
        } else {
            viewModel.clearSignInAnswer()
        }

    }

    override fun onPause() {
        super.onPause()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fireBaseUserRepoImpl = FirebaseUserRepoImpl()
        val viewModelProviderFactory = UserViewModelFactory(fireBaseUserRepoImpl)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(UserViewModel::class.java)

        // Прослушивание данных логина и регистрации
        viewModel.signInAnswer.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    Snackbar.make(requireView(), "Привет, ${response.data!!.name}", 1000).show()
                    findNavController().navigate(R.id.action_login_to_home_dest)
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(requireView(), "Ошибка: $message", 1000).show()
                        Log.e(TAG, "An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        // Вход по почте
        view.findViewById<Button>(R.id.login_button)?.setOnClickListener {
            signInByEmailAndPassword(
                email_edit_text.text.toString(),
                password_edit_text.text.toString()
            )
        }

        // Вход через google аккаунт
        view.findViewById<com.google.android.gms.common.SignInButton>(R.id.google_button)
            ?.setOnClickListener {
                startSignInGoogle()
            }

        // Регистрация
        view.findViewById<TextView>(R.id.registration_text)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_login_to_registation_dest, null)
        )
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

    private fun signInByEmailAndPassword(email: String, password: String) {
        viewModel.onSignInByEmailAndPasswordResult(email, password)
    }

    private fun getCurrentUser(): Resource<User?, Exception> {
        return viewModel.getUser()
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

            viewModel.onSignInResultByGoogle(LoginResult(requestCode, userToken))
        }
    }
}