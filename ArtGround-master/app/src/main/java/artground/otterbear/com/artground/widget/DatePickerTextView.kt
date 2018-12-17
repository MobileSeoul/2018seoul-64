package artground.otterbear.com.artground.widget

import android.app.DatePickerDialog
import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.View
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.*

class DatePickerTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private val dateFormat = SimpleDateFormat("yyyy. MM. dd", Locale.KOREA)
    private var listener: ((Date) -> Boolean)? = null

    init {
        setOnClickListener(this)
    }

    fun setDateInfoListener(listener: (Date) -> Boolean) {
        this.listener = listener
    }

    fun getDateInfo(): Date? {
        text.toString().run {
            return try {
                dateFormat.parse(this)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun setDateInfo(date: Date) {
        text = dateFormat.format(date)
    }

    override fun onClick(v: View?) {
        Calendar.getInstance().apply {
            time = generateDate()
            DatePickerDialog(context, this@DatePickerTextView, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Calendar.getInstance().apply {
            clear()
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dateFormat.format(time).apply {
                listener?.invoke(time)?.let { if (it) text = this } ?: run { text = this }
            }
        }
    }

    private fun generateDate(): Date {
        text.toString().run {
            return try {
                dateFormat.parse(this)
            } catch (e: Exception) {
                Date()
            }
        }
    }
}