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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import zikrulla.production.quranapp.R
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.AyahItem
import zikrulla.production.quranapp.data.model.LastItem
import zikrulla.production.quranapp.data.model.MultiTypeItem
import zikrulla.production.quranapp.data.model.Resource
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
import zikrulla.production.quranapp.viewmodel.imp.SurahDetailsViewModelImp


@AndroidEntryPoint
class SurahDetailsFragment : Fragment() {

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
    private var firstVisibleItemPosition: Int? = null
    private var mBound = false

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
            )
        ) { ayah, position, _playing ->
            var change = false
            if (lastItem?.lastAudio != null)
                change = lastItem?.lastAudio != ayah.ayahUzArEntity.number
            if (change)
                adapter.updateItem(lastItem?.lastPosition, false)
            lastItem = LastItem(ayah.ayahUzArEntity.number, ayah.ayahUzArEntity.numberInSurah)
            viewModel.setLastItem(lastItem!!)
            player(lastItem, change)
            adapter.updateItem(position, !(_playing ?: false))
        }
        val layoutManager = LinearLayoutManager(requireContext())

        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            }
        })
    }

    private fun click() {
        binding.apply {
            back.setOnClickListener {
                findNavController().popBackStack()
                setService(false)
            }
            swipe.setOnRefreshListener {
                viewModel.fetchSurah(surahEntity?.number ?: 1)
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
                setService(false)
                findNavController().popBackStack()
            }
        }
    }

    private fun player(_lastItem: LastItem?, change: Boolean) {
        binding.player.isVisible = true
        if (mBound) {
            if (change) {
                audioService?.releaseMediaPlayer()
                audioService?.createMediaPlayer(
                    Audios.getAyah(
                        _lastItem?.lastAudio ?: 0,
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
            adapter.updateItem(_lastItem?.lastPosition, playing)
        } else {
            setService(true, _lastItem?.lastAudio)
            adapter.updateItem(_lastItem?.lastPosition, true)
        }
    }

    private fun setService(isStart: Boolean = true, number: Int? = null) {
        intent = Intent(requireActivity(), AudioService::class.java)
        if (isStart) {
            intent?.putExtra(
                DATA_URL,
                Audios.getAyah(number!!, Edition.ALAFASY)
            )
            requireActivity().startService(intent)
            requireActivity().bindService(intent!!, connection, Context.BIND_EXTERNAL_SERVICE)
        } else {
            audioService?.onStop()
            handler.removeCallbacks(runnable)
            viewModel.saveVisibleItemPosition(surahEntity?.number!!, firstVisibleItemPosition)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observe() {
        viewModel.getAyahUzAr().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    Log.d(TAG, "observe: Error ${it.e}")
                    swipeVisible(false)
                }

                is Resource.Loading -> {
                    Log.d(TAG, "observe: Loading")
                    swipeVisible(true)
                }

                is Resource.Success -> {
                    val items = arrayListOf(MultiTypeItem(ITEM_SURAH_INFO, surahEntity!!))
                    items.addAll(it.data.map { MultiTypeItem(ITEM_AYAH, AyahItem(it, false)) })
                    adapter.submitList(items)
                    binding.recyclerView.scrollToPosition(surahEntity?.lastReadAyah ?: 0)
                    swipeVisible(false)
                }
            }
        }
        viewModel.getService().observe(viewLifecycleOwner) {
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
        }
        viewModel.getLastAudioUrl().observe(viewLifecycleOwner) {
            if (it != null)
                binding.player.isVisible = true
            lastItem = it
            binding.mediaTitle.text = "${surahEntity?.englishName} Ayah ${lastItem?.lastPosition}"
        }
    }

    private fun swipeVisible(isVisibility: Boolean) {
        binding.swipe.isRefreshing = isVisibility
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

    private fun updateMediaPlayer(duration: Int, currentPosition: Int?, playing: Boolean?) {
        val icon = if (playing == false) R.drawable.ic_play else R.drawable.ic_pause

        binding.apply {
            seekBar.max = duration
            seekBar.progress = currentPosition ?: 0
            play.setImageResource(icon)
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
}