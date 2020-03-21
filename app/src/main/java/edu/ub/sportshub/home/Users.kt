package edu.ub.sportshub.home


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView

import edu.ub.sportshub.R
import edu.ub.sportshub.profile.ProfileActivity

/**
 * A simple [Fragment] subclass.
 */
class Users : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userCard = view.findViewById<CardView>(R.id.userCard)

        userCard.setOnClickListener {
            userCardClicked()
        }
    }

    private fun userCardClicked() {
        val intent = Intent(activity, ProfileActivity::class.java)
        startActivity(intent)
    }


}
