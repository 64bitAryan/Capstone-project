package com.project.findme.mainactivity.mainfragments.ui.personsearch

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.findme.adapter.UserAdapter
import com.project.findme.data.entity.User
import com.project.findme.utils.Constants.SEARCH_TIME_DELAY
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentSearchPersonBinding
import com.squareup.okhttp.Dispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
        
        //viewModel.searchPerson("user")

        var job: Job? = null
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                text?.let {
                    viewModel.searchPerson(it.toString())
                }
            }
        }

        userAdapter.setOnUserClickListener { user->
            Toast.makeText(requireContext(), user.uid, Toast.LENGTH_LONG).show()
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
            snackbar(userList.toString())
            userAdapter.users = userList
        })
    }

    fun setUpRecyclerView(binding: FragmentSearchPersonBinding) = binding.recyclerViewSearchList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
            itemAnimator = null
        }
}