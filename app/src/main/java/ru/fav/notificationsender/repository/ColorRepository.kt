package ru.fav.notificationsender.repository

interface ColorRepository {
    fun addColor(color: Int)
    fun removeColor(color: Int)
    fun getAllColors(): List<Int>
    fun containsColor(color: Int): Boolean
}