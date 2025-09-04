package com.example.controlegasto.data

import androidx.room.TypeConverter
import com.example.controlegasto.domain.entities.PaymentMethod
import java.math.BigDecimal
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromTimeStamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }
    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
    @TypeConverter
    fun fromString(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }

    @TypeConverter
    fun bigDecimalToString(decimal: BigDecimal?): String? {
        return decimal?.toPlainString()
    }

    @TypeConverter
    fun toPaymentMethod(value: String?) = value?.let { PaymentMethod.valueOf(it) }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod?) = value?.name
}