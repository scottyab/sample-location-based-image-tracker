package com.scottyab.challenge.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.scottyab.challenge.databinding.ActivityActivitiesListBinding
import com.scottyab.challenge.domain.model.Activity
import com.scottyab.challenge.presentation.common.throttledClick
import com.scottyab.challenge.presentation.common.viewBinding
import com.scottyab.challenge.presentation.snapshots.SnapshotsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * OMG naming! I should have used the Thesaurus for "Activity" */
class ActivitiesActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityActivitiesListBinding::inflate)
    private val viewModel: ActivitiesViewModel by viewModel()

    private val activitiesAdapter = ActivitiesAdapter(
        onItemClick = { viewModel.activityItemClick(it.id) }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.state.observe(this) { state ->
            bindView(state.activities, state.shouldShowEmpty)
        }

        viewModel.openNewActivityLiveData.observe(this) {
            startActivity(SnapshotsActivity.openIntent(this))
        }

        viewModel.openExistingActivityLiveData.observe(this) { activityId ->
            startActivity(SnapshotsActivity.openIntentExisting(this, activityId))
        }

        initView()
    }

    private fun initView() {
        binding.activitiesRecyclerview.adapter = activitiesAdapter
        binding.newActivityFab.throttledClick {
            viewModel.startNewActivityClick()
        }
    }

    private fun bindView(activities: List<Activity>, shouldShowEmpty: Boolean) {
        activitiesAdapter.submitList(activities)
        binding.activitiesEmptyContainer.isVisible = shouldShowEmpty
    }
}
