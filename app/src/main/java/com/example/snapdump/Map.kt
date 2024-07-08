package com.example.snapdump

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.GpsStatus
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.*
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Map.newInstance] factory method to
 * create an instance of this fragment.
 */
class Map : Fragment(), MapListener, GpsStatus.Listener {
    lateinit var mMap: MapView
    lateinit var controller: IMapController;
    lateinit var mMyLocationOverlay: MyLocationNewOverlay;
    lateinit var apiService: ApiService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mMap = view.findViewById(R.id.osmmap)
        Configuration.getInstance().load(context, context?.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mMap.setMultiTouchControls(true)
        mMap.controller.setZoom(5.0)
        mMap.controller.setCenter(GeoPoint(0.0, 0.0))

        val retrofit = Retrofit.Builder()
            .baseUrl("https://labs.anontech.info/cse489/t3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        fetchEntities()

        return view
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        // event?.source?.getMapCenter()
        Log.e("TAG", "onCreate:la ${event?.source?.getMapCenter()?.latitude}")
        Log.e("TAG", "onCreate:lo ${event?.source?.getMapCenter()?.longitude}")
        //  Log.e("TAG", "onScroll   x: ${event?.x}  y: ${event?.y}", )
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        //  event?.zoomLevel?.let { controller.setZoom(it) }


        Log.e("TAG", "onZoom zoom level: ${event?.zoomLevel}   source:  ${event?.source}")
        return false;
    }

    override fun onGpsStatusChanged(p0: Int) {
        TODO("Not yet implemented")
    }

    private fun fetchEntities() {
        apiService.getEntities().enqueue(object : Callback<List<Entity>> {
            override fun onResponse(call: Call<List<Entity>>, response: Response<List<Entity>>) {
                if (response.isSuccessful) {
                    response.body()?.let { entities ->
                        Log.d("MapFragment", "Fetched entities: $entities")
                        addMarkers(entities)
                    }
                } else{
                    Log.e("MapFragment", "API call failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Entity>>, t: Throwable) {
                Log.e("MapFragment", "API call failed: ${t.message}")
            }
        })
    }

    private fun addMarkers(entities: List<Entity>) {
        for (entity in entities) {
            val marker = Marker(mMap)
            marker.position = GeoPoint(entity.lat, entity.lon)
            marker.title = entity.title


            marker.setOnMarkerClickListener { marker, mapView ->
                showImageDialog(entity)
                true
            }

            mMap.overlays.add(marker)
        }
        mMap.invalidate()
    }

    private fun showImageDialog(entity: Entity) {
        // Create a dialog to show the image
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_image, null)
        dialogBuilder.setView(dialogView)

        // Find the ImageView in the dialog layout
        val imageView = dialogView.findViewById<ImageView>(R.id.dialog_image_view)

        val imageUrl = getFullImageUrl(entity.image)
        // Load the image into the ImageView using Glide
        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        // Set the dialog title
        dialogBuilder.setTitle(entity.title)
        dialogBuilder.setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }

        val dialog = dialogBuilder.create()
        dialog.show()
    }



    private fun getFullImageUrl(relativeUrl: String): String {
        return "https://labs.anontech.info/cse489/t3/$relativeUrl"
    }

}