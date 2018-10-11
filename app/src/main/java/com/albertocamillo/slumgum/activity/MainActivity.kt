package com.albertocamillo.slumgum.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.albertocamillo.slumgum.R
import com.albertocamillo.slumgum.data.Swarm
import com.albertocamillo.slumgum.manager.DatabaseManager
import com.albertocamillo.slumgum.util.StringExtension.capitalizeWords
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private var mUsersDatabaseReference: DatabaseReference? = null
    private var mSwarmsDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private val TAG = this@MainActivity.javaClass.simpleName
    private var mGoogleMap: GoogleMap? = null
    private val swarmList = ArrayList<Swarm>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            //Launch swarm creation
            launchNewSwarmActivity()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        initialise()
    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mUsersDatabaseReference = mDatabase?.reference?.child(DatabaseManager.Table.USERS.tableName)
        mSwarmsDatabaseReference = mDatabase?.reference?.child(DatabaseManager.Table.SWARMS.tableName)
        mAuth = FirebaseAuth.getInstance()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MainActivity)
    }

    // Include the OnCreate() method here too, as described above.
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        loadSwarms()

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        googleMap.isMyLocationEnabled = true
    }

    override fun onStart() {
        super.onStart()
        loadUser()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
            R.id.nav_logout -> {
                logout()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun launchLauncher() {
        val intent = Intent(this@MainActivity, LauncherActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun logout() {
        mAuth?.signOut()
        launchLauncher()
    }

    private fun launchNewSwarmActivity() {
        val intent = Intent(this@MainActivity, ReportSwarmActivity::class.java)
        startActivity(intent)
    }

    private fun loadSwarms() {
        mSwarmsDatabaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                clearSwarmsAndMarkers()
                addMarkersOnTheMap(dataSnapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun loadUser() {
        val mUser = mAuth?.currentUser
        mUser?.reload()
        val mUserReference = mUsersDatabaseReference?.child(mUser?.uid ?: "")
        mUserReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvName?.text = "${snapshot.child("firstName").value as String} ${snapshot.child("lastName").value as String}".capitalizeWords()
                tvEmail?.text = mUser?.email
                if (mUser?.isEmailVerified == false) {
                    tvVerifyEmail.visibility = View.VISIBLE
                } else {
                    tvVerifyEmail.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }
        })
    }

    private fun addMarkersOnTheMap(dataSnapshot: DataSnapshot) {
        for (ds in dataSnapshot.children) {
            val swarm = ds.getValue(Swarm::class.java)
            swarm?.let {
                swarmList.add(it)
                val latest = swarmList[swarmList.size - 1]
                //Update map
                mGoogleMap?.addMarker(MarkerOptions().position(LatLng(latest.latitude, latest.longitude)).title(latest.description))
            }
        }
    }

    private fun clearSwarmsAndMarkers() {
        swarmList.clear()
        mGoogleMap?.clear()
    }

    override fun onResume() {
        super.onResume()
        loadUser()
    }
}
