package com.seo.app.seostudio.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.seo.app.seostudio.R
import java.lang.Exception
import java.util.*

object LoadingDialog {

    var dialog: AlertDialog? = null

    fun showLoadingDialog(context: Context) {
        val factory = LayoutInflater.from(context)
        val renameDialogView: View = factory.inflate(R.layout.dialog_loading, null)

    //    if(dialog == null) {
            dialog = AlertDialog.Builder(context).create()
            dialog?.setView(renameDialogView)
            dialog?.setCancelable(false)
            dialog?.setCanceledOnTouchOutside(false)

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog?.window?.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.attributes = lp
     //   }
        try {
            if (dialog?.isShowing == false)
            {
                dialog?.show()
            }
        } catch (e: java.lang.IllegalArgumentException) {
        } catch (e: Exception) {
        }
    }

    fun hideLoadingDialog() {
        try {
            dialog?.dismiss()
        } catch (e: java.lang.IllegalArgumentException) {
            Log.i("InterstitialADTag", "hideLoadingDialog: ${e.printStackTrace()} ")
        } catch (e: Exception) {
            Log.i("InterstitialADTag", "hideLoadingDialog: ${e.printStackTrace()} ")
        }
    }

    fun inShowing(): Boolean? {
        return dialog?.isShowing
    }

}