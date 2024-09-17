package com.prototype.beach

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.prototype.beach.databinding.ActivitySigninBinding

class SignInActivity :AppCompatActivity(){
    // data binding
    private lateinit var signinBinding : ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        signinBinding = DataBindingUtil.setContentView(this, R.layout.activity_signin)


        initButtons()
    }

    private fun initButtons(){
        // for the username field
        signinBinding.inputUsernameEditTextView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                signinBinding.inputPasswordEditTextView.requestFocus()
                true
            } else {false}
        }

        // for the submit button
        signinBinding.inputPasswordEditTextView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val username: String = signinBinding.inputUsernameEditTextView.text.toString()
                val password: String = signinBinding.inputPasswordEditTextView.text.toString()

                checkCredentials(username, password)

                true
            } else {false}
        }

        signinBinding.submitButtonView.setOnClickListener{
            val username: String = signinBinding.inputUsernameEditTextView.text.toString()
            val password: String = signinBinding.inputPasswordEditTextView.text.toString()

            checkCredentials(username, password)
        }
    }

    private fun checkCredentials(username : String, password : String){
        if (username == "admin" && password == "1234") {
            Log.d("Signin", "Successfully logged in as $username")
            finish()
        } else {
            // Update the TextView with the error message
            signinBinding.signinResponseTextView.text = "*Incorrect Credentials"

            // Optionally clear the password field after submitting
            signinBinding.inputPasswordEditTextView.text.clear()
        }
    }
}