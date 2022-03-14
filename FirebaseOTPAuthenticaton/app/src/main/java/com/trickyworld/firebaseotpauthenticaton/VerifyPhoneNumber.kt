package com.trickyworld.firebaseotpauthenticaton

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit


class VerifyPhoneNumber : AppCompatActivity() {
    private lateinit var timer: CountDownTimer
    private lateinit var phoneNumber : String

    private lateinit var resendOTPbtn: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String

    private lateinit var codeByUserEditText: EditText
    private lateinit var verifyBtn: Button

    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                //1. Google Play services can automatically detect the incoming verification SMS
                // and perform verification without user action.
                //2. phone number can be instantly verified without needing to send or enter a verification code.
                // (Official documentation)

                signInTheUserByCredentials(phoneAuthCredential)

                /* val code = phoneAuthCredential.smsCode
                 if (code != null) {
                     verifyCodeSignInUserByCredentials(code)
                 } else {
                     Toast.makeText(this@VerifyPhoneNumber, "code is null", Toast.LENGTH_SHORT).show()
                 }*/
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // Called in response to an invalid verification request,
                // such as invalid phone number or verification code request.
                Log.i("otp", e.message.toString())
            }

            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
                resendToken = forceResendingToken
                Log.i("verifyPhone", "verificationid $s")
                Log.i("verifyPhone", "resendToken $resendToken")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_phone_number_activity)

        codeByUserEditText = findViewById(R.id.verification_code_by_user)
        verifyBtn = findViewById(R.id.verify_btn)

        resendOTPbtn = findViewById(R.id.resend_otp_tv)

        phoneNumber = intent.getStringExtra("phoneNo")!!

        val firebaseApp = FirebaseApp.initializeApp(applicationContext)
        auth = FirebaseAuth.getInstance(firebaseApp!!)

        timer = object: CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                resendOTPbtn.text = "Wait..." + millisUntilFinished / 1000
                resendOTPbtn.isEnabled = false
            }

            override fun onFinish() {
                resendOTPbtn.text = "Did not receive the code! RESEND"
                resendOTPbtn.isEnabled= true
            }
        }
        timer.start()

        sendVerificationCode(phoneNumber)

        verifyBtn.setOnClickListener(View.OnClickListener {
            val code = codeByUserEditText.text.toString().trim()
            verifyCode(code)
        })

        resendOTPbtn.setOnClickListener {
            timer.start()
            resendVerificationCode(phoneNumber, resendToken)
        }
    }

    private fun sendVerificationCode(phoneNo: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+92$phoneNo")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBack).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(phoneNo: String, token: PhoneAuthProvider.ForceResendingToken?) {

        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+92$phoneNo")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBack)
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private fun verifyCode(codeByUser: String) {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, codeByUser)
            signInTheUserByCredentials(credential)
        } catch (e: Exception) {
            Toast.makeText(this, "Verification Code is wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInTheUserByCredentials(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this@VerifyPhoneNumber, OnCompleteListener { task: Task<AuthResult?> ->

                if (task.isSuccessful) {
                    val intent = Intent(this@VerifyPhoneNumber, WelcomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@VerifyPhoneNumber, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}