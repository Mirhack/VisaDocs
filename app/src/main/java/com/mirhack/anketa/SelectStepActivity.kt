package com.mirhack.anketa

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_select_step.*
import java.io.File

class SelectStepActivity : AppCompatActivity() {

    private val requestCodeProfile = 1
    private val requestCodePassport = 2
    private val requestCodeInternationalPassport = 3
    private val requestCodeBooing = 4
    private val requestCodePlane = 5
    private val requestCodePermissionStorage = 6
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    var readySteps = StringBuilder("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_step)


//        Получаем токен FCM
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("AnketaLog", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                Log.d("AnketaLog", token)
            })


        //Получаем разрешения на запись файлов
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //System OS  >= Marshmallow , check  if permissions enabled or not
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //permission not granted, request it
                val permissions = arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                requestPermissions(permissions, requestCodePermissionStorage)
            }
        }

        //Создаем папку для файлов и фото
        if (!Companion.directory.exists()) {
            Companion.directory.mkdirs()
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            //Если вход не успешный
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            requestCodePermissionStorage -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    Toast.makeText(this, "Доступ к файлам разрешен", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        this,
                        "Доступ к файлам запрещен, дальнейщая работа невозможна",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }

    fun uploadFiles(view: View) {
        when {
            !readySteps.contains(" anketa ") -> Toast.makeText(
                this,
                "Вы не заполнили анкету",
                Toast.LENGTH_SHORT
            ).show()
            !readySteps.contains(" passport ") -> Toast.makeText(
                this,
                "Вы не добавили фото паспорта",
                Toast.LENGTH_SHORT
            ).show()
            !readySteps.contains(" internationalPassport ") -> Toast.makeText(
                this,
                "Вы не добавили фото загранпаспорта",
                Toast.LENGTH_SHORT
            ).show()
            else -> {
                val storage: StorageReference = FirebaseStorage.getInstance().reference
                val sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)
                val touristName = sharedPreferences.getString("touristName", "Турист")
                val pdfReference = storage.child("$touristName/Анкета.pdf")
                val pdfFile = File(sharedPreferences.getString("filePath", ""))
                pdfReference.putFile(Uri.fromFile(pdfFile))

                val passportUriString = sharedPreferences.getStringSet("passport", setOf())
                val internationalPassportUriString =
                    sharedPreferences.getStringSet("internationalPassport", setOf())
                val bookingUriString = sharedPreferences.getStringSet("booking", setOf())
                val planeUriString = sharedPreferences.getStringSet("plane", setOf())
//        Создаем map для прогона Set ов через цикл
                val imagesMap = mapOf<String, Set<String>>(
                    "passport" to passportUriString,
                    "internationalPassport" to internationalPassportUriString,
                    "booking" to bookingUriString,
                    "plane" to planeUriString
                )

                for (imagesUriString in imagesMap) {
                    var counter = 1
                    val fileName = when (imagesUriString.key) {
                        "passport" -> "Паспорт"
                        "internationalPassport" -> "Загранпаспорт"
                        "booking" -> "Отель"
                        "plane" -> "Перелет"
                        else -> ""
                    }
                    for (uriString in imagesUriString.value) {
                        val uri = Uri.parse(uriString)
                        //получаем mime-type из uri
                        val cr: ContentResolver = this.contentResolver
                        val mime = cr.getType(uri)

                        //получаем расширение из mime-type
                        val extension = when (mime) {
                            "image/jpeg" -> ".jpg"
                            else -> ".nothing"
                        }

                        val fileReference =
                            storage.child("$touristName/${fileName}_$counter$extension")//название файла соответствует его порядковому номеру
                        fileReference.putFile(uri)
                        counter++
                    }
                }
                Toast.makeText(this, "Анкета отправлена", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun edit(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivityForResult(intent, requestCodeProfile)
    }

    fun addPassport(view: View) {
        val intent = Intent(this, PassportActivity::class.java)
        startActivityForResult(intent, requestCodePassport)
    }

    fun addInternationalPassport(view: View) {
        val intent = Intent(this, InternationalPassportActivity::class.java)
        startActivityForResult(intent, requestCodeInternationalPassport)
    }

    fun addBooking(view: View) {
        val intent = Intent(this, BookingActivity::class.java)
        startActivityForResult(intent, requestCodeBooing)
    }

    fun addPlaneTickets(view: View) {
        val intent = Intent(this, PlaneTicketsActivity::class.java)
        startActivityForResult(intent, requestCodePlane)
    }

    fun logout(view: View) {
        FirebaseAuth.getInstance().signOut()
        intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestCodeProfile -> {
                    ivAnketa.setColorFilter(Color.rgb(255, 255, 255))
                    ivAnketa.setBackgroundColor(ContextCompat.getColor(this, R.color.iconColor))
                    readySteps.append(" anketa ")
                }
                requestCodePassport -> {
                    ivPassport.setColorFilter(Color.rgb(255, 255, 255))
                    ivPassport.setBackgroundColor(ContextCompat.getColor(this, R.color.iconColor))
                    readySteps.append(" passport ")
                }
                requestCodeInternationalPassport -> {
                    ivZagranpassport.setColorFilter(Color.rgb(255, 255, 255))
                    ivZagranpassport.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.iconColor
                        )
                    )
                    readySteps.append(" internationalPassport ")
                }
                requestCodeBooing -> {
                    ivHotel.setColorFilter(Color.rgb(255, 255, 255))
                    ivHotel.setBackgroundColor(ContextCompat.getColor(this, R.color.iconColor))
                }
                requestCodePlane -> {
                    ivPlane.setColorFilter(Color.rgb(255, 255, 255))
                    ivPlane.setBackgroundColor(ContextCompat.getColor(this, R.color.iconColor))
                }
            }
        }
    }

    companion object {
        val directory = File(Environment.getExternalStorageDirectory().path+"/Visa Profile")
    }

}
