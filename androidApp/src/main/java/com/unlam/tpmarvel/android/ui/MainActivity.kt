package com.unlam.tpmarvel.android.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.unlam.tpmarvel.model.Character
import com.unlam.tpmarvel.ui.CharactersViewModel
import com.unlam.tpmarvel.ui.CharactersViewModelFactory
import com.unlam.tpmarvel.utils.ScreenState
import com.unlam.tpmarvel.ui.VerticalSpaceItemDecoration
import com.unlam.tpmarvel.android.R
import com.unlam.tpmarvel.android.databinding.ActivityMainBinding
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(FlowPreview::class)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CharactersViewModel
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var charactersAdapter: CharactersAdapter
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAuth()
        setupViewModel()
        setupRecyclerView()
        setupSearch()
        observeViewModel()
        setupSwipeRefresh()

    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun setupAuth() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            CharactersViewModelFactory(this.applicationContext)
        )[CharactersViewModel::class.java]
    }

    private fun setupRecyclerView() {
        charactersAdapter = CharactersAdapter()
        binding.charactersList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = charactersAdapter
            addItemDecoration(
                VerticalSpaceItemDecoration(
                resources.getDimensionPixelSize(R.dimen.item_spacing)
            )
            )

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        binding.searchEdit.clearFocus()
                    }
                }
            })
        }
    }

    private fun setupSearch() {
        binding.searchEdit.addTextChangedListener { text ->
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                text?.toString()?.let { query ->
                    viewModel.searchCharacter(query)
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.apply {
            setOnRefreshListener {
                viewModel.loadCharacters()
            }
            setColorSchemeResources(R.color.marvel_red)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenState
                    .debounce(300)
                    .collect { state ->
                        updateUI(state)
                    }
            }
        }
    }

    private fun updateUI(state: ScreenState) {
        binding.swipeRefresh.isRefreshing = false

        when (state) {
            is ScreenState.Loading -> showLoading(true)
            is ScreenState.ShowCharacters -> {
                showLoading(false)
                displayCharacters(state.list)
            }
            is ScreenState.Error -> {
                showLoading(false)
                showError(state.message)
            }
        }
    }

    private fun displayCharacters(characters: List<Character>) {
        charactersAdapter.submitList(characters)
        binding.emptyState.isVisible = characters.isEmpty()
    }

    private fun showLoading(show: Boolean) {
        binding.apply {
            loadingIndicator.isVisible = show
            charactersList.isVisible = !show
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                signOut()
                true
            }
            R.id.action_refresh -> {
                viewModel.loadCharacters()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        try {
            firebaseAuth.signOut()
            navigateToLoginScreen()
        } catch (e: Exception) {
            Timber.e(e, "Error during sign out")
            showError(getString(R.string.error_logout))
        }
    }

    private fun navigateToLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        searchJob?.cancel()
    }
}