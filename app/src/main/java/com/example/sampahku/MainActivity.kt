package com.example.sampahku;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent; // untuk import fungsi Intent
import android.graphics.Typeface;
import android.widget.ImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

import android.net.Uri; // untuk intent yang implicit

// saya justru masih ragu ini library kebanyakan dipake atau gk ya?
// (kinda done?) FIGURE SOMETHING OUT OR SMTH IDK
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //navbar yang dibuat menurut desain figma
    // memakai linearlayout
    private LinearLayout navHome;
    private LinearLayout navReward;
    private LinearLayout navQr;
    private LinearLayout navStatistik;
    private LinearLayout navProfil;

    //linear layout lagi namun untuk tombol "TUkar poin"
    private LinearLayout tblTukarPoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //initialisasi item-item yang ada di navbar di bawah layar nanti
        // ditambah tombol tukar poinnya
        navHome = findViewById(R.id.nav_home);
        navReward = findViewById(R.id.nav_reward);
        navQr = findViewById(R.id.nav_qr);
        navStatistik = findViewById(R.id.nav_statistik);
        navProfil = findViewById(R.id.nav_profil);
        ImageView ivProfile = findViewById(R.id.iv_profile);

        tblTukarPoin = findViewById(R.id.btn_tukar_poin);

        //setOnClickListener
        navHome.setOnClickListener(this);
        navReward.setOnClickListener(this);
        navQr.setOnClickListener(this);
        navStatistik.setOnClickListener(this);
        navProfil.setOnClickListener(this);
        tblTukarPoin.setOnClickListener(this);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfilActivity.class));
            }
        });

        //set data-data utk item aktivitas
        // karena memakai <include> maka saya perlu set
        // secara manual
        setupAktivitasItems();
        setupEdukasiItems();
        setActiveNav();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nav_home) {
            // Ini hanya memastikan apakah tombol home bekerja
            // aslinya sdh di home
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            // menggunakan inten untuk menuju ke halaman Reward
        } else if (v.getId() == R.id.nav_reward) {
            startActivity(new Intent(MainActivity.this, RewardActivity.class));

            //menggunakan inten untuk menuju ke halaman scan QR
        } else if (v.getId() == R.id.nav_qr) {
            startActivity(new Intent(MainActivity.this, QrActivity.class));
        // mari pakai inten untuk menuju ke halaman statistik!!!!!!!!
        } else if (v.getId() == R.id.nav_statistik) {
            startActivity(new Intent(MainActivity.this, StatistikActivity.class));

            // inten ke halaman profil
        } else if (v.getId() == R.id.nav_profil) {
            startActivity(new Intent(MainActivity.this, ProfilActivity.class));

        } else if (v.getId() == R.id.btn_tukar_poin) {
            startActivity(new Intent(MainActivity.this, RewardActivity.class));
        }
    }

    //ini utk set data untuk 3 aktivitas yang baru
    // karena memakai <include> seperti sebelumnya, maka perlu
    // override datanya di sini:
    private void setupAktivitasItems() {
        View itemBotol = findViewById(R.id.item_botol); // (di Statistik sesuaikan ID nya)
        View itemKertas = findViewById(R.id.item_kertas); // (di Statistik sesuaikan ID nya)

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
            public void onFailure(Call<List<RiwayatResponse>> call, Throwable t) {}
        });
    }

    //set data untuk 3 item edukasinya
    private void setupEdukasiItems() {
        View item1 = findViewById(R.id.item_edukasi_1);
        View item2 = findViewById(R.id.item_edukasi_2);

        // Teks sementara selagi menunggu balasan dari Django
        ((TextView) item1.findViewById(R.id.tv_judul_edukasi)).setText("Memuat video...");
        ((TextView) item2.findViewById(R.id.tv_judul_edukasi)).setText("Memuat video...");

        // Tembak API Edukasi
        ApiClient.getService().getEdukasi().enqueue(new Callback<List<EdukasiResponse>>() {
            @Override
            public void onResponse(Call<List<EdukasiResponse>> call, Response<List<EdukasiResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EdukasiResponse> list = response.body();

                    // Mengisi Item Edukasi Pertama (jika data di database tersedia)
                    if (list.size() > 0) {
                        EdukasiResponse edukasi1 = list.get(0);
                        ((TextView) item1.findViewById(R.id.tv_judul_edukasi)).setText(edukasi1.getJudul());
                        ((TextView) item1.findViewById(R.id.tv_desc_edukasi)).setText(edukasi1.getDeskripsi());

                        // Menempelkan ID YouTube ke tombol agar bisa ditonton
                        item1.findViewById(R.id.btn_tonton_video).setOnClickListener(v -> bukaYoutube(edukasi1.getVideoIdYoutube()));
                    }

                    // Mengisi Item Edukasi Kedua (jika data di database tersedia)
                    if (list.size() > 1) {
                        EdukasiResponse edukasi2 = list.get(1);
                        ((TextView) item2.findViewById(R.id.tv_judul_edukasi)).setText(edukasi2.getJudul());
                        ((TextView) item2.findViewById(R.id.tv_desc_edukasi)).setText(edukasi2.getDeskripsi());

                        // Menempelkan ID YouTube ke tombol agar bisa ditonton
                        item2.findViewById(R.id.btn_tonton_video).setOnClickListener(v -> bukaYoutube(edukasi2.getVideoIdYoutube()));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<EdukasiResponse>> call, Throwable t) {
                // Bisa menampilkan Toast atau log jika gagal memuat data
            }
        });
    }

    // semoga bisa mengatasi masalah navbar punya warna berbeda setiap halaman
    private void setActiveNav() {
        setNavColor(R.id.nav_home,      R.color.green_primary, Typeface.BOLD);
        setNavColor(R.id.nav_reward,    R.color.gray_text,     Typeface.NORMAL);
        setNavColor(R.id.nav_statistik, R.color.gray_text,     Typeface.NORMAL);
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

    //INI UNTUK FITUR NONTON VIDEO YOUTUBE TUTORIAL
    //WEEE PAKAI IMPLICIT INTENT LAGIII
    // seperti kaya yang gojek itu, kalau blm ada appnya maka akan buka browser
    private void bukaYoutube(String videoId) {
        // memcoba buka di app YouTube
        Intent appIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("vnd.youtube:" + videoId));

        if (appIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(appIntent);
        } else {
            // fallback ke browser kalau tidak ada appnya
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://youtube.com/watch?v=" + videoId));
            startActivity(webIntent);
        }
    }
}