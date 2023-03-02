package com.scottyab.challenge.presentation.snapshots

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scottyab.challenge.databinding.ListItemSnapshotBinding
import com.scottyab.challenge.presentation.common.ImageLoader
import com.scottyab.challenge.presentation.common.throttledClick
import com.scottyab.challenge.presentation.snapshots.model.SnapshotUi

internal class SnapshotAdapter(
    private val imageLoader: ImageLoader,
    private val onItemClick: (item: SnapshotUi) -> Unit
) : ListAdapter<SnapshotUi, SnapshotAdapter.SnapshotViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotViewHolder {
        val binding = ListItemSnapshotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SnapshotViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(holder: SnapshotViewHolder, position: Int) {
        holder.bind(article = getItem(position), onArticleSelected = onItemClick)
    }

    class SnapshotViewHolder(
        private val binding: ListItemSnapshotBinding,
        private val imageLoader: ImageLoader
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: SnapshotUi, onArticleSelected: (snapshotUi: SnapshotUi) -> Unit) {
            imageLoader.load(article.imageUrl, binding.snapshotImageview)
            // ensuring there's CD from a accessibility point of view
            binding.snapshotImageview.contentDescription = article.title
            binding.root.throttledClick {
                onArticleSelected.invoke(article)
            }
        }
    }
}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SnapshotUi>() {
    override fun areItemsTheSame(oldItem: SnapshotUi, newItem: SnapshotUi) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SnapshotUi, newItem: SnapshotUi) =
        oldItem == newItem
}
