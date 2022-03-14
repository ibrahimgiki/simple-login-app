package com.trickyworld.firebaseotpauthenticaton

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private var phoneNumber: TextInputLayout? = null
    private var getOTPBtn: AppCompatButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        phoneNumber = findViewById(R.id.phone_number)
        getOTPBtn = findViewById(R.id.get_otp_btn)

        getOTPBtn!!.setOnClickListener {
            goToNextActivity(phoneNumber!!.editText!!.text.toString().trim())
        }

    }

    private fun goToNextActivity(phoneNumber : String){
        val intent = Intent(this@MainActivity, VerifyPhoneNumber::class.java)
        intent.putExtra("phoneNo", phoneNumber)
        startActivity(intent)
    }

}