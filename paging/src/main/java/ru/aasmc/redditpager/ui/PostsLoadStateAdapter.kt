package ru.aasmc.redditpager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import ru.aasmc.paging.R
import ru.aasmc.paging.databinding.NetworkStateItemBinding

class PostsLoadStateAdapter(
    private val adapter: PostsAdapter
): LoadStateAdapter<NetworkStateItemViewHolder>() {

    override fun onBindViewHolder(holder: NetworkStateItemViewHolder, loadState: LoadState) {
        holder.bindTo(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): NetworkStateItemViewHolder {
        val binding = NetworkStateItemBinding.bind(
            LayoutInflater.from(parent.context).inflate(
                R.layout.network_state_item, parent, false
            )
        )
        return NetworkStateItemViewHolder(binding) {
            adapter.retry()
        }
    }
}