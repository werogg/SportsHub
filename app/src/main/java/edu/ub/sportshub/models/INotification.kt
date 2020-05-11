package edu.ub.sportshub.models

interface INotification {
    fun getMessage(originUsername : String): String
}