package com.mirhack.anketa

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_auth.*


class AuthActivity : AppCompatActivity() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val fontLobster = ResourcesCompat.getFont(this,R.font.lobster)
        tvLogoName.typeface= fontLobster

        btnSignIn.setOnClickListener {
            //Нажатие кнопки Вход
            val signInTaskResult = mAuth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            signInTaskResult.addOnCompleteListener {
                if (signInTaskResult.isSuccessful) {
                    //Если вход успешный
                    val intent = Intent(this, SelectStepActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Успешный вход", Toast.LENGTH_LONG).show()
                } else {
                    //Если авторизация не удалась
                    Toast.makeText(this, signInTaskResult.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        btnRegistration.setOnClickListener {
            // Нажатие кнопки регистрация
            val registrationTaskResult =
                mAuth.createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            registrationTaskResult.addOnCompleteListener {
                if (registrationTaskResult.isSuccessful)
                //Если удалось создать нового пользователя
                    Toast.makeText(this, "User create successful", Toast.LENGTH_LONG).show()
                else
                //Если пользователя создать не удалось
                    Toast.makeText(this, registrationTaskResult.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }


}
