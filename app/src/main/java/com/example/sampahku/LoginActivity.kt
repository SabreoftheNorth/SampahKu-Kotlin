// Hallo, jeg heter herr x og jeg kommer fra Indonesia
package com.example.sampahku

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
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

// untuk bagian implicit intentnya
class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private var editEmail: EditText? = null
    private var editPassword: EditText? = null
    private var btnLogin: Button? = null
    private var tvFgtPassword: TextView? = null
    private var tvRegister: TextView? = null
    private var ivGoogle: ImageView? = null
    private var ivApple: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // hide action bar ketika sudah masuk aplikasi
        // agar rapi
        if (getSupportActionBar() != null) {
            getSupportActionBar()!!.hide()
        }

        // inisialisasi view-view yang ada
        editEmail = findViewById<EditText>(R.id.edt_email)
        editPassword = findViewById<EditText>(R.id.edt_password)
        btnLogin = findViewById<Button>(R.id.btn_login)
        tvFgtPassword = findViewById<TextView>(R.id.tv_forgot_password)
        tvRegister = findViewById<TextView>(R.id.tv_register)
        ivGoogle = findViewById<ImageView>(R.id.iv_google)
        ivApple = findViewById<ImageView>(R.id.iv_apple)

        // set listener, menggunakan 'setOnClickListener'
        btnLogin!!.setOnClickListener(this)
        tvFgtPassword!!.setOnClickListener(this)
        tvRegister!!.setOnClickListener(this)
        ivGoogle!!.setOnClickListener(this)
        ivApple!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.getId() == R.id.btn_login) {
            handleLogin()
        } else if (v.getId() == R.id.iv_google) {
            // IMPLICIT INTENT UNTUK GOOGLE DAN APPLE ID BABAYYY
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://accounts.google.com/signin")
            )
            startActivity(intent)
        } else if (v.getId() == R.id.iv_apple) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://appleid.apple.com/sign-in")
            )
            startActivity(intent)
        } else if (v.getId() == R.id.tv_forgot_password) {
            Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
        } else if (v.getId() == R.id.tv_register) {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // untuk menangani proses login
    // validasi input juga
    private fun handleLogin() {
        val email = editEmail!!.getText().toString().trim { it <= ' ' }
        val password = editPassword!!.getText().toString().trim { it <= ' ' }

        var isEmptyFields = false
        if (TextUtils.isEmpty(email)) {
            isEmptyFields = true
            editEmail!!.setError(getString(R.string.error_email_empty))
        }
        if (TextUtils.isEmpty(password)) {
            isEmptyFields = true
            editPassword!!.setError(getString(R.string.error_password_empty))
        }

        if (!isEmptyFields) {
            Toast.makeText(this, "Mencoba masuk...", Toast.LENGTH_SHORT).show()

            // Mengirim request ke Django
            service.loginUser(email, password)!!.enqueue(object : Callback<LoginResponse?> {
                override fun onResponse(
                    call: Call<LoginResponse?>,
                    response: Response<LoginResponse?>
                ) {
                    // Mengecek apakah server membalas
                    if (response.isSuccessful() && response.body() != null) {
                        // Mengecek apakah status JSON dari Django adalah "success"
                        if ("success" == response.body()!!.status) {
                            Toast.makeText(
                                this@LoginActivity, "Selamat datang, " + response.body()!!
                                    .nama, Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                response.body()!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Email atau password salah!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if ("success" == response.body()!!.status) {
                        val sharedPref = getSharedPreferences("SampahkuPrefs", MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putInt("USER_ID", response.body()!!.userId)
                        editor.apply()
                        // ---------------------------------------------
                        Toast.makeText(
                            this@LoginActivity,
                            "Selamat datang, " + response.body()!!.nama,
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                    // Akan masuk ke sini jika server mati atau internet tidak ada
                    Toast.makeText(
                        this@LoginActivity,
                        "Koneksi ke server gagal: " + t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }

    companion object {
        private const val VALID_EMAIL = "rakha@user.com"
        private const val VALID_PASSWORD = "user123"
    }
}