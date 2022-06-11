package com.example.startraders

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.widget.EditText
import com.example.startraders.Repository.RetrofitManger
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


var mYear = 0
var mMonth: Int = 0
var mDay: Int = 0

val DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
val DATE_FORMAT_ddMMYYYY = "dd-MM-yyyy"

fun getCurrentDate(): String
{
    val sdf = SimpleDateFormat(DATE_FORMAT_ddMMYYYY)


    val c = Calendar.getInstance();
    mYear = c.get(Calendar.YEAR);
    mMonth = c.get(Calendar.MONTH);
    mDay = c.get(Calendar.DAY_OF_MONTH);

    return sdf.format(c.time)
}


fun convertToYYYY_MM_DD(dateAsString: String): String
{

    val inputFormat: DateFormat = SimpleDateFormat(DATE_FORMAT_ddMMYYYY)
    val outputFormat: DateFormat = SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD)
    val inputDateStr = dateAsString
    var date: Date? = null
    date = inputFormat.parse(inputDateStr)
    val outputDateStr: String = outputFormat.format(date)
    return outputDateStr
}

fun convertToDD_MM_YY(dateAsString: String): String
{

    val inputFormat: DateFormat = SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD)
    val outputFormat: DateFormat = SimpleDateFormat(DATE_FORMAT_ddMMYYYY)
    val inputDateStr = dateAsString
    var date: Date? = null
    date = inputFormat.parse(inputDateStr)
    val outputDateStr: String = outputFormat.format(date)
    return outputDateStr
}

fun getDateFromString(dateAsString: String): Date?
{

    var date: Date? = null
    val format = SimpleDateFormat(DATE_FORMAT_ddMMYYYY)
    try
    {
        date = format.parse(dateAsString)
        System.out.println(date)
    } catch (e: ParseException)
    {
        e.printStackTrace()
    }
    return date
}

fun pickDate(baseActivity: Activity?, editText: EditText?, apiResponse: RetrofitManger.ApiResponse? = null)
{


    if (editText!!.text.length == 0) getCurrentDate()
    val datePickerDialog = DatePickerDialog(baseActivity!!, R.style.DialogTheme, { view, year, monthOfYear, dayOfMonth ->
        try
        {
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val outputFormat: DateFormat = SimpleDateFormat(DATE_FORMAT_ddMMYYYY)
            val inputDateStr = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
            var date: Date? = null
            date = inputFormat.parse(inputDateStr)
            val outputDateStr: String = outputFormat.format(date)
            if (editText != null)
            {
                editText.setText(outputDateStr)
                apiResponse?.onResponseObtained(true, null)

            }
        } catch (e: ParseException)
        {
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