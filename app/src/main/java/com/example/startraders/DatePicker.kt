package com.example.startraders

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.widget.EditText
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


var mYear = 0
var mMonth : Int = 0
var mDay : Int = 0

fun getCurrentDate() : String {
    val sdf = SimpleDateFormat("yyyy-MM-dd")


    val c = Calendar.getInstance();
    mYear = c.get(Calendar.YEAR);
    mMonth = c.get(Calendar.MONTH);
    mDay = c.get(Calendar.DAY_OF_MONTH);

    return sdf.format(c.time)
}

fun getDateFromString(dateAsString:String):Date?
{

    var date:Date?=null
    val format = SimpleDateFormat("yyyy-MM-dd")
    try {
        date= format.parse(dateAsString)
        System.out.println(date)
    } catch (e : ParseException) {
        e.printStackTrace()
    }
    return date
}
fun pickDate(baseActivity : Activity?, editText : EditText?) {


    if (editText!!.text.length == 0) getCurrentDate()
    val datePickerDialog = DatePickerDialog(baseActivity!!, R.style.DialogTheme,
            { view, year, monthOfYear, dayOfMonth ->
                try {
                    mYear = year
                    mMonth = monthOfYear
                    mDay = dayOfMonth
                    val inputFormat : DateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val outputFormat : DateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val inputDateStr = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                    var date : Date? = null
                    date = inputFormat.parse(inputDateStr)
                    val outputDateStr : String = outputFormat.format(date)
                    if (editText != null) editText.setText(outputDateStr)
                } catch (e : ParseException) {
                    e.printStackTrace()
                }
            }, mYear, mMonth, mDay)
    datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ok", datePickerDialog)
    datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", datePickerDialog)

    datePickerDialog.show()
    datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setAllCaps(false)
    datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setAllCaps(false)

    datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
}