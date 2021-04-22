package com.charlotte.judon.gifstats.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.PICK_FILE_CODE
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private var roomListSales = ArrayList<Sale>()
    private lateinit var mView: View

    companion object {
        const val URL_ITCH = "https://itch.io/export-purchases"
        @JvmStatic
        fun newInstance(listSales : ArrayList<Sale>) =
            HomeFragment().apply {
                this.roomListSales = listSales
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_home, container, false)
        mView.rafraichissement_btn.setOnClickListener {
            pickFile()
        }
        mView.telechargement_btn.setOnClickListener {
            downloadCsvFromItch()
        }
        return mView
    }


    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent,
            PICK_FILE_CODE
        )
    }

    private fun downloadCsvFromItch() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(URL_ITCH)
        startActivity(intent)
    }


}