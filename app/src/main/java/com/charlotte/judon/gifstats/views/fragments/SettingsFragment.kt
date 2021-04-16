package com.charlotte.judon.gifstats.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.viewModel.ViewModel
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    private lateinit var viewModel : ViewModel
    private lateinit var listSales : List<Sale>
    private lateinit var mView : View

    companion object {

        @JvmStatic
        fun newInstance(viewModel: ViewModel, listSales : List<Sale>) : SettingsFragment
        {
            return SettingsFragment()
                .apply {
                    this.viewModel = viewModel
                    this.listSales = listSales
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_settings, container, false)

        mView.button_delete.setOnClickListener {
            viewModel.deleteAllSales(requireContext(), listSales)
        }
        return mView
    }

}