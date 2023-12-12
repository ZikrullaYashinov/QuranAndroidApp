package zikrulla.production.quranapp.ui.screen.surahdetails

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
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
import androidx.activity.result.contract.ActivityResultContracts
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
import kotlinx.coroutines.launch
import zikrulla.production.quranapp.R
import zikrulla.production.quranapp.data.local.entity.AyahUzArEntity
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
import zikrulla.production.quranapp.util.Constants.DATA_URI
import zikrulla.production.quranapp.util.Constants.DATA_URL
import zikrulla.production.quranapp.util.Constants.ITEM_AYAH
import zikrulla.production.quranapp.util.Constants.ITEM_SURAH_INFO
import zikrulla.production.quranapp.util.Constants.TAG
import zikrulla.production.quranapp.util.Edition
import zikrulla.production.quranapp.viewmodel.imp.SurahDetailsViewModelImp
import java.io.IOException
import java.io.InputStream
import java.net.URL
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

    // Create intent to launch file picker
    private val REQUEST_SAF_CODE = 101
    private var uri: Uri? = null
//    private var uri: Uri? = Uri.parse("content://com.android.providers.downloads.documents/document/37")


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
                viewModel.updateIsFavourite(
                    ayah.ayahUzArEntity.number,
                    !ayah.ayahUzArEntity.favourite
                )
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

    private fun selectAudioUri() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/*" // Set type to "audio/*" for sound files
            putExtra(
                Intent.EXTRA_TITLE,
                "${surahEntity?.englishName} ${Edition.ALAFASY}.mp3"
            ) // Set file name
        }
        getContent.launch(intent)
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d(TAG, "selectAudioUri: ${result.data?.data}")
            uri = result.data?.data ?: return@registerForActivityResult

            launch {
                downloadAudioFromUrl(
                    Audios.getSurah(surahEntity?.number ?: 0, Edition.ALAFASY),
                    uri!!
                )
                surahEntity?.let {
                    it.downloadedUri = uri.toString()
                    viewModel.updateSurah(it)
                }
            }
        }

    private fun downloadAudioFromUrl(urlString: String, uri: Uri) {

        // Get content resolver
        val contentResolver = requireContext().contentResolver

        // Open URL connection
        val url = URL(urlString)
        val urlConnection = url.openConnection()

        // Get content length for progress tracking
        val contentLength = urlConnection.contentLength

        // Open input stream for URL
        val inputStream = urlConnection.getInputStream()

        // Open output stream for the chosen location
        val outputStream = contentResolver.openOutputStream(uri) ?: return

        // Buffer size for efficient data transfer
        val buffer = ByteArray(1024)

        // Download progress variable
        var downloadedBytes = 0

        // Read data from URL
        var bytesRead: Int = inputStream.read(buffer)

        // Write data to chosen location and update progress
        while (bytesRead != -1) {
            outputStream.write(buffer, 0, bytesRead)
            downloadedBytes += bytesRead

            // Update progress bar or text
            val progress = (downloadedBytes * 100f / contentLength).toLong()
            // Update progress UI according to your implementation
            Log.d(TAG, "downloadAudioFromUrl: $progress")

            bytesRead = inputStream.read(buffer)
        }

        // Close streams
        outputStream.close()
        inputStream.close()

        // Show download completion message
//        Toast.makeText(context, "Audio downloaded successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun click() {
        binding.apply {
            back.setOnClickListener {
                findNavController().popBackStack()
                setService(false)
                saveLastRead()
            }
            swipe.setOnRefreshListener {
                viewModel.fetchSurah(surahEntity?.number ?: 1)
            }
            play.setOnClickListener {
                player(lastItem, false)
            }
            playSurah.setOnClickListener {
                player(lastItem, true, isSurah = true)
//                if (uri == null) {
//                    surahEntity?.downloadedUri?.let {
//                        Log.d(TAG, "click: $it")
//                        uri = Uri.parse(it)
//                    }
//                }
//
//                Log.d(TAG, "uri: ${uri.toString()}")
//                Log.d(TAG, "surah: ${surahEntity?.downloadedUri.toString()}")
//
//                if (uri == null) {
//                    Log.d(TAG, "click uri null: ${uri.toString()}")
//                    selectAudioUri()
//                } else {
//                    try {
//                        val hasAudio = hasAudio(requireContext(), uri!!)
//                        Log.d(TAG, "click: $hasAudio")
//                        player(lastItem, true, isSurah = true)
//                    } catch (e: Exception) {
//                        Log.d(TAG, "click: ${e.message}")
//                        selectAudioUri()
//                    }
//                }
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

    private fun hasAudio(context: Context, uri: Uri): Boolean {
        var inputStream: InputStream? = null
        return try {
            // Open an InputStream using the ContentResolver
            inputStream = context.contentResolver.openInputStream(uri)

            // Check if the InputStream is not null, indicating that the resource is available
            inputStream != null
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            // Close the InputStream to release resources
            inputStream?.close()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun observe() {
        viewModel.stateAyahUzAr.onEach { resource ->
            when (resource) {
                is Resource.Error -> {
                    Log.d(TAG, "observe: Error ${resource.e}")
                    swipeVisible(false)
                }

                is Resource.Loading -> {
                    Log.d(TAG, "observe: Loading")
                    swipeVisible(true)
                }

                is Resource.Success -> {
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

    private fun player(lastItem: LastItem?, change: Boolean, isSurah: Boolean = false) {
        binding.player.isVisible = true
        val url =
            if (isSurah) {
                Audios.getSurah(surahEntity?.number!!, Edition.ALAFASY)
            } else {
                Audios.getAyah(lastItem?.lastAudio ?: 0, Edition.ALAFASY)
            }
        if (mBound) {
            if (change) {
                audioService?.releaseMediaPlayer()
                if (isSurah)
                    audioService?.createMediaPlayer(
                        uri = uri
                    )
                else
                    audioService?.createMediaPlayer(
                        url = url
                    )
            } else {
                if (playing == true) {
                    audioService?.pause()
                } else {
                    audioService?.resume()
                }
            }
            if (!isSurah) {
                playing = audioService?.isPlaying()
                adapter.updateItem(lastItem?.lastPosition, playing)
            }
        } else {
            setService(true, url, isSurah)
            adapter.updateItem(lastItem?.lastPosition, true)
        }
    }

    private fun setService(
        isStart: Boolean = true,
        urlString: String? = null,
        isSurah: Boolean = false
    ) {
        if (isStart) {
            intent = Intent(requireActivity(), AudioService::class.java)
//            if (isSurah) {
//                Log.d(TAG, "setService: $uri")
//                intent?.putExtra(
//                    DATA_URI,
//                    uri.toString()
//                )
//            } else
            intent?.putExtra(DATA_URL, urlString)
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
        viewModel.updateIsFavourite(surahEntity?.number!!)
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