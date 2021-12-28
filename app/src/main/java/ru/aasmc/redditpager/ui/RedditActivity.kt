package ru.aasmc.redditpager.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.paging.LoadState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.aasmc.redditpager.GlideApp
import ru.aasmc.redditpager.ServiceLocator
import ru.aasmc.redditpager.databinding.ActivityMainBinding
import ru.aasmc.redditpager.databinding.ActivityRedditBinding
import ru.aasmc.redditpager.paging.asMergesLoadStates
import ru.aasmc.redditpager.repo.RedditPostRepository

class RedditActivity : AppCompatActivity() {
    lateinit var binding: ActivityRedditBinding
        private set

    private val model: SubredditViewModel by viewModels {
        object: AbstractSavedStateViewModelFactory(this, null) {
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                val repoTypeParam = intent.getIntExtra(KEY_REPOSITORY_TYPE, 0)
                val repoType = RedditPostRepository.Type.values()[repoTypeParam]
                val repo = ServiceLocator.instance(this@RedditActivity)
                    .getRepository(repoType)
                @Suppress("UNCHECKED_CAST")
                return SubredditViewModel(repo, handle) as T
            }
        }
    }

    private lateinit var adapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initSwipeToRefresh()
        initSearch()
    }

    private fun initAdapter() {
        val glide = GlideApp.with(this)
        adapter = PostsAdapter(glide)
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostsLoadStateAdapter(adapter),
            footer = PostsLoadStateAdapter(adapter)
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collect { loadStates ->
                    binding.swipeRefresh.isRefreshing = loadStates.mediator?.refresh is LoadState.Loading
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.posts.collectLatest {
                    adapter.submitData(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow
                    // Use a state-machine to track LoadStates such that we only transition to
                    // NotLoading from a RemoteMediator load if it was also presented to UI.
                    .asMergesLoadStates()
                    // Only emit when REFRESH changes, as we only want to react on loads replacing the
                    // list.
                    .distinctUntilChangedBy { it.refresh }
                    // Only react to cases where REFRESH completes i.e., NotLoading.
                    .filter { it.refresh is LoadState.NotLoading }
                    // Scroll to top is synchronous with UI updates, even if remote load was triggered.
                    .collect { binding.list.scrollToPosition(0) }
            }
        }
    }

    private fun initSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener { adapter.refresh() }
    }

    private fun initSearch() {
        binding.input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updatedSubredditFromInput()
                true
            } else {
                false
            }
        }
        binding.input.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updatedSubredditFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updatedSubredditFromInput() {
        binding.input.text.trim().toString().let {
            if (it.isNotBlank()) {
                model.showSubreddit(it)
            }
        }
    }

    companion object {
        const val KEY_REPOSITORY_TYPE = "repository_type"
        fun intentFor(context: Context, type: RedditPostRepository.Type): Intent {
            val intent = Intent(context, RedditActivity::class.java)
            intent.putExtra(KEY_REPOSITORY_TYPE, type.ordinal)
            return intent
        }
    }
}





































