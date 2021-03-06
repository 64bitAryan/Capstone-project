package com.project.findme.mainactivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.findme.utils.Constants.FRAGMENTS_LIST
import com.project.findme.utils.Constants.FRAGMENTS_LIST_BOTTOM_NAV
import com.project.findme.utils.Constants.SLACK_COMMUNITY_JOINING_LINK
import com.ryan.findme.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavBar: BottomNavigationView
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        toggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close)


        bottomNavBar = findViewById(R.id.bottom_nav_view)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.findNavController()

        val navView = findViewById<NavigationView>(R.id.side_nav_view)

        appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.homeFragment,
                R.id.personSearchFragment,
                R.id.allUsers,
                R.id.userProfileFragment,
                R.id.signOutDialogFragment,
            )
        ).setOpenableLayout(drawerLayout)
            .build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavBar.setupWithNavController(navController)
        navView.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in FRAGMENTS_LIST_BOTTOM_NAV) {
                bottomNavBar.visibility = View.GONE
            } else {
                bottomNavBar.visibility = View.VISIBLE
            }
        }

        navView.menu.findItem(R.id.openForum).setOnMenuItemClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(SLACK_COMMUNITY_JOINING_LINK))
            startActivity(browserIntent)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val headerView: View = navView.getHeaderView(0)
        val userEmail = headerView.findViewById<TextView>(R.id.text_view_user_email)
        val userName = headerView.findViewById<TextView>(R.id.text_view_user_name)
        val profile = headerView.findViewById<ConstraintLayout>(R.id.layout_profile_user)

        userEmail.text = Firebase.auth.currentUser?.email ?: "guest@gmail.com"
        userName.text = Firebase.auth.currentUser?.displayName ?: "Guest"
        profile.setOnClickListener {
            navController.navigate(R.id.userProfileFragment)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser!!.providerData.size > 0) {
            val authProvider =
                firebaseUser.providerData[firebaseUser.providerData.size - 1].providerId
            val navMenu: Menu = navView.menu
            navMenu.findItem(R.id.changePasswordFragment).isVisible = authProvider == "password"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return when (navController.currentDestination?.id) {
                in FRAGMENTS_LIST -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    super.onOptionsItemSelected(item)
                }
                in listOf(R.id.createPostFragment, R.id.createTextPostFragment) -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    onBackPressed()
                    true
                }
                else -> {
                    true
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            navController,
            appBarConfiguration
        ) || super.onSupportNavigateUp()
    }

}