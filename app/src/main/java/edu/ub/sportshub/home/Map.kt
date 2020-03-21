package edu.ub.sportshub.home


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

/**
 * A simple [Fragment] subclass.
 */
class Map : Fragment() , OnMapReadyCallback {

    private lateinit var mMapView : MapView

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
        val coords1 = LatLng(41.3857552,2.1640565)
        val coords2 = LatLng(41.3875249,2.162479)
        val coords3 = LatLng(41.3886544,2.1597137)
        val coords4 = LatLng(41.3869079,2.1670084)
        val coords5 = LatLng(41.3864364,2.1593399)
        googleMap?.addMarker(MarkerOptions().position(coords1).title("Nike F.C"))?.showInfoWindow()
        googleMap?.addMarker(MarkerOptions().position(coords2).title("Tennis Match, Sabadell"))
        googleMap?.addMarker(MarkerOptions().position(coords3).title("Evento muestra 3"))
        googleMap?.addMarker(MarkerOptions().position(coords4).title("Evento muestra 4"))
        googleMap?.addMarker(MarkerOptions().position(coords5).title("Evento muestra 5"))
        val location = CameraUpdateFactory.newLatLngZoom(coords1, 15F)
        googleMap?.animateCamera(location)
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
