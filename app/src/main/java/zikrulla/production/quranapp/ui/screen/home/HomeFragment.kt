package zikrulla.production.quranapp.ui.screen.home

import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import zikrulla.production.quranapp.R
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.MultiTypeItem
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.databinding.FragmentHomeBinding
import zikrulla.production.quranapp.receiver.InternetReceiver
import zikrulla.production.quranapp.ui.adapter.SurahNameAdapter
import zikrulla.production.quranapp.util.Constants
import zikrulla.production.quranapp.util.Constants.ITEM_SURAH_LAST_READ
import zikrulla.production.quranapp.util.Constants.ITEM_SURAH_NAME
import zikrulla.production.quranapp.util.Constants.TAG
import zikrulla.production.quranapp.viewmodel.imp.HomeResource
import zikrulla.production.quranapp.viewmodel.imp.HomeViewModelImp
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class HomeFragment : Fragment(), CoroutineScope {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModelImp by viewModels()
    private lateinit var job: Job
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
        refreshSwipe()
        job = Job()
    }

    private fun setNotInternetView(isVisible: Boolean) {
        binding.notInternetView.isVisible = isVisible
    }

    private fun click() {
        binding.apply {
            swipe.setOnRefreshListener {
                refreshSwipe()
            }
            notInternetViewRefresh.setOnClickListener {
                refreshSwipe()
            }
        }
    }

    private fun refreshSwipe() {
        viewModel.fetchSurahListNameDB()
        swipeVisible(true)
        setNotInternetView(false)
    }

    private fun observe() {
        viewModel.getSurahNameList()
            .onEach { resourceSurahes ->
                when (resourceSurahes) {
                    is HomeResource.Error -> {
                        Log.d(TAG, "observe: Error ${resourceSurahes.e}")
                        swipeVisible(false)
                    }

                    is HomeResource.Loading -> {
                        Log.d(TAG, "observe: Loading")
                        swipeVisible(false)
                    }

                    is HomeResource.NotInternet -> {
                        setNotInternetView(true)
                    }

                    is HomeResource.Success -> {
                        Log.d(TAG, "observe: Success ${resourceSurahes.data}")
                        setNotInternetView(false)
                        swipeVisible(false)
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

    private fun swipeVisible(isVisibility: Boolean) {
        binding.swipe.isRefreshing = isVisibility
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.d(TAG, "CoroutineExceptionHandler: $throwable")
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job + handler

}