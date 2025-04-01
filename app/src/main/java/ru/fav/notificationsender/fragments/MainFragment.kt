package ru.fav.notificationsender.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ru.fav.notificationsender.MainActivity
import ru.fav.notificationsender.R
import ru.fav.notificationsender.adapter.ColorsAdapter
import ru.fav.notificationsender.databinding.FragmentMainBinding
import ru.fav.notificationsender.model.NotificationData
import ru.fav.notificationsender.model.NotificationLevel
import ru.fav.notificationsender.model.NotificationType
import ru.fav.notificationsender.repository.ColorRepository
import ru.fav.notificationsender.repository.ColorRepositoryImpl
import ru.fav.notificationsender.utils.NotificationsHandler

class MainFragment: Fragment(R.layout.fragment_main) {
    private var viewBinding: FragmentMainBinding? = null
    private var notificationsHandler: NotificationsHandler? = null
    private var notificationCounter = 0
    private val colorRepository: ColorRepository = ColorRepositoryImpl()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentMainBinding.bind(view)
        if (notificationsHandler == null) {
            notificationsHandler = (requireActivity() as? MainActivity)?.notificationsHandler
        }

        initColors()
        initViews()
    }

    private fun initColors() {
        colorRepository.addColor(Color.RED)
        colorRepository.addColor(Color.GREEN)
        colorRepository.addColor(Color.YELLOW)
    }

    private fun initViews() {
        viewBinding?.apply {
            setupSpinner(spinnerImportance)

            btnShowNotification.setOnClickListener {
                showNotification()
            }

            circularImageView.setOnClickListener { showImageOptionsDialog() }
            btnDelete.setOnClickListener { clearImage() }

            colorsRecyclerView.adapter = ColorsAdapter(colorRepository.getAllColors()) { color ->
                applyTheme(getThemeForColor(color))
            }
            colorsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            dropDownBtn.setOnClickListener { toggleDropDown() }
            btnResetTheColor.setOnClickListener { applyTheme(R.style.Base_Theme_NotificationSender) }
        }
    }


    private fun setupSpinner(spinner: Spinner) {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.importance_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }


    private fun showNotification() {
        val title = viewBinding?.etTitleInput?.text.toString()
        val message = viewBinding?.etTextInput?.text.toString()
        val importance = viewBinding?.spinnerImportance?.selectedItem?.toString().orEmpty()

        when {
            title.isEmpty() -> showToast(getString(R.string.notification_title_empty))
            message.isEmpty() -> showToast(getString(R.string.notification_text_empty))
            else -> {
                notificationsHandler?.showNotification(
                    NotificationData(
                        id = ++notificationCounter,
                        title = title,
                        message = message,
                        notificationType = getNotificationType(importance)
                    )
                )
            }
        }
    }

    private fun getNotificationType(importance: String): NotificationType {
        return when (NotificationLevel.fromString(importance)) {
            NotificationLevel.MAX -> NotificationType.URGENT
            NotificationLevel.HIGH -> NotificationType.PRIVATE
            NotificationLevel.LOW -> NotificationType.LOW
            else -> NotificationType.DEFAULT
        }
    }


    private fun showImageOptionsDialog() {
        val options = arrayOf(
            getString(R.string.load_default),
            getString(R.string.choose_from_gallery)
        )

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.choose_action))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> loadImage()
                    1 -> givePermission()
                }
            }
            .show()
    }

    private fun givePermission() {
        val readPermission: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (requireContext().checkSelfPermission(readPermission) != PackageManager.PERMISSION_GRANTED) {
            (requireActivity() as? MainActivity)?.permissionsHandler?.requestSinglePermission(
                permission = readPermission,
                onGranted = { openGallery() },
                onDenied = { showToast(getString(R.string.permission_denied)) }
            )
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                loadImage(uri)
            }
        }

    private fun loadImage(imageUri: Uri? = null) {
        val imageUrl = imageUri?.toString()
            ?: "https://pm1.aminoapps.com/7597/2ac8a446f97569973a0259f3141022da5d3342ber1-1080-1080v2_hq.jpg"

        viewBinding?.circularImageView?.let {
            Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(it)
        }

        viewBinding?.btnDelete?.isVisible = true
    }


    private fun clearImage() {
        viewBinding?.circularImageView?.setImageDrawable(null)
        viewBinding?.btnDelete?.isVisible = false
    }

    private fun getThemeForColor(color: Int): Int {
        return when (color) {
            Color.RED -> R.style.RedTheme
            Color.GREEN -> R.style.GreenTheme
            Color.YELLOW -> R.style.YellowTheme
            else -> R.style.Base_Theme_NotificationSender
        }
    }

    private fun toggleDropDown() {
        viewBinding?.apply {
            val isDropDownVisible = colorsRecyclerView.isVisible
            colorsRecyclerView.visibility = if (isDropDownVisible) View.GONE else View.VISIBLE
            dropDownBtn.setImageResource(
                if (isDropDownVisible) R.drawable.ic_baseline_keyboard_double_arrow_down_24
                else R.drawable.ic_baseline_keyboard_double_arrow_up_24
            )
        }
    }

    private fun applyTheme(themeResId: Int) {
        (requireActivity() as? MainActivity)?.applyTheme(themeResId)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
        notificationsHandler = null
    }

    companion object {
        const val MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT_TAG"
    }
}