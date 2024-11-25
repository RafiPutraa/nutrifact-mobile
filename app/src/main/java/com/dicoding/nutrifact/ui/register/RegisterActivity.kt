package com.dicoding.nutrifact.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import cn.pedant.SweetAlert.SweetAlertDialog
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.databinding.ActivityRegisterBinding
import com.dicoding.nutrifact.ui.ViewModelFactory
import com.dicoding.nutrifact.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance()
    }
    private var loadingDialog: SweetAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            validateInputs()
        }
    }

    private fun validateInputs() {
        val username = binding.edRegisterUsername.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()
        val isValidUsername = binding.edRegisterUsername.validate()
        val isValidEmail = binding.edRegisterEmail.validate()
        val isValidPassword = binding.edRegisterPassword.validate()
        val isValidVerif = binding.edRegisterVerif.validate()

        Log.d(
            "RegisterActivity",
            "isValidUsername: $isValidUsername, isValidEmail: $isValidEmail, isValidPassword: $isValidPassword"
        )

        val isPasswordMatch =
            binding.edRegisterPassword.text.toString() == binding.edRegisterVerif.text.toString()

        if (!isPasswordMatch) {
            binding.edRegisterVerif.error = getString(R.string.error_password_not_match)
        } else {
            binding.edRegisterVerif.error = null
        }

        if (isValidUsername && isValidEmail && isValidPassword && isValidVerif && isPasswordMatch) {
            register(username, email, password)
        } else {
            Toast.makeText(this, "Register Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun register(name: String, email: String, password: String) {
        registerViewModel.register(name, email, password).observe(this, Observer { result ->
            Log.d("RegisterActivity", "Result: $result")
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Registration Successful")
                        .setContentText("You have successfully registered.\nPlease log in.")
                        .setConfirmText("OK")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .show()
                }
                is ResultState.Error -> {
                    showLoading(false)
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Registration Failed")
                        .setContentText(result.error)
                        .setConfirmText("OK")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                        }
                        .show()
                }
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            if (loadingDialog == null) {
                loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).apply {
                    titleText = "Loading"
                    setCancelable(false)
                    show()
                }
            } else {
                loadingDialog?.show()
            }
        } else {
            loadingDialog?.dismissWithAnimation()
        }
    }
}