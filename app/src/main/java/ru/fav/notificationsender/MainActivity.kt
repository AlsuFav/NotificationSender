package ru.fav.notificationsender

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import ru.fav.notificationsender.base.BaseActivity
import ru.fav.notificationsender.base.NavigationAction
import ru.fav.notificationsender.databinding.ActivityMainBinding
import ru.fav.notificationsender.fragments.MainFragment
import ru.fav.notificationsender.utils.NotificationsHandler
import ru.fav.notificationsender.utils.PermissionsHandler


class MainActivity : BaseActivity(){
    override var mainContainerId = R.id.main_fragment_container

    private var viewBinding: ActivityMainBinding? = null
    var notificationsHandler: NotificationsHandler? = null
    var permissionsHandler: PermissionsHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(currentThemeResId)
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding?.root)

        val isFromNotification = intent?.getBooleanExtra("isFromNotification", false) ?: false
        if (isFromNotification && savedInstanceState == null) {
            showToast(getString(R.string.launched_from_notification))
        }

        navigate(
            destination = MainFragment(),
            destinationTag = MainFragment.MAIN_FRAGMENT_TAG,
            action = NavigationAction.REPLACE,
            isAddToBackStack = false
        )


        permissionsHandler = PermissionsHandler(
            onSinglePermissionGranted = {},
            onSinglePermissionDenied = {}
        )
        permissionsHandler?.initContracts(this)

        checkAndRequestNotificationPermission()

        notificationsHandler = notificationsHandler ?: NotificationsHandler(applicationContext)
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionsHandler?.requestSinglePermission(
                permission = android.Manifest.permission.POST_NOTIFICATIONS,
                onGranted = { showToast(getString(R.string.permission_notifications_granted)) },
                onDenied = { showToast(getString(R.string.permission_notifications_denied)) })
        }
    }

    fun applyTheme(themeResId: Int) {
        currentThemeResId = themeResId
        recreate()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        viewBinding = null
        notificationsHandler = null
        permissionsHandler = null
        super.onDestroy()
    }

    companion object {
        var currentThemeResId: Int = R.style.Base_Theme_NotificationSender
    }
}

