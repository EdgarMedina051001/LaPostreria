package com.example.lapostreria

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
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
import com.example.lapostreria.databinding.ActivityLoginBinding
import com.example.lapostreria.databinding.ActivityStartBinding
import com.example.lapostreria.model.UsuarioModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private var usuarioNombre: String ?= null
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        //Inicializar Firebase Auth
        auth = Firebase.auth
        //Inicializar la base de datos de Firebase
        database = Firebase.database.reference
        //Inicializar Google Sign-In
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.btnCrearCuenta.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnAccederCuentaLogin.setOnClickListener{
            //Obtener el texto de los EditText
            email = binding.tietCorreoLogin.text.toString().trim()
            password = binding.tietContraseALogin.text.toString().trim()
            
            if (email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Logeado Correctamente.", Toast.LENGTH_SHORT).show()
                crearUsuarioCuenta(email, password)
            }
        }

        binding.btnGoogle.setOnClickListener {
            val SignIntent = googleSignInClient.signInIntent
            launcher.launch(SignIntent)

        }

        // Cambiar el color de la barra de estado
        window.statusBarColor = ContextCompat.getColor(this, R.color.Color2)
        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                val account: GoogleSignInAccount = task.result
                val credential= GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful){
                        //Se inicio sesión correctamente con Google
                        Toast.makeText(this, "Se inicio sesión correctamente con Google.", Toast.LENGTH_SHORT).show()
                        updateUi(null)
                    }else{
                        Toast.makeText(this, "Ha ocurrido un error, intente nuevamente.", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Ha ocurrido un error, intente nuevamente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun crearUsuarioCuenta(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val usuario = auth.currentUser
                updateUi(usuario)
            }else{
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val usuario = auth.currentUser
                        guardarUsuarioDatos()
                        updateUi(usuario)
                    }else{
                        Toast.makeText(this, "No se pudo Iniciar Sesión.", Toast.LENGTH_SHORT).show()
                        Log.d("Cuenta", "crearCuentaUsuario: Auntenticacion Fallida.", task.exception)
                    }
                }
            }
        }
    }

    private fun guardarUsuarioDatos() {
        //Obtener el texto de los EditText
        email = binding.tietCorreoLogin.text.toString().trim()
        password = binding.tietContraseALogin.text.toString().trim()

        val usuario = UsuarioModel(usuarioNombre, email, password)
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        usuarioId?.let {
            database.child("Usuarios").child(it).setValue(usuario)
        }
    }

    private fun updateUi(usuario: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
        }else{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}