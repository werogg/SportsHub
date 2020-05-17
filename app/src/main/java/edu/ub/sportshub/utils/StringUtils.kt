package edu.ub.sportshub.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.firebase.Timestamp
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

object StringUtils {

    /**
     * @param value number of likes
     * @return string with Xk if > 999 - Xm if > 999k
     */
    fun compactNumberString(value : Int?) : String {
        if (value == null) return "error"
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
        return if (location.size > 0) {
            val address = location[0]
            address.getAddressLine(0)
        } else {
            ""
        }
    }

    fun getAddressArrayFromName(context: Context, string: String): MutableList<String> {
        val geoCoder = Geocoder(context, Locale.getDefault())
        val addresses = geoCoder.getFromLocationName(string, 15)

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

    fun hashString(input: String, algorithm: String): String {
        return MessageDigest
            .getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }

    fun generateRandomId() : String {
        return UUID.randomUUID().toString()
    }
}