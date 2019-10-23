package com.mirhack.anketa

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.activity_add_photos.*
import java.io.File
import java.text.SimpleDateFormat

class BookingActivity : AppCompatActivity() {


    private val requestCodeCamera = 0
    private val requestCodeGallery = 1
    private val images = mutableSetOf<String>() //Коллекция будущих фото
    private val animation = AlphaAnimation(0f, 1f) //Анимация текста
    private var counter = 0 // Счётчик добавленных фото
    private var isOpen = false
    //Создаем папку для фото
    private var directory = SelectStepActivity.directory
    private var photoUri: Uri? = null
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photos)

        //Настройки, в которых хранятся изображения
        val sharedPreferences = this.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        //Получаем set изображений из настроек
        val bookingUriString = sharedPreferences.getStringSet("booking", setOf())
        if (bookingUriString.isNullOrEmpty()) { //если сохраненных фоток нет
            animation.duration = 1500 //Длительность анимации при первом появлении текста
            tvText.text = "Добавьте фото брони отеля"
            tvText.startAnimation(animation)
        } else { //если есть сохраненные фотки, для каждого фото создаем ImageView
            bookingUriString.forEach { uriString ->
                val inflater = LayoutInflater.from(this)  //Создание миниатюры изображения
                val relativeLayout =
                    inflater.inflate(R.layout.image_view, llScrollView, false) as RelativeLayout
                val imageView = relativeLayout.getChildAt(0) as ImageView

                images.add(uriString) //Добавляем URI к коллекции в формате URI to String (Даже если есть - создается новая коллекция)
                imageView.setImageURI(Uri.parse(uriString))
                llScrollView.addView(relativeLayout)
                counter++ //Увеличиваем счётчик фото для показа правильного текста

                onImageClick(relativeLayout, uriString)
            }

            tvText.text = when (counter) {  //Если страница № то выводим текст №
                1 -> "Можно переходить к следующему шагу"
                else -> "Можно переходить к следующему шагу"
            }
            animation.duration = 1500
            tvText.startAnimation(animation)
        }

        val fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        val fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close)
        val rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward)
        val rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward)

        fun animateFab() {
            if (isOpen) {
                btnAdd.startAnimation(rotateBackward)
                btnPickPhoto.startAnimation(fabClose)
                btnTakePhoto.startAnimation(fabClose)
                btnPickPhoto.isClickable = false
                btnTakePhoto.isClickable = false
                isOpen = false
            } else {
                btnAdd.startAnimation(rotateForward)
                btnPickPhoto.startAnimation(fabOpen)
                btnTakePhoto.startAnimation(fabOpen)
                btnPickPhoto.isClickable = true
                btnTakePhoto.isClickable = true
                isOpen = true
            }
        }

        btnAdd.setOnClickListener { animateFab() }

        btnTakePhoto.setOnClickListener {
            photoUri = generateFileUri()
            //открываем камеру
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            counter++
            startActivityForResult(intent, requestCodeCamera)
        }


        btnPickPhoto.setOnClickListener {
            //  Открываем выбор изображений
            intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*" //MIME type
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            counter++
            startActivityForResult(intent, requestCodeGallery)
        }

        btnNextStep.setOnClickListener {
            setResult(Activity.RESULT_OK) // Для подсветки иконки главного меню
            val editor = sharedPreferences.edit()
            editor.putStringSet("booking", images)
            editor.apply() //В настройки записывается коллекция из URI to String
            finish()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode != Activity.RESULT_CANCELED) {
            val inflater = LayoutInflater.from(this)  //Создание миниатюры изображения
            val relativeLayout =
                inflater.inflate(R.layout.image_view, llScrollView, false) as RelativeLayout
            val imageView = relativeLayout.getChildAt(0) as ImageView

            when (requestCode) {
                requestCodeGallery -> {
                    imageUri = intent!!.data
                    contentResolver.takePersistableUriPermission(
                        imageUri!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    ) //Получаем долгий доступ к uri
                }
                requestCodeCamera -> {
                    imageUri = photoUri
                }
            }
            images.add(imageUri.toString()) //Добавляем URI к коллекции в формате URI to String
            imageView.setImageURI(imageUri)
            llScrollView.addView(relativeLayout)
            //При создании imageView запускаем функцию, которая вешает на него onClickListener и передает туда URI изображения
            onImageClick(
                relativeLayout,
                imageUri.toString()
            )
            animation.duration = 2000 //Длительность анимации текста после добавления фото
            tvText.text = when (counter) {  //Если страница № то выводим текст №
                1 -> "Можно переходить к следующему шагу"
                else -> "Можно переходить к следующему шагу"
            }
            if (counter > 0) {
                //После добавления одного фото включение кнопки продолжить
                btnNextStep.visibility = View.VISIBLE
            }
            tvText.startAnimation(animation)
        } else {
            counter--  //Уменьшаем счётчик, если пользователь не выбрал или не сделал фото}
            Log.d("AnketaLog", "Пользователь не выбрал или не сделал фото")
        }
    }

    private fun generateFileUri(): Uri {
        val dateFormat = "dd.MM.yyyy HH:mm:ss"
        val sdf = SimpleDateFormat(dateFormat)
        val date = sdf.format(System.currentTimeMillis())
        val imageFile = File(
            directory, "photo_"
                    + date + ".jpg"
        )
        Log.d(
            "AnketaLog",
            FileProvider.getUriForFile(
                this,
                "com.mirhack.anketa.fileProvider",
                imageFile
            ).toString()
        )
        return FileProvider.getUriForFile(this, "com.mirhack.anketa.fileProvider", imageFile)

    }

    private fun onImageClick(imageView: RelativeLayout, uriString: String) {

        val onPositiveClick = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                images.remove(uriString)
                tvText.text = "Добавьте бронь отеля"
                imageView.visibility = View.GONE
                counter--
            }
        }
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Подтвердите действие").setMessage("Удалить фото?")
            .setPositiveButton("Да", onPositiveClick)

        imageView.setOnClickListener { dialog.show() }

    }

}