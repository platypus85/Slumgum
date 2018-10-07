package com.albertocamillo.slumgum.manager

import com.google.firebase.auth.FirebaseAuth

class AuthenticationManager{
    companion object {
        fun isUserLoggedIn(): Boolean{
            val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
            return mAuth?.currentUser != null
        }
    }
}