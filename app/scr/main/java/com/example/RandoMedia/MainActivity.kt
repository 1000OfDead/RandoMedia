package com.example.randommedia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.randommedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

        private lateinit var binding: ActivityMainBinding
            private lateinit var repository: MediaRepository
                private var mediaItems: List<MediaItem> = emptyList()
                    private var permanentlyDenied = false

                        private val permissions: Array<String> =
                                if (Build.VERSION.SDK_INT >= 33) {
                                                arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
                                } else {
                                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }

                                    private val permissionLauncher =
                                            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                                                        if (result.values.all { it }) {
                                                                            onPermissionGranted()
                                                        } else {
                                                                            permanentlyDenied = permissions.any { !shouldShowRequestPermissionRationale(it) }
                                                                                            showPermissionScreen()
                                                        }
                                            }

                                                override fun onCreate(savedInstanceState: Bundle?) {
                                                            super.onCreate(savedInstanceState)
                                                                    binding = ActivityMainBinding.inflate(layoutInflater)
                                                                            setContentView(binding.root)
                                                                                    repository = MediaRepository(this)

                                                                                            binding.btnRandom.setOnClickListener { showRandomMedia() }
                                                                                                    binding.btnGrant.setOnClickListener {
                                                                                                                    if (permanentlyDenied) openAppSettings() else permissionLauncher.launch(permissions)
                                                                                                    }
                                                }

                                                    override fun onStart() {
                                                                super.onStart()
                                                                        if (hasPermissions()) onPermissionGranted() else permissionLauncher.launch(permissions)
                                                    }

                                                        override fun onStop() {
                                                                    super.onStop()
                                                                            binding.videoView.stopPlayback()
                                                        }

                                                            private fun hasPermissions() = permissions.all {
                                                                        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
                                                            }

                                                                private fun showPermissionScreen() {
                                                                            binding.permissionPanel.visibility = View.VISIBLE
                                                                                    binding.btnRandom.visibility = View.GONE
                                                                                            binding.imageView.visibility = View.GONE
                                                                                                    binding.videoView.visibility = View.GONE
                                                                                                            binding.statusText.visibility = View.GONE
                                                                }

                                                                    private fun onPermissionGranted() {
                                                                                binding.permissionPanel.visibility = View.GONE
                                                                                        binding.btnRandom.visibility = View.VISIBLE
                                                                                                Thread {
                                                                                                                val items = repository.loadAll()
                                                                                                                            runOnUiThread {
                                                                                                                                                mediaItems = items
                                                                                                                                                                if (items.isEmpty()) {
                                                                                                                                                                                        binding.statusText.setText(R.string.empty_gallery)
                                                                                                                                                                } else {
                                                                                                                                                                                        binding.statusText.setText(R.string.press_button)
                                                                                                                                                                }
                                                                                                                                                                                binding.statusText.visibility = View.VISIBLE
                                                                                                                            }
                                                                                                }.start()
                                                                    }

                                                                        private fun showRandomMedia() {
                                                                                    val item = repository.random(mediaItems)
                                                                                            if (item == null) {
                                                                                                            binding.statusText.setText(R.string.empty_gallery)
                                                                                                                        binding.statusText.visibility = View.VISIBLE
                                                                                                                                    return
                                                                                            }
                                                                                                    binding.statusText.visibility = View.GONE
                                                                                                            if (item.isVideo) showVideo(item.uri) else showImage(item.uri)
                                                                        }

                                                                            private fun showImage(uri: Uri) {
                                                                                        binding.videoView.stopPlayback()
                                                                                                binding.videoView.visibility = View.GONE
                                                                                                        binding.imageView.visibility = View.VISIBLE
                                                                                                                Glide.with(this)
                                                                                                                            .load(uri)
                                                                                                                                        .centerCrop()
                                                                                                                                                    .listener(object : RequestListener<android.graphics.drawable.Drawable> {
                                                                                                                                                                        override fun onLoadFailed(
                                                                                                                                                                                                e: GlideException?, model: Any?,
                                                                                                                                                                                                                    target: Target<android.graphics.drawable.Drawable>, isFirstResource: Boolean
                                                                                                                                                                        ): Boolean {
                                                                                                                                                                                                toastError(); return false
                                                                                                                                                                        }

                                                                                                                                                                                        override fun onResourceReady(
                                                                                                                                                                                                                resource: android.graphics.drawable.Drawable, model: Any,
                                                                                                                                                                                                                                    target: Target<android.graphics.drawable.Drawable>?,
                                                                                                                                                                                                                                                        dataSource: DataSource, isFirstResource: Boolean
                                                                                                                                                                                        ) = false
                                                                                                                                                    })
                                                                                                                                                                .into(binding.imageView)
                                                                            }

                                                                                private fun showVideo(uri: Uri) {
                                                                                            binding.imageView.visibility = View.GONE
                                                                                                    binding.videoView.visibility = View.VISIBLE
                                                                                                            try {
                                                                                                                            val controller = MediaController(this)
                                                                                                                                        controller.setAnchorView(binding.videoView)
                                                                                                                                                    binding.videoView.setMediaController(controller)
                                                                                                                                                                binding.videoView.setOnErrorListener { _, _, _ -> toastError(); true }
                                                                                                                                                                            binding.videoView.setOnPreparedListener { it.start() }
                                                                                                                                                                                        binding.videoView.setVideoURI(uri)
                                                                                                            } catch (e: Exception) {
                                                                                                                            toastError()
                                                                                                            }
                                                                                }

                                                                                    private fun toastError() =
                                                                                            Toast.makeText(this, R.string.file_error, Toast.LENGTH_SHORT).show()

                                                                                                private fun openAppSettings() {
                                                                                                            startActivity(
                                                                                                                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                                                                                                                data = Uri.fromParts("package", packageName, null)
                                                                                                                            }
                                                                                                            )
                                                                                                }
}
                                                                                                                                                                        ))
                                                                                                                                                                }
                                                                                                                            }
                                                        }
                                }
}