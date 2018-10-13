package com.albertocamillo.slumgum.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.net.ConnectivityManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.albertocamillo.slumgum.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

open class BaseActivity: AppCompatActivity(){

    override fun onResume() {
        checkNetworkConnection()
        super.onResume()
    }

    fun showLongSnackbar(parentLayout: View, message: String) {
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show()
    }

    fun showShortSnackbar(parentLayout: View, message: String) {
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
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

    private fun requestPermission(permissionType: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permissionType), requestCode)
    }

    companion object {
        internal const val LOCATION_REQUEST_CODE = 101
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun checkNetworkConnection() {
        if (!isNetworkAvailable()) {
            val rootView = getRootView()
            rootView?.let {
                showLongSnackbar(it, getString(R.string.connection_unavailable))
            }
        }
    }

    private fun getRootView(): View? {
        val contentViewGroup = findViewById<View>(android.R.id.content) as ViewGroup?
        var rootView: View? = null

        contentViewGroup?.let {
            rootView = it.getChildAt(0)
        }

        if (rootView == null)
            rootView = window.decorView.rootView

        return rootView
    }

    fun showShortToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun centerMapOnUser(googleMap: GoogleMap) {
        val mapSettings = googleMap?.uiSettings
        mapSettings?.isZoomControlsEnabled = true
        //Request user location
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true

            val manager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val mCriteria = Criteria()
            val bestProvider = manager.getBestProvider(mCriteria, true)

            val mLocation = manager.getLastKnownLocation(bestProvider)
            if (mLocation != null) {

                Log.e("TAG", "GPS is on")
                val currentLatitude = mLocation.latitude
                val currentLongitude = mLocation.longitude
                val userLocation = LatLng(currentLatitude, currentLongitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)
            }

        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE)
        }
    }
}