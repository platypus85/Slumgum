package com.albertocamillo.slumgum.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import com.albertocamillo.slumgum.R
import com.albertocamillo.slumgum.data.Swarm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_report_swarm.*
import java.io.IOException
import java.util.*


class ReportSwarmActivity : BaseActivity() {

    private var user: FirebaseUser? = null
    private var mDatabase: DatabaseReference? = null
    private val REQUIRED = "Required"
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 71
    //Firebase
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_swarm)
        storage = FirebaseStorage.getInstance()
        storageReference = storage?.reference

        mDatabase = FirebaseDatabase.getInstance().reference
        user = FirebaseAuth.getInstance().currentUser

        btnReportSwarm.setOnClickListener {
            reportSwarm()
            etSwarmDescription.setText("")
        }

        btnChoosePhoto.setOnClickListener {
            chooseImage()
        }
    }

    private fun reportSwarm() {
        val description = etSwarmDescription.text.toString()

        if (TextUtils.isEmpty(description)) {
            etSwarmDescription.error = REQUIRED
            return
        }

        uploadImage(description)
    }

    private fun writeNewMessage(description: String, downloadUrl: String = "") {
        val swarm = Swarm(-34.035011, 151.063294, description, Calendar.getInstance().time, false, downloadUrl)
        val swarmValues = swarm.toMap()
        val childUpdates = HashMap<String, Any>()

        val key = mDatabase?.child("swarms")?.push()?.key

        childUpdates["/swarms/$key"] = swarmValues
        childUpdates["/user-swarms/${user?.uid}/$key"] = swarmValues
        mDatabase?.updateChildren(childUpdates)
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.data != null) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                ivPhoto.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(description: String) {

        filePath?.let {
            val ref = storageReference?.child("images/" + UUID.randomUUID().toString())
            ref?.putFile(it)?.addOnSuccessListener { taskSnapshot ->
                Toast.makeText(this@ReportSwarmActivity, "Uploaded", Toast.LENGTH_SHORT).show()
                ref.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it
                    writeNewMessage(description, downloadUrl.toString())
                }

            }?.addOnFailureListener { exception ->
                Toast.makeText(this@ReportSwarmActivity, "Failed " + exception.message, Toast.LENGTH_SHORT).show()
            }?.addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            }
        } ?: writeNewMessage(description)
    }
}