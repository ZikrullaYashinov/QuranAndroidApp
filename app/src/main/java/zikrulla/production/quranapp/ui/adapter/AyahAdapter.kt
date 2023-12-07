package zikrulla.production.quranapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import zikrulla.production.quranapp.R
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.AyahItem
import zikrulla.production.quranapp.data.model.MultiTypeItem
import zikrulla.production.quranapp.databinding.ItemAyahBinding
import zikrulla.production.quranapp.databinding.ItemSurahInfoBinding
import zikrulla.production.quranapp.util.Constants.ITEM_AYAH

class AyahAdapter(
    private var itemList: List<MultiTypeItem>,
    private val playClick: (AyahItem, position: Int, playing: Boolean?) -> Unit,
    private val saveClick: (AyahItem) -> Unit,
    private val shareClick: (AyahItem) -> Unit,
) : Adapter<ViewHolder>() {

    inner class VhAyah(private val binding: ItemAyahBinding) : ViewHolder(binding.root) {
        fun bind(ayah: AyahItem, position: Int) {
            binding.apply {
                ayahAr.text = ayah.ayahUzArEntity.textAr
                ayahUz.text = ayah.ayahUzArEntity.textUz
                number.text = ayah.ayahUzArEntity.numberInSurah.toString()
                play.setOnClickListener { playClick.invoke(ayah, position, ayah.playing) }
                save.setOnClickListener { saveClick.invoke(ayah) }
                share.setOnClickListener { shareClick.invoke(ayah) }
                play.setImageResource(if (!ayah.playing) R.drawable.ic_play else R.drawable.ic_pause)
                val saveIcon =
                    if (ayah.ayahUzArEntity.favourite) R.drawable.ic_save_fill else R.drawable.ic_save
                save.setImageResource(saveIcon)
            }
        }
    }

    inner class VhSurahInfo(private val binding: ItemSurahInfoBinding) : ViewHolder(binding.root) {
        fun bind(surahEntity: SurahEntity, position: Int) {
            binding.apply {
                surahNameAr.text = surahEntity.name
                surahName.text = surahEntity.englishName
                surahRevelationType.text = surahEntity.revelationType
                surahNumberOfAyahs.text = surahEntity.numberOfAyahs.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ITEM_AYAH -> VhAyah(
                ItemAyahBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> VhSurahInfo(
                ItemSurahInfoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is VhAyah -> {
                holder.bind(itemList[position].obj as AyahItem, position)
            }

            is VhSurahInfo -> {
                holder.bind(itemList[position].obj as SurahEntity, position)
            }
        }

    }

    override fun getItemViewType(position: Int): Int = itemList[position].type

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(itemList: List<MultiTypeItem>) {
        this.itemList = itemList
        notifyDataSetChanged()
    }

    fun updateItem(position: Int?, playing: Boolean?) {
        (itemList[position!!].obj as AyahItem).playing = playing ?: false
        notifyItemChanged(position)
    }
}