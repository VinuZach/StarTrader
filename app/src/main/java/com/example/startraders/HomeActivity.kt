package com.example.startraders

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.startraders.Repository.RetrofitManger
import com.example.startraders.Repository.RetrofitMethods
import com.example.startraders.Repository.SharedPrefData
import com.example.startraders.Repository.SharedPrefData.Companion.COLLECTION_AGENT_ID
import com.example.startraders.Repository.SharedPrefData.Companion.INVOICE_ID
import com.example.startraders.models.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.util.*

/**
 * Home activity
 * creates a bill for selected customer from list of allocated customer,
 * selects whether customer pays in cash(then denominations is required),cheque, or RTGS
 * takes a print using bluetooth printer
 *
 *
 * @constructor Create empty Home activity
 */
class HomeActivity : AppCompatActivity()
{

    lateinit var receiptDateTextInputEditText: TextInputEditText
    lateinit var prevReceiptDateTextInputEditText: AutoCompleteTextView
    lateinit var customerNameAutoTextView: AutoCompleteTextView
    lateinit var prevReceiptNumberAutoCompleteTextView: AutoCompleteTextView
    lateinit var discountRadioGroup: RadioGroup
    lateinit var discountLayout: LinearLayout


    lateinit var cashToggleButton: ToggleButton
    lateinit var chequeToggleButton: ToggleButton
    lateinit var rtgsToggleButton: ToggleButton
    lateinit var cashLayout: View
    lateinit var chequeLayout: View
    lateinit var rtgslayout: View
    lateinit var selectedCashDenomination: LinearLayout
    lateinit var addMoreCashDenomination: Button

    var denominationValue = 0.0
    var noOfPiecesValue = 0
    var totalAmtValue = 0.0

    lateinit var submitButton: AppCompatButton

    lateinit var customerList: MutableList<CustomerDetails>
    lateinit var progressLayout: RelativeLayout
    var selectedCustomerDetails: CustomerDetails? = null
    lateinit var loadingMessage: TextView
    lateinit var collectionAgent_id: String
    lateinit var invoiceID: String
    lateinit var totalAmountAutoComplete: AutoCompleteTextView
    lateinit var rtgsDateAutoComplete: TextInputEditText
    lateinit var rtgsNumberAutoComplete: TextInputEditText
    lateinit var remarkAutoComplete: AutoCompleteTextView


    lateinit var chequeNumberAutoComplete: TextInputEditText
    lateinit var chequeDateAutoComplete: TextInputEditText

    private val TAG = "HomeActivity12321"


    lateinit var denominationList: MutableList<Denominations>

    lateinit var denominationAdapter: ArrayAdapter<String>
    var totalCashAmount = 0.0
    var cashAmountMutableData = MutableLiveData<Double>()

    lateinit var discountDetails: DiscountModel
    lateinit var discountAmtTextInputEditText: TextInputEditText
    lateinit var outStandingBal_Parent: LinearLayout
    val printerManger = PrinterManger()

    var outStandingBalance: String? = null


    lateinit var printPreviewParent: View
    lateinit var billDataEntryScrollView: ScrollView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homea)
        init()
        discountDetails = DiscountModel("", "", "")


        denominationList = mutableListOf()

        val denominationRedValue = resources.getStringArray(R.array.denominationvalue)

        denominationAdapter = ArrayAdapter<String>(this@HomeActivity, android.R.layout.simple_dropdown_item_1line, denominationRedValue)

        collectionAgent_id = RepositoryManager.sharedPrefData.getDataWithoutLiveData(this, COLLECTION_AGENT_ID, "")
        invoiceID = RepositoryManager.sharedPrefData.getDataWithoutLiveData(this, INVOICE_ID, "")

        findViewById<TextInputEditText>(R.id.invoiceid).setText(invoiceID)
        retrieveCustomer()
        cashAmountMutableData.observe(this, androidx.lifecycle.Observer {
            if (cashToggleButton.isChecked)
            {
                totalAmountAutoComplete.setText("" + it)
            }
        })



        rtgsDateAutoComplete.setOnClickListener {
            Log.d("asdsdwe", "onCreate: ")
            pickDate(HomeActivity@ this, rtgsDateAutoComplete)

        }

        chequeDateAutoComplete.setOnClickListener {
            Log.d("asdsdwe", "onCreate: ")
            pickDate(HomeActivity@ this, chequeDateAutoComplete)

        }

//        receiptDateTextInputEditText.setOnClickListener {
//            Log.d("asdsdwe", "onCreate: ")
//            pickDate(HomeActivity@ this, receiptDateTextInputEditText)
//
//        }


