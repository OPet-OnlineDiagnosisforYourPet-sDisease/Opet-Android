package com.example.meowbottom.ui.community

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meowbottom.adapter.CommunityAdapter
import com.example.meowbottom.adapter.CommunityPageAdapter
import com.example.meowbottom.adapter.LoadingStateAdapter
import com.example.meowbottom.data.CommunityItem
import com.example.meowbottom.data.StoryItem
import com.example.meowbottom.databinding.FragmentCommunityBinding
import com.example.meowbottom.response.ListStoryItem
import com.example.meowbottom.response.StoriesItem
import com.example.meowbottom.ui.login.LoginActivity
import com.example.meowbottom.ui.login.LoginViewModelFactory
import com.example.meowbottom.ui.login.UserPreference
import com.example.meowbottom.ui.login.UserViewModel
import com.example.meowbottom.ui.symptom.SymptomViewModel
import com.example.meowbottom.ui.upload.UploadActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class CommunityFragment : Fragment() {

    private lateinit var binding: FragmentCommunityBinding
    //private val symptomViewModel by viewModels<SymptomViewModel>()
    private val communityViewModel by viewModels<CommunityViewModel>()
    private lateinit var userViewModel: UserViewModel
    private val communityPageViewModel: CommunityPageViewModel by viewModels {
        ViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        observeLoadingState()
        //setupArticle()
        setupPage()
        binding.fabAdd.setOnClickListener {
            val intentUpload = Intent(context, UploadActivity::class.java)
            startActivity(intentUpload)
        }
    }

    private fun setupPage() {

        //showLoading(false)
        communityPageViewModel.setLoading(true)
        val adapter = CommunityPageAdapter()
        binding.rvCommunity.adapter = adapter
        binding.rvCommunity.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCommunity.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        userViewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModelFactory(UserPreference.getInstance(requireActivity().dataStore))
        )[UserViewModel::class.java]

        userViewModel.getUser().observe(viewLifecycleOwner, {user ->
            if (user.isLogin) {
                //val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLU11MG5KOEkzN3RVTFhhUW4iLCJpYXQiOjE2ODU0Njk5NDR9.nv2Gex8CRBRepy0sfL3OxxDeSWsunbW6KOJ0IVG6zv4"
                val token = "Bearer ${user.token}"
                communityPageViewModel.story(token).observe(this , {
                    communityPageViewModel.setLoading(false)
                    adapter.submitData(viewLifecycleOwner.lifecycle, it)
                })
                /*communityViewModel.listStory(token)
                communityViewModel.listStory.observe(viewLifecycleOwner, { lisStory ->
                    setStoryListData(lisStory)
                })
                communityViewModel.isLoading.observe(viewLifecycleOwner, {
                    showLoading(it)
                })*/
            } else {
                val intentLogin = Intent(context, LoginActivity::class.java)
                intentLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intentLogin.putExtra("FRAGMENT", "Community")
                startActivity(intentLogin)
            }
        })
    }

    private fun observeLoadingState() {
        communityPageViewModel.isLoading.observe(this, { isLoading ->
            if (isLoading) {
                // Show loading indicator
                binding.progressBar.visibility = View.VISIBLE
            } else {
                // Hide loading indicator
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun setupArticle() {
        userViewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModelFactory(UserPreference.getInstance(requireActivity().dataStore))
        )[UserViewModel::class.java]

        userViewModel.getUser().observe(viewLifecycleOwner, {user ->
            if (user.isLogin) {
                //val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLU11MG5KOEkzN3RVTFhhUW4iLCJpYXQiOjE2ODU0Njk5NDR9.nv2Gex8CRBRepy0sfL3OxxDeSWsunbW6KOJ0IVG6zv4"
                val token = "Bearer ${user.token}"
                communityViewModel.listStory(token)
                communityViewModel.listStory.observe(viewLifecycleOwner, { lisStory ->
                    setStoryListData(lisStory)
                })
                communityViewModel.isLoading.observe(viewLifecycleOwner, {
                    showLoading(it)
                })
            } else {
                val intentLogin = Intent(context, LoginActivity::class.java)
                intentLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intentLogin.putExtra("FRAGMENT", "Community")
                startActivity(intentLogin)
            }
        })

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setStoryListData(lisStory: List<StoriesItem?>?) {
        val listStory = ArrayList<CommunityItem>()
        for (item in lisStory!!) {
            listStory.add(
                CommunityItem(
                    id = item?.id!!,
                    name = """
                        ${item?.senderName}
                    """.trimIndent(),
                    description = """
                        ${item?.description}
                    """.trimIndent(),
                    photoUrl = """
                        ${item?.photo}
                    """.trimIndent(),
                    createdAt = """
                        ${item?.createdAt}
                    """.trimIndent(),
                    photoUser = """
                        ${item?.senderProfil}
                    """.trimIndent()
                )
            )
        }
        setAdapter(listStory)
    }

    private fun setAdapter(listStory: ArrayList<CommunityItem>) {

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvCommunity.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvCommunity.addItemDecoration(itemDecoration)

        val adapter = CommunityAdapter(listStory)
        binding.rvCommunity.adapter = adapter
        binding.rvCommunity.setHasFixedSize(true)

    }

    companion object {

        fun newInstance(): CommunityFragment {
            return CommunityFragment()
        }
    }
}