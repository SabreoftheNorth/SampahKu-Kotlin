package com.example.sampahku;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class QrActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivBack;
    private LinearLayout btnResend;
    private TextView tvCountdownLabel;

    //konsep timer countdown untuk kode qr-nya:
    // parameter pertama adalah 90000: total durasi dalam milidetik
    /*
    * 90000 ms = 1 menit 30 detik (90 detik)
    * lalu ada parameter kedua 1000.
    * 1000 adalah interval update dalam milidetik juga.
    * Jadi setiap 1 detik sekali, onTick() itu dipanggil
    * Setiap detik, fungsi itu akan mengupdate teks countdownnya
    * di mana onFinish() akan dipanggil ketika waktu sdh habis
    *
    * */
    private CountDownTimer countDownTimer;
    private static final long DURATION_MS = 90000; // 90 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // inisialisasi view-nya
        ivBack = findViewById(R.id.iv_back);
        btnResend = findViewById(R.id.btn_resend);
        tvCountdownLabel = findViewById(R.id.tv_countdown_label);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnResend.setOnClickListener(this);

        // dipakai untuk mengaktifkan countdown
        startCountdown();
    }

    @Override
    public void onClick(View v) {
       // if (v.getId() == R.id.iv_back) {
            //finish();

        //}
            if (v.getId() == R.id.btn_resend) {
            btnResend.setAlpha(0.4f);

            // ini dipakai untuk reset countdown ketika tombol resend ditekan
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            startCountdown();
        }
    }

    // memulai fungsi perhitungan
    private void startCountdown() {
        btnResend.setEnabled(false);
        btnResend.setAlpha(0.4f);

        countDownTimer = new CountDownTimer(DURATION_MS, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                long totalDetik = millisUntilFinished / 1000;
                long menit = totalDetik / 60;
                long detik = totalDetik % 60;

                String waktu = String.format("%02d:%02d", menit, detik);
                tvCountdownLabel.setText("Kode Akan Kadarluasa Dalam " + waktu);
            }

            @Override
            public void onFinish() {
                tvCountdownLabel.setText("Kode Telah Kadaluarsa");
                btnResend.setEnabled(true);
                btnResend.setAlpha(1.0f);
                btnResend.setBackgroundResource(R.drawable.bg_button_green);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}