package com.unlam.tpmarvel.android.ui

import android.text.method.ScrollingMovementMethod
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.unlam.tpmarvel.model.Character
import com.unlam.tpmarvel.android.R
import com.unlam.tpmarvel.android.databinding.ListItemCharacterBinding
import com.unlam.tpmarvel.android.model.AndroidCharacter
import com.unlam.tpmarvel.android.model.AndroidMedia
import timber.log.Timber

class CharacterViewHolder(private val binding: ListItemCharacterBinding) : RecyclerView.ViewHolder(binding.root) {

    private var currentImageUrl: String? = null

    init {
        binding.description.movementMethod = ScrollingMovementMethod()
    }

    fun bind(character: Character) {
        val androidCharacter = AndroidCharacter(
            id = character.id,
            name = character.name,
            description = character.description,
            thumbnailUrl = character.thumbnailUrl,
            movies = character.movies.map {
                AndroidMedia(title = it.title, imageUrl = it.imageUrl)
            },
            series = character.series.map {
                AndroidMedia(title = it.title, imageUrl = it.imageUrl)
            }
        )

        binding.root.setOnClickListener {
            binding.root.context.startActivity(
                CharacterDetalleActivity.newIntent(binding.root.context, androidCharacter)
            )
        }

        binding.apply {
            name.text = androidCharacter.name
            description.apply {
                isVisible = true
                text = androidCharacter.description.ifBlank {
                    root.context.getString(R.string.no_description_available)
                }
                setTextIsSelectable(true)
            }

            root.contentDescription = root.context.getString(
                R.string.character_card_content_description,
                androidCharacter.name
            )

            loadCharacterImage(androidCharacter)
        }
    }

    private fun loadCharacterImage(character: AndroidCharacter) {
        if (currentImageUrl == character.thumbnailUrl) {
            return  // La misma imagen ya está cargada
        }

        binding.apply {
            if (character.thumbnailUrl.isNotEmpty() &&
                !character.thumbnailUrl.contains("image_not_available")) {

                currentImageUrl = character.thumbnailUrl
                image.isVisible = true

                Picasso.get()
                    .load(character.thumbnailUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .fit()
                    .centerCrop()
                    .into(image, object : Callback {
                        override fun onSuccess() {
                            image.animate()
                                .alpha(1f)
                                .setDuration(300)
                                .start()
                            image.contentDescription = root.context.getString(
                                R.string.character_image_content_description,
                                character.name
                            )
                        }
                        override fun onError(e: Exception?) {
                            Timber.e(e, "Error loading image for ${character.name}")
                            image.setImageResource(R.drawable.error_image)
                            image.alpha = 1f
                        }
                    })
            } else {
                currentImageUrl = null
                image.setImageResource(R.drawable.no_image_available)
            }
        }
    }

    fun clearImage() {
        currentImageUrl = null
        if (binding.image.drawable != null) {
            Picasso.get().cancelRequest(binding.image)
            binding.image.setImageDrawable(null)
        }
    }
}