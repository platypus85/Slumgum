package com.albertocamillo.slumgum.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.albertocamillo.slumgum.R
import com.albertocamillo.slumgum.data.Swarm
import com.google.android.gms.maps.CameraUpdateFactory
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

    private var mSwarmListener: ChildEventListener? = null

    val swarmList = ArrayList<Swarm>()

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

        firebaseListenerInit()

        initSwarms()
    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mUsersDatabaseReference = mDatabase?.reference?.child("Users")
        mSwarmsDatabaseReference = mDatabase?.reference?.child("swarms")
        mAuth = FirebaseAuth.getInstance()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MainActivity)
    }

    // Include the OnCreate() method here too, as described above.
    override fun onMapReady(googleMap: GoogleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        mGoogleMap = googleMap
        populateMap()
    }

    override fun onStart() {
        super.onStart()
        val mUser = mAuth?.currentUser
        val mUserReference = mUsersDatabaseReference?.child(mUser?.uid ?: "")

        mUserReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvName?.text = "${snapshot.child("firstName").value as String} ${snapshot.child("lastName").value as String}".capitalize()
                tvEmail?.text = mUser?.email
                if (mUser?.isEmailVerified == false) {
                    tvVerifyEmail.visibility = View.VISIBLE
                } else {
                    tvVerifyEmail.visibility = View.GONE
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
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

    private fun firebaseListenerInit() {

        val childEventListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // A new swarm has been added
                // onChildAdded() will be called for each node at the first time
                val swarm = dataSnapshot.getValue(Swarm::class.java)
                swarm?.let {
                    swarmList.add(it)
                    Log.e(TAG, "onChildAdded:" + it.description)

                    val latest = swarmList[swarmList.size - 1]

                    //Update map
                    mGoogleMap?.let {
                        it.addMarker(MarkerOptions().position(LatLng(latest.latitude, latest.longitude)).title(latest.description))
                        it.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latest.latitude, latest.longitude)))
                    }

                }


            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.e(TAG, "onChildChanged:" + dataSnapshot.key)

                // A swarm has changed
                val swarm = dataSnapshot.getValue(Swarm::class.java)
                Toast.makeText(this@MainActivity, "onChildChanged: " + swarm?.description, Toast.LENGTH_SHORT).show()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.e(TAG, "onChildRemoved:" + dataSnapshot.key)

                // A swarm has been removed
                val swarm = dataSnapshot.getValue(Swarm::class.java)
                Toast.makeText(this@MainActivity, "onChildRemoved: " + swarm?.description, Toast.LENGTH_SHORT).show()
                populateMap()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.e(TAG, "onChildMoved:" + dataSnapshot.key)

                // A swarm has changed position
                val swarm = dataSnapshot.getValue(Swarm::class.java)
                Toast.makeText(this@MainActivity, "onChildMoved: " + swarm?.description, Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "postswarms:onCancelled", databaseError.toException())
                Toast.makeText(this@MainActivity, "Failed to load swarm.", Toast.LENGTH_SHORT).show()
            }
        }

        mSwarmsDatabaseReference?.addChildEventListener(childEventListener)

        // copy for removing at onStop()
        mSwarmListener = childEventListener
    }

    private fun initSwarms() {
        val swarmsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                swarmList.clear()
                dataSnapshot.children.mapNotNullTo(swarmList) { it.getValue<Swarm>(Swarm::class.java) }
                populateMap()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        mSwarmsDatabaseReference?.child("swarms")?.addListenerForSingleValueEvent(swarmsListener)
    }

    override fun onStop() {
        super.onStop()

        mSwarmListener?.let {
            val listener = it
            mSwarmsDatabaseReference?.removeEventListener(listener)
        }

        for (swarm in swarmList) {
            Log.e(TAG, "listItem: " + swarm.description)
        }
    }

    private fun populateMap() {
        for (swarm in swarmList) {
            //Update map
            mGoogleMap?.let {
                it.addMarker(MarkerOptions().position(LatLng(swarm.latitude, swarm.longitude)).title(swarm.description))
                it.moveCamera(CameraUpdateFactory.newLatLng(LatLng(swarm.latitude, swarm.longitude)))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        populateMap()
    }
}
