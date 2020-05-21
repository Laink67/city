package ru.laink.city.ui.fragments.signInUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.registration_fragment.*
import ru.laink.city.R
import ru.laink.city.firebase.FirebaseUserRepoImpl
import ru.laink.city.ui.factory.UserViewModelFactory
import ru.laink.city.ui.viewmodels.UserViewModel
import ru.laink.city.util.Resource
import timber.log.Timber

class RegistrationFragment : Fragment(R.layout.registration_fragment) {
    lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.registration_fragment, container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fireBaseUserRepoImpl = FirebaseUserRepoImpl()
        val viewModelProviderFactory =
            UserViewModelFactory(fireBaseUserRepoImpl)
        viewModel =
            ViewModelProviders.of(this, viewModelProviderFactory).get(UserViewModel::class.java)

        viewModel.signUpAnswer.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    Snackbar.make(requireView(), "Регистрация успешно завершена", 1000).show()
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(requireView(), "Ошибка: $message", 1000).show()
                        Timber.e("An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        view.findViewById<MaterialButton>(R.id.singUp_button)?.setOnClickListener {
            signUp(
                login_register_edit_text.text.toString(),
                email_register_edit_text.text.toString(),
                password_register_edit_text.text.toString()
            )
        }
    }

    private fun signUp(login: String, email: String, password: String) {
        viewModel.signUp(login, email, password)
    }

    private fun hideProgressBar() {
        register_progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        register_progressBar.visibility = View.VISIBLE
    }

}