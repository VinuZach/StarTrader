package com.example.startraders

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.startraders.Repository.RetrofitManger
import com.example.startraders.Repository.SharedPrefData
import com.example.startraders.models.LastDayCollection
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CollectionReportActivity : AppCompatActivity()
{
    private val TAG = "CollectionRcti123"
    val fontFamily = FontFamily(Font(R.font.lexend_thin, FontWeight.Thin), Font(R.font.lexend_bold, FontWeight.Bold),
        Font(R.font.lexend_extrabold, FontWeight.ExtraBold), Font(R.font.lexend_light, FontWeight.Light),
        Font(R.font.lexend_medium, FontWeight.Medium), Font(R.font.lexend_regular, FontWeight.Normal),
        Font(R.font.lexend_semibold, FontWeight.SemiBold))
    val printerManger = PrinterManger()
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val collectionAgentName = RepositoryManager.sharedPrefData.getDataWithoutLiveData(this, SharedPrefData.EXECUTIVE_NAME, "")
        Log.d(TAG, "onCreate: " + collectionAgentName)
        setContent {


            getSupportActionBar()?.setTitle("Collection Report");
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
            CollectionPage()


        }
    }

    override fun onSupportNavigateUp(): Boolean
    {
        onBackPressed()
        return true
    }


    @Composable
    fun CollectionPage()
    {
        val lastDayCollection = remember { mutableStateListOf<LastDayCollection>() }
        var isCallingApi by remember {
            mutableStateOf(false)
        }
        var isResulObtained by remember {
            mutableStateOf(false)
        }
        var messageToShow by remember {
            mutableStateOf("")
        }

        val sdf = SimpleDateFormat(DATE_FORMAT_ddMMYYYY)
        val currentDate = sdf.format(Date())

        if (!isCallingApi)
        {
            isCallingApi = true
            isResulObtained = false
            messageToShow = "Retrieving Collection Report "
            val collectionAgent_id = RepositoryManager.sharedPrefData.getDataWithoutLiveData(this, SharedPrefData.COLLECTION_AGENT_ID, "28")

            Log.d(TAG, "CollectionPage: " + currentDate + "   " + collectionAgent_id)
            RepositoryManager.retrofitObject.retrieveCollectionReport(collectionAgent_id, currentDate, object : RetrofitManger.ApiResponse
            {
                override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
                {
                    Log.d(TAG, "onResponseObtained: " + lastDayCollection.size)
                    lastDayCollection.clear()

                    if (isSuccess)
                    {

                        lastDayCollection.addAll(responseData as (ArrayList<LastDayCollection>))
                        isResulObtained = true
                    }
                    else messageToShow = responseData as String

                }

            })
        }
        if (!isResulObtained) ShowMessage(messageToShow)
        else ShowResponeResult(lastDayCollection, currentDate)
    }

    @Composable
    fun ShowResponeResult(lastDayCollection: SnapshotStateList<LastDayCollection>, currentDate: String)
    {
        val accentColor = Color(ContextCompat.getColor(this@CollectionReportActivity, R.color.accentcolor))
        val white = Color(ContextCompat.getColor(this@CollectionReportActivity, R.color.white))

        var totalAmount = 0.0
        if (lastDayCollection.size > 0)
        {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (bottomButton, topView) = createRefs()
                Column(modifier = Modifier.constrainAs(topView) {
                    top.linkTo(parent.top)
                    bottom.linkTo(bottomButton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                }) {

                    LazyColumn() {

                        item {
                            lastDayCollection.forEach {
                                it.total?.let {
                                    totalAmount = totalAmount + it.toDouble()
                                }
                                DisplayCollectionItem(it)


                            }
                        }
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Divider(thickness = 2.dp, color = accentColor)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                                    Text(text = "Total Amount", fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    Text(text = getString(R.string.currency_symbol) + " " + totalAmount, fontFamily = fontFamily,
                                        fontWeight = FontWeight.Normal)
                                }
                            }

                            Log.d(TAG, "ShowResponeResult: " + totalAmount)
                        }

                    }


                }

                Button(onClick = {
                    setPrinterDialog(totalAmount, lastDayCollection, currentDate)
                },

                    modifier = Modifier.padding(10.dp).padding(20.dp).fillMaxWidth().constrainAs(bottomButton) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }, colors = ButtonDefaults.textButtonColors(backgroundColor = accentColor), enabled = true,
                    border = BorderStroke(2.dp, accentColor)) {
                    Text(text = "Print", color = white, fontFamily = fontFamily, fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp))
                }
            }

        }
        else ShowMessage("No Data in Collection Report")
    }

    private fun setPrinterDialog(totalAmount: Double, lastDayCollection: SnapshotStateList<LastDayCollection>, currentDate: String)
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
        val collectionAgentName = RepositoryManager.sharedPrefData.getDataWithoutLiveData(this, SharedPrefData.EXECUTIVE_NAME, "")
        connectionStatusTextView.setOnClickListener {
            connectionErrorTextView.visibility = View.GONE
            printerManger.listDevices(this, connectionStatus)
        }
        cancelButton.setOnClickListener {

            printerManger.cancelConnection()
            dialog.cancel()
        }

        printButton.setOnClickListener {
            printerManger.printCollectionReport(collectionAgentName, totalAmount, lastDayCollection.toTypedArray(), currentDate)

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
                printerManger.printCollectionReport(collectionAgentName, totalAmount, lastDayCollection.toTypedArray(), currentDate)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, ":asdsadas ")
        printerManger.handleActivityResult(this, requestCode, resultCode, data)
    }

    @Composable
    private fun DisplayCollectionItem(lastDayCollection: LastDayCollection)
    {

        val accentColor = Color(ContextCompat.getColor(this@CollectionReportActivity, R.color.accentcolor))
        val colorPrimaryDark = Color(ContextCompat.getColor(this@CollectionReportActivity, R.color.primaryDark))


        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            Column() {
                Text(text = "" + lastDayCollection.customerName, fontFamily = fontFamily, fontWeight = FontWeight.Bold)
                Text(text = "" + lastDayCollection.recievedStatus, fontFamily = fontFamily, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp, start = 10.dp), color = colorPrimaryDark)
            }

            Text(text = getString(R.string.currency_symbol) + " " + lastDayCollection.total, fontFamily = fontFamily,
                fontWeight = FontWeight.Normal, color = accentColor)
        }


    }


    @Preview("fullPageMessage")
    @Composable
    private fun ShowMessage(messageToShow: String = "asd")
    {

        Text(text = messageToShow, modifier = Modifier.fillMaxSize().padding(20.dp), textAlign = TextAlign.Center)

    }
}