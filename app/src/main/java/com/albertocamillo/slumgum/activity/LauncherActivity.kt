package com.albertocamillo.slumgum.activity

import android.content.Intent
import android.os.Bundle
import com.albertocamillo.slumgum.R
import com.albertocamillo.slumgum.manager.AuthenticationManager.Companion.isUserLoggedIn
import kotlinx.android.synthetic.main.activity_launcher.*

class LauncherActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        initialise()
    }

    private fun initialise() {
        checkUserExistenceAndLogin()

        btnCreateAccount.setOnClickListener {
            val myIntent = Intent(this@LauncherActivity, RegistrationActivity::class.java)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(myIntent)
        }

        btnLogin.setOnClickListener {
            val myIntent = Intent(this@LauncherActivity, LoginActivity::class.java)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(myIntent)
        }
    }

    private fun checkUserExistenceAndLogin() {
        if (isUserLoggedIn()) {
            val myIntent = Intent(this@LauncherActivity, MainActivity::class.java)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(myIntent)
            finish()
        }
    }
}