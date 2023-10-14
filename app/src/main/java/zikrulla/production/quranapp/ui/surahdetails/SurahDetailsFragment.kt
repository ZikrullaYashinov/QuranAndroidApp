package zikrulla.production.quranapp.ui.surahdetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import zikrulla.production.quranapp.adapter.AyahAdapter
import zikrulla.production.quranapp.databinding.FragmentSurahDetaailsBinding
import zikrulla.production.quranapp.db.entity.SurahEntity
import zikrulla.production.quranapp.model.Resource
import zikrulla.production.quranapp.util.Util
import zikrulla.production.quranapp.util.Util.ARG_SURAH_NAME

@AndroidEntryPoint
class SurahDetailsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            surahEntity = requireArguments().getSerializable(ARG_SURAH_NAME) as SurahEntity
        }
    }

    private lateinit var binding: FragmentSurahDetaailsBinding
    private val viewModel: SurahDetailsViewModel by viewModels()
    private val adapter by lazy { AyahAdapter(emptyList()) }
    private var surahEntity: SurahEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSurahDetaailsBinding.inflate(inflater, container, false)

        load()
        click()
        observe()

        return binding.root
    }

    private fun load() {
        viewModel.fetchSurah(surahEntity?.number ?: 1)
        binding.apply {
            recyclerView.adapter = adapter
            surahNameAr.text = surahEntity?.name
            surahName.text = surahEntity?.englishName
            surahRevelationType.text = surahEntity?.revelationType
            surahNumberOfAyahs.text = surahEntity?.numberOfAyahs.toString()
        }
    }

    private fun click() {
        binding.apply {
            back.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun observe() {
        viewModel.getSurah().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    Log.d(Util.TAG, "observe: Error ${it.e}")
                }

                is Resource.Loading -> {
                    Log.d(Util.TAG, "observe: Loading")
                }

                is Resource.Success -> {
//                    Log.d(Util.TAG, "observe: Success ${it.data}")
                    binding.apply {
                        surahNameAr.text = it.data.name
                        surahName.text = it.data.englishName
                        surahRevelationType.text = it.data.revelationType
                        surahNumberOfAyahs.text = it.data.numberOfAyahs.toString()
                    }
                }
            }
        }
        viewModel.getAyahUzAr().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    Log.d(Util.TAG, "observe: Error ${it.e}")
                }

                is Resource.Loading -> {
                    Log.d(Util.TAG, "observe: Loading")
                }

                is Resource.Success -> {
//                    Log.d(Util.TAG, "observe: Success ${it.data}")
                    adapter.submitList(it.data)
                }
            }
        }
    }

    private fun swipeVisible(isVisibility: Boolean) {
        binding.swipe.isRefreshing = isVisibility
    }

    companion object {
        @JvmStatic
        fun newInstance() = SurahDetailsFragment()
    }
}