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
import com.charlotte.judon.gifstats.utils.BACKSTACK
import com.charlotte.judon.gifstats.utils.PICK_FILE_CODE
import com.charlotte.judon.gifstats.utils.URL_ITCH
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlin.collections.ArrayList

/**
 * Home Fragment used to download and refresh CSV File provide by [Itch.io : https://itch.io]
 * @link [InstructionsFragment]
 * @author Charlotte JUDON
 */
class HomeFragment : Fragment() {

    private var roomListSales = ArrayList<Sale>()
    private lateinit var mView: View

    companion object {
        @JvmStatic
        fun newInstance(listSales : ArrayList<Sale>) =
            HomeFragment().apply {
                this.roomListSales = listSales
            }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_home, container, false)
        mView.refresh_csv_btn.setOnClickListener {
            pickFile()
        }
        mView.download_btn.setOnClickListener {
            downloadCsvFromItch()
        }

        mView.instructions_btn.setOnClickListener {
            goToInstructions()
        }
        return mView
    }

    /**
     * Lunch File Explorer
     */
    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent,
            PICK_FILE_CODE
        )
    }

    /**
     * Lunch Internet Explorer to go to Itch.io website (https://itch.io/export-purchases)
     */
    private fun downloadCsvFromItch() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(URL_ITCH)
        startActivity(intent)
    }

    private fun goToInstructions() {
        parentFragmentManager.apply {
            val instructionsFragment = InstructionsFragment()
            val ft = this.beginTransaction()
            ft.replace(R.id.container, instructionsFragment).addToBackStack(BACKSTACK).commit()
        }
    }


}