package go.out.application

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import go.out.application.FirebaseDBHelper.Companion.auth
import go.out.application.databinding.ActivityMainBinding
import go.out.application.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser!!
        val userID = currentUser.uid
        val emailUser = currentUser.email

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_friends, R.id.nav_create
            ), drawerLayout
        )
        

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_exit -> {
                    startLoginActivity()
                    true
                }
                R.id.nav_addContact -> {
                    FirebaseDBHelper.showAddFriendDialog(this){}
                    true
                }
                else -> {
                    findNavController(R.id.nav_host_fragment_content_main).navigate(menuItem.itemId)
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }
        val headerView = navView.getHeaderView(0)
        val navUserEmailTextView: TextView = headerView.findViewById(R.id.textView)
        navUserEmailTextView.text = emailUser

        val db = FirebaseDBHelper.dbUsers.child(userID)
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val nomeUtente = dataSnapshot.child("nome").getValue(String::class.java)
                    if (nomeUtente != null) {
                        val navUserName: TextView = headerView.findViewById(R.id.textViewNome)
                        navUserName.text = nomeUtente
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
            }
        })

    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Chiude l'activity corrente
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}