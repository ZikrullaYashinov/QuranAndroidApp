package zikrulla.production.quranapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import zikrulla.production.quranapp.R
import zikrulla.production.quranapp.data.model.Ayah
import zikrulla.production.quranapp.databinding.ItemAyahBinding

class AyahAdapter(
    private var ayahList: List<Ayah>,
    private val playClick: (Ayah, position: Int, _playing: Boolean?) -> Unit
) : Adapter<AyahAdapter.Vh>() {

    var playing: Boolean? = null

    inner class Vh(private val binding: ItemAyahBinding) : ViewHolder(binding.root) {
        fun bind(ayah: Ayah, position: Int) {
            binding.apply {
                ayahAr.text = ayah.ayahUzArEntity.textAr
                ayahUz.text = ayah.ayahUzArEntity.textUz
                number.text = ayah.ayahUzArEntity.numberInSurah.toString()
                play.setOnClickListener {
                    playClick.invoke(ayah, position, ayah.playing)
                }
                play.setImageResource(if (!ayah.playing) R.drawable.ic_play else R.drawable.ic_pause)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Vh(ItemAyahBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = ayahList.size

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.bind(ayahList[position], position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(ayahList: List<Ayah>) {
        this.ayahList = ayahList
        notifyDataSetChanged()
    }

    fun updateItem(position: Int?, _playing: Boolean?) {
        ayahList[position!!].playing = _playing ?: false
        notifyItemChanged(position)
    }
}