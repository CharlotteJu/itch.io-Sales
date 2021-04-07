package com.charlotte.judon.gifstats.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.views.adapters.SaleAdapter
import kotlinx.android.synthetic.main.fragment_list.view.*

class ListFragment : Fragment() {
    private lateinit var salesList : List<Sale>
    private lateinit var adapter : SaleAdapter
    private lateinit var mView: View

    companion object {

        @JvmStatic
        fun newInstance(salesList: List<Sale>) : ListFragment
        {
            return ListFragment().apply {
                this.salesList = salesList
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        mView = inflater.inflate(R.layout.fragment_list, container, false)
        configureRcv()
        return mView
    }

    private fun configureRcv()
    {
        adapter = SaleAdapter(
            salesList,
            requireContext()
        )
        mView.rcv.adapter = adapter
        mView.rcv.layoutManager = LinearLayoutManager(requireContext())

    }



}