package com.scottyab.challenge.presentation.snapshots

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.scottyab.challenge.R
import com.scottyab.challenge.databinding.ActivitySnapshotsListBinding
import com.scottyab.challenge.presentation.common.ImageLoader
import com.scottyab.challenge.presentation.common.SimpleThrottler
import com.scottyab.challenge.presentation.common.action
import com.scottyab.challenge.presentation.common.createShareIntent
import com.scottyab.challenge.presentation.common.openAppSettings
import com.scottyab.challenge.presentation.common.snack
import com.scottyab.challenge.presentation.common.viewBinding
import com.scottyab.challenge.presentation.snapshots.model.SnapshotUi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SnapshotsActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivitySnapshotsListBinding::inflate)
    private val imageLoader by inject<ImageLoader>()
    private val clickThrottler by inject<SimpleThrottler>()
    private val viewModel: SnapshotsViewModel by viewModel()

    private lateinit var adapter: SnapshotAdapter

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            // todo more robust checking of all the permissions needed
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // request background location permission (needs to be a separate request)
                checkBackgroundLocationPermission()
            } else -> {
                snack(getString(R.string.location_permission_denied_msg)) {
                    action(getString(R.string.location_permission_denied_action)) { openAppSettings() }
                }
            }
        }
    }

    private val backgroundPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                snack(getString(R.string.location_permission_granted_msg))
            } else {
                snack(getString(R.string.background_location_permission_denied_msg)) {
                    action(getString(R.string.location_permission_denied_action)) { openAppSettings() }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

        viewModel.state.observe(this) { state ->
            bindView(state.snapshots, state.toggleButtonTextResId)
        }

        // todo add rational info to explain to user why we need all these permission
        checkRequiredPermissions()

        if (savedInstanceState == null) {
            val activityId = intent.extras?.getString(EXTRA_ACTIVITY_ID)
            activityId?.let {
                viewModel.loadSnapshots(it)
            }
        }
    }

    private fun checkRequiredPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        locationPermissionRequest.launch(permissions.toTypedArray())
    }

    private fun checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundPermissionRequest.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            snack(getString(R.string.location_permission_granted_msg))
        }
    }

    private fun bindView(snapshots: List<SnapshotUi>, toggleButtonTextResId: Int) {
        adapter.submitList(snapshots)
        binding.snapshotsRecyclerview.smoothScrollToPosition(0)
        binding.toolbar.menu.findItem(R.id.menu_toggle)?.title = getString(toggleButtonTextResId)
    }

    private fun initView() {
        adapter = SnapshotAdapter(
            imageLoader = imageLoader,
            onItemClick = { snapshot ->
                // This is added for demo purposes to show we could do something with onClick
                // if this were real app this would be handled by VM or navigator
                snack(snapshot.title) {
                    action(getString(R.string.snapshot_share)) {
                        startActivity(
                            createShareIntent(
                                getString(R.string.snapshot_share_text, snapshot.imageUrl)
                            )
                        )
                    }
                }
            }
        )

        binding.apply {
            setContentView(root)
            snapshotsRecyclerview.adapter = adapter
            toolbar.inflateMenu(R.menu.main_menu)
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_toggle -> clickThrottler.throttle { viewModel.trackingClick() }
                    R.id.menu_reset -> clickThrottler.throttle { viewModel.resetClick() }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    companion object {
        private const val OPEN_FROM_NOTIFICATION_RC = 1
        private const val EXTRA_ACTIVITY_ID = "EXTRA_ACTIVITY_ID"

        fun openIntent(context: Context) = Intent(context, SnapshotsActivity::class.java)

        fun openIntentExisting(context: Context, activityId: String) =
            openIntent(context).putExtra(EXTRA_ACTIVITY_ID, activityId)

        fun openFromNotificationPendingIntent(context: Context) = PendingIntent.getActivity(
            context,
            OPEN_FROM_NOTIFICATION_RC,
            openIntent(context),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
