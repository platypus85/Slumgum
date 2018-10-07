package com.albertocamillo.slumgum.activity

import android.app.Activity
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager


open class BaseActivity: AppCompatActivity(){

    fun showSnackbar(parentLayout: View, message: String){
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show()
    }

    fun hideKeyboard() {
        val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = this.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}