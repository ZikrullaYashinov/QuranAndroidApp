package zikrulla.production.quranapp.ui.screen.surahdetails

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import zikrulla.production.quranapp.R
import zikrulla.production.quranapp.data.local.entity.AyahUzArEntity
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.AyahItem
import zikrulla.production.quranapp.data.model.LastItem
import zikrulla.production.quranapp.data.model.MultiTypeItem
import zikrulla.production.quranapp.databinding.FragmentSurahDetaailsBinding
import zikrulla.production.quranapp.service.AudioService
import zikrulla.production.quranapp.ui.adapter.AyahAdapter
import zikrulla.production.quranapp.util.Audios
import zikrulla.production.quranapp.util.Constants.ARG_SURAH_NAME
import zikrulla.production.quranapp.util.Constants.DATA_URL
import zikrulla.production.quranapp.util.Constants.ITEM_AYAH
import zikrulla.production.quranapp.util.Constants.ITEM_SURAH_INFO
import zikrulla.production.quranapp.util.Constants.TAG
import zikrulla.production.quranapp.util.Edition
import zikrulla.production.quranapp.viewmodel.imp.SurahDetailsResource
import zikrulla.production.quranapp.viewmodel.imp.SurahDetailsViewModelImp
import kotlin.coroutines.CoroutineContext


