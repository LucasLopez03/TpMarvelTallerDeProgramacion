package com.unlam.tpmarvel.android.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.unlam.tpmarvel.android.R
import com.unlam.tpmarvel.android.databinding.ItemMediaBinding
import com.unlam.tpmarvel.android.model.AndroidMedia
import timber.log.Timber

class MediaAdapter : ListAdapter<AndroidMedia, MediaAdapter.MediaViewHolder>(MediaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItem(position: Int): AndroidMedia = super.getItem(position)

    override fun onViewRecycled(holder: MediaViewHolder) {
        super.onViewRecycled(holder)
        holder.clearImage()
    }

    class MediaViewHolder(
        private val binding: ItemMediaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentImageUrl: String? = null

        fun bind(media: AndroidMedia) {
            binding.mediaTitle.text = media.title

            binding.apply {
                root.contentDescription = root.context.getString(
                    R.string.media_item_content_description,
                    media.title
                )
                mediaImage.contentDescription = root.context.getString(
                    R.string.media_image_content_description,
                    media.title
                )
            }

            loadImage(media.imageUrl)
        }

        private fun loadImage(imageUrl: String) {
            if (currentImageUrl == imageUrl) {
                return
            }

            currentImageUrl = imageUrl
            binding.mediaImage.alpha = 0f

            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .fit()
                .centerCrop()
                .into(binding.mediaImage, object : Callback {
                    override fun onSuccess() {
                        binding.mediaImage.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start()
                    }

                    override fun onError(e: Exception?) {
                        Timber.e(e, "Error loading image for URL: $imageUrl")
                        binding.mediaImage.setImageResource(R.drawable.error_image)
                        binding.mediaImage.alpha = 1f
                    }
                })
        }

        fun clearImage() {
            currentImageUrl = null
            if (binding.mediaImage.drawable != null) {
                Picasso.get().cancelRequest(binding.mediaImage)
                binding.mediaImage.setImageDrawable(null)
            }
        }
    }

    private class MediaDiffCallback : DiffUtil.ItemCallback<AndroidMedia>() {
        override fun areItemsTheSame(oldItem: AndroidMedia, newItem: AndroidMedia): Boolean =
            oldItem.title == newItem.title && oldItem.imageUrl == newItem.imageUrl

        override fun areContentsTheSame(oldItem: AndroidMedia, newItem: AndroidMedia): Boolean =
            oldItem == newItem
    }
}