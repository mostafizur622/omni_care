package com.srapp.Util

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.format.DateFormat
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class MyUtilsClass {
    val calendar= Calendar.getInstance()

    companion object {
        val dateFormatTakeAttendance = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        val dateFormatHRAttendance = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val dateFormatTakeAttendanceStudentList = SimpleDateFormat("dd-MM-yyyy", Locale.US)

        fun getDateFormat(date: String): String {
            val dateFormatprev = SimpleDateFormat("yyyy-mm-dd")
            val dateFormatprevParse = dateFormatprev.parse(date)
            val dateFormat = SimpleDateFormat("dd/mm/yyyy")
            val userDateOfBirth = dateFormat.format(dateFormatprevParse!!)
            return userDateOfBirth
        }

        fun getDateFormatForSerachData(date: String): String {
            val dateFormatprev = SimpleDateFormat("dd/mm/yyyy") //dd/mm/yyyy
            val dateFormatprevParse = dateFormatprev.parse(date)
            val dateFormat = SimpleDateFormat("yyyy-mm-dd")
            val userDateOfBirth = dateFormat.format(dateFormatprevParse!!)

            return userDateOfBirth
        }

        fun getDate(time: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = time
            return DateFormat.format("dd MMM, yyyy", cal).toString()
        }

        fun showsDatePicker(context: Context, dateListener: DatePickerDialog.OnDateSetListener) {
            val calendar= Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(context, dateListener,
                Calendar.getInstance().get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))

            //datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        fun getDateFormatForTakeAtdStdData(date: String): String {
            val dateFormatprev = SimpleDateFormat("yyyy/MM/dd") //dd/mm/yyyy
            val dateFormatprevParse = dateFormatprev.parse(date)
            val dateFormat = SimpleDateFormat("dd-MM-yyyy")
            val takeAtdStudentDate = dateFormat.format(dateFormatprevParse!!)

            return takeAtdStudentDate
        }

        fun postDateFormatForTakeAtdStdData(date: String): String {
            val dateFormatprev = SimpleDateFormat("dd-MM-yyyy") //dd/mm/yyyy
            val dateFormatprevParse = dateFormatprev.parse(date)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val takeAtdStudentDate = dateFormat.format(dateFormatprevParse!!)

            return takeAtdStudentDate
        }

        fun convertToBitmap(b: ByteArray): Bitmap {
            Log.e("ArraySize", b.size.toString())
            return BitmapFactory.decodeByteArray(b, 0, b.size)
        }

    }
}