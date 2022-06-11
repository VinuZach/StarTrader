package com.example.startraders

import android.content.Context
import android.util.Log
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.customlibrary.A1RecyclerAdapter
import com.example.startraders.Repository.RetrofitManger
import com.example.startraders.models.CustomerDetails

open class CustomerListWithFilter(context: Context, val arrayList: List<CustomerDetails>, override val layoutResourceId: Int,val apiResponse: RetrofitManger.ApiResponse) :
    A1RecyclerAdapter<CustomerDetails>(context, arrayList), Filterable
{
    private val filter: CustomerFilter = CustomerFilter(this,arrayList)
    override fun onDataBind(model: CustomerDetails, position: Int, holder: RecyclerView.ViewHolder?)
    {
        holder?.itemView?.let {

            val textView = it.findViewById<TextView>(android.R.id.text1)
            textView?.text = model.customerName
            it.setOnClickListener {
                apiResponse.onResponseObtained(true,model)
            }
        }


    }

    override fun getFilter(): Filter
    {
        return filter
    }

    override fun getItemCount(): Int
    {
        Log.d("asdsadsad", "onDataBind: asdasdas "+arrayList.size)
        return arrayList.size
    }
    class CustomerFilter(adapter: CustomerListWithFilter,val mArrayList: List<CustomerDetails>) : Filter()
    {
        private val adapter: CustomerListWithFilter


        private var mFilteredList: List<CustomerDetails> = ArrayList<CustomerDetails>()
        override fun performFiltering(constraint: CharSequence): FilterResults
        {
            val charString: String = constraint.toString()
            if (charString.isEmpty())
            {
                mFilteredList = mArrayList
            }
            else
            {
                val filteredList: ArrayList<CustomerDetails> = ArrayList()
                if (mArrayList != null)
                {
                    for (branch in mArrayList)
                    {
                        if (branch.customerName.lowercase().startsWith(charString))
                        {
                            filteredList.add(branch)
                        }
                    }
                }
                mFilteredList = filteredList
            }
            val filterResults = FilterResults()
            filterResults.values = mFilteredList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults)
        {
            mFilteredList = results.values as ArrayList<CustomerDetails>
            Log.d("asdsadsad", "publishResults: "+mFilteredList.size)
            adapter.notifyDataSetChanged()
            //  this.adapter.notifyDataSetChanged();
        }

        init
        {
            this.adapter = adapter
        }
    }

}