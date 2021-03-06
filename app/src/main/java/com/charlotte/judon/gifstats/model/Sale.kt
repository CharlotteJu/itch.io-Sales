package com.charlotte.judon.gifstats.model
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Data class to get a complete [Sale]
 * Used when CSV is reading
 * Used by the Database
 */
@Entity(tableName = "sale")
data class Sale constructor(
    @PrimaryKey @ColumnInfo(name = "id") var id : Long,
    @ColumnInfo(name = "objectName") var objectName : String,
    @ColumnInfo(name = "amount") var amount : Double,
    @ColumnInfo(name = "sourcePayment") var sourcePayment : String, //source
    @ColumnInfo(name = "dateDate") var dateDate : Date,
    @ColumnInfo(name = "dateString") var dateString : String,
    @ColumnInfo(name = "hour") var hour : String,
    @ColumnInfo(name = "email") var email : String,
    @ColumnInfo(name = "fullName") var fullName : String, //full_name
    @ColumnInfo(name = "donation") var donation : Boolean,
    @ColumnInfo(name = "onSale") var onSale : Boolean, //on_sale
    @ColumnInfo(name = "countryCode") var countryCode : String, //country_code
    @ColumnInfo(name = "ip") var ip : String,
    @ColumnInfo(name = "productPrice") var productPrice : Double, //product_price
    @ColumnInfo(name = "taxAdded") var taxAdded : Double, //tax_added
    @ColumnInfo(name = "tip") var tip : Double,
    @ColumnInfo(name = "marketPlaceFree") var marketPlaceFree : Double, //marketplace_fee
    @ColumnInfo(name = "sourceFee") var sourceFee : Double, //source_fee
    @ColumnInfo(name = "payout") var payout : String,
    @ColumnInfo(name = "amountDelivered") var amountDelivered : Double, //amount_delivery
    @ColumnInfo(name = "currency") var currency: String,
    @ColumnInfo(name = "sourceId") var sourceId : String //source_id
)