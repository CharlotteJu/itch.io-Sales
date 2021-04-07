package com.charlotte.judon.gifstats.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.charlotte.judon.gifstats.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_suivi_global.view.*
import uk.co.senab.photoview.PhotoViewAttacher


class SuiviGlobalFragment : Fragment() {

    private lateinit var mView: View

    companion object
    {
        const val URL_ALWAYS_DATA = "https://gif.alwaysdata.net/stats/pop.jpg"
        @JvmStatic
        fun newInstance() = SuiviGlobalFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView =  inflater.inflate(R.layout.fragment_suivi_global, container, false)
        Picasso.get().load(URL_ALWAYS_DATA).into(mView.image)
        val photoAttacher = PhotoViewAttacher(mView.image)
        photoAttacher.update()
        mView.always_data_btn.setOnClickListener {
            launchInternet()
        }
        return mView
    }

    private fun launchInternet()
    {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(URL_ALWAYS_DATA)
        startActivity(intent)
    }



}
