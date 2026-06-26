package com.example.sampahku;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class ProfilActivity extends AppCompatActivity implements View.OnClickListener {

    // ini adalah navbar, navbar punya home, reward, qr, statistik, profil
    private LinearLayout navHome;
    private LinearLayout navReward;
    private LinearLayout navQr;
    private LinearLayout navStatistik;
    private LinearLayout navProfil;

    private void tampilkanDialogEdit() {
        // 1. Memanggil layout XML yang baru kita buat
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profil, null);
        EditText etNama = dialogView.findViewById(R.id.et_edit_nama);
        EditText etTelepon = dialogView.findViewById(R.id.et_edit_telepon);
        EditText etAlamat = dialogView.findViewById(R.id.et_edit_alamat);

        // 2. Mengambil teks yang sedang tampil di layar untuk dijadikan teks awal (supaya user tidak mengetik dari nol)
        View itemNama = findViewById(R.id.item_nama_pengguna);
        View itemTelepon = findViewById(R.id.item_telepon);
        View itemAlamat = findViewById(R.id.item_alamat);

        etNama.setText(((TextView) itemNama.findViewById(R.id.tv_value)).getText().toString());
        etTelepon.setText(((TextView) itemTelepon.findViewById(R.id.tv_value)).getText().toString());
        etAlamat.setText(((TextView) itemAlamat.findViewById(R.id.tv_value)).getText().toString());

        // 3. Membuat Pop-up Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String namaBaru = etNama.getText().toString().trim();
                String teleponBaru = etTelepon.getText().toString().trim();
                String alamatBaru = etAlamat.getText().toString().trim();

                // Eksekusi API Update
                eksekusiUpdateProfil(namaBaru, teleponBaru, alamatBaru);
            }
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void eksekusiUpdateProfil(String nama, String telepon, String alamat) {
        // MENGAMBIL ID DARI BRANKAS
        SharedPreferences sharedPref = getSharedPreferences("SampahkuPrefs", Context.MODE_PRIVATE);
        int currentUserId = sharedPref.getInt("USER_ID", -1);

        if (currentUserId == -1) return; // Cegah eksekusi jika tidak ada user login

        Toast.makeText(this, "Menyimpan...", Toast.LENGTH_SHORT).show();

        // Memanggil API PATCH yang kita buat di ApiService
        ApiClient.getService().updateProfil(currentUserId, nama, telepon, alamat)
                .enqueue(new Callback<ProfilResponse>() {
                    @Override
                    public void onResponse(Call<ProfilResponse> call, Response<ProfilResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(ProfilActivity.this, "Berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                            // Segarkan ulang data di layar dengan memanggil method setupProfilData() Anda lagi
                            setupProfilData();

                            // Opsional: Update nama di header (tulisan Rakha Atha Muhammad)
                            TextView tvNamaHeader = findViewById(R.id.tv_nama);
                            tvNamaHeader.setText(response.body().getNama());
                        } else {
                            Toast.makeText(ProfilActivity.this, "Gagal mengupdate.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfilResponse> call, Throwable t) {
                        Toast.makeText(ProfilActivity.this, "Error Jaringan", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // tombol backnya BISA YESS!!!
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // tombol edit profil
        ImageView ivEdit = findViewById(R.id.iv_edit);
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Memanggil fungsi pop-up edit profil
                tampilkanDialogEdit();
            }
        });

        // findviewbyid tapi ini depresi :(
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

        // setup data-data profil dan menu
        setupProfilData();
        setupMenuItems();
        setActiveNav();
    }

    // pokoknya patokannya ada di MainActivity untuk intent intent ini
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } else if (v.getId() == R.id.nav_reward) {
            Intent intent = new Intent(ProfilActivity.this, RewardActivity.class);
            startActivity(intent);

        } else if (v.getId() == R.id.nav_qr) {
            Intent intent = new Intent(ProfilActivity.this, QrActivity.class);
            startActivity(intent);

        } else if (v.getId() == R.id.nav_statistik) {
            Intent intent = new Intent(ProfilActivity.this, StatistikActivity.class);
            startActivity(intent);

        } else if (v.getId() == R.id.nav_profil) {
            Toast.makeText(this, "Profil", Toast.LENGTH_SHORT).show();
        }
    }

    // set data-data informasi profil
    private void setupProfilData() {
        View itemNama = findViewById(R.id.item_nama_pengguna);
        View itemEmail = findViewById(R.id.item_email);
        View itemTelepon = findViewById(R.id.item_telepon);
        View itemAlamat = findViewById(R.id.item_alamat);

        ((TextView) itemNama.findViewById(R.id.tv_label)).setText("Nama Pengguna :");
        ((TextView) itemEmail.findViewById(R.id.tv_label)).setText("Email :");
        ((TextView) itemTelepon.findViewById(R.id.tv_label)).setText("No. Telepon :");
        ((TextView) itemAlamat.findViewById(R.id.tv_label)).setText("Alamat :");

        // Tampilkan tulisan "Loading..." sementara data diambil
        ((TextView) itemNama.findViewById(R.id.tv_value)).setText("Loading...");
        ((TextView) itemEmail.findViewById(R.id.tv_value)).setText("Loading...");

        SharedPreferences sharedPref = getSharedPreferences("SampahkuPrefs", Context.MODE_PRIVATE);
        int currentUserId = sharedPref.getInt("USER_ID", -1); // -1 adalah nilai default jika gagal/kosong

        if (currentUserId == -1) {
            Toast.makeText(this, "Sesi login tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tarik data profil dari API Django
        ApiClient.getService().getProfil(currentUserId).enqueue(new Callback<ProfilResponse>() {
            @Override
            public void onResponse(Call<ProfilResponse> call, Response<ProfilResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfilResponse profil = response.body();
                    ((TextView) itemNama.findViewById(R.id.tv_value)).setText(profil.getNama());
                    ((TextView) itemEmail.findViewById(R.id.tv_value)).setText(profil.getEmail());
                    ((TextView) itemTelepon.findViewById(R.id.tv_value)).setText(profil.getNoTelepon());
                    ((TextView) itemAlamat.findViewById(R.id.tv_value)).setText(profil.getAlamat());
                }
            }

            @Override
            public void onFailure(Call<ProfilResponse> call, Throwable t) {
                Toast.makeText(ProfilActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void konfirmasiHapusAkun() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Akun Permanen")
                .setMessage("Apakah Anda yakin ingin menghapus akun ini? Semua poin dan data riwayat Anda akan musnah dan tidak bisa dikembalikan.")
                .setPositiveButton("Ya, Hapus!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eksekusiHapusAkun();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void eksekusiHapusAkun() {
        // Ambil ID dari brankas memori
        SharedPreferences sharedPref = getSharedPreferences("SampahkuPrefs", Context.MODE_PRIVATE);
        int currentUserId = sharedPref.getInt("USER_ID", -1);

        if (currentUserId == -1) return; // Cegah jika ID tidak ada

        Toast.makeText(this, "Menghapus data ke server...", Toast.LENGTH_SHORT).show();

        // Tembak API DELETE ke Django
        ApiClient.getService().hapusProfil(currentUserId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Di Retrofit, response 204 (No Content) dihitung sebagai isSuccessful()
                if (response.isSuccessful()) {
                    Toast.makeText(ProfilActivity.this, "Akun berhasil dihapus selamanya.", Toast.LENGTH_LONG).show();

                    // 1. Bersihkan memori login dari HP
                    sharedPref.edit().clear().apply();

                    // 2. Tendang user kembali ke halaman Login
                    Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProfilActivity.this, "Gagal menghapus akun.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfilActivity.this, "Error Jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ini untuk setiap ikon dan label yg ada di menu halaman
    private void setupMenuItems() {
        setMenuItem(R.id.menu_pin,      R.drawable.ic_pin,    "Atur Pin Penukaran Reward");
        setMenuItem(R.id.menu_password, R.drawable.ic_lock,   "Ubah Password");
        setMenuItem(R.id.menu_privasi,  R.drawable.ic_shield, "Hapus Akun Permanen");
        setMenuItem(R.id.menu_bahasa,   R.drawable.ic_globe,  "Bahasa");
        setMenuItem(R.id.menu_bantuan,  R.drawable.ic_help,   "Bantuan");
        setMenuItem(R.id.menu_keluar,   R.drawable.ic_logout, "Keluar");

        // khusus utk tombol Keluar, bisa kembali ke LoginActivity dan clear back stack
        // WAAAAAAAAAAH HEBAT
        RelativeLayout menuKeluar = findViewById(R.id.menu_keluar);
        menuKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("SampahkuPrefs", Context.MODE_PRIVATE);
                sharedPref.edit().clear().apply();

                Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        RelativeLayout menuHapus = findViewById(R.id.menu_privasi);
        menuHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Jangan langsung hapus! Tampilkan konfirmasi peringatan dulu
                konfirmasiHapusAkun();
            }
        });

        // menu lain yg belum diimplementasi: "coming sooooonnn..."
        int[] menuIds = {
                R.id.menu_pin, R.id.menu_password,
                R.id.menu_privasi, R.id.menu_bahasa, R.id.menu_bantuan
        };
        for (int id : menuIds) {
            RelativeLayout menu = findViewById(id);
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProfilActivity.this,
                            getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // helper utk set icon dan label setiap item menu
    private void setMenuItem(int menuId, int iconRes, String label) {
        View menu = findViewById(menuId);
        ((ImageView) menu.findViewById(R.id.iv_menu_icon)).setImageResource(iconRes);
        ((TextView) menu.findViewById(R.id.tv_menu_label)).setText(label);
    }

    // lebih buat navbar, highlight bagian profil dan yg lain abu2
    private void setActiveNav() {
        setNavColor(R.id.nav_home, R.color.gray_text, Typeface.NORMAL);
        setNavColor(R.id.nav_reward, R.color.gray_text, Typeface.NORMAL);
        setNavColor(R.id.nav_statistik, R.color.gray_text, Typeface.NORMAL);
        setNavColor(R.id.nav_profil, R.color.green_primary, Typeface.BOLD);
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