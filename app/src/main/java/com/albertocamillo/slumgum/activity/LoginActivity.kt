package com.albertocamillo.slumgum.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.albertocamillo.slumgum.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private val TAG = this@LoginActivity.javaClass.simpleName
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initialise()
    }

    private fun initialise() {

        mAuth = FirebaseAuth.getInstance()

        btnCreateAccount?.setOnClickListener {
            val myIntent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(myIntent)
        }

        btnLogin?.setOnClickListener {
            loginUser()
        }

        tvForgotPassword.setOnClickListener {
            val myIntent = Intent(this@LoginActivity, ResetPasswordActivity::class.java)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(myIntent)
        }
    }

    private fun loginUser() {
        val email = etEmail?.text.toString()
        val password = etPassword?.text.toString()

        hideKeyboard()

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            Log.d(TAG, "Logging in user.")

            showProgress()

            mAuth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->

                        hideProgress()

                        if (task.isSuccessful) {
                            // Sign in success, update UI with signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            updateUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithEmail:failure", task.exception)
                            showSnackbar(llLoginRoot, task.exception?.message
                                    ?: getString(R.string.authentication_failed))
                        }
                    }
        } else {
            showSnackbar(llLoginRoot, getString(R.string.enter_all_details))
        }
    }

    private fun updateUI() {
        val myIntent = Intent(this@LoginActivity, MainActivity::class.java)
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(myIntent)
    }

    private fun showProgress() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        btnLogin.visibility = View.GONE
        pbLogin.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        btnLogin.visibility = View.VISIBLE
        pbLogin.visibility = View.GONE
    }
}