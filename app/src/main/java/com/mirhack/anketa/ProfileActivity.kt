package com.mirhack.anketa

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val calendar = Calendar.getInstance() //Получаем текущую дату из календаря
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dateFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(dateFormat)

        //Выбор даты рождения
        etDateOfBirth.setOnClickListener {
            //По клику показываем диалог с выбором даты
            DatePickerDialog(
                this,
                object : DatePickerDialog.OnDateSetListener { //Создаем слушатель OnDateSet
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        calendar.set(
                            year,
                            month,
                            dayOfMonth
                        ) //Устанавливаем календарь для форматирования даты
                        etDateOfBirth.setText(sdf.format(calendar.time)) //Форматируем дату
                    }
                },
                year,
                month,
                day
            ).show()
        }

        //Форматируем номер телефона
        etMobileNumber.addTextChangedListener(object : TextWatcher {
            var length = 0
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > length) {  //Если длина строки изменилась в большую сторону
                    when (s.toString().length) {
                        1 -> {
                            if (s.toString() == "7" || s.toString() == "8") {
                                etMobileNumber.setText("+7 ")
                            } else if (s.toString() == "9") {
                                s?.insert(0, "+7 ")
                            }
                            length = s.toString().length
                        }
                        2 -> if (s.toString() == "+7") {
                            etMobileNumber.append(" ")
                            length = s.toString().length
                        }
                        6 -> {
                            s?.append(" ")
                            length = s.toString().length
                        }
                        10 -> {
                            s?.append(" ")
                            length = s.toString().length
                        }
                        13 -> {
                            s?.append(" ")
                            length = s.toString().length
                        }
                        else -> etMobileNumber.setSelection(s.toString().length)
                    }
                } else { //Если длина изменилась в меньшую сторону
                    length = s.toString().length
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        //Выбор начальной даты поездки
        etTravelDateFrom.setOnClickListener {
            //По клику показываем диалог с выбором даты
            DatePickerDialog(
                this,
                object : DatePickerDialog.OnDateSetListener { //Создаем слушатель OnDateSet
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        calendar.set(
                            year,
                            month,
                            dayOfMonth
                        ) //Устанавливаем календарь для форматирования даты
                        etTravelDateFrom.setText(sdf.format(calendar.time)) //Форматируем дату
                    }
                },
                year,
                month,
                day
            ).show()
        }

        //Выбор конечной даты поездки
        etTravelDateTo.setOnClickListener {
            //По клику показываем диалог с выбором даты
            DatePickerDialog(
                this,
                object : DatePickerDialog.OnDateSetListener { //Создаем слушатель OnDateSet
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        calendar.set(
                            year,
                            month,
                            dayOfMonth
                        ) //Устанавливаем календарь для форматирования даты
                        etTravelDateTo.setText(sdf.format(calendar.time)) //Форматируем дату
                    }
                },
                year,
                month,
                day
            ).show()
        }

        //Нужна ли страховка?
        swInsurance.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //Добавляем элемент и прокручиваем список к нему
                llInsuranceDates.visibility = View.VISIBLE
                svMain.post {
                    svMain.smoothScrollTo(llMain.width, llMain.height)
                }
            } else
                llInsuranceDates.visibility = View.GONE
        }

        //Выбор начальной даты страховки
        etInsuranceDateFrom.setOnClickListener {
            //По клику показываем диалог с выбором даты
            DatePickerDialog(
                this,
                object : DatePickerDialog.OnDateSetListener { //Создаем слушатель OnDateSet
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        calendar.set(
                            year,
                            month,
                            dayOfMonth
                        ) //Устанавливаем календарь для форматирования даты
                        etInsuranceDateFrom.setText(sdf.format(calendar.time)) //Форматируем дату
                    }
                },
                year,
                month,
                day
            ).show()
        }

        //Выбор конечной даты страховки
        etInsuranceDateTo.setOnClickListener {
            //По клику показываем диалог с выбором даты
            DatePickerDialog(
                this,
                object : DatePickerDialog.OnDateSetListener { //Создаем слушатель OnDateSet
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        calendar.set(
                            year,
                            month,
                            dayOfMonth
                        ) //Устанавливаем календарь для форматирования даты
                        etInsuranceDateTo.setText(sdf.format(calendar.time)) //Форматируем дату
                    }
                },
                year,
                month,
                day
            ).show()
        }

        //Частный или деловой визит
        swBusinessVisit.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //Добавляем элемент и прокручиваем список к нему
                llInvite.visibility = View.VISIBLE
                svMain.post {
                    svMain.smoothScrollTo(llMain.width, llMain.height)
                }
            } else
                llInvite.visibility = View.GONE
        }
    }

    //        обрабатываем нажатие кнопки
    fun savePdf(view: View) {
        val FONT = "/assets/fonts/arial.ttf"
        val bf = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
        val font = Font(bf, 14F, Font.NORMAL)
        val sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)

        //Проверяем, заполнены ли все поля и переходим к созданию pdf
        if (validateForm()) {
            //CreateDocument
            val pdfDoc = Document()
            //pdf file name
            val fileName = "Anketa.pdf"
            //Создаем папку для анкеты
            val directory = File(Environment.getExternalStorageDirectory().path + "/Visa Profile")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            //        создаем файл анкеты
            val file = File(directory, fileName)

            //сохраняем путь в настройки
            val editor = sharedPreferences.edit()
            editor.putString("filePath", file.canonicalPath)


            //Create instance of PDFWriter class
            PdfWriter.getInstance(pdfDoc, FileOutputStream(file))
            //open document for writing
            pdfDoc.open()

            //add paragraph for each string
            pdfDoc.add(
                Paragraph(
                    "${getString(R.string.full_name)}: ${etFullName?.text.toString()}",
                    font
                )
            )

            editor.putString("touristName", etFullName.text.toString())
            editor.apply()

            pdfDoc.add(
                Paragraph(
                    "${getString(R.string.other_names)}: ${etOtherNames?.text.toString()}",
                    font
                )
            )
            pdfDoc.add(
                Paragraph(
                    "${getString(R.string.marital_status)}: ${spMaritalStatus?.selectedItem.toString()}",
                    font
                )
            )
            pdfDoc.add(
                Paragraph(
                    "${getString(R.string.date_of_birth)}: ${etDateOfBirth?.text.toString()}",
                    font
                )
            )
            pdfDoc.add(
                Paragraph(
                    "${getString(R.string.residential_address)}: ${etResidentialAddress?.text.toString()}",
                    font
                )
            )
            pdfDoc.add(
                Paragraph(
                    "${getString(R.string.mobile_number)}: ${etMobileNumber?.text.toString()}",
                    font
                )
            )
            pdfDoc.add(
                Paragraph(
                    "${getString(R.string.e_mail)}: ${etEmail?.text.toString()}",
                    font
                )
            )
            pdfDoc.add(
                Paragraph(
                    "${getString(R.string.place_of_work)}: ${etPlaceOfWork?.text.toString()}",
                    font
                )
            )
            pdfDoc.add(
                Paragraph(
                    "${getString(R.string.position)}: ${etPosition?.text.toString()}",
                    font
                )
            )
            pdfDoc.add(
                Paragraph(
                    "${getString(R.string.city_trip)}: ${etCityTrip?.text.toString()}",
                    font
                )
            )
            pdfDoc.add(Paragraph("Поездка с: ${etTravelDateFrom?.text.toString()}", font))
            pdfDoc.add(Paragraph("Поездка по: ${etTravelDateTo?.text.toString()}", font))


            if (swInsurance.isChecked) {
                pdfDoc.add(
                    Paragraph(
                        "${getString(R.string.get_insurance)} c ${etInsuranceDateFrom?.text.toString()} по ${etInsuranceDateTo?.text.toString()} ",
                        font
                    )
                )
            } else {
                pdfDoc.add(Paragraph("Страховка не требуется", font))
            }

            if (swBusinessVisit.isChecked) {
                pdfDoc.add(Paragraph(getString(R.string.business_visit), font))
                pdfDoc.add(
                    Paragraph(
                        "${getString(R.string.inviting_company)}: ${etInvitingCompany?.text.toString()}",
                        font
                    )
                )
                pdfDoc.add(
                    Paragraph(
                        "${getString(R.string.inviting_company_address)}: ${etInvitingCompanyAddress?.text.toString()}",
                        font
                    )
                )
                pdfDoc.add(
                    Paragraph(
                        "${getString(R.string.inviting_company_phone)}: ${etInvitingCompanyPhone?.text.toString()}",
                        font
                    )
                )
            }

            pdfDoc.add(
                Paragraph(
                    "${getString(R.string.how_did_you_know)}: ${howDidYouKnow?.selectedItem.toString()}",
                    font
                )
            )

            pdfDoc.close()
            setResult(Activity.RESULT_OK)
            finish()
        }

    }

    private fun validateForm(): Boolean {
        val fields = arrayOf(
            etFullName,
            etDateOfBirth,
            etResidentialAddress,
            etMobileNumber,
            etEmail,
            etPlaceOfWork,
            etPosition,
            etCityTrip,
            etTravelDateFrom,
            etTravelDateTo
        )

        for (field in fields) {
            if (field.text.isEmpty()) {
                field.background.setColorFilter(
                    ContextCompat.getColor(this, R.color.emptyField),
                    PorterDuff.Mode.SRC_ATOP
                )
//                field.background = ContextCompat.getDrawable(this, R.drawable.emty_field)
                Toast.makeText(this, "Заполнены не все поля", Toast.LENGTH_LONG).show()
                return false
            }
        }

        return true
    }

}
