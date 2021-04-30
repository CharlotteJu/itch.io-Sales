package com.charlotte.judon.gifstats.views.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.CustomCurrency
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.*
import com.charlotte.judon.gifstats.viewModel.Injection
import com.charlotte.judon.gifstats.viewModel.ViewModel
import com.charlotte.judon.gifstats.viewModel.ViewModelFactory
import com.charlotte.judon.gifstats.views.fragments.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.android.material.navigation.NavigationView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.collections.ArrayList

/**
 * Only one app's Activity, hosts all fragments and reads CSV information
 * @author Charlotte JUDON
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var csv : Uri
    private val csvListSales = ArrayList<Sale>()
    private var roomListSales = ArrayList<Sale>()
    private lateinit var viewModel : ViewModel
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var mToolbar: Toolbar
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: NavigationView
    private var listCurrencies : List<CustomCurrency> = mutableListOf()
    private lateinit var currentCurrency : CustomCurrency
    private lateinit var currentDateFormat : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this,
            R.color.colorPrimaryDark
        )

        checkReadPermission()
        getSharedPreferences()
        viewModelFactory = Injection.configViewModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ViewModel::class.java)

        this.viewModel.getAllSales().observe(this, {
            roomListSales = it as ArrayList<Sale>
            displayFragment(HomeFragment.newInstance(roomListSales))
        })
        getRateFromJson()
        configureToolbar()
        configureNavigationView()

    }

    private fun configureToolbar() {
        mToolbar = findViewById(R.id.main_activity_toolbar)
        setSupportActionBar(mToolbar)
    }

    private fun configureNavigationView()
    {
        mDrawerLayout = findViewById(R.id.main_activity_drawer_layout)
        val actionBarToggle = ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        mDrawerLayout.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()
        mNavigationView = findViewById(R.id.main_activity_navigation_view)
        mNavigationView.setNavigationItemSelectedListener(this)
    }


    private fun displayFragment(fragment : Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
            .addToBackStack(BACKSTACK).commit()
    }

    /**
     * Get the Exchange Rate from API call [https://www.floatrates.com/daily/usd.json]
     */
    private fun getRateFromJson(){
        Fuel.get("https://www.floatrates.com/daily/usd.json").responseJson { _, _, result ->
            result.success {
                listCurrencies =  UtilsCurrency.castJsonInListCurrencies(it.obj())
                editSharedPreferencesForCurrencies()
                getSharedPreferences()
            }
            result.failure {
                getSharedPreferences()
            }
        }
    }

    /**
     * Reading the CSV File provided by Itch.io [https://itch.io] with a [BufferedReader]
     * Date converted by [UtilsGeneral.convertStringToDate]
     * @explications : line is split by a "," when the CSV is downloaded to the phone
     *                  but it's split by a ";" when the CSV is downloaded to a computer then transferred to the phone
     */
    private fun readCSV()
    {
        var fileReader : BufferedReader? = null

        try {
            var line : String?
            val fis = contentResolver.openInputStream(csv)
            val isr = InputStreamReader(fis)
            fileReader = BufferedReader(isr)

            fileReader.readLine()
            line = fileReader.readLine()

            while (line != null){
                var tokens = line.split(",")

                if(tokens.size == 1) {
                    tokens = line.split(";")
                }

                val date = UtilsGeneral.convertStringToDate(tokens[4])

                val dateString = tokens[4].substring(0, 10)
                val timeString = tokens[4].substring(11, 19)

                val sale = Sale(
                        tokens[0].toLong(),
                        tokens[1],
                        tokens[2].toDouble(),
                        tokens[3],
                        date,
                        dateString,
                        timeString,
                        tokens[5],
                        tokens[6],
                        tokens[7].toBoolean(),
                        tokens[8].toBoolean(),
                        tokens[9],
                        tokens[10],
                        tokens[11].toDouble(),
                        tokens[12].toDouble(),
                        tokens[13].toDouble(),
                        tokens[14].toDouble(),
                        tokens[15].toDouble(),
                        tokens[16],
                        tokens[17].toDouble(),
                        tokens[18],
                        tokens[19])

                csvListSales.add(sale)
                line = fileReader.readLine()
            }
            this.viewModel.updateListSales(csvListSales, roomListSales, applicationContext)
        }
        catch (e: Exception) {
            println("Reading CSV Error!")
            e.printStackTrace()
            val stringCsv = applicationContext.resources.getString(R.string.csv_problem)
            Toast.makeText(applicationContext, stringCsv, Toast.LENGTH_LONG).show()
        } finally {
            try {
                fileReader?.close()
            } catch (e: IOException) {
                println("Closing fileReader Error!")
                e.printStackTrace()
            }
        }
    }

    /**
     * Edit the SharedPreferences after the API call
     * @link [getRateFromJson]
     */
    private fun editSharedPreferencesForCurrencies(){
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_CURRENCY, MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        edit.putString(CURRENCY_USD, UtilsCurrency.castCurrencyInString(listCurrencies[0]))
        edit.putString(CURRENCY_CAD, UtilsCurrency.castCurrencyInString(listCurrencies[1]))
        edit.putString(CURRENCY_GBP, UtilsCurrency.castCurrencyInString(listCurrencies[2]))
        edit.putString(CURRENCY_EUR, UtilsCurrency.castCurrencyInString(listCurrencies[3]))
        edit.putString(CURRENCY_JPY, UtilsCurrency.castCurrencyInString(listCurrencies[4]))
        edit.putString(CURRENCY_AUD, UtilsCurrency.castCurrencyInString(listCurrencies[5]))
        edit.apply()

    }

    private fun checkReadPermission() {
        if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permissions,
                READ_EXTERNAL_STORAGE_PERMISSION_CODE)
        }
    }

    private fun pickFile()
    {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent,
            PICK_FILE_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && data.data != null) {
            csv = data.data!!
            readCSV()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            pickFile()
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            R.id.action_home -> displayFragment(HomeFragment.newInstance(roomListSales))
            R.id.action_date -> displayFragment(GraphsFragment.newInstance(roomListSales, currentCurrency, listCurrencies, currentDateFormat))
            R.id.action_date_explain -> displayFragment(DateDetailFragment.newInstance(roomListSales))
            R.id.action_map -> displayFragment(MapFragment.newInstance(roomListSales))
            R.id.action_list -> displayFragment(ListMonthFragment.newInstance(roomListSales, currentCurrency, listCurrencies, currentDateFormat))
            R.id.action_settings -> displayFragment(SettingsFragment.newInstance(viewModel, roomListSales, currentCurrency, listCurrencies, currentDateFormat))
        }
        // To Close drawerLayout auto
        this.mDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Get the SharedPreferences recorded for [listCurrencies], [currentCurrency] and [currentDateFormat]
     */
     fun getSharedPreferences(){
         val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_CURRENCY, Context.MODE_PRIVATE)
         listCurrencies = UtilsCurrency.getListCurrenciesFromSharedPreferences(sharedPreferences)
        if(sharedPreferences.contains(KEY_CURRENT_CURRENCY)) {
            currentCurrency = UtilsCurrency.castStringInCurrency(sharedPreferences.getString(KEY_CURRENT_CURRENCY, null))
        } else {
            currentCurrency = CustomCurrency(CURRENCY_USD.toUpperCase(), 1.0, 1.0, "$")
        }


         val sharedDateFormat = getSharedPreferences(SHARED_PREFERENCES_DATE_FORMAT, Context.MODE_PRIVATE)
         currentDateFormat = sharedDateFormat.getString(KEY_CURRENT_DATE_FORMAT, US_DATE_FORMAT)!!
    }
}