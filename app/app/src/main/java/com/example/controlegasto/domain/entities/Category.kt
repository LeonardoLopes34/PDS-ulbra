package com.example.controlegasto.domain.entities

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: Long
) {
    companion object {
        fun default(): Category {
            return Category(
                id = 0,
                name = "Sem Categoria",
                color = Color.Gray.value.toLong()
            )
        }
    }
}

