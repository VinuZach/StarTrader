package com.example.startraders

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.customlibrary.A1RecyclerAdapter
import com.example.startraders.Repository.RetrofitManger
import com.example.startraders.Repository.RetrofitMethods
import com.example.startraders.Repository.SharedPrefData
import com.example.startraders.models.CustomerDetails
import com.example.startraders.models.CustomerList
import com.example.startraders.models.LastFiveReceiptsResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class BillHistoryActivity : AppCompatActivity()
{

    private val TAG = "BillHistoryActivity12"
    lateinit var dateFilterLinearLayout: LinearLayout
    lateinit var startDateTextInputEditText: TextInputEditText
    lateinit var startDateTextInputLayout: TextInputLayout
    lateinit var endDateTextInputEditText: TextInputEditText
    lateinit var endDateTextInputLayout: TextInputLayout

    lateinit var billHistoryRecyclerView: RecyclerView
    lateinit var fullBillDetailsLayout: View
    var customerList: MutableList<CustomerDetails>? = null
    lateinit var progressLayout: RelativeLayout
    lateinit var loadingMessage: TextView
    lateinit var loadingProgressBar: ProgressBar

    lateinit var fullBillDetailsCustomerNameTextView: TextView
    lateinit var fullBillDetailsBillDateTextView: TextView
    lateinit var fullBillDetailsBillNUmberTextView: TextView
    lateinit var fullBillDetailsTotalAmtTextView: TextView
    lateinit var fullBillDetailsPaymentModeTextView: TextView
    lateinit var printBillDetails: Button
    val printerManger = PrinterManger()
    lateinit var collectionAgent_id: String
    lateinit var customerRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_history)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        init()
        startDateTextInputEditText.setText(getCurrentDate())
        endDateTextInputEditText.setText(getCurrentDate())
        getSupportActionBar()?.setTitle("Statement History");

        customerList = mutableListOf<CustomerDetails>()
        customerList = intent.extras?.get("customerList") as MutableList<CustomerDetails>?

        collectionAgent_id = RepositoryManager.sharedPrefData.getDataWithoutLiveData(this, SharedPrefData.COLLECTION_AGENT_ID, "28")
        retrieveLastFiveReciepts(collectionAgent_id, startDateTextInputEditText.text.toString(), endDateTextInputEditText.text.toString())

        RetrofitMethods.retrieveCustomerList(collectionAgent_id, object : RetrofitManger.ApiResponse
        {
            override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
            {
                if (isSuccess)
                {
                    val customerListFromApi = responseData as CustomerList
                    customerList = customerListFromApi.data as MutableList<CustomerDetails>
                    setCustomerAdapter()

                }
            }

        })
