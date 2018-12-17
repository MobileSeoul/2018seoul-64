package artground.otterbear.com.artground.db

import android.os.AsyncTask

abstract class Task<Params, Result> : AsyncTask<Params, Void, Result>() {
    //abstract val callback: Callback<Result?>?
    abstract fun run(params: Array<out Params>): Result?

    abstract val callback: ((Result) -> Unit)?

    override fun doInBackground(vararg params: Params): Result? {
        return run(params)
    }

    override fun onPostExecute(result: Result) {
        super.onPostExecute(result)
        //callback?.onComplete(result)
        callback?.invoke(result)
    }
}