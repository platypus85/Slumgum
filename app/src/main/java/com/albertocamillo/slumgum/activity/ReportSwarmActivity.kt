package com.albertocamillo.slumgum.activity

import android.os.Bundle
import android.text.TextUtils
import com.albertocamillo.slumgum.R
import com.albertocamillo.slumgum.data.Swarm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_report_swarm.*
import java.util.*

class ReportSwarmActivity: BaseActivity(){

    private var user: FirebaseUser? = null

    private var mDatabase: DatabaseReference? = null

    private val REQUIRED = "Required"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_swarm)

        mDatabase = FirebaseDatabase.getInstance().reference
        user = FirebaseAuth.getInstance().currentUser

        btnReportSwarm.setOnClickListener {
            reportSwarm()
            etSwarmDescription.setText("")
        }
    }

    private fun reportSwarm(){
        val description = etSwarmDescription.text.toString()

        if (TextUtils.isEmpty(description)) {
            etSwarmDescription.error = REQUIRED
            return
        }

        writeNewMessage(description)
    }

    private fun writeNewMessage(description: String) {
        val swarm = Swarm(-34.035011, 151.063294, description, Calendar.getInstance().time)
        val swarmValues = swarm.toMap()
        val childUpdates = HashMap<String, Any>()

        val key = mDatabase?.child("swarms")?.push()?.key

        childUpdates["/swarms/$key"] = swarmValues
        childUpdates["/user-swarms/${user?.uid}/$key"] = swarmValues
        mDatabase?.updateChildren(childUpdates)
    }
}