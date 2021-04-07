package com.charlotte.judon.gifstats.model
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "sale")
data class Sale constructor(
    @PrimaryKey @ColumnInfo(name = "id") var id : Long,
    @ColumnInfo(name = "objectName") var objectName : String,
    @ColumnInfo(name = "amount") var amount : Double,
    @ColumnInfo(name = "sourcePayment") var sourcePayment : String,
    @ColumnInfo(name = "dateDate") var dateDate : Date,
    @ColumnInfo(name = "dateString") var dateString : String,
    @ColumnInfo(name = "hour") var hour : String,
    @ColumnInfo(name = "email") var email : String,
    @ColumnInfo(name = "fullName") var fullName : String,
    @ColumnInfo(name = "donation") var donation : Boolean,
    @ColumnInfo(name = "onSale") var onSale : Boolean,
    @ColumnInfo(name = "countryCode") var countryCode : String,
    @ColumnInfo(name = "ip") var ip : String,
    @ColumnInfo(name = "productPrice") var productPrice : Double,
    @ColumnInfo(name = "taxAdded") var taxAdded : Double,
    @ColumnInfo(name = "tip") var tip : Double,
    @ColumnInfo(name = "marketPlaceFree") var marketPlaceFree : Double,
    @ColumnInfo(name = "sourceFee") var sourceFee : Double,
    @ColumnInfo(name = "payout") var payout : String,
    @ColumnInfo(name = "amountDelivered") var amountDelivered : Double,
    @ColumnInfo(name = "currency") var currency: String,
    @ColumnInfo(name = "sourceId") var sourceId : String
)