package ru.aasmc.redditpager.ui

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import ru.aasmc.paging.databinding.NetworkStateItemBinding

/**
 * A View Holder that can display a loading or have click action.
 * It is used to show the network state of paging.
 */
class NetworkStateItemViewHolder(
    private val binding: NetworkStateItemBinding,
    private val retryCallback: () -> Unit
): RecyclerView.ViewHolder(
    binding.root
) {
    fun bindTo(loadState: LoadState) {
        binding.retryButton.setOnClickListener {
            retryCallback()
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
        binding.errorMsg.text = (loadState as? LoadState.Error)?.error?.message
    }
}