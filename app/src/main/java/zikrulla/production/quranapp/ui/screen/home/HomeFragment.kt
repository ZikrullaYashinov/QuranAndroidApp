package zikrulla.production.quranapp.ui.screen.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import zikrulla.production.quranapp.R
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.MultiTypeItem
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.databinding.FragmentHomeBinding
import zikrulla.production.quranapp.ui.adapter.SurahNameAdapter
import zikrulla.production.quranapp.util.Constants
import zikrulla.production.quranapp.util.Constants.ITEM_SURAH_LAST_READ
import zikrulla.production.quranapp.util.Constants.ITEM_SURAH_NAME
import zikrulla.production.quranapp.util.Constants.TAG
import zikrulla.production.quranapp.viewmodel.imp.HomeViewModelImp

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModelImp by viewModels()
    private val adapter by lazy {
        SurahNameAdapter(emptyList()) {
            val bundle = Bundle()
            bundle.putSerializable(Constants.ARG_SURAH_NAME, it)
            findNavController().navigate(R.id.action_homeFragment_to_surahDetailsFragment, bundle)
        }
    }
    private var lastReadSurahId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        load()
        click()
        observe()
    }

    private fun load() {
        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
        lastReadSurahId = viewModel.getLastRead() ?: 0
    }

    private fun click() {

    }

    private fun observe() {
        viewModel.getSurahNameList()
            .onEach { resourceSurahes ->
                when (resourceSurahes) {
                    is Resource.Error -> {
                        Log.d(TAG, "observe: Error ${resourceSurahes.e}")
                    }

                    is Resource.Loading -> {
                        Log.d(TAG, "observe: Loading")
                    }

                    is Resource.Success -> {
                        Log.d(TAG, "observe: Success ${resourceSurahes.data}")
                        val multiTypeItems = mutableListOf<MultiTypeItem>()
                        if (lastReadSurahId > 0)
                            multiTypeItems.add(
                                MultiTypeItem(
                                    ITEM_SURAH_LAST_READ,
                                    resourceSurahes.data[lastReadSurahId - 1]
                                )
                            )
                        resourceSurahes.data.map {
                            multiTypeItems.add(MultiTypeItem(ITEM_SURAH_NAME, it))
                        }
                        adapter.submitList(multiTypeItems)
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

}