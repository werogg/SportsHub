package edu.ub.sportshub.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object StringUtils {

    /**
     * @param value number of likes
     * @return string with Xk if > 999 - Xm if > 999k
     */
    fun compactNumberString(value : Int) : String {
        return when {
            value < 1000 -> {
                return "$value"
            }
            value in 1000..999999 -> {
                return "$value" + "k"
            }
            else -> "$value" + "m"
        }
    }

    /**
     * Return timestamp date in string with format dd/MM/yyyy
     */
    fun getFormatedDateFromTimestamp(timestamp: Timestamp) : String {
        return SimpleDateFormat("dd/MM/yyyy").format(timestamp.toDate())
    }

    /**
     * Return timestamp hour in string with format HH::mm a (a is AM/PM)
     */
    fun getFormatedHourFromTimestamp(timestamp: Timestamp) : String {
        return SimpleDateFormat("HH:mm a").format(timestamp.toDate())
    }

    /**
     * Return the full address for a given latitude and longitude
     */
    fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double) : String {
        val geocoder = Geocoder(context)
        val location = geocoder.getFromLocation(latitude, longitude, 1)
        val address = location[0]
        return address.getAddressLine(0)
    }

    fun getAdressArrayFromName(context: Context, string: String): MutableList<String> {
        // TODO Improve that method, not enough good results
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(string, 15)

        val addressesStrings = mutableListOf<String>()

        for (address in addresses) {
            if (address.maxAddressLineIndex != -1) {
                addressesStrings.add(address.getAddressLine(0))
            }
        }

        return addressesStrings
    }

    fun getLocationFromName(context: Context, string: String): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocationName(string, 7)[0]
    }
}