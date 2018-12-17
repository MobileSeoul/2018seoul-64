package artground.otterbear.com.artground.common

import android.util.Log
import artground.otterbear.com.artground.BuildConfig

class AppLogger {
    companion object {
        fun LOGV(msg: String) {
            Logger(Log.VERBOSE, msg)
        }

        fun LOGD(msg: String) {
            Logger(Log.DEBUG, msg)
        }

        fun LOGI(msg: String) {
            Logger(Log.INFO, msg)
        }

        fun LOGW(msg: String) {
            Logger(Log.WARN, msg)
        }

        fun LOGE(msg: String) {
            Logger(Log.ERROR, msg)
        }

        /**
         * 해당 시점의 호출 여부만 출력 하고 싶을 때 사용
         */
        fun stamp() {
            Logger(Log.ERROR, "")
        }

        private fun Logger(priority: Int, msg: String) {
            if (BuildConfig.DEBUG) {
                val msgBuilder = StringBuilder()
                msgBuilder.append("[").append(Thread.currentThread().stackTrace[4].methodName)
                        .append("()").append("]").append(" :: ").append(msg)
                        .append(" (").append(Thread.currentThread().stackTrace[4].fileName).append(":")
                        .append(Thread.currentThread().stackTrace[4].lineNumber).append(")")

                Log.println(priority, Thread.currentThread().stackTrace[4].fileName.replace(".kt", ""), msgBuilder.toString())
            }
        }
    }
}