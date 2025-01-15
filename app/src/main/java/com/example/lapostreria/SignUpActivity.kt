package com.example.lapostreria

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lapostreria.databinding.ActivitySignUpBinding
import com.example.lapostreria.databinding.ActivityStartBinding
import com.example.lapostreria.model.UsuarioModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var nombre: String
    private lateinit var celular: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Cambiar el color de la barra de estado
        window.statusBarColor = ContextCompat.getColor(this, R.color.Color2)
        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        //Inicializar FirebaseAuth
        auth = Firebase.auth
        //Inicializar Firebase Database
        database = Firebase.database.reference
        //Inicializar Google Sign-In
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.btnRegistrarCuentaSign.setOnClickListener {
            nombre = binding.tietNombreSign.text.toString().trim()
            celular = binding.tietCelularSign.text.toString().trim()
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (email.isEmpty() || celular.isEmpty() || celular.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                crearCuenta(email, password)
            }
        }

        binding.btnAccederCuenta.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoogle.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val cuenta: GoogleSignInAccount? = task.result
                    val credential = GoogleAuthProvider.getCredential(cuenta?.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Bienvenido. 😊", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Inicio de Sesión Con Google Falló.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Inicio de Sesión Con Google Falló.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private fun crearCuenta(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Se creo la cuenta correctamente.", Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al crear la cuenta.", Toast.LENGTH_SHORT).show()
                Log.d("Cuenta", "crearCuenta: Fallo", task.exception)
            }
        }
    }

    private fun saveUserData() {
        //Obtener el texto de los EditText
        email = binding.email.text.toString().trim()
        celular = binding.tietCelularSign.text.toString().trim()
        nombre = binding.tietNombreSign.text.toString().trim()
        password = binding.password.text.toString().trim()

        val usuario = UsuarioModel(nombre, email, password, celular)
        val usuarioId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("Usuarios").child(usuarioId).setValue(usuario)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}