//        }


        startDateTextInputEditText.setOnClickListener {
            Log.d("asdsdwe", "onCreate: ")
            pickDate(HomeActivity@ this, startDateTextInputEditText, object : RetrofitManger.ApiResponse
            {
                override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
                {
                    if (isSuccess) retrieveLastFiveReciepts(collectionAgent_id, startDateTextInputEditText.text.toString(),
                        endDateTextInputEditText.text.toString())
                }

            })

        }





        endDateTextInputEditText.setOnClickListener {

            pickDate(HomeActivity@ this, endDateTextInputEditText, object : RetrofitManger.ApiResponse
            {
                override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
                {
                    if (isSuccess) retrieveLastFiveReciepts(collectionAgent_id, startDateTextInputEditText.text.toString(),
                        endDateTextInputEditText.text.toString())
                }

            })


        }


    }

    private fun setCustomerAdapter()
    {
        Log.d(TAG, "setCustomerAdapter: customerList " + customerList?.size)

        customerList?.let {
            adapter = CustomerListWithFilter(this@BillHistoryActivity, it, android.R.layout.simple_dropdown_item_1line,
                object : RetrofitManger.ApiResponse
                {
                    override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
                    {
                        if (isSuccess)
                        {
                            val selectedCustomer = responseData as CustomerDetails
                            searchEditText.setQuery(selectedCustomer.customerName, false)
                            retrieveLastFiveReciepts(collectionAgent_id, startDateTextInputEditText.text.toString(),
                                endDateTextInputEditText.text.toString(), selectedCustomer)
                        }
                    }

                })
            customerRecyclerView.adapter = adapter

        }
    }

    private fun retrieveLastFiveReciepts(collectionAgent_id: String, startDate: String, endDate: String, customer: CustomerDetails? = null)
    {
        displayOverLayMessage("Retrieving Bill Details   ")

        var customerId = ""
        if (customer != null)
        {
            customerId = customer.id
            noBillsErrorMessage = "No Bills Available" + " for " + customer.customerName + " between " + startDate + " and " + endDate
        }
        else noBillsErrorMessage = "No Bills Available" + " between " + startDate + " and " + endDate

        hideFullBillDetailsLayout(true)
        RetrofitMethods.retrieveLastFiveBills(collectionAgent_id, (startDate), (endDate), customerId,
            object : RetrofitManger.ApiResponse
            {
                override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
                {
                    hideOverlayMessage()
                    if (isSuccess)
                    {
                        displayBillReceipts(responseData as ArrayList<LastFiveReceiptsResponse>)
                    }
                    else
                    {
                        val errorMessge = responseData as String
                        Toast.makeText(this@BillHistoryActivity, errorMessge, Toast.LENGTH_SHORT).show()
                    }
                }

            })

    }

    var noBillsErrorMessage = "No Bills Available"
    private fun displayBillReceipts(arrayList: ArrayList<LastFiveReceiptsResponse>)
    {
        Log.d(TAG, "displayBillReceipts: " + arrayList.size)
        if (arrayList.size == 0)
        {


            displayOverLayMessage(noBillsErrorMessage, true)
        }
        else
        {
            var selectedBill:LastFiveReceiptsResponse?=null
            val billHistoryAdapter = object : A1RecyclerAdapter<LastFiveReceiptsResponse>(this, arrayList)
            {
                override val layoutResourceId: Int
                    get() = R.layout.item_billitem

                override fun onDataBind(model: LastFiveReceiptsResponse, position: Int, holder: RecyclerView.ViewHolder?)
                {

                    holder?.itemView?.let {
                        val customerNameTextView = it.findViewById<TextView>(R.id.customername);
                        val billDateTextView = it.findViewById<TextView>(R.id.billdate);
                        val billNumberTextView = it.findViewById<TextView>(R.id.billnumber);
                        val billTotalAmountTextView = it.findViewById<TextView>(R.id.amount);
                        customerNameTextView.text = arrayList.get(position).customer_name
                        billDateTextView.text = arrayList.get(position).date
                        billNumberTextView.text = "BILL NUMBER : " + model.invoiceNumber
                        billTotalAmountTextView.text = getString(R.string.currency_symbol) + " " + model.totalAmount

                        it.setOnClickListener {


                            fullBillDetailsCustomerNameTextView.text = model.customer_name
                            fullBillDetailsBillDateTextView.text = model.date
                            fullBillDetailsBillNUmberTextView.text = model.invoiceNumber
                            fullBillDetailsTotalAmtTextView.text = model.totalAmount
                            fullBillDetailsPaymentModeTextView.text = model.receivedStatus
                            selectedBill=model
                            hideFullBillDetailsLayout(false)
                        }


                        printBillDetails.setOnClickListener {
                            selectedBill?.let {
                                Log.d("asdhsde2323", "onDataBind: "+it.customer_name)
                                setUpPrinterConnection(it)
                            }

                        }
                    }

                }

            }
            billHistoryRecyclerView.adapter = billHistoryAdapter
        }
    }

    private fun setUpPrinterConnection(selectedBill: LastFiveReceiptsResponse)
    {


        val connectionStatus = MutableLiveData<String>()
        printerManger.listDevices(this, connectionStatus)

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window: Window? = dialog.getWindow()

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_bluetoothprinter)
        val connectionStatusTextView = dialog.findViewById<TextView>(R.id.connectionstatus)
        val connectionErrorTextView = dialog.findViewById<TextView>(R.id.error)
        connectionErrorTextView.visibility = View.GONE
        val printButton = dialog.findViewById<AppCompatButton>(R.id.print)
        val cancelButton = dialog.findViewById<AppCompatButton>(R.id.cancel)
        val cardView = dialog.findViewById<CardView>(R.id.cardview)

        printButton.isEnabled = false
        printButton.alpha = 0.5f
        dialog.show()
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        connectionStatusTextView.alpha = 0.5f
        connectionStatusTextView.isEnabled = false

        connectionStatusTextView.setOnClickListener {
            connectionErrorTextView.visibility = View.GONE
            printerManger.listDevices(this, connectionStatus)
        }
        cancelButton.setOnClickListener {

            printerManger.cancelConnection()
            dialog.cancel()
        }

        printButton.setOnClickListener {
            printerManger.printData((selectedBill.date), selectedBill.invoiceNumber,
                CustomerDetails(selectedBill.customer_name, selectedBill.customerId), selectedBill.totalAmount, selectedBill.receivedStatus,
                "", true)

        }

        connectionStatus.observe(this, androidx.lifecycle.Observer {
            Log.d(TAG, "listDevices: " + it)
            connectionStatusTextView.setText(it)
            connectionErrorTextView.visibility = View.GONE

            if (it.equals("Connected to printer"))
            {
                connectionErrorTextView.visibility = View.GONE
                printButton.isEnabled = true
                printButton.alpha = 1f
                connectionStatusTextView.alpha = 1f
                cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.accentcolor))

                connectionStatusTextView.alpha = 1f
                printerManger.printData((selectedBill.date), selectedBill.invoiceNumber,
                    CustomerDetails(selectedBill.customer_name, selectedBill.customerId), selectedBill.totalAmount,
                    selectedBill.receivedStatus, "", true)

            }
            else if (!it.equals(getString(R.string.bt_connecting)))
            {
                connectionErrorTextView.visibility = View.VISIBLE
                cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.red))

                connectionStatusTextView.alpha = 1f
                connectionStatusTextView.isEnabled = true
            }
            else
            {
                Log.d(TAG, "setUpPrinterConnection: ")

                connectionErrorTextView.visibility = View.GONE
                cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
                connectionStatusTextView.alpha = 0.5f
                connectionStatusTextView.isEnabled = false

            }


        })

    }

    fun init()
    {
        dateFilterLinearLayout = findViewById(R.id.datefilters)
        startDateTextInputEditText = findViewById(R.id.startdate)
        endDateTextInputEditText = findViewById(R.id.enddate)
        startDateTextInputLayout = findViewById(R.id.startdateinput)
        endDateTextInputLayout = findViewById(R.id.endtextinputlayout)

        billHistoryRecyclerView = findViewById(R.id.billhistory)
        fullBillDetailsLayout = findViewById(R.id.fullbilldetails)
        customerRecyclerView = findViewById(R.id.customerlist)
        fullBillDetailsLayout.visibility = View.GONE

        progressLayout = findViewById(R.id.progressLayout)
        loadingMessage = findViewById(R.id.message)
        loadingProgressBar = findViewById(R.id.progress)

        fullBillDetailsCustomerNameTextView = fullBillDetailsLayout.findViewById(R.id.customername)
        fullBillDetailsBillDateTextView = fullBillDetailsLayout.findViewById(R.id.billdate)
        fullBillDetailsBillNUmberTextView = fullBillDetailsLayout.findViewById(R.id.billnumber)
        fullBillDetailsTotalAmtTextView = fullBillDetailsLayout.findViewById(R.id.totalamt)
        fullBillDetailsPaymentModeTextView = fullBillDetailsLayout.findViewById(R.id.paymenttype)
        printBillDetails = fullBillDetailsLayout.findViewById(R.id.print)

    }

    override fun onBackPressed()
    {
        if (fullBillDetailsLayout.visibility == View.GONE) super.onBackPressed()
        else
        {
            hideFullBillDetailsLayout(true)
        }
    }

    fun hideFullBillDetailsLayout(toHide: Boolean)
    {
        if (toHide)
        {
            fullBillDetailsLayout.visibility = View.GONE
            billHistoryRecyclerView.visibility = View.VISIBLE
            searchMenuItem?.setVisible(true)
        }
        else
        {
            searchMenuItem?.collapseActionView()
            searchMenuItem?.setVisible(false)
            fullBillDetailsLayout.visibility = View.VISIBLE
            billHistoryRecyclerView.visibility = View.GONE
        }

    }

    override fun onSupportNavigateUp(): Boolean
    {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, ":asdsadas ")
        printerManger.handleActivityResult(this, requestCode, resultCode, data)
    }

    var adapter: CustomerListWithFilter? = null
    lateinit var searchEditText: SearchView
    var searchMenuItem: MenuItem? = null
    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.billhistory_menu, menu)

        searchMenuItem = menu?.findItem(R.id.action_search)
        searchEditText = searchMenuItem?.actionView as SearchView

        searchMenuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener
        {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean
            {
                Log.d(TAG, "onMenuItemActionExpand: ")
                dateFilterLinearLayout.visibility = View.VISIBLE


                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean
            {
                dateFilterLinearLayout.visibility = View.GONE
                customerRecyclerView.visibility = View.GONE

                return true
            }

        })




        searchEditText.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(p0: String?): Boolean
            {
                Log.d(TAG, "onQueryTextSubmit: ")

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean
            {

                Log.d(TAG, "onQueryTextChange: " + p0)
                if (p0.toString().length > 0) customerRecyclerView.visibility = View.VISIBLE

                adapter?.getFilter()?.filter(p0.toString());

                return true
            }

        })
        return true
    }

    fun displayOverLayMessage(message: String, isErrorMessage: Boolean = false)
    {
        if (isErrorMessage)
        {
            progressLayout.setBackgroundColor(ContextCompat.getColor(this@BillHistoryActivity, R.color.white))
            loadingProgressBar.visibility = View.GONE
        }
        else
        {
            progressLayout.setBackgroundColor(ContextCompat.getColor(this@BillHistoryActivity, R.color.lightgrey))
            loadingProgressBar.visibility = View.VISIBLE
        }
        progressLayout.visibility = View.VISIBLE

        loadingMessage.text = message
    }

    fun hideOverlayMessage()
    {
        progressLayout.visibility = View.GONE

    }

}