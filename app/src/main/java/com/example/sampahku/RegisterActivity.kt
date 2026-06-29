// halaman register, inten dari halaman login
package com.example.sampahku

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sampahku.ApiClient.service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var editName: EditText? = null
    private var editEmail: EditText? = null
    private var editPassword: EditText? = null
    private var editConfirmPassword: EditText? = null
    private var btnRegister: Button? = null
    private var tvLogin: TextView? = null
    private var ivGoogle: ImageView? = null
    private var ivApple: ImageView? = null
    private var ivBack: ImageView? = null // tombol back hijau utk balik ke halaman login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // sama seperti di halaman login
        // menyembunyikan action bar ketika sudah sampai ke halaman
        if (getSupportActionBar() != null) {
            getSupportActionBar()!!.hide()
        }

        // insialisasi view-view-nya, mirip dgn halaman login
        editName = findViewById<EditText>(R.id.edt_name)
        editEmail = findViewById<EditText>(R.id.edt_email)
        editPassword = findViewById<EditText>(R.id.edt_password)
        editConfirmPassword = findViewById<EditText>(R.id.edt_confirm_password)
        btnRegister = findViewById<Button>(R.id.btn_register)
        tvLogin = findViewById<TextView>(R.id.tv_login)
        ivGoogle = findViewById<ImageView>(R.id.iv_google)
        ivApple = findViewById<ImageView>(R.id.iv_apple)
        ivBack = findViewById<ImageView>(R.id.iv_back)

        // setOnClickListener
        btnRegister!!.setOnClickListener(this)
        tvLogin!!.setOnClickListener(this)
        ivGoogle!!.setOnClickListener(this)
        ivApple!!.setOnClickListener(this)
        ivBack!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.getId() == R.id.btn_register) {
            handleRegister()
        } else if (v.getId() == R.id.iv_google) {
            Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
        } else if (v.getId() == R.id.iv_apple) {
            Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
        } else if (v.getId() == R.id.tv_login) {
            // inten untuk kembali ke halaman login (LoginActivity)
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else if (v.getId() == R.id.iv_back) {
            finish() // Kembali ke halaman sebelumnya (LoginActivity)
        }
    }

    // validasi dan proses registrasi akunnya
    private fun handleRegister() {
        val name = editName!!.getText().toString().trim { it <= ' ' }
        val email = editEmail!!.getText().toString().trim { it <= ' ' }
        val password = editPassword!!.getText().toString().trim { it <= ' ' }
        val confirmPassword = editConfirmPassword!!.getText().toString().trim { it <= ' ' }

        // Validasi input kosong (Gunakan logika validasi bawaan Anda)
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Harap isi semua data!", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            editConfirmPassword!!.setError(getString(R.string.error_password_mismatch))
            return
        }

        Toast.makeText(this, "Sedang membuat akun...", Toast.LENGTH_SHORT).show()

        // Mengirim data ke Django (Menyediakan nilai default "-" untuk no_telepon dan alamat, serta 0 untuk total poin)
        service.registerUser(name, email, password, "-", "-", 0)!!
            .enqueue(object : Callback<ProfilResponse?> {
                override fun onResponse(
                    call: Call<ProfilResponse?>,
                    response: Response<ProfilResponse?>
                ) {
                    if (response.isSuccessful()) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Pendaftaran Berhasil! Silakan Login.",
                            Toast.LENGTH_LONG
                        ).show()
                        // Lempar kembali ke halaman Login
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Gagal: Email mungkin sudah digunakan.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ProfilResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Error Jaringan: " + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}