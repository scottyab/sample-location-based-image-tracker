package com.scottyab.challenge.presentation.activities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scottyab.challenge.databinding.ListItemActivityBinding
import com.scottyab.challenge.domain.model.Activity
import com.scottyab.challenge.presentation.common.throttledClick

internal class ActivitiesAdapter(
    private val onItemClick: (item: Activity) -> Unit
) : ListAdapter<Activity, ActivitiesAdapter.ActivityViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding =
            ListItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(
            activity = getItem(position),
            onActivitySelected = onItemClick
        )
    }

    class ActivityViewHolder(
        private val binding: ListItemActivityBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            activity: Activity,
            onActivitySelected: (item: Activity) -> Unit
        ) {
            binding.apply {
                title.text = activity.title
                root.throttledClick { onActivitySelected.invoke(activity) }
            }
        }
    }
}
private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Activity>() {
    override fun areItemsTheSame(oldItem: Activity, newItem: Activity) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Activity, newItem: Activity) =
        oldItem == newItem
}
