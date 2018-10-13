package com.albertocamillo.slumgum.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.albertocamillo.slumgum.R
import com.albertocamillo.slumgum.manager.DatabaseManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : BaseActivity() {

    private val TAG = this@RegistrationActivity.javaClass.simpleName
    private var mUsersDatabase: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        initialise()
    }

    private fun initialise() {

        mDatabase = FirebaseDatabase.getInstance()
        mUsersDatabase = mDatabase?.reference?.child(DatabaseManager.Table.USERS.tableName)
        mAuth = FirebaseAuth.getInstance()

        btnCreateAccount.setOnClickListener { createNewAccount() }
    }

    private fun createNewAccount() {

        val firstName = etFirstName.text.toString()
        val lastName = etLastName.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        hideKeyboard()

        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
                && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            Log.d(TAG, "Creating user.")

            showProgress()

            mAuth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->

                        hideProgress()

                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val userId = mAuth?.currentUser?.uid
                            //Verify Email
                            verifyEmail()

                            //update user profile information
                            val currentUserDb = mUsersDatabase?.child(userId ?: "")
                            currentUserDb?.child("firstName")?.setValue(firstName)
                            currentUserDb?.child("lastName")?.setValue(lastName)
                            currentUserDb?.child("emailAddress")?.setValue(email)

                            updateUserInfoAndUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            showLongSnackbar(llRegistrationRoot, task.exception?.message
                                    ?: getString(R.string.general_error))
                        }
                    }
        } else {
            showLongSnackbar(llRegistrationRoot, getString(R.string.enter_all_details))
        }
    }

    private fun updateUserInfoAndUI() {
        val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun verifyEmail() {
        val mUser = mAuth?.currentUser
        mUser?.sendEmailVerification()
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.e(TAG, "sendEmailVerification:success")
                        Toast.makeText(this,
                                String.format(getString(R.string.verification_email_sent_success), mUser.email),
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "sendEmailVerification:failure", task.exception)
                        Toast.makeText(this,
                                getString(R.string.verification_email_sent_error),
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun showProgress() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        btnCreateAccount.visibility = View.GONE
        pbRegistration.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        btnCreateAccount.visibility = View.VISIBLE
        pbRegistration.visibility = View.GONE
    }
}