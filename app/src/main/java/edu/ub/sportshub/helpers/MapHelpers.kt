package edu.ub.sportshub.helpers

import android.location.Address
import android.location.Geocoder
import com.google.firebase.firestore.GeoPoint
import java.lang.NullPointerException

class MapHelpers {

    constructor()

    /*
    public fun getLocationFromAddress(strAddress : String) : GeoPoint {

        val coder = Geocoder(this);
        val address : MutableList<Address> ;
        var  p1 : GeoPoint?= null;

        address = coder.getFromLocationName(strAddress,5);
        if (address.isEmpty()!!) {
            //throw NullPointerException; //Como retornar nulo sino se lanza excepcion
        }
        val location : Address = address.get(0);
        location.latitude;
        location.longitude;

        p1 = GeoPoint((location.latitude * 1E6).toDouble(),
            (location.longitude * 1E6).toDouble());

        return p1;

    }
    */





}