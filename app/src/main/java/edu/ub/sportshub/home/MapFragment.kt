package edu.ub.sportshub.home


import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import edu.ub.sportshub.R
import edu.ub.sportshub.event.EventActivity
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.Event

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment() , OnMapReadyCallback {

    private lateinit var mMapView : MapView
    private var databaseHelper = StoreDatabaseHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMapView = view.findViewById(R.id.mapView2)
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        databaseHelper.getEventsCollection().get()
            .addOnSuccessListener { it ->
                for (event in it) {
                    val event = event.toObject(Event::class.java)
                    if (!event.isCompleted()) {
                        val lat = event.getPosition().latitude
                        val lng = event.getPosition().longitude
                        val coord = LatLng(lat,lng)
                        googleMap?.addMarker(MarkerOptions().position(coord).title(event.getTitle()).snippet(event.getId()))

                        googleMap?.setOnInfoWindowClickListener {mapMarker ->
                            val intent = Intent(context, EventActivity::class.java)
                            intent.putExtra("eventId", mapMarker.snippet)
                            startActivity(intent)
                        }
                    }

                }



            }
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }



}
