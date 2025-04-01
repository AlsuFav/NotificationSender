package ru.fav.notificationsender.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.fav.notificationsender.databinding.ItemColorBinding

class ColorsAdapter(
    private val colors: List<Int>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ItemColorBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ColorViewHolder(binding = binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position])
    }

    override fun getItemCount() = colors.size

    inner class ColorViewHolder(private val binding: ItemColorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val colorView = binding.colorView
        fun bind(color: Int) {
            colorView.setBackgroundColor(color)
            colorView.setOnClickListener {
                onClick(color)
            }
        }
    }
}