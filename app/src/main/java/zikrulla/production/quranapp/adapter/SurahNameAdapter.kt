package zikrulla.production.quranapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import zikrulla.production.quranapp.databinding.ItemSurahBinding
import zikrulla.production.quranapp.db.entity.SurahEntity
import zikrulla.production.quranapp.model.SurahName

class SurahNameAdapter(
    private var list: List<SurahEntity>,
    private val itemClick: (surah: SurahEntity) -> Unit
) : Adapter<SurahNameAdapter.Vh>() {

    inner class Vh(private val binding: ItemSurahBinding) : ViewHolder(binding.root) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Vh(ItemSurahBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.bind(list[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<SurahEntity>) {
        this.list = list
        notifyDataSetChanged()
    }
}