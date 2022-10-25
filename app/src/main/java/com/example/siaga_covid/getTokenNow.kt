package com.example.siaga_covid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_send_notification_kotlin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class getTokenNow : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_token_now)
        FirebaseService.sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
            etToken.setText(it.token)
        }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        val db = FirebaseFirestore.getInstance()
       val btnSend = findViewById<Button>(R.id.btnSend);
        val etTittle = findViewById<EditText>(R.id.etTitle)
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val etToken = findViewById<EditText>(R.id.etToken)
        mAuth = FirebaseAuth.getInstance();
        val user = mAuth!!.currentUser?.uid;
        val getTokenNow = etToken.text.toString()
        // Update one field, creating the document if it does not already exist.




        btnSend.setOnClickListener {
            val title = etTittle.text.toString()
            val message = etMessage.text.toString()
            val recipientToken = etToken.text.toString()
            // Update one field, creating the document if it does not already exist.
            val users = hashMapOf("Device" to recipientToken)

            db.collection("users").document(user.toString())
                    .set(users, SetOptions.merge())
            if(title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                PushNotification(
                        NotificationData(title, message),
                        recipientToken
                ).also {
                    sendNotification(it)
                }
            }
            val Mulai = Intent(applicationContext, Show_Profile::class.java)
            startActivity(Mulai)
        }
    }

    val TAG = "SendNotification_Kotlin"

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}