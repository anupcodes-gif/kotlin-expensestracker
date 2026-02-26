package com.example.expensetracker.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.R
import com.example.expensetracker.databinding.FragmentRegisterBinding
import com.example.expensetracker.utils.isValidEmail
import com.example.expensetracker.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeAuthState()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            when {
                name.isBlank() -> binding.tilName.error = "Name is required"
                !email.isValidEmail() -> binding.tilEmail.error = "Enter a valid email"
                password.length < 6 -> binding.tilPassword.error = "Minimum 6 characters"
                password != confirmPassword -> binding.tilConfirmPassword.error = "Passwords don't match"
                else -> {
                    binding.tilName.error = null
                    binding.tilEmail.error = null
                    binding.tilPassword.error = null
                    binding.tilConfirmPassword.error = null
                    viewModel.register(email, password, name)
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { state ->
                    binding.progressBar.isVisible = state.isLoading
                    binding.btnRegister.isEnabled = !state.isLoading

                    if (state.isSuccess && state.user != null) {
                        findNavController().navigate(R.id.action_registerFragment_to_dashboardFragment)
                    }

                    state.error?.let { error ->
                        binding.root.showSnackbar(error)
                        viewModel.clearError()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
