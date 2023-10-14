package zikrulla.production.quranapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import zikrulla.production.quranapp.databinding.ItemAyahBinding
import zikrulla.production.quranapp.db.entity.AyahUzArEntity

class AyahAdapter(
    private var ayahList: List<AyahUzArEntity>
) : Adapter<AyahAdapter.Vh>() {

    inner class Vh(private val binding: ItemAyahBinding) : ViewHolder(binding.root) {
        fun bind(ayah: AyahUzArEntity) {
            binding.apply {
                ayahAr.text = ayah.textAr
                ayahUz.text = ayah.textUz
                number.text = ayah.numberInSurah.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Vh(ItemAyahBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = ayahList.size

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.bind(ayahList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(ayahList: List<AyahUzArEntity>) {
        this.ayahList = ayahList
        notifyDataSetChanged()
    }
}