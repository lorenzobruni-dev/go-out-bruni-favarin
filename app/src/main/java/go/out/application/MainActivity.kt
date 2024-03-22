package go.out.application

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import go.out.application.FirebaseDBHelper.Companion.auth
import go.out.application.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser!!
        val userID = currentUser?.uid
        val emailUser = currentUser.email

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_friends, R.id.nav_create
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val headerView = navView.getHeaderView(0)
        val navUserEmailTextView: TextView = headerView.findViewById(R.id.textView)
        navUserEmailTextView.text = emailUser

        if (userID != null) {
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

    }



    private fun showAddFriendDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("aggiungi amico")

        val inputEmail = EditText(this)
        inputEmail.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(inputEmail)

        builder.setPositiveButton("Ricerca") { dialog, _ ->
            val email = inputEmail.text.toString()
            FirebaseDBHelper.searchUserByEmail(email) { friendID, message ->
                if (friendID != null) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        }
        builder.setNegativeButton("annulla") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Chiude l'activity corrente
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}