@AndroidEntryPoint
class SurahDetailsFragment : Fragment(), CoroutineScope {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            surahEntity = requireArguments().getSerializable(ARG_SURAH_NAME) as SurahEntity
        }
    }

    private lateinit var binding: FragmentSurahDetaailsBinding
    private lateinit var handler: Handler
    private lateinit var adapter: AyahAdapter
    private val viewModel: SurahDetailsViewModelImp by viewModels()
    private var surahEntity: SurahEntity? = null
    private var audioService: AudioService? = null
    private var intent: Intent? = null
    private var lastItem: LastItem? = null
    private var playing: Boolean? = null
    private var lastVisibleItemPosition: Int? = null
    private var mBound = false
    private var isUpdateMediaPlayer = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSurahDetaailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        load()
        click()
        observe()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun load() {
        viewModel.fetchSurah(surahEntity?.number ?: 1)
        handler = Handler(Looper.getMainLooper())

        adapter = AyahAdapter(
            listOf(
                MultiTypeItem(
                    ITEM_SURAH_INFO,
                    surahEntity!!
                )
            ), { ayah, position, playing ->
                var change = false
                if (lastItem?.lastAudio != null)
                    change = lastItem?.lastAudio != ayah.ayahUzArEntity.number
                if (change)
                    adapter.updateItem(lastItem?.lastPosition, false)
                lastItem = LastItem(ayah.ayahUzArEntity.number, ayah.ayahUzArEntity.numberInSurah)
                viewModel.setLastItem(lastItem!!)
                player(lastItem, change)
                adapter.updateItem(position, !(playing ?: false))
            }, { ayah: AyahItem ->
                viewModel.saveLastRead(ayah.ayahUzArEntity.number, !ayah.ayahUzArEntity.favourite)
            }, { ayah: AyahItem ->
                shareItem(ayah.ayahUzArEntity, surahEntity!!)
            }
        )

        val layoutManager = LinearLayoutManager(requireContext())
        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            }
        })
    }

    private fun click() {
        binding.apply {
            back.setOnClickListener {
                findNavController().popBackStack()
                setService(false)
                saveLastRead()
            }
            swipe.setOnRefreshListener {
                refreshSwipe()
            }
            notInternetViewRefresh.setOnClickListener {
                refreshSwipe()
            }
            play.setOnClickListener {
                player(lastItem, false)
            }
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) audioService?.seekTo(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                findNavController().popBackStack()
                saveLastRead()
                setService(false)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observe() {
        viewModel.stateAyahUzAr.onEach { resource ->
            when (resource) {
                is SurahDetailsResource.Error -> {
                    Log.d(TAG, "observe: Error ${resource.e}")
                    swipeVisible(false)
                }

                is SurahDetailsResource.Loading -> {
                    Log.d(TAG, "observe: Loading")
                    setNotInternetView(false)
                    swipeVisible(true)
                }

                is SurahDetailsResource.NotInternet -> {
                    setNotInternetView(true)
                    swipeVisible(false)
                }

                is SurahDetailsResource.Success -> {
                    setNotInternetView(false)
                    swipeVisible(false)
                    val items = arrayListOf(MultiTypeItem(ITEM_SURAH_INFO, surahEntity!!))
                    items.addAll(resource.data.map {
                        MultiTypeItem(
                            ITEM_AYAH,
                            AyahItem(it, false)
                        )
                    })
                    adapter.submitList(items)
                    viewModel.setLastReadIsUpdate()
                    updateMediaPlayer()
                    swipeVisible(false)
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.stateService.onEach {
            if (it != null) {
                binding.player.isVisible = true
                audioService = it
                mBound = true
                handler.postDelayed(runnable, 1000)
            } else {
                mBound = false
                updateMediaPlayer(0, 0, false)
                setService(false)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.stateLastItem.onEach {
            if (it != null)
                binding.player.isVisible = true
            lastItem = it
            binding.mediaTitle.text = "${surahEntity?.englishName} Ayah ${lastItem?.lastPosition}"
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.stateLastReadIsUpdate.onEach {
            scrollToPosition()
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setNotInternetView(isVisible: Boolean) {
        binding.notInternetView.isVisible = isVisible
    }

    private fun refreshSwipe() {
        viewModel.fetchSurah(surahEntity?.number ?: 1)
    }

    private fun player(lastItem: LastItem?, change: Boolean) {
        binding.player.isVisible = true
        if (mBound) {
            if (change) {
                audioService?.releaseMediaPlayer()
                audioService?.createMediaPlayer(
                    Audios.getAyah(
                        lastItem?.lastAudio ?: 0,
                        Edition.ALAFASY
                    )
                )
            } else {
                if (playing == true) {
                    audioService?.pause()
                } else {
                    audioService?.resume()
                }
            }
            playing = audioService?.isPlaying()
            adapter.updateItem(lastItem?.lastPosition, playing)
        } else {
            setService(true, lastItem?.lastAudio)
            adapter.updateItem(lastItem?.lastPosition, true)
        }
    }

    private fun setService(isStart: Boolean = true, number: Int? = null) {
        if (isStart) {
            intent = Intent(requireActivity(), AudioService::class.java)
            intent?.putExtra(
                DATA_URL,
                Audios.getAyah(number!!, Edition.ALAFASY)
            )
            requireActivity().startService(intent)
            requireActivity().bindService(intent!!, connection, Context.BIND_EXTERNAL_SERVICE)
        } else {
            audioService?.onStop()
            handler.removeCallbacks(runnable)
            if (playing != null)
                adapter.updateItem(lastItem?.lastPosition, playing)
        }
    }

    private fun saveLastRead() {
        viewModel.saveVisibleItemPosition(surahEntity?.number!!, lastVisibleItemPosition)
        viewModel.saveLastRead(surahEntity?.number!!)
    }

    private fun swipeVisible(isVisibility: Boolean) {
        binding.swipe.isRefreshing = isVisibility
    }

    private fun scrollToPosition() {
        binding.recyclerView.scrollToPosition(surahEntity?.lastReadAyah ?: 0)
    }

    private fun shareItem(ayah: AyahUzArEntity, surah: SurahEntity) {
        val ayahResString = getString(R.string.ayah).lowercase()
        val text =
            "${surah.name} (${surah.englishName} $ayahResString ${ayah.numberInSurah})\n\n${ayah.textAr}\n\n${ayah.textUz}"
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun updateMediaPlayer() {
        isUpdateMediaPlayer = true
    }

    private fun updateMediaPlayer(duration: Int, currentPosition: Int?, playing: Boolean?) {
        val icon = if (playing == false) R.drawable.ic_play else R.drawable.ic_pause

        binding.apply {
            seekBar.max = duration
            seekBar.progress = currentPosition ?: 0
            play.setImageResource(icon)
        }

        if (isUpdateMediaPlayer && playing == true) {
            adapter.updateItem(lastItem?.lastPosition, playing)
            isUpdateMediaPlayer = false
        }

    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioService.LocalBinder
            audioService = binder.getService()

            Log.d(TAG, "onServiceConnected: ServiceConnection")

            val duration = audioService?.getDuration() ?: 0
            val currentPosition = audioService?.currentPosition()

            playing = audioService?.isPlaying()

            updateMediaPlayer(duration, currentPosition, playing)

            mBound = true

            viewModel.fetchService(mBound, audioService)
            handler.postDelayed(runnable, 1000)
        }

        override fun onBindingDied(name: ComponentName?) {
            Log.d(TAG, "onBindingDied: ")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
            viewModel.fetchService(mBound)
            adapter.updateItem(lastItem?.lastPosition, false)
            Log.d(TAG, "onServiceDisconnected: ")
        }

    }

    val runnable = object : Runnable {
        override fun run() {
            val duration = audioService?.getDuration() ?: 0
            val currentPosition = audioService?.currentPosition()
            playing = audioService?.isPlaying()

            Log.d(TAG, "run: $currentPosition")

            updateMediaPlayer(duration, currentPosition, playing)

            handler.postDelayed(this, 1000)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SurahDetailsFragment()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}