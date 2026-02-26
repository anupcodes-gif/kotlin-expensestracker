package com.example.expensetracker.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.R
import com.example.expensetracker.databinding.FragmentProfileBinding
import com.example.expensetracker.ui.auth.AuthViewModel
import com.example.expensetracker.utils.toCurrency
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateUserInfo()
        setupButtons()
        observeAuthState()
    }

    private fun populateUserInfo() {
        val user = authViewModel.currentUser
        binding.tvUserName.text = user?.displayName ?: "User"
        binding.tvUserEmail.text = user?.email ?: ""
        // Set avatar initials
        val initial = (user?.displayName?.firstOrNull() ?: user?.email?.firstOrNull() ?: 'U').uppercaseChar()
        binding.tvAvatar.text = initial.toString()
    }

    private fun setupButtons() {
        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
        }
    }

    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authState.collect { state ->
                    if (state.user == null && !state.isLoading) {
                        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
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