//        prevReceiptNumberAutoCompleteTextView.setAdapter(adapter)
//        prevReceiptNumberAutoCompleteTextView.threshold = 1
//        prevReceiptNumberAutoCompleteTextView.setOnClickListener {
//            prevReceiptNumberAutoCompleteTextView.showDropDown()
//        }


        discountRadioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener
        {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int)
            {
                Log.d("asdsdwe", "onCheckedChanged: " + checkedId)
                if (checkedId == R.id.radioyes)
                {
                    discountLayout.visibility = View.VISIBLE
                }
                else
                {
                    discountDetails.receiptDate = ""
                    discountDetails.receiptNo = ""
                    discountDetails.requestedAmt = ""
                    discountLayout.visibility = View.GONE
                }
            }

        })

        cashToggleButton.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener
        {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean)
            {
                totalAmountAutoComplete.text.clear()
                if (isChecked)
                {
                    cashAmountMutableData.postValue(totalCashAmount)
                    totalAmountAutoComplete.isEnabled = false
                    chequeToggleButton.isChecked = false
                    rtgsToggleButton.isChecked = false
                    cashLayout.visibility = View.VISIBLE
                    chequeLayout.visibility = View.GONE
                    rtgslayout.visibility = View.GONE
                }
                else cashLayout.visibility = View.GONE
            }

        })
        chequeToggleButton.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener
        {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean)
            {
                totalAmountAutoComplete.text.clear()
                if (isChecked)
                {
                    totalAmountAutoComplete.isEnabled = true
                    cashToggleButton.isChecked = false
                    rtgsToggleButton.isChecked = false


                    cashLayout.visibility = View.GONE
                    chequeLayout.visibility = View.VISIBLE
                    rtgslayout.visibility = View.GONE
                }
            }

        })
        rtgsToggleButton.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener
        {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean)
            {
                totalAmountAutoComplete.text.clear()
                if (isChecked)
                {
                    totalAmountAutoComplete.isEnabled = true
                    chequeToggleButton.isChecked = false
                    cashToggleButton.isChecked = false

                    cashLayout.visibility = View.GONE
                    chequeLayout.visibility = View.GONE
                    rtgslayout.visibility = View.VISIBLE
                }
            }

        })


        val denomination = cashLayout.findViewById<AutoCompleteTextView>(R.id.denomination)
        val noOfPieces = cashLayout.findViewById<TextInputEditText>(R.id.noofpieces)
        val totalAmt = cashLayout.findViewById<TextInputEditText>(R.id.total)


        denomination.setAdapter(denominationAdapter)
        denomination.threshold = 1
        denomination.setOnClickListener {
            denomination.showDropDown()
        }

        denomination.setOnItemClickListener(object : AdapterView.OnItemClickListener
        {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                calculateInitalTotal(noOfPieces.editableText, totalAmt, denomination)
            }

        })

        noOfPieces.setOnTouchListener(object : View.OnTouchListener
        {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean
            {
                noOfPieces.text?.clear()
                return false
            }

        })
        noOfPieces.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                Log.d("asdsdj", "onTextChanged: " + s.toString())
                calculateInitalTotal(noOfPieces.editableText, totalAmt, denomination)


            }

            override fun afterTextChanged(s: Editable?)
            {

            }

        })
        addMoreCashDenomination.setOnClickListener {
            if (totalAmt.text.toString().toDouble() > 0)
            {
                addNewLayout()
                //  denomination.setText("2000")
                noOfPieces.setText("0")
                totalAmt.setText("0")
            }
            else Snackbar.make(it, "Enter Denominations", Snackbar.LENGTH_LONG).show();

        }
        submitButton.setOnClickListener {
            Log.d(TAG, "onCreate: " + selectedCustomerDetails)

            if (selectedCustomerDetails == null)
            {

                Toast.makeText(this@HomeActivity, "Select a Customer", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            if (discountRadioGroup.checkedRadioButtonId == R.id.radioyes)
            {
                if (discountDetails.receiptNo.length <= 0 && discountDetails.receiptDate.length <= 0)
                {
                    Toast.makeText(this@HomeActivity, "Enter discount details", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else
                {
                    discountDetails.requestedAmt = discountAmtTextInputEditText.text.toString()

                }
            }
            else
            {
                discountDetails.receiptDate = ""
                discountDetails.receiptNo = ""
                discountDetails.requestedAmt = ""

            }

            if (cashToggleButton.isChecked)
            {

                if (totalAmountAutoComplete.text.toString().length <= 0)
                {
                    Toast.makeText(this@HomeActivity, "Enter Denomination Amount", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                displayConfirmation(object : OnDialogButtonClick
                {
                    override fun onButtonClick(isYes: Boolean)
                    {
                        if (isYes)
                        {
                            displayOverLayMessage("Sending receipt details")
                            RepositoryManager.retrofitObject.uploadReceipt_cash(selectedCustomerDetails!!, invoiceID,
                                receiptDateTextInputEditText.text.toString(), remarkAutoComplete.text.toString(), collectionAgent_id,
                                totalAmountAutoComplete.text.toString(), discountDetails, object : RetrofitManger.ApiResponse
                                {
                                    override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
                                    {
                                        handleReceiptSendResponse(isSuccess, responseData)
                                    }

                                })
                        }
                    }
                })

            }
            else
            {
                if (totalAmountAutoComplete.text.toString().length <= 0)
                {
                    Toast.makeText(this@HomeActivity, "Enter Amount", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }


                if (rtgsToggleButton.isChecked)
                {

                    if (totalAmountAutoComplete.text.toString().length <= 0)
                    {
                        Toast.makeText(this@HomeActivity, "Enter Amount", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (rtgsNumberAutoComplete.text.toString().length <= 0)
                    {
                        Toast.makeText(this@HomeActivity, "Enter RTGS Number", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (rtgsDateAutoComplete.text.toString().length <= 0)
                    {
                        Toast.makeText(this@HomeActivity, "Enter RTGS Date", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    displayConfirmation(object : OnDialogButtonClick
                    {
                        override fun onButtonClick(isYes: Boolean)
                        {
                            if (isYes)
                            {


                                displayOverLayMessage("Sending receipt details")
                                RepositoryManager.retrofitObject.uploadReceipt_RTGS(collectionAgent_id,
                                    receiptDateTextInputEditText.text.toString(), invoiceID, selectedCustomerDetails!!,
                                    totalAmountAutoComplete.text.toString(), rtgsDateAutoComplete.text.toString(),
                                    rtgsNumberAutoComplete.text.toString(), remarkAutoComplete.text.toString(), discountDetails,
                                    object : RetrofitManger.ApiResponse
                                    {
                                        override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
                                        {
                                            handleReceiptSendResponse(isSuccess, responseData)
                                        }

                                    })
                            }
                        }
                    })

                }
                else if (chequeToggleButton.isChecked)
                {


                    if (chequeNumberAutoComplete.text.toString().length <= 0)
                    {
                        Toast.makeText(this@HomeActivity, "Enter cheque Number", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    if (chequeDateAutoComplete.text.toString().length <= 0)
                    {
                        Toast.makeText(this@HomeActivity, "Enter cheque Date", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    displayConfirmation(object : OnDialogButtonClick
                    {
                        override fun onButtonClick(isYes: Boolean)
                        {

                            if (isYes)
                            {
                                displayOverLayMessage("Sending receipt details")
                                RepositoryManager.retrofitObject.uploadReceipt_cheque(remarkAutoComplete.text.toString(),
                                    receiptDateTextInputEditText.text.toString(), invoiceID, collectionAgent_id, selectedCustomerDetails!!,
                                    chequeNumberAutoComplete.text.toString(), chequeDateAutoComplete.text.toString(),
                                    totalAmountAutoComplete.text.toString(), discountDetails, object : RetrofitManger.ApiResponse
                                    {
                                        override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
                                        {
                                            handleReceiptSendResponse(isSuccess, responseData)

                                        }

                                    })
                            }
                        }
                    })

                }


            }
        }
        customerNameAutoTextView.setOnItemClickListener { parent, view, position, id ->
            selectedCustomerDetails = parent.getItemAtPosition(position) as CustomerDetails
            Log.d("Asdwe", "onCreate: " + selectedCustomerDetails?.customerName)
            Log.d("Asdwe", "onCreate: " + selectedCustomerDetails?.id)
            outStandingBal_Parent.visibility = View.GONE
            retrieveOutstandingBalance()


        }

    }

    override fun onResume()
    {
        super.onResume()
        Log.d("asdsdwe", "onCreate: " + getCurrentDate())
        receiptDateTextInputEditText.setText(getCurrentDate())
        prevReceiptDateTextInputEditText.setText(getCurrentDate())

    }

    /**
     * Set up printer connection
     *display dialog to connect to bluetooth printer
     * and resets ui after
     * @param paymentMode
     */
    private fun setUpPrinterConnection(paymentMode: String)
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
            resetUi()
            printerManger.cancelConnection()
            dialog.cancel()
        }
        Log.d("ajshdhsajdh", "printData: " + receiptDateTextInputEditText.text.toString())
        Log.d("ajshdhsajdh", "printData: " + outStandingBalance)
        printButton.setOnClickListener {
            printerManger.printData(receiptDateTextInputEditText.text.toString(), invoiceID, selectedCustomerDetails!!,
                totalAmountAutoComplete.text.toString(), paymentMode, outStandingBalance)
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
                printerManger.printData(receiptDateTextInputEditText.text.toString(), invoiceID, selectedCustomerDetails!!,
                    totalAmountAutoComplete.text.toString(), paymentMode, outStandingBalance)
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

    /**
     * Handle receipt send response
     * i.e only if bill details is send successful to server will the printout be obtained
     * @param isSuccess
     * @param responseData
     */
    private fun handleReceiptSendResponse(isSuccess: Boolean, responseData: Any?)
    {
        hideOverlayMessage()
        if (isSuccess)
        {
            Toast.makeText(this@HomeActivity, "Receipt send successfully", Toast.LENGTH_SHORT).show()

            incrementReceiptNumber()

        }
        else
        {
            val errorMessge = responseData as String
            Toast.makeText(this@HomeActivity, errorMessge, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Increment receipt number
     *
     */
    private fun incrementReceiptNumber()
    {

        val TAG: String = "ceu343kk"


        Log.d(TAG, "orginal onCreate: " + invoiceID)

        RepositoryManager.sharedPrefData.saveDataToDataStore<String>(this@HomeActivity, INVOICE_ID,
            data = getModifiedReceiptNumber(invoiceID, collectionAgent_id))
        val paymentMode: String
        if (cashToggleButton.isChecked) paymentMode = "Cash"
        else if (chequeToggleButton.isChecked) paymentMode = "Cheque"
        else paymentMode = "RTGS"



        setUpPrinterConnection(paymentMode)

    }

    private fun displayPrintPreview(paymentMode: String)
    {


        printPreviewParent.findViewById<TextView>(R.id.billdate).text = "Date: " + receiptDateTextInputEditText.text.toString()
        printPreviewParent.findViewById<TextView>(R.id.billnumber).text = "Bill No: " + invoiceID
        printPreviewParent.findViewById<TextView>(R.id.customername).text = "Received from: " + selectedCustomerDetails!!.customerName
        printPreviewParent.findViewById<TextView>(R.id.amount).text = "Received Amount: " + totalAmountAutoComplete.text.toString()
        val totalAmount = totalAmountAutoComplete.text.toString()
        printPreviewParent.findViewById<TextView>(R.id.amountwrd).text =
            "Amount in words : " + Currency.convertToIndianCurrency(totalAmount)
        printPreviewParent.findViewById<TextView>(R.id.openbal).text = "Opening Balance : " + outStandingBalance
        printPreviewParent.findViewById<TextView>(R.id.paymode).text = "Payment Mode : " + paymentMode

        try
        {
            val closingBalance = outStandingBalance?.toDouble()?.minus(totalAmount.toDouble())
            printPreviewParent.findViewById<TextView>(R.id.closebal).text = "Closing Balance : " + closingBalance
        } catch (e: Exception)
        {
        }

    }


    /**
     * Reset ui
     * and retrieve customerlist details
     */
    private fun resetUi()
    {
        discountDetails = DiscountModel("", "", "")
        invoiceID = RepositoryManager.sharedPrefData.getDataWithoutLiveData(this, INVOICE_ID, "")
        findViewById<TextInputEditText>(R.id.invoiceid).setText(invoiceID)
        totalCashAmount = 0.0
        submitButton.isEnabled = false
        outStandingBal_Parent.visibility = View.GONE
        selectedCashDenomination.removeAllViews()
        cashAmountMutableData.postValue(totalCashAmount)
        totalAmountAutoComplete.text.clear()
        remarkAutoComplete.text.clear()
        customerNameAutoTextView.text.clear()
        discountAmtTextInputEditText.text?.clear()
        prevReceiptNumberAutoCompleteTextView.text?.clear()
        prevReceiptDateTextInputEditText.text?.clear()
        chequeNumberAutoComplete.text?.clear()
        chequeDateAutoComplete.text?.clear()

        rtgsDateAutoComplete.text?.clear()
        rtgsNumberAutoComplete.text?.clear()
        selectedCustomerDetails = null
        outStandingBalance = "-1"
        retrieveCustomer()

    }

    /**
     * Retreive discount dates
     *
     * @param selectedCustomerDetails
     */
    private fun retreiveDiscountDates()
    {

        RepositoryManager.retrofitObject.retrieveCustomerDiscountDate(selectedCustomerDetails!!.id, object : RetrofitManger.ApiResponse
        {
            override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
            {
                var discountDates: MutableList<String>
                discountDates = mutableListOf()

                if (isSuccess)
                {
                    discountDates = (responseData as CustomerDiscountDates).date as MutableList<String>

                }
                val adapter: ArrayAdapter<String> =
                    ArrayAdapter<String>(this@HomeActivity, android.R.layout.simple_dropdown_item_1line, discountDates)

                prevReceiptDateTextInputEditText.setAdapter(adapter)
                prevReceiptDateTextInputEditText.threshold = 1
                prevReceiptDateTextInputEditText.setOnClickListener {
                    prevReceiptDateTextInputEditText.showDropDown()
                }

            }

        })

        prevReceiptDateTextInputEditText.setOnItemClickListener { parent, view, position, id ->
            val prevReceiptDate = parent.getItemAtPosition(position) as String

            RepositoryManager.retrofitObject.retrieveCustomerPrevReceipt(selectedCustomerDetails!!.id, prevReceiptDate,
                object : RetrofitManger.ApiResponse
                {
                    override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
                    {

                        var discountReceiptNo: MutableList<String>
                        discountReceiptNo = mutableListOf()

                        if (isSuccess)
                        {
                            discountReceiptNo = (responseData as CustomerDiscountDates).date as MutableList<String>

                        }
                        val adapter: ArrayAdapter<String> =
                            ArrayAdapter<String>(this@HomeActivity, android.R.layout.simple_dropdown_item_1line, discountReceiptNo)

                        prevReceiptNumberAutoCompleteTextView.setAdapter(adapter)
                        prevReceiptNumberAutoCompleteTextView.threshold = 1
                        prevReceiptNumberAutoCompleteTextView.setOnClickListener {
                            prevReceiptNumberAutoCompleteTextView.showDropDown()
                        }

                        prevReceiptNumberAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                            val prevReceiptNo = parent.getItemAtPosition(position) as String

                            discountDetails.receiptDate = prevReceiptDate
                            discountDetails.receiptNo = prevReceiptNo
                            discountDetails.requestedAmt = ""
                        }


                    }

                })


        }

    }

    /**
     * Retrieve outstanding balance
     *
     */
    private fun retrieveOutstandingBalance()
    {
        findViewById<LinearLayout>(R.id.outstandingpar).setOnClickListener { retrieveOutstandingBalance() }
        val dataEntryFields = findViewById<LinearLayout>(R.id.hhhhh);
        dataEntryFields.visibility = View.GONE
        val outstandingBalance = findViewById<TextView>(R.id.outstandingbal)
        outstandingBalance.text = ""
        findViewById<TextView>(R.id.outstandinglabel).visibility = View.VISIBLE
        RepositoryManager.retrofitObject.retrieveCustomerBalance(selectedCustomerDetails!!.id, object : RetrofitManger.ApiResponse
        {
            override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
            {

                outStandingBal_Parent.visibility = View.VISIBLE
                if (isSuccess)
                {
                    enableDisableButton(true)
                    dataEntryFields.visibility = View.VISIBLE

                    val response = responseData as CustomerOutResponse
                    Log.d(TAG, "onResponseObtained: " + response.data?.outstandingBalance)
                    outStandingBalance = response.data?.outstandingBalance

                    outstandingBalance.text = outStandingBalance
                    outstandingBalance.setTextColor(ContextCompat.getColor(this@HomeActivity, R.color.black))
                }
                else
                {
                    findViewById<TextView>(R.id.outstandinglabel).visibility = View.GONE
                    outstandingBalance.text = "Click to refresh Balance"
                    outstandingBalance.setTextColor(ContextCompat.getColor(this@HomeActivity, R.color.red))
                    enableDisableButton(false)
                    val errorMessage = responseData as String
                    Toast.makeText(this@HomeActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
                retreiveDiscountDates()
            }

        })
    }

    /**
     * Calculate inital total
     * when cash payment option is selected
     * @param s
     * @param totalAmt
     * @param denomination
     */
    private fun calculateInitalTotal(s: Editable?, totalAmt: TextInputEditText, denomination: AutoCompleteTextView)
    {
        try
        {
            val newValue = s.toString().toDouble() * denomination.text.toString().toDouble()

            totalAmt.setText("" + newValue)
            denominationValue = denomination.text.toString().toDouble()
            noOfPiecesValue = s.toString().toInt()
            totalAmtValue = newValue
        } catch (e: Exception)
        {
            Log.d("asdsdj", "onTextChanged: error " + e.localizedMessage)
            totalAmt.setText("" + 0)
        }
    }

    /**
     * Initialise ui variables
     *
     */
    fun init()
    {
        receiptDateTextInputEditText = findViewById(R.id.receiptdate)
        prevReceiptDateTextInputEditText = findViewById(R.id.prevrecieptdate)
        customerNameAutoTextView = findViewById(R.id.customername)
        prevReceiptNumberAutoCompleteTextView = findViewById(R.id.prevrecieptno)
        discountRadioGroup = findViewById(R.id.radioGroup)
        discountLayout = findViewById(R.id.discountlayout)
        submitButton = findViewById(R.id.submit)
        totalAmountAutoComplete = findViewById(R.id.totalamt)
        discountLayout.visibility = View.GONE
        discountRadioGroup.check(R.id.radioNo)
        progressLayout = findViewById(R.id.progressLayout)
        loadingMessage = findViewById(R.id.message)
        billDataEntryScrollView = findViewById(R.id.scrollView)

        discountAmtTextInputEditText = findViewById(R.id.prevrecieptamt)


        outStandingBal_Parent = findViewById<LinearLayout>(R.id.outstandingpar)

        cashToggleButton = findViewById(R.id.cashtoggle)
        chequeToggleButton = findViewById(R.id.chequetoggle)
        rtgsToggleButton = findViewById(R.id.rtgstoggle)
        cashLayout = findViewById(R.id.cashlayout)
        chequeLayout = findViewById(R.id.chequelayout)
        remarkAutoComplete = findViewById(R.id.remark)
        rtgslayout = findViewById(R.id.rtgslayout)
        cashToggleButton.isChecked = true

        addMoreCashDenomination = cashLayout.findViewById(R.id.addcash)
        selectedCashDenomination = cashLayout.findViewById(R.id.selectedcash)

        rtgsDateAutoComplete = rtgslayout.findViewById(R.id.rtgsdate)
        rtgsNumberAutoComplete = rtgslayout.findViewById(R.id.rtgsnumber)


        chequeNumberAutoComplete = chequeLayout.findViewById(R.id.chequenumber)
        chequeDateAutoComplete = chequeLayout.findViewById(R.id.chequedate)
    }

    /**
     * Add new layout for denominations when cash is selected
     * which takes denominations
     */
    private fun addNewLayout()
    {

        val inflater = LayoutInflater.from(HomeActivity@ this)
        val addedDeno = inflater.inflate(R.layout.layout_cash, null, false)
        addedDeno.findViewById<Button>(R.id.addcash).visibility = View.GONE
        val cancelButton = addedDeno.findViewById<ImageView>(R.id.cancel)
        cancelButton.visibility = View.VISIBLE


        lateinit var denominationAutoCompleteTextView: AutoCompleteTextView
        denominationAutoCompleteTextView = addedDeno.findViewById<AutoCompleteTextView>(R.id.denomination)
        val noOfPieces = addedDeno.findViewById<TextInputEditText>(R.id.noofpieces)
        val totalAmt = addedDeno.findViewById<TextInputEditText>(R.id.total)


        cancelButton.setOnClickListener {
            try
            {
                totalCashAmount -= totalAmt.text.toString().toDouble()

                cashAmountMutableData.postValue(totalCashAmount)
                selectedCashDenomination.removeView(addedDeno)
            } catch (e: Exception)
            {
            }
        }

        denominationAutoCompleteTextView.setText("" + denominationValue)
        noOfPieces.setText("" + noOfPiecesValue)
        totalAmt.setText("" + totalAmtValue)

        totalCashAmount += totalAmtValue
        cashAmountMutableData.postValue(totalCashAmount)
        denominationValue = 0.0
        noOfPiecesValue = 0
        totalAmtValue = 0.0
        denominationList.add(Denominations("" + denominationValue, "" + noOfPieces))

        for (i in denominationList)
        {
            Log.d(TAG, "addNewLayout: " + i.denomination)
            Log.d(TAG, "addNewLayout: " + i.count)
            Log.d(TAG, "-------------------------------------------")
        }
        denominationAutoCompleteTextView.setAdapter(denominationAdapter)
        denominationAutoCompleteTextView.threshold = 1

        denominationAutoCompleteTextView.setOnClickListener {
            denominationAutoCompleteTextView.showDropDown()
        }
        denominationAutoCompleteTextView.isEnabled = false
        noOfPieces.isEnabled = false

        denominationAutoCompleteTextView.setOnItemClickListener(object : AdapterView.OnItemClickListener
        {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                //   calculateCashTotal(noOfPieces.editableText, totalAmt,denominationAutoCompleteTextView)
            }

        })


        noOfPieces.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                Log.d("asdsdj", "onTextChanged: " + s.toString())


                calculateCashTotal(s, totalAmt, denominationAutoCompleteTextView)

            }

            override fun afterTextChanged(s: Editable?)
            {

            }

        })
        selectedCashDenomination.addView(addedDeno)
    }

    /**
     * Calculate cash total
     * @see HomeActivity.addNewLayout
     * @param s
     * @param totalAmt
     * @param denominationAutoCompleteTextView
     */
    private fun calculateCashTotal(s: CharSequence?, totalAmt: TextInputEditText, denominationAutoCompleteTextView: AutoCompleteTextView)
    {
        try
        {
            val newValue = s.toString().toDouble() * denominationAutoCompleteTextView.text.toString().toDouble()
            totalAmt.setText("" + newValue)
        } catch (e: Exception)
        {
            Log.d("asdsdj", "onTextChanged: error " + e.localizedMessage)
            totalAmt.setText("" + 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.home_menu, menu)

        val s = SpannableString("Version " + getString(R.string.versionName))
        s.setSpan(ForegroundColorSpan(Color.BLUE), 0, s.length, 0)
        menu?.findItem(R.id.version)?.setTitle(s)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item.itemId == R.id.statement)
        {


            showDialog()
        }
        if (item.itemId == R.id.logout)
        {
            displayOverLayMessage(getString(R.string.user_logging_out))
            RepositoryManager.retrofitObject.logoutUser(collectionAgent_id, object : RetrofitManger.ApiResponse
            {
                override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
                {
                    if (isSuccess)
                    {
                        RepositoryManager.sharedPrefData.saveDataToDataStore(this@HomeActivity, SharedPrefData.IS_USER_LOGGED_IN, false)
                        RepositoryManager.sharedPrefData.saveDataToDataStore<String>(this@HomeActivity, SharedPrefData.INVOICE_ID,
                            data = "")

                        RepositoryManager.sharedPrefData.saveDataToDataStore<String>(this@HomeActivity, COLLECTION_AGENT_ID, data = "")

                        startActivity(Intent(this@HomeActivity, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                        finish()
                    }
                    else
                    {
                        Toast.makeText(this@HomeActivity, "No response obtained", Toast.LENGTH_SHORT).show()
                        progressLayout.visibility = View.GONE
                        submitButton.isEnabled = true
                    }
                }

            })
        }
        if (item.itemId == R.id.billhistory)
        {
            val intent = Intent(this@HomeActivity, BillHistoryActivity::class.java)
            intent.putExtra("customerList", ArrayList(customerList))
            startActivity(intent)
        }
        if (item.itemId == R.id.collection)
        {
            startActivity(Intent(this@HomeActivity, CollectionReportActivity::class.java))
//            val connectionStatus = MutableLiveData<String>()
//
//            printerManger.listDevices(this, connectionStatus)
//            connectionStatus.observe(this, androidx.lifecycle.Observer {
//                Log.d(TAG, "listDevices: " + it)
//                if (it.equals("Connected to printer"))
//
//                    printerManger.printCollectionReport()
//        })
        }

        return super.onOptionsItemSelected(item)
    }

    var selectedCustomerInDialog: CustomerDetails? = null

    /**
     * Show statement request dialog
     *
     */
    private fun showDialog()
    {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window: Window? = dialog.getWindow()

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_statementrequest)

        val yesBtn = dialog.findViewById(R.id.request) as AppCompatButton
        val cancelButton = dialog.findViewById(R.id.cancel) as AppCompatButton


        lateinit var starteDate: TextInputEditText
        starteDate = dialog.findViewById(R.id.startdate)

        lateinit var endDate: TextInputEditText
        endDate = dialog.findViewById(R.id.enddate)

        starteDate.setOnClickListener {

            pickDate(this, starteDate)
        }
        endDate.setOnClickListener {
            pickDate(this, endDate)
        }

        yesBtn.setOnClickListener {
            Log.d("asdjehwe", "showDialog: " + starteDate.text.toString())
            val startDateObject = getDateFromString(starteDate.text.toString())
            val endDateObject = getDateFromString(endDate.text.toString())
            if (starteDate.text.toString().length <= 0)
            {
                Toast.makeText(this, "Start date is required", Toast.LENGTH_SHORT).show()
            }
            else if (endDate.text.toString().length <= 0)
            {
                Toast.makeText(this, "EndDate date is required", Toast.LENGTH_SHORT).show()
            }
            else if (startDateObject?.after(endDateObject) == true)
            {
                Toast.makeText(this, "Start date is after end date", Toast.LENGTH_SHORT).show()
            }
            else if (selectedCustomerInDialog == null)
            {
                Toast.makeText(this, "Select customer", Toast.LENGTH_SHORT).show()
            }
            else
            {
                dialog.dismiss()
                displayOverLayMessage(getString(R.string.receipt_statement_request))
                RepositoryManager.retrofitObject.requestStatement(selectedCustomerInDialog!!.id, starteDate.text.toString(),
                    endDate.text.toString(), object : RetrofitManger.ApiResponse
                    {
                        override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
                        {
                            if (isSuccess)
                            {
                                hideOverlayMessage()
                                Toast.makeText(this@HomeActivity, "Statement requested", Toast.LENGTH_SHORT).show()
                            }
                            else
                            {
                                val errorMessage = responseData as String
                                Toast.makeText(this@HomeActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            }

                        }

                    })
            }

        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }


        lateinit var customerNameAutoTextView: AutoCompleteTextView
        customerNameAutoTextView = dialog.findViewById(R.id.customername)


        val adapter: ArrayAdapter<CustomerDetails> =
            ArrayAdapter<CustomerDetails>(this@HomeActivity, android.R.layout.simple_dropdown_item_1line, customerList)


        customerNameAutoTextView.setAdapter(adapter)
        customerNameAutoTextView.threshold = 1
        customerNameAutoTextView.setOnClickListener {
            customerNameAutoTextView.showDropDown()
        }

        customerNameAutoTextView.setOnItemClickListener { parent, view, position, id ->
            selectedCustomerInDialog = parent.getItemAtPosition(position) as CustomerDetails
            Log.d("Asdwe", "onCreate: " + selectedCustomerInDialog?.customerName)
            Log.d("Asdwe", "onCreate: " + selectedCustomerInDialog?.id)

        }
        dialog.show()
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    /**
     * Display confirmation for submitting bill receipt
     *
     * @param onDialogButtonClick
     */
    fun displayConfirmation(onDialogButtonClick: OnDialogButtonClick)
    {


        val paymentMode: String
        if (cashToggleButton.isChecked) paymentMode = "Cash"
        else if (chequeToggleButton.isChecked) paymentMode = "Cheque"
        else paymentMode = "RTGS"


        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window: Window? = dialog.getWindow()

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_submitconfimation)
        val messageTextView: TextView = dialog.findViewById(R.id.confirmtext)

        printPreviewParent = dialog.findViewById(R.id.printpreviewlayout)
        displayPrintPreview(paymentMode)
        messageTextView.text = "Total received amount is "
        messageTextView.append(getColoredString(this, "Rs. " + totalAmountAutoComplete.text, ContextCompat.getColor(this, R.color.black)));
        dialog.findViewById<AppCompatButton>(R.id.confirm).setOnClickListener {

            dialog.cancel()
            onDialogButtonClick.onButtonClick(true)
        }

        dialog.findViewById<AppCompatButton>(R.id.cancel).setOnClickListener {
            dialog.cancel()
            onDialogButtonClick.onButtonClick(false)
        }

        dialog.show()
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    fun getColoredString(context: Context?, text: CharSequence?, color: Int): Spannable
    {
        val spannable: Spannable = SpannableString(text)
        spannable.setSpan(ForegroundColorSpan(color), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }

    fun displayOverLayMessage(message: String)
    {
        progressLayout.visibility = View.VISIBLE
        submitButton.isEnabled = false
        loadingMessage.text = message
    }

    fun hideOverlayMessage()
    {
        progressLayout.visibility = View.GONE
        submitButton.isEnabled = true
    }

    interface OnDialogButtonClick
    {

        fun onButtonClick(isYes: Boolean)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("BluetoothOpertion", ":asdsadas ")
        printerManger.handleActivityResult(this, requestCode, resultCode, data)
    }

    /**
     * Retrieve assigned customerlist of agency
     *
     */
    private fun retrieveCustomer()
    {
        RetrofitMethods.retrieveCustomerList(collectionAgent_id, apiResponse = object : RetrofitManger.ApiResponse
        {
            override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
            {
                Log.d("asdwe", "onResponse: 111 " + isSuccess)
                progressLayout.visibility = View.GONE
                if (isSuccess)
                {

                    val customerListFromApi = responseData as CustomerList

                    customerList = customerListFromApi.data as MutableList<CustomerDetails>

                    val adapter: ArrayAdapter<CustomerDetails> =
                        ArrayAdapter<CustomerDetails>(this@HomeActivity, android.R.layout.simple_dropdown_item_1line, customerList)
                    customerNameAutoTextView.text.clear()
                    selectedCustomerDetails = null
                    customerNameAutoTextView.setAdapter(adapter)
                    customerNameAutoTextView.threshold = 1
                    customerNameAutoTextView.setOnClickListener {
                        customerNameAutoTextView.showDropDown()
                    }
                }
                else
                {
                    customerList = mutableListOf<CustomerDetails>()
                    val errorMessage = responseData as String
                    Toast.makeText(this@HomeActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    enableDisableButton(false)
                }
            }

        })
    }

    fun enableDisableButton(enable: Boolean)
    {
        submitButton.isEnabled = enable
        submitButton.background =
            ContextCompat.getDrawable(this@HomeActivity, if (enable) R.drawable.button_selected else R.drawable.button_default)
    }

    companion object
    {
        val TAG: String = "sadtrthg"
        fun getModifiedReceiptNumber(invoiceID: String, collectionAgent_id: String, toIncrement: Boolean = true): String
        {
            val splitValue = invoiceID.split("-")
            val newReceiptNumber: String
            val incrementValue: Int
            if (toIncrement) incrementValue = 1
            else incrementValue = 0
            Log.d(TAG, "incrementReceiptNumber: " + splitValue)
            val leftSplit = splitValue[0].split("*")
            Log.d(TAG, "incrementReceiptNumber: " + leftSplit)
            val leftEnd: String
            val rightEnd: String
            val incrementingValue: String
            if (leftSplit.size >= 2)
            {
                leftEnd = collectionAgent_id + "**" + splitValue[0].split("*")[splitValue[0].split("*").size - 1]

                Log.d(TAG, "getModifiedReceiptNumber: " + splitValue[0].split("*"))

                val rightSplit = splitValue[splitValue.size - 1].split("/")
                rightEnd = rightSplit[rightSplit.size - 1]
                incrementingValue = rightSplit[0]
                newReceiptNumber = leftEnd + "-" + (incrementingValue.toInt() + incrementValue) + "/" + rightEnd
                Log.d(TAG, "onCreate: left end " + leftEnd)
                Log.d(TAG, "onCreate: incrementingValue " + incrementingValue)
                Log.d(TAG, "onCreate:rightEnd " + rightEnd)
                Log.i(TAG, "increamentReceiptNumber: " + newReceiptNumber)
            }
            else
            {

                incrementingValue = invoiceID.substring(invoiceID.indexOf("-") + 1, invoiceID.indexOf("/"))
                leftEnd = invoiceID.substring(0, invoiceID.indexOf("-") + 1)
                rightEnd = invoiceID.substring(invoiceID.indexOf("/"), invoiceID.length)

                Log.d(TAG, "onCreate2: incrementingValue " + incrementingValue)
                Log.d(TAG, "onCreate2: left end " + leftEnd)
                Log.d(TAG, "onCreate 2: right end " + rightEnd)
                newReceiptNumber = collectionAgent_id + "**" + leftEnd + (incrementingValue.toInt() + incrementValue) + rightEnd


                Log.i(TAG, "increamentReceiptNumber: " + newReceiptNumber)

            }
            return newReceiptNumber
        }
    }
}
