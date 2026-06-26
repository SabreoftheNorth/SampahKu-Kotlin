// halaman register, inten dari halaman login
package com.example.sampahku;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ImageView ivTogglePassword;
    private ImageView ivToggleConfirmPassword;
    private ImageView ivGoogle;
    private ImageView ivApple;
    private ImageView ivBack; // tombol back hijau utk balik ke halaman login


    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // sama seperti di halaman login
        // menyembunyikan action bar ketika sudah sampai ke halaman
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // insialisasi view-view-nya, mirip dgn halaman login
        editName = findViewById(R.id.edt_name);
        editEmail = findViewById(R.id.edt_email);
        editPassword  = findViewById(R.id.edt_password);
        editConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        ivTogglePassword = findViewById(R.id.iv_toggle_password);
        ivToggleConfirmPassword = findViewById(R.id.iv_toggle_confirm_password);
        ivGoogle = findViewById(R.id.iv_google);
        ivApple  = findViewById(R.id.iv_apple);
        ivBack = findViewById(R.id.iv_back);

        // setOnClickListener
        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        ivTogglePassword.setOnClickListener(this);
        ivToggleConfirmPassword.setOnClickListener(this);
        ivGoogle.setOnClickListener(this);
        ivApple.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_register) {
            handleRegister();
        } else if (v.getId() == R.id.iv_google) {
            Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.iv_apple) {
            Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.tv_login) {
            // inten untuk kembali ke halaman login (LoginActivity)
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (v.getId() == R.id.iv_toggle_password) {
            togglePasswordVisibility();
        } else if (v.getId() == R.id.iv_back) {
            finish(); // Kembali ke halaman sebelumnya (LoginActivity)
        } else if (v.getId() == R.id.iv_toggle_confirm_password) {
            toggleConfirmPasswordVisibility();
        }
    }

    // validasi dan proses registrasi akunnya
    private void handleRegister() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        // Validasi input kosong (Gunakan logika validasi bawaan Anda)
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Harap isi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            editConfirmPassword.setError(getString(R.string.error_password_mismatch));
            return;
        }

        Toast.makeText(this, "Sedang membuat akun...", Toast.LENGTH_SHORT).show();

        // Mengirim data ke Django (Menyediakan nilai default "-" untuk no_telepon dan alamat, serta 0 untuk total poin)
        ApiClient.getService().registerUser(name, email, password, "-", "-", 0)
                .enqueue(new Callback<ProfilResponse>() {
                    @Override
                    public void onResponse(Call<ProfilResponse> call, Response<ProfilResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Pendaftaran Berhasil! Silakan Login.", Toast.LENGTH_LONG).show();
                            // Lempar kembali ke halaman Login
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Gagal: Email mungkin sudah digunakan.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfilResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Error Jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // toggle password on/off
    // sama dgn yang ada di halaman login
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
            isPasswordVisible = false;
        } else {
            editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivTogglePassword.setImageResource(R.drawable.ic_eye_on);
            isPasswordVisible = true;
        }
        editPassword.setSelection(editPassword.getText().length());
    }

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            editConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off);
            isConfirmPasswordVisible = false;
        } else {
            editConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_on);
            isConfirmPasswordVisible = true;
        }
        editConfirmPassword.setSelection(editConfirmPassword.getText().length());
    }
}