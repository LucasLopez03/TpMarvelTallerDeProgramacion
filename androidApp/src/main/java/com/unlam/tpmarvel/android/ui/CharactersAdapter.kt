package com.unlam.tpmarvel.android.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.unlam.tpmarvel.model.Character
import com.unlam.tpmarvel.android.databinding.ListItemCharacterBinding

class CharactersAdapter : ListAdapter<Character, CharacterViewHolder>(CharacterDiffCallback()){

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        return CharacterViewHolder(
            ListItemCharacterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: CharacterViewHolder) {
        super.onViewRecycled(holder)
        holder.clearImage()
    }

    override fun getItemId(position: Int): Long = getItem(position).id

    private class CharacterDiffCallback : DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        const val VIEW_TYPE_CHARACTER = 0
    }
}