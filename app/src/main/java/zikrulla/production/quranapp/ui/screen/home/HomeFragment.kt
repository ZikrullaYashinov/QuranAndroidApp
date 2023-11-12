package zikrulla.production.quranapp.ui.screen.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import zikrulla.production.quranapp.R
import zikrulla.production.quranapp.ui.adapter.SurahNameAdapter
import zikrulla.production.quranapp.databinding.FragmentHomeBinding
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.util.Constants.ARG_SURAH_NAME
import zikrulla.production.quranapp.util.Constants.TAG
import zikrulla.production.quranapp.viewmodel.imp.HomeViewModelImp

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModelImp by viewModels()
    private val adapter by lazy {
        SurahNameAdapter(emptyList()) {
            val bundle = Bundle()
            bundle.putSerializable(ARG_SURAH_NAME, it)
            findNavController().navigate(R.id.surahDetailsFragment, bundle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        load()
        click()
        observe()

        return binding.root
    }

    private fun load() {
        binding.apply {
            recyclerView.adapter = adapter
        }
    }

    private fun click() {

    }

    private fun observe() {
        viewModel.getSurahNameList().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    Log.d(TAG, "observe: Error ${it.e}")
                }

                is Resource.Loading -> {
                    Log.d(TAG, "observe: Loading")
                }

                is Resource.Success -> {
                    Log.d(TAG, "observe: Success ${it.data}")
                    adapter.submitList(it.data)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}