package com.example.randommedia

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

data class MediaItem(val uri: Uri, val isVideo: Boolean)

class MediaRepository(private val context: Context) {

        fun loadAll(): List<MediaItem> {
                    val result = ArrayList<MediaItem>()
                            result += query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false)
                                    result += query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true)
                                            return result
        }

            private fun query(collection: Uri, isVideo: Boolean): List<MediaItem> {
                        val items = ArrayList<MediaItem>()
                                try {
                                                context.contentResolver.query(
                                                                    collection,
                                                                                    arrayOf(MediaStore.MediaColumns._ID),
                                                                                                    null, null, null
                                                )?.use { cursor ->
                                                                val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                                                                                while (cursor.moveToNext()) {
                                                                                                        val uri = ContentUris.withAppendedId(collection, cursor.getLong(idCol))
                                                                                                                            items += MediaItem(uri, isVideo)
                                                                                }
                                                }
                                } catch (e: SecurityException) {
                                                // нет доступа — вернём то, что есть
                                }
                                        return items
            }

                fun random(items: List<MediaItem>): MediaItem? = items.randomOrNull()
}
                                                                                }}
                                                )
                                }
        }
}