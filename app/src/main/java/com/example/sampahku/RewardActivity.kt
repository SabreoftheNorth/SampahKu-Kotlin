package com.example.sampahku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Typeface; // apa perlu ya?
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri; //agar bisa intent implicit

public class RewardActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivBack;

    // list navbar isinya
    private LinearLayout navHome;
    private LinearLayout navReward;
    private LinearLayout navQr;
    private LinearLayout navStatistik;
    private LinearLayout navProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // inisialisasi tombol back
        // masih bermasalah sih...
        // NVM.. FIXED!
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // inisialisasi bottom navigation bar
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

        // set data untuk item-item rewardnya
        setupRewardItems();
        setActiveNav(); // untuk navbar ganti warna ketika di halaman reward
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } else if (v.getId() == R.id.nav_reward) {
            // sudah di Reward, jadi ya...
            Toast.makeText(this, "Reward", Toast.LENGTH_SHORT).show();

        } else if (v.getId() == R.id.nav_qr) {
            startActivity(new Intent(RewardActivity.this, QrActivity.class));

        } else if (v.getId() == R.id.nav_statistik) {
            startActivity(new Intent(RewardActivity.this, StatistikActivity.class));

        } else if (v.getId() == R.id.nav_profil) {
            startActivity(new Intent(RewardActivity.this, ProfilActivity.class));
        }
    }

    // ini utk melakukan set data untuk semua item reward
    // (tapi karena udh pakai <include>, kita override datanya di sini)
    private void setupRewardItems() {
        View itemLast = findViewById(R.id.item_last_reward);
        View item1 = findViewById(R.id.item_reward_1);
        View item2 = findViewById(R.id.item_reward_2);
        View item3 = findViewById(R.id.item_reward_3);

        View[] itemViews = {itemLast, item1, item2, item3};

        ApiClient.getService().getReward().enqueue(new Callback<List<RewardResponse>>() {
            @Override
            public void onResponse(Call<List<RewardResponse>> call, Response<List<RewardResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RewardResponse> list = response.body();

                    for (int i = 0; i < Math.min(list.size(), itemViews.length); i++) {
                        RewardResponse reward = list.get(i);
                        View itemView = itemViews[i];

                        ((TextView) itemView.findViewById(R.id.tv_reward_name)).setText(reward.getNamaReward());
                        ((TextView) itemView.findViewById(R.id.tv_reward_desc)).setText(reward.getDeskripsi());
                        ((TextView) itemView.findViewById(R.id.tv_reward_points)).setText(reward.getPoinDibutuhkan() + " Poin");

                        int resId = getResources().getIdentifier(reward.getLogoResourceName(), "drawable", getPackageName());
                        if (resId != 0) {
                            ((ImageView) itemView.findViewById(R.id.iv_reward_logo)).setImageResource(resId);
                        }

                        if (reward.getLogoResourceName().contains("gopay")) {
                            itemView.findViewById(R.id.btn_tukar).setOnClickListener(v -> bukaGopay());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<RewardResponse>> call, Throwable t) {}
        });
    }

    /*
     * jadi ini bentuk implicit intent untuk membuka aplikasi GoPay.
     * "aplikasi" padahal cuma websitenya saja
     * kalau terinstall ya, akan langsung kebuka app-nya.
     */
    private void bukaGopay() {
        // coba buka app GoPay langsung via deep link (kalau ada)
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("gojek://gopay"));

        // cek apa ada app yang bisa handle intent ini
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent); // maka buka app GoPay
        } else {
            // fallback: buka browser ke website GoPay
            Intent browser = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://gopay.co.id"));
            startActivity(browser);
        }
    }

    private void setActiveNav() {
        setNavColor(R.id.nav_home, R.color.gray_text, Typeface.NORMAL);
        setNavColor(R.id.nav_reward, R.color.green_primary, Typeface.BOLD);
        setNavColor(R.id.nav_statistik, R.color.gray_text, Typeface.NORMAL);
        setNavColor(R.id.nav_profil, R.color.gray_text, Typeface.NORMAL);
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