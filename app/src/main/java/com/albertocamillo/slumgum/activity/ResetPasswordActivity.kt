package com.albertocamillo.slumgum.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.albertocamillo.slumgum.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity: BaseActivity(){

    private val TAG = this@ResetPasswordActivity.javaClass.simpleName
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        initialise()
    }

    private fun initialise() {

        mAuth = FirebaseAuth.getInstance()
        btnSubmit.setOnClickListener { sendPasswordResetEmail() }
    }

    private fun sendPasswordResetEmail() {

        val email = etEmail.text.toString()

        hideKeyboard()

        if (!TextUtils.isEmpty(email)) {

            Log.d(TAG, "Sending password reset.")

            showProgress()

            mAuth?.sendPasswordResetEmail(email)
                    ?.addOnCompleteListener { task ->

                        hideProgress()

                        if (task.isSuccessful) {
                            val message = getString(R.string.reset_email_sent_success)
                            Log.d(TAG, message)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            updateUI()
                        } else {
                            Log.w(TAG, task.exception?.message)
                            showSnackbar(llResetPasswordRoot, task.exception?.message
                                    ?: getString(R.string.reset_email_sent_failed))
                        }
                    }
        } else {
            showSnackbar(llResetPasswordRoot, getString(R.string.enter_all_details))
        }
    }

    private fun updateUI() {
        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun showProgress() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        btnSubmit.visibility = View.GONE
        pbReset.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        btnSubmit.visibility = View.VISIBLE
        pbReset.visibility = View.GONE
    }

}