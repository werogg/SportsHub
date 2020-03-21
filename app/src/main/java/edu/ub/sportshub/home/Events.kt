package edu.ub.sportshub.home


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView

import edu.ub.sportshub.R
import edu.ub.sportshub.event.EventActivity

/**
 * A simple [Fragment] subclass.
 */
class Events : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nikeCard = view.findViewById<CardView>(R.id.nikeCard)

        nikeCard.setOnClickListener {
            nikeCardClicked()
        }

    }

    private fun nikeCardClicked() {
        val intent = Intent(activity, EventActivity::class.java)
        startActivity(intent)
    }


}
