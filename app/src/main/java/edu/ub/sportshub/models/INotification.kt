package edu.ub.sportshub.models

import android.content.Context

interface INotification {
    fun getMessage(context : Context, originUsername : String): String
}