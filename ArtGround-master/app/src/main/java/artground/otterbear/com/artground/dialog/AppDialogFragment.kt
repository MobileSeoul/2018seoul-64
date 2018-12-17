package artground.otterbear.com.artground.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import artground.otterbear.com.artground.R
import kotlinx.android.synthetic.main.app_dialog_frame.*
import java.io.Serializable

/**
 * [Concept]
 * 1. 상단 title 영역 + 중앙 content 영역 + 하단 button 영역
 * 2. Dialog Button 의 경우 Positive, Neutral, Negative 세 종류가 있고 기본 visibility 속성은 GONE
 * 3. Dialog 구현 시 ButtonSpec 을 통해 어떤 버튼을 활성화 시킬 것인지 정의 해야 함
 */
abstract class AppDialogFragment : DialogFragment(), View.OnClickListener {
    var resultHandler: ((type: Result, data: Bundle?) -> Unit)? = null
    private var resultData: Bundle? = null

    /**
     * positive, neutral, negative 각 버튼 클릭 시 세부 동작 구현을 할때 오버라이딩 하여 사용
     * 이때 return 값이 true 이면 Dialog 이벤트가 처리 된것으로 간주하고 dismiss 시킴
     */
    open fun performPositiveButtonClick() = true

    open fun performNeutralButtonClick() = true
    open fun performNegativeButtonClick() = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context?.let { ctx ->
            return Dialog(ctx, R.style.DialogTheme).apply {
                setCanceledOnTouchOutside(false)
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dialogFrame = inflater.inflate(R.layout.app_dialog_frame, container, false)
        val contentContainer = dialogFrame.findViewById(R.id.contentContainer) as FrameLayout
        contentContainer.addView(inflater.inflate(contentLayoutResId, contentContainer, false))
        return dialogFrame
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        title.setText(titleResId)
        getButtonsSpec()?.apply {
            positive?.let { buildButtonSpec(it, Result.POSITIVE) }
            neutral?.let { buildButtonSpec(it, Result.NEUTRAL) }
            negative?.let { buildButtonSpec(it, Result.NEGATIVE) }
        }
    }

    private fun buildButtonSpec(buttonTextResId: Int, type: Result) {
        when (type) {
            Result.POSITIVE -> positiveBtn
            Result.NEUTRAL -> neutralBtn
            Result.NEGATIVE -> negativeBtn
        }.apply {
            tag = type
            visibility = View.VISIBLE
            text = getString(buttonTextResId)
            setOnClickListener(this@AppDialogFragment)
        }
    }

    fun show(activity: FragmentActivity) {
        show(activity.supportFragmentManager, dialogTag)
    }

    fun show(fragment: Fragment) {
        show(fragment.childFragmentManager, dialogTag)
    }

    /**
     * Dialog 이벤트 후 특정 결과값을 저장해야 하는 경우 dismiss 직전에 호출하여 Bundle 객체에 결과 값을 저장시켜 사용
     */
    fun setResult(resultData: Bundle) {
        this.resultData = resultData
    }

    /**
     * 등록 된 result handler 가 있는 경우 dismiss 전에 결과값을 매개변수로 하여 콜백을 호출함
     */
    fun dismiss(type: Result) {
        resultHandler?.invoke(type, resultData)
        dismiss()
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        super.show(manager, tag ?: dialogTag)
    }

    override fun onClick(v: View?) {
        v?.let {
            val handled: Boolean = when (it.id) {
                R.id.positiveBtn -> performPositiveButtonClick()
                R.id.neutralBtn -> performNeutralButtonClick()
                R.id.negativeBtn -> performNegativeButtonClick()
                else -> throw UnknownError("Unknown button be clicked")
            }
            if (handled) dismiss(it.tag as Result)
        }
    }

    /**
     * dialog tag
     */
    abstract val dialogTag: String

    /**
     * dialog content 영역에 inflate 될 layoutResId
     */
    abstract val contentLayoutResId: Int

    /**
     * dialog titleResId
     */
    abstract val titleResId: Int

    /**
     * 각 Dialog 에 대한 활성화 button 정보
     */
    abstract fun getButtonsSpec(): ButtonsSpec?
}

/**
 * 각 button 의 titleResId 를 매개변수로 하여 null 이 아닐 경우 활성화 시키는 것으로 간주
 */
data class ButtonsSpec(val positive: Int? = null, val neutral: Int? = null, val negative: Int? = null) : Serializable {
    override fun toString(): String = "($positive, $neutral, $negative)"
}

enum class Result {
    POSITIVE, NEUTRAL, NEGATIVE
}