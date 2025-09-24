package com.example.controlegasto.data

import androidx.room.TypeConverter
import com.example.controlegasto.domain.entities.PaymentMethod
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class Converters {
    @TypeConverter
    fun fromTimeStamp(value: Long?): LocalDate? {
        return value?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }    }
    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()    }
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