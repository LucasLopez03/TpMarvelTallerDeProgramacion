package com.unlam.tpmarvel.android.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.unlam.tpmarvel.ui.HorizontalSpaceItemDecoration
import com.unlam.tpmarvel.android.databinding.ActivityCharacterDetailBinding
import com.unlam.tpmarvel.android.model.AndroidCharacter
import timber.log.Timber
import com.unlam.tpmarvel.android.R

class CharacterDetalleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCharacterDetailBinding
    private val moviesAdapter = MediaAdapter()
    private val seriesAdapter = MediaAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharacterDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerViews()
        loadCharacterDetails()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }
    }

    private fun setupRecyclerViews() {
        binding.moviesRecyclerView.apply {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(this@CharacterDetalleActivity,
                LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(
                HorizontalSpaceItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.item_spacing))
            )
        }

        binding.seriesRecyclerView.apply {
            adapter = seriesAdapter
            layoutManager = LinearLayoutManager(this@CharacterDetalleActivity,
                LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(
                HorizontalSpaceItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.item_spacing))
            )
        }
    }

    private fun loadCharacterDetails() {
        val character = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_CHARACTER, AndroidCharacter::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_CHARACTER)
        }

        character?.let { characterData ->
            Timber.d("Character loaded: ${characterData.name}")
            Timber.d("Comics: ${characterData.movies.size}")
            Timber.d("Series: ${characterData.series.size}")

            binding.apply {
                detailToolbar.title = characterData.name

                characterDescription.text = characterData.description.ifBlank {
                    getString(R.string.no_description_available)
                }

                loadCharacterImage(characterData.thumbnailUrl)

                moviesSectionTitle.text = getString(R.string.movies_title)

                moviesSection.isVisible = characterData.movies.isNotEmpty()
                seriesSection.isVisible = characterData.series.isNotEmpty()

                moviesAdapter.submitList(characterData.movies)
                seriesAdapter.submitList(characterData.series)
            }
        }
    }

    private fun loadCharacterImage(imageUrl: String) {
        binding.apply {
            characterImage.alpha = 0f
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .fit()
                .centerCrop()
                .into(characterImage, object : Callback {
                    override fun onSuccess() {
                        characterImage.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start()
                    }
                    override fun onError(e: Exception?) {
                        Timber.e(e, "Error loading image")
                        characterImage.setImageResource(R.drawable.error_image)
                        characterImage.alpha = 1f
                    }
                })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        private const val EXTRA_CHARACTER = "extra_character"

        fun newIntent(context: Context, character: AndroidCharacter): Intent {
            return Intent(context, CharacterDetalleActivity::class.java).apply {
                putExtra(EXTRA_CHARACTER, character)
            }
        }
    }
}