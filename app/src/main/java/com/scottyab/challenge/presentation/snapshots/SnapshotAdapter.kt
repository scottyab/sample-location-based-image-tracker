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
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SnapshotViewHolder {
        val binding = ListItemSnapshotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SnapshotViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(
        holder: SnapshotViewHolder,
        position: Int
    ) {
        holder.bind(snapshotUi1 = getItem(position), onSelected = onItemClick)
    }

    class SnapshotViewHolder(
        private val binding: ListItemSnapshotBinding,
        private val imageLoader: ImageLoader
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            snapshotUi1: SnapshotUi,
            onSelected: (snapshotUi: SnapshotUi) -> Unit
        ) {
            imageLoader.load(snapshotUi1.imageUrl, binding.snapshotImageview)
            // ensuring there's CD from a accessibility point of view
            binding.snapshotImageview.contentDescription = snapshotUi1.title
            binding.root.throttledClick {
                onSelected.invoke(snapshotUi1)
            }
        }
    }
}

private val DIFF_CALLBACK =
    object : DiffUtil.ItemCallback<SnapshotUi>() {
        override fun areItemsTheSame(
            oldItem: SnapshotUi,
            newItem: SnapshotUi
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: SnapshotUi,
            newItem: SnapshotUi
        ) = oldItem == newItem
    }
