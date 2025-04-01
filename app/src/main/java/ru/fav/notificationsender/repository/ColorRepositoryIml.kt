package ru.fav.notificationsender.repository

class ColorRepositoryImpl : ColorRepository {
    private val colors = mutableListOf<Int>()

    override fun addColor(color: Int) {
        if (!colors.contains(color)) {
            colors.add(color)
        }
    }

    override fun removeColor(color: Int) {
        colors.remove(color)
    }

    override fun getAllColors(): List<Int> {
        return colors.toList()
    }

    override fun containsColor(color: Int): Boolean {
        return colors.contains(color)
    }
}