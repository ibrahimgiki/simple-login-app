package com.trickyworld.simpleloginapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var userNameField: TextInputLayout
    private lateinit var passwordField: TextInputLayout
    private lateinit var loginBtn: AppCompatButton

    private lateinit var messageFromServerTextView: TextView
    private var messageFromServer = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        messageFromServerTextView = findViewById(R.id.message_from_server)

        userNameField = findViewById(R.id.username)
        passwordField = findViewById(R.id.password)
        loginBtn = findViewById(R.id.login_btn)

        loginBtn!!.setOnClickListener {
            loginFunc(userNameField.editText!!.text.toString(),   passwordField.editText!!.text.toString())
        }
    }

    private fun loginFunc(username: String, password: String) {

        val userServiceInterface = RetrofitClient.getRetrofitInstance().create(UserServiceInterface::class.java)
        val  call = userServiceInterface.login(username, password)

        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
               if (response.isSuccessful) {
                   val jsonObject =  response.body()!!.asJsonObject
                   messageFromServer = jsonObject.get("message").toString()
                    //if (jsonObject.get("message").toString().contains("Logged in successfully")){ }
               }else {
                   messageFromServer = response.message().toString()
               }
               messageFromServerTextView.text = messageFromServer
               Toast.makeText(this@LoginActivity, messageFromServer, Toast.LENGTH_LONG).show()
               Log.i("server response", messageFromServer)
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
               messageFromServer = t.message.toString()
               messageFromServerTextView.text = messageFromServer
               Toast.makeText(this@LoginActivity, messageFromServer, Toast.LENGTH_LONG).show()
               Log.i("server response", messageFromServer)
            }
        })

    }
}