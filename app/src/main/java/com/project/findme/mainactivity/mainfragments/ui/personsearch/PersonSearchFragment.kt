package com.project.findme.mainactivity.mainfragments.ui.personsearch

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.findme.adapter.UserAdapter
import com.project.findme.utils.Constants.SEARCH_TIME_DELAY
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentSearchPersonBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PersonSearchFragment : Fragment(R.layout.fragment_search_person) {

    @Inject
    lateinit var userAdapter: UserAdapter
    val viewModel: PersonViewModel by viewModels()
    private lateinit var binding: FragmentSearchPersonBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchPersonBinding.bind(view)

        setUpRecyclerView(binding)
        subscribeToObserve(binding)

        binding.etSearch.setText(viewModel.searchQuery)

        var job: Job? = null
        binding.etSearch.addTextChangedListener { text ->
            viewModel.searchQuery = text.toString()
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                text?.let {
                    viewModel.searchPerson()
                }
            }
        }

        userAdapter.setOnUserClickListener { user ->
            findNavController().navigate(
                PersonSearchFragmentDirections.actionPersonSearchFragmentToSearchedProfileFragment(
                    uid = user.uid,
                    username = user.userName
                )
            )
        }
    }

    private fun subscribeToObserve(binding: FragmentSearchPersonBinding) {
        viewModel.searchPersonStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.searchProgressbar.isVisible = false
                snackbar(it)
            },
            onLoading = {
                binding.searchProgressbar.isVisible = true
                userAdapter.users = listOf()
            }
        ) { userList ->
            binding.searchProgressbar.isVisible = false
            if (userList.isEmpty()) {
                binding.recyclerViewSearchList.isVisible = false
                binding.textViewEmptySearchList.isVisible = true
            } else {
                binding.recyclerViewSearchList.isVisible = true
                binding.textViewEmptySearchList.isVisible = false
                userAdapter.users = userList
            }
        })
    }

    fun setUpRecyclerView(binding: FragmentSearchPersonBinding) =
        binding.recyclerViewSearchList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
            itemAnimator = null
        }
}