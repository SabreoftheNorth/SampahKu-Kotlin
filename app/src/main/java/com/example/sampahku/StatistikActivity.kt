package com.example.sampahku

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sampahku.ApiClient.service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatistikActivity : AppCompatActivity(), View.OnClickListener {
    private var btnTukarPoin: View? = null

    // navbarnya
    private var navHome: LinearLayout? = null
    private var navReward: LinearLayout? = null
    private var navQr: View? = null
    private var navStatistik: LinearLayout? = null
    private var navProfil: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistik)

        if (getSupportActionBar() != null) {
            getSupportActionBar()!!.hide()
        }

        // tombol back, FUNGSI BASICALLY THE SAME WITH OTHERS LAH
        // singapore mode activated fr fr
        val ivBack = findViewById<ImageView>(R.id.iv_back)
        ivBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })

        // tombol utk tukar poin, ambil dari beranda saja yukss
        btnTukarPoin = findViewById(R.id.btn_tukar_poin)
        btnTukarPoin!!.setOnClickListener(this)

        // navbar di bawah
        navHome = findViewById(R.id.nav_home)
        navReward = findViewById(R.id.nav_reward)
        navQr = findViewById(R.id.nav_qr)
        navStatistik = findViewById(R.id.nav_statistik)
        navProfil = findViewById(R.id.nav_profil)

        navHome!!.setOnClickListener(this)
        navReward!!.setOnClickListener(this)
        navQr!!.setOnClickListener(this)
        navStatistik!!.setOnClickListener(this)
        navProfil!!.setOnClickListener(this)

        // set data aktivitas
        setupAktivitasItems()

        // highlight tab Statistik
        setActiveNav()
    }

    override fun onClick(v: View) {
        if (v.getId() == R.id.nav_home) {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        } else if (v.getId() == R.id.nav_reward) {
            startActivity(Intent(this@StatistikActivity, RewardActivity::class.java))
        } else if (v.getId() == R.id.nav_qr) {
            startActivity(Intent(this@StatistikActivity, QrActivity::class.java))
        } else if (v.getId() == R.id.nav_statistik) {
            Toast.makeText(this, "Statistik", Toast.LENGTH_SHORT).show()
        } else if (v.getId() == R.id.nav_profil) {
            startActivity(Intent(this@StatistikActivity, ProfilActivity::class.java))
        } else if (v.getId() == R.id.btn_tukar_poin) {
            startActivity(Intent(this@StatistikActivity, RewardActivity::class.java))
        }
    }

    private fun setupAktivitasItems() {
        // ID di bawah ini sudah diperbaiki khusus untuk halaman Statistik
        val itemBotol = findViewById<View>(R.id.item_stat_botol)
        val itemKertas = findViewById<View>(R.id.item_stat_kertas)

        service.riwayat!!.enqueue(object : Callback<MutableList<RiwayatResponse?>?> {
            override fun onResponse(
                call: Call<MutableList<RiwayatResponse?>?>,
                response: Response<MutableList<RiwayatResponse?>?>
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    val list: List<RiwayatResponse> = response.body()!!.filterNotNull()

                    if (list.size > 0) {
                        val r1 = list.get(0)
                        (itemBotol.findViewById<View?>(R.id.tv_nama_sampah) as TextView).setText(r1.namaSampah)
                        (itemBotol.findViewById<View?>(R.id.tv_berat) as TextView).setText(r1.berat.toString() + " kg")
                        (itemBotol.findViewById<View?>(R.id.tv_poin) as TextView).setText("+" + r1.poinDidapat + " poin")
                        (itemBotol.findViewById<View?>(R.id.tv_tanggal_lokasi) as TextView).setText(
                            r1.tanggalLokasi
                        )
                    }

                    if (list.size > 1) {
                        val r2 = list.get(1)
                        (itemKertas.findViewById<View?>(R.id.tv_nama_sampah) as TextView).setText(r2.namaSampah)
                        (itemKertas.findViewById<View?>(R.id.tv_berat) as TextView).setText(r2.berat.toString() + " kg")
                        (itemKertas.findViewById<View?>(R.id.tv_poin) as TextView).setText("+" + r2.poinDidapat + " poin")
                        (itemKertas.findViewById<View?>(R.id.tv_tanggal_lokasi) as TextView).setText(
                            r2.tanggalLokasi
                        )
                    }
                }
            }

            override fun onFailure(call: Call<MutableList<RiwayatResponse?>?>, t: Throwable) {
                Toast.makeText(this@StatistikActivity, "Gagal memuat statistik", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    // basically highlight tab di navbar agar statistik jadi hijau dan yang lainnya jadi abu2
    // (DONE) APPLY ALL THIS FIX TO ALL THE ACTIVITIES AS WELL
    // welp that's done
    private fun setActiveNav() {
        setNavColor(R.id.nav_home, R.color.gray_text, Typeface.NORMAL)
        setNavColor(R.id.nav_reward, R.color.gray_text, Typeface.NORMAL)
        setNavColor(R.id.nav_statistik, R.color.green_primary, Typeface.BOLD)
        setNavColor(R.id.nav_profil, R.color.gray_text, Typeface.NORMAL)
    }

    private fun setNavColor(navId: Int, colorRes: Int, typefaceStyle: Int) {
        val tab = findViewById<View?>(navId)
        if (tab == null) return
        if (tab is LinearLayout) {
            for (i in 0 until tab.childCount) {
                val child = tab.getChildAt(i)
                if (child is ImageView) {
                    child.setColorFilter(
                        getResources().getColor(colorRes, getTheme())
                    )
                } else if (child is TextView) {
                    child.setTextColor(
                        getResources().getColor(colorRes, getTheme())
                    )
                    child.setTypeface(null, typefaceStyle)
                }
            }
        }
    }
}
