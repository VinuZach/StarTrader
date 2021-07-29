package com.example.startraders

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.print.sdk.PrinterConstants
import com.android.print.sdk.PrinterConstants.Connect
import com.android.print.sdk.PrinterInstance
import com.example.startraders.Printer.BluetoothOperation
import com.example.startraders.Printer.IPrinterOpertion
import com.example.startraders.Printer.MainActivity
import com.example.startraders.models.CustomerDetails

class PrinterManger {
    companion object {

        private var isConnected = false
    }

    private  val TAG = "PrinterManger1232"
    var myOpertion : IPrinterOpertion? = null
    private var mPrinter : PrinterInstance? = null
    var connectionStatus = MutableLiveData<String>()


    fun listDevices(activity : Activity, connectionStatus : MutableLiveData<String>) {
        this.connectionStatus=connectionStatus
        myOpertion = BluetoothOperation(activity, mHandler)
        Log.d(TAG, "listDevices: "+isConnected)

        if (!isConnected) {

            myOpertion?.chooseDevice()
        }
        else {

            myOpertion?.close()
            myOpertion = null
            mPrinter = null
        }

    }

    fun cancelConnection()
    {
        myOpertion?.close()
        myOpertion = null
        mPrinter = null
        isConnected=false
    }
    fun handleActivityResult(activity : Activity,requestCode : Int, resultCode : Int, data : Intent?) {
        when (requestCode) {
            MainActivity.CONNECT_DEVICE -> if (resultCode == Activity.RESULT_OK) {
                connectionStatus.postValue(activity.getString(R.string.bt_connecting))
                Thread { myOpertion!!.open(data) }.start()
            }
            MainActivity.ENABLE_BT -> if (resultCode == Activity.RESULT_OK) {
                myOpertion!!.chooseDevice()
            }
            else {
                connectionStatus.postValue(activity.getString(R.string.bt_not_enabled))

            }
        }
    }

    fun printData(date : String, invoiceNumber : String, customer : CustomerDetails,
                  totalAmount : String,
                  paymentMode : String,
                  outStandingBalance : String?
    ) {

        mPrinter!!.init()
        val sb = StringBuffer()
        // mPrinter.setPrinter(BluetoothPrinter.COMM_LINE_HEIGHT, 80);
        mPrinter!!.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)

        mPrinter!!.setCharacterMultiple(1, 0)
        mPrinter!!.printText("Star Traders"+"\n")
        mPrinter!!.setCharacterMultiple(0, 0)
        mPrinter!!.printText("28th Mile,Trivandrum"+"\n")
        mPrinter!!.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        mPrinter!!.printText("9496368178,9495020057,9496888780"+"\n")

        mPrinter!!.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        mPrinter!!.printText("==============================\n")
        mPrinter!!.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        mPrinter!!.printText("RECEIPT\n")

        mPrinter!!.printText("==============================\n")

        mPrinter!!.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        sb.append("Bill No: "+invoiceNumber+"\n")
        sb.append("Date: "+date+"\n")
        sb.append("Received from: "+customer.customerName+"\n")
        sb.append("Received Amount: "+totalAmount+"\n")
        sb.append("Amount in words : "+Currency.convertToIndianCurrency(totalAmount)+"\n")



        sb.append("Opening Balance : "+outStandingBalance+"\n")
        var closingBalance:Double?=0.0
        try {
            closingBalance= outStandingBalance?.toDouble()?.minus(totalAmount.toDouble())
        } catch (e : Exception) {
        }
        sb.append("Closing Balance : "+closingBalance+"\n")
        sb.append("Payment Mode : "+paymentMode+"\n")

        sb.append("\n\n\n Signature")

        Log.d(TAG, "printData: "+sb.toString())
        mPrinter!!.printText(sb.toString())
        mPrinter!!.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)
    }

    private val mHandler : Handler = object : Handler() {
        override fun handleMessage(msg : Message) {
            Log.d(TAG, "handleMessage: asdasd")
            when (msg.what) {
                Connect.SUCCESS -> {
                    isConnected = true
                    mPrinter = myOpertion!!.printer
                    connectionStatus.postValue("Connected to printer")
                }
                Connect.FAILED -> {
                    isConnected = false
                    connectionStatus.postValue("Connection failed")

                }
                Connect.CLOSED -> {
                    isConnected = false
                    connectionStatus.postValue("Connection closed")

                }
                else -> {
                }
            }


        }
    }

}