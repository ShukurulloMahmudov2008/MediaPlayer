package com.example.mediaplayer

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.databinding.ItemRvBinding

class RvAdapter(var list: List<Music>, var rvItemClick: RvItemClick)
    :RecyclerView.Adapter<RvAdapter.Vh>(){

    inner class Vh(val binding:ItemRvBinding):RecyclerView.ViewHolder(binding.root){

        fun onBind(music: Music, position:Int){
            binding.txtItemArtist.text = music.author
            binding.txtItemTitle.text = music.title
            if (music.imagePath!=""){
                val bm = BitmapFactory.decodeFile(list[position].imagePath)
                binding.imageView.setImageBitmap(bm)
            }
            itemView.setOnClickListener {
                rvItemClick.itemClick(music, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context),parent,false))
//        return Vh(LayoutInflater.from(parent.context).inflate(R.layout.item_rv, parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
}
interface RvItemClick{
    fun itemClick(music: Music, position: Int)
}