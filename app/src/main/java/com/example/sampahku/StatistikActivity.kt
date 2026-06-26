package com.example.sampahku;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class StatistikActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout btnTukarPoin;

    // navbarnya
    private LinearLayout navHome;
    private LinearLayout navReward;
    private LinearLayout navQr;
    private LinearLayout navStatistik;
    private LinearLayout navProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistik);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // tombol back, FUNGSI BASICALLY THE SAME WITH OTHERS LAH
        // singapore mode activated fr fr
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // tombol utk tukar poin, ambil dari beranda saja yukss
        btnTukarPoin = findViewById(R.id.btn_tukar_poin);
        btnTukarPoin.setOnClickListener(this);

        // navbar di bawah
        navHome = findViewById(R.id.nav_home);
        navReward = findViewById(R.id.nav_reward);
        navQr = findViewById(R.id.nav_qr);
        navStatistik = findViewById(R.id.nav_statistik);
        navProfil = findViewById(R.id.nav_profil);

        navHome.setOnClickListener(this);
        navReward.setOnClickListener(this);
        navQr.setOnClickListener(this);
        navStatistik.setOnClickListener(this);
        navProfil.setOnClickListener(this);

        // set data aktivitas
        setupAktivitasItems();

        // highlight tab Statistik
        setActiveNav();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } else if (v.getId() == R.id.nav_reward) {
            startActivity(new Intent(StatistikActivity.this, RewardActivity.class));

        } else if (v.getId() == R.id.nav_qr) {
            startActivity(new Intent(StatistikActivity.this, QrActivity.class));

        } else if (v.getId() == R.id.nav_statistik) {
            Toast.makeText(this, "Statistik", Toast.LENGTH_SHORT).show();

        } else if (v.getId() == R.id.nav_profil) {
            startActivity(new Intent(StatistikActivity.this, ProfilActivity.class));

        } else if (v.getId() == R.id.btn_tukar_poin) {
            startActivity(new Intent(StatistikActivity.this, RewardActivity.class));
        }
    }

    private void setupAktivitasItems() {
        // ID di bawah ini sudah diperbaiki khusus untuk halaman Statistik
        View itemBotol = findViewById(R.id.item_stat_botol);
        View itemKertas = findViewById(R.id.item_stat_kertas);

        ApiClient.getService().getRiwayat().enqueue(new Callback<List<RiwayatResponse>>() {
            @Override
            public void onResponse(Call<List<RiwayatResponse>> call, Response<List<RiwayatResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RiwayatResponse> list = response.body();

                    if (list.size() > 0) {
                        RiwayatResponse r1 = list.get(0);
                        ((TextView) itemBotol.findViewById(R.id.tv_nama_sampah)).setText(r1.getNamaSampah());
                        ((TextView) itemBotol.findViewById(R.id.tv_berat)).setText(r1.getBerat() + " kg");
                        ((TextView) itemBotol.findViewById(R.id.tv_poin)).setText("+" + r1.getPoinDidapat() + " poin");
                        ((TextView) itemBotol.findViewById(R.id.tv_tanggal_lokasi)).setText(r1.getTanggalLokasi());
                    }

                    if (list.size() > 1) {
                        RiwayatResponse r2 = list.get(1);
                        ((TextView) itemKertas.findViewById(R.id.tv_nama_sampah)).setText(r2.getNamaSampah());
                        ((TextView) itemKertas.findViewById(R.id.tv_berat)).setText(r2.getBerat() + " kg");
                        ((TextView) itemKertas.findViewById(R.id.tv_poin)).setText("+" + r2.getPoinDidapat() + " poin");
                        ((TextView) itemKertas.findViewById(R.id.tv_tanggal_lokasi)).setText(r2.getTanggalLokasi());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<RiwayatResponse>> call, Throwable t) {
                Toast.makeText(StatistikActivity.this, "Gagal memuat statistik", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // basically highlight tab di navbar agar statistik jadi hijau dan yang lainnya jadi abu2
    // (DONE) APPLY ALL THIS FIX TO ALL THE ACTIVITIES AS WELL
    // welp that's done
    private void setActiveNav() {
        setNavColor(R.id.nav_home,      R.color.gray_text,     Typeface.NORMAL);
        setNavColor(R.id.nav_reward,    R.color.gray_text,     Typeface.NORMAL);
        setNavColor(R.id.nav_statistik, R.color.green_primary, Typeface.BOLD);
        setNavColor(R.id.nav_profil,    R.color.gray_text,     Typeface.NORMAL);
    }

    private void setNavColor(int navId, int colorRes, int typefaceStyle) {
        LinearLayout tab = findViewById(navId);
        if (tab == null) return;
        for (int i = 0; i < tab.getChildCount(); i++) {
            View child = tab.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setColorFilter(
                        getResources().getColor(colorRes, getTheme()));
            } else if (child instanceof TextView) {
                ((TextView) child).setTextColor(
                        getResources().getColor(colorRes, getTheme()));
                ((TextView) child).setTypeface(null, typefaceStyle);
            }
        }
    }
    }