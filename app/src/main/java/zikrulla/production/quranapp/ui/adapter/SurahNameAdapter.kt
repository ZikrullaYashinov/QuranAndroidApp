package zikrulla.production.quranapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import zikrulla.production.quranapp.R
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.MultiTypeItem
import zikrulla.production.quranapp.databinding.ItemSurahBinding
import zikrulla.production.quranapp.databinding.ItemSurahLastReadBinding
import zikrulla.production.quranapp.util.Constants

class SurahNameAdapter(
    private var list: List<MultiTypeItem>,
    private val itemClick: (surah: SurahEntity) -> Unit
) : Adapter<ViewHolder>() {

    inner class VhSurahName(private val binding: ItemSurahBinding) : ViewHolder(binding.root) {
        fun bind(surah: SurahEntity) {
            binding.apply {
                surahNameAr.text = surah.name
                surahNumberOfAyahs.text = surah.numberOfAyahs.toString()
                number.text = surah.number.toString()
                surahName.text = surah.englishName
                surahRevelationType.text = surah.revelationType
                rootLayout.setOnClickListener {
                    itemClick.invoke(surah)
                }
            }
        }
    }

    inner class VhSurahLastRead(private val binding: ItemSurahLastReadBinding) :
        ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(surahEntity: SurahEntity, position: Int) {
            binding.apply {
                lastSurahName.text = surahEntity.englishName
                lastAyahNumber.text = "${lastRead.context.getString(R.string.ayah_number)}${surahEntity.lastReadAyah}"
            }
        }
    }

    override fun getItemViewType(position: Int): Int = list[position].type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            Constants.ITEM_AYAH -> VhSurahName(
                ItemSurahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            else -> VhSurahLastRead(
                ItemSurahLastReadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val multiTypeItem = list[position].obj as SurahEntity
        if (holder is VhSurahName) {
            holder.bind(multiTypeItem)
        } else if (holder is VhSurahLastRead) {
            holder.bind(multiTypeItem, position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<MultiTypeItem>) {
        this.list = list
        notifyDataSetChanged()
    }
}