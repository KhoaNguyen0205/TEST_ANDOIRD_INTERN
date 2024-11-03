package com.example.myapp

import PhotosAdapter
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"





class Photos : Fragment(R.layout.fragment_photos) {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerViewPhotos: RecyclerView
    private lateinit var photosAdapter: PhotosAdapter
    private val photosList = mutableListOf<Uri>()

    private val photosObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            loadPhotos() // Reload photos when a change is detected
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewPhotos = view.findViewById(R.id.recyclerViewPhotos)
        recyclerViewPhotos.layoutManager = GridLayoutManager(requireContext(), 3)
        photosAdapter = PhotosAdapter(requireContext(), photosList)
        recyclerViewPhotos.adapter = photosAdapter

        loadPhotos()

        // Register the observer to listen for changes in the media content
        requireContext().contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            photosObserver
        )
    }

    private fun loadPhotos() {
        photosList.clear() // Clear the list before loading new data
        val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)

        val cursor = requireContext().contentResolver.query(
            uriExternal,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(uriExternal, id.toString())
                photosList.add(contentUri)
            }
            photosAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister the observer when the view is destroyed
        requireContext().contentResolver.unregisterContentObserver(photosObserver)
    }
}