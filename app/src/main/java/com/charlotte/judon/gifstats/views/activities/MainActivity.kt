package com.charlotte.judon.gifstats.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.Utils
import com.charlotte.judon.gifstats.viewModel.Injection
import com.charlotte.judon.gifstats.viewModel.ViewModel
import com.charlotte.judon.gifstats.viewModel.ViewModelFactory
import com.charlotte.judon.gifstats.views.fragments.*
import com.google.android.material.navigation.NavigationView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 101
const val PICK_FILE_CODE = 102
const val BACKSTACK = "BACKSTACK"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var csv : Uri
    private val csvListSales = ArrayList<Sale>()
    private var roomListSales = ArrayList<Sale>()
    private lateinit var viewModel : ViewModel
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var mToolbar: Toolbar
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: NavigationView


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
        viewModelFactory = Injection.configViewModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ViewModel::class.java)

        this.viewModel.getAllSales().observe(this, androidx.lifecycle.Observer {
            roomListSales = it as ArrayList<Sale>
            displayFragment(HomeFragment.newInstance(roomListSales))
        })
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
        val actionBarToogle = ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        mDrawerLayout.addDrawerListener(actionBarToogle)
        actionBarToogle.syncState()
        mNavigationView = findViewById(R.id.main_activity_navigation_view)
        mNavigationView.setNavigationItemSelectedListener(this)
    }


    private fun displayFragment(fragment : Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
            .addToBackStack(BACKSTACK).commit()
    }

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
                val tokens = line.split(",")

                val date = Utils.convertStringToDate(tokens[4])

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
        } finally {
            try {
                fileReader!!.close()
            } catch (e: IOException) {
                println("Closing fileReader Error!")
                e.printStackTrace()
            }
        }
    }

    private fun checkReadPermission()
    {
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
            R.id.action_date -> displayFragment(GraphsFragment.newInstance(roomListSales))
            R.id.action_date_explain -> displayFragment(DateDetailFragment.newInstance(roomListSales))
            R.id.action_map -> displayFragment(MapFragment.newInstance(roomListSales))
            R.id.action_list -> displayFragment(ListMonthFragment.newInstance(roomListSales))
            R.id.action_settings -> displayFragment(SettingsFragment.newInstance(viewModel, roomListSales))
        }
        // To Close drawerLayout auto
        this.mDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}