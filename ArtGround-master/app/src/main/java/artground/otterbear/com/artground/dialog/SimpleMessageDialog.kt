package artground.otterbear.com.artground.dialog

import android.os.Bundle
import artground.otterbear.com.artground.R
import kotlinx.android.synthetic.main.message_dialog_content.*

/**
 * 메세지만을 출력하는 Dialog Sealed class
 * Dialog 유형에 따라 클래스를 추가하여 세부 정보를 구현한다
 */
sealed class SimpleMessageDialog : AppDialogFragment() {
    override val dialogTag: String = javaClass.simpleName
    override val contentLayoutResId: Int = R.layout.message_dialog_content

    abstract val messageResId: Int

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        message.setText(messageResId)
    }

    override fun getButtonsSpec(): ButtonsSpec? {
        return ButtonsSpec(positive = android.R.string.ok, negative = android.R.string.cancel)
    }

    class PermissionInfo : SimpleMessageDialog() {
        override val titleResId: Int = R.string.permission_usage_info_title
        override val messageResId: Int = R.string.test_dialog_msg

        override fun performPositiveButtonClick(): Boolean {
            val result = Bundle().apply {
                putString("resultkey", "ok")
            }
            setResult(result)
            return true
        }
    }
}