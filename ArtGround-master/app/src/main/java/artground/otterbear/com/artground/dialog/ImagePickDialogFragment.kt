package artground.otterbear.com.artground.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import artground.otterbear.com.artground.R
import artground.otterbear.com.artground.common.AppLogger
import kotlinx.android.synthetic.main.dialog_image_pick.view.*

class ImagePickDialogFragment : DialogFragment() {

    private var listener: ((Int) -> Unit)? = null
    private lateinit var dialogView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        AppLogger.stamp()
        dialogView = LayoutInflater.from(context!!).inflate(R.layout.dialog_image_pick, null)
        return AlertDialog.Builder(context!!)
                .setTitle("이미지 옵션")
                .setView(dialogView)
                .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialogView.apply {
            modify.setOnClickListener {
                listener?.invoke(0)
                dismiss()
            }

            delete.setOnClickListener {
                listener?.invoke(1)
                dismiss()
            }
        }
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }
}