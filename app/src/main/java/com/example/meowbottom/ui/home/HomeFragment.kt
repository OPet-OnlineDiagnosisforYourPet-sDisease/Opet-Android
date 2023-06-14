package com.example.meowbottom.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.meowbottom.R
import com.example.meowbottom.adapter.ArticleAdapter
import com.example.meowbottom.adapter.ArticleGridAdapter
import com.example.meowbottom.data.ItemArticle
import com.example.meowbottom.databinding.FragmentHomeBinding
import com.example.meowbottom.response.ArticleItem
import com.example.meowbottom.ui.disclaimer.DisclaimerActivity
import com.example.meowbottom.ui.login.LoginViewModelFactory
import com.example.meowbottom.ui.login.UserPreference
import com.example.meowbottom.ui.login.UserViewModel
import com.example.meowbottom.ui.upload.UploadActivity


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class HomeFragment : Fragment() {


    private lateinit var binding: FragmentHomeBinding
/*
    private val symptomViewModel by viewModels<SymptomViewModel>()

*/
    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var userViewModel: UserViewModel


    private var isGrid = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.ivDog.setOnClickListener {
            val intentDog = Intent(context, DisclaimerActivity::class.java)
            intentDog.putExtra("DOG", "dog")
            startActivity(intentDog)
        }
        binding.ivCat.setOnClickListener {
            Toast.makeText(context, getString(R.string.development), Toast.LENGTH_SHORT).show()
        }
        binding.ivBird.setOnClickListener {
            Toast.makeText(context, getString(R.string.development), Toast.LENGTH_SHORT).show()
        }
        binding.ivFish.setOnClickListener {
            Toast.makeText(context, getString(R.string.development), Toast.LENGTH_SHORT).show()
        }

        setupArticle()
        setupHome()
    }

    private fun setupHome() {
        userViewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModelFactory(UserPreference.getInstance(requireActivity().dataStore))
        )[UserViewModel::class.java]

        userViewModel.getUser().observe(viewLifecycleOwner, { user ->
            if (user.isLogin) {
                binding.tvTitle.visibility = View.VISIBLE
                binding.ivProfile.visibility = View.VISIBLE
                binding.tvWelcome.visibility = View.VISIBLE
                binding.tvTitle.text = user.username
                Glide.with(requireActivity())
                    .load(user.profil)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.article)
                    .into(binding.ivProfile)
            } else {
                binding.tvTitle.text = getString(R.string.home)
            }
        })
    }

    private fun setupArticle() {
        val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLU11MG5KOEkzN3RVTFhhUW4iLCJpYXQiOjE2ODU0Njk5NDR9.nv2Gex8CRBRepy0sfL3OxxDeSWsunbW6KOJ0IVG6zv4"
        homeViewModel.listArticle()
        homeViewModel.listArticle.observe(viewLifecycleOwner, { listArticle ->
            setStoryListData(listArticle)
        })
        homeViewModel.isLoading.observe(viewLifecycleOwner, {
            showLoading(it)
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setStoryListData(lisStory: List<ArticleItem?>?) {
        val listArticle = ArrayList<ItemArticle>()
        for (item in lisStory!!) {
            listArticle.add(
                ItemArticle(
                    author = item?.penulis!!,
                    title = item.judul!!,
                    description = item.deskripsi!!,
                    date = item.tanggal!!,
                    photo = item.gambar!!
                )
            )
        }
        setAdapter(listArticle)
    }

    private fun setAdapter(listArticle: ArrayList<ItemArticle>) {

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvArticles.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvArticles.addItemDecoration(itemDecoration)

        val adapter = ArticleAdapter(listArticle)
        binding.rvArticles.adapter = adapter
        binding.rvArticles.setHasFixedSize(true)


        binding.ivGrid.setOnClickListener {
            if (isGrid) {
                val layoutManager = GridLayoutManager(requireContext(), 2)
                binding.rvArticles.layoutManager = layoutManager
                val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
                binding.rvArticles.addItemDecoration(itemDecoration)

                val adapter = ArticleGridAdapter(listArticle)
                binding.rvArticles.adapter = adapter
                binding.rvArticles.setHasFixedSize(true)

                //binding.rvArticles.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.ivGrid.setImageResource(R.drawable.baseline_view_list_24)
                isGrid = false
            } else {
                val layoutManager = LinearLayoutManager(requireContext())
                binding.rvArticles.layoutManager = layoutManager
                val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
                binding.rvArticles.addItemDecoration(itemDecoration)

                val adapter = ArticleAdapter(listArticle)
                binding.rvArticles.adapter = adapter
                binding.rvArticles.setHasFixedSize(true)

               /* binding.rvArticles.layoutManager = layoutManager*/
                binding.ivGrid.setImageResource(R.drawable.baseline_grid_view_24)
                isGrid = true
            }
        }

    }

    companion object {

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}