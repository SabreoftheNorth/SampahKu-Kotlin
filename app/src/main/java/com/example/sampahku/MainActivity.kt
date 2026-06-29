package com.example.sampahku

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sampahku.ApiClient.service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var navHome: LinearLayout? = null
    private var navReward: LinearLayout? = null
    private var navQr: View? = null
    private var navStatistik: LinearLayout? = null
    private var navProfil: LinearLayout? = null
    private var tblTukarPoin: LinearLayout? = null

    // Broadcast Receiver
    private val systemReceiver = SystemReceiver()

    // Education RecyclerView components
    private var rvEdukasi: RecyclerView? = null
    private var edukasiAdapter: EdukasiAdapter? = null
    private var ivEdukasiToggle: ImageView? = null
    private var edukasiList: MutableList<EdukasiResponse> = mutableListOf()

    // Preferences keys
    private val PREFS_NAME = "HomePrefs"
    private val KEY_EDUKASI_VIEW_TYPE = "edukasi_view_type"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi Channel Notifikasi
        NotificationHelper.createNotificationChannel(this)
        requestNotificationPermission()

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        navHome = findViewById(R.id.nav_home)
        navReward = findViewById(R.id.nav_reward)
        navQr = findViewById(R.id.nav_qr)
        navStatistik = findViewById(R.id.nav_statistik)
        navProfil = findViewById(R.id.nav_profil)
        val ivProfile = findViewById<ImageView>(R.id.iv_profile)
        val ivNotif = findViewById<ImageView>(R.id.iv_notif)
        tblTukarPoin = findViewById(R.id.btn_tukar_poin)

        // Education toggle and RV
        ivEdukasiToggle = findViewById(R.id.iv_edukasi_toggle)
        rvEdukasi = findViewById(R.id.rv_edukasi)

        navHome!!.setOnClickListener(this)
        navReward!!.setOnClickListener(this)
        navQr!!.setOnClickListener(this)
        navStatistik!!.setOnClickListener(this)
        navProfil!!.setOnClickListener(this)
        tblTukarPoin!!.setOnClickListener(this)
        ivEdukasiToggle!!.setOnClickListener { toggleEdukasiViewMode() }

        ivProfile.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProfilActivity::class.java))
        }

        // Fitur Tambahan: Klik ikon lonceng untuk test notifikasi (IDE 1)
        ivNotif.setOnClickListener {
            NotificationHelper.sendNotification(
                this,
                "Halo Rakha!",
                "Jangan lupa setor sampahmu hari ini ya!"
            )
        }

        // Load saved view type for education
        val savedViewType = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_EDUKASI_VIEW_TYPE, EdukasiAdapter.VIEW_TYPE_LIST)

        updateEdukasiToggleIcon(savedViewType)
        setupEdukasiRecyclerView(savedViewType)

        setupAktivitasItems()
        fetchEdukasiItems()
        setActiveNav()
    }

    /**
     * Mendaftarkan Broadcast Receiver secara dinamis saat Activity aktif
     */
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            @Suppress("DEPRECATION")
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        }
        registerReceiver(systemReceiver, filter)
    }

    /**
     * Melepaskan Broadcast Receiver saat Activity tidak terlihat untuk menghemat baterai
     */
    override fun onStop() {
        super.onStop()
        unregisterReceiver(systemReceiver)
    }

    /**
     * Meminta izin notifikasi untuk Android 13+
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
    }

    private fun setupEdukasiRecyclerView(viewType: Int) {
        val spanCount = if (viewType == EdukasiAdapter.VIEW_TYPE_GRID) 2 else 1
        rvEdukasi!!.layoutManager = GridLayoutManager(this, spanCount)
        
        edukasiAdapter = EdukasiAdapter(edukasiList, viewType) { videoId ->
            bukaYoutube(videoId)
        }
        rvEdukasi!!.adapter = edukasiAdapter
    }

    private fun toggleEdukasiViewMode() {
        val currentViewType = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_EDUKASI_VIEW_TYPE, EdukasiAdapter.VIEW_TYPE_LIST)

        val nextViewType = when (currentViewType) {
            EdukasiAdapter.VIEW_TYPE_LIST -> EdukasiAdapter.VIEW_TYPE_GRID
            EdukasiAdapter.VIEW_TYPE_GRID -> EdukasiAdapter.VIEW_TYPE_CARD
            EdukasiAdapter.VIEW_TYPE_CARD -> EdukasiAdapter.VIEW_TYPE_LIST
            else -> EdukasiAdapter.VIEW_TYPE_LIST
        }

        // Save new view type
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_EDUKASI_VIEW_TYPE, nextViewType)
            .apply()

        updateEdukasiToggleIcon(nextViewType)

        // Update layout manager span count
        val spanCount = if (nextViewType == EdukasiAdapter.VIEW_TYPE_GRID) 2 else 1
        (rvEdukasi!!.layoutManager as GridLayoutManager).spanCount = spanCount
        
        // Update adapter view type
        edukasiAdapter?.updateViewType(nextViewType)
    }

    private fun updateEdukasiToggleIcon(viewType: Int) {
        val iconRes = when (viewType) {
            EdukasiAdapter.VIEW_TYPE_LIST -> R.drawable.ic_view_list
            EdukasiAdapter.VIEW_TYPE_GRID -> R.drawable.ic_view_grid
            EdukasiAdapter.VIEW_TYPE_CARD -> R.drawable.ic_view_card
            else -> R.drawable.ic_view_list
        }
        ivEdukasiToggle!!.setImageResource(iconRes)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.nav_home -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
            R.id.nav_reward -> startActivity(Intent(this, RewardActivity::class.java))
            R.id.nav_qr -> startActivity(Intent(this, QrActivity::class.java))
            R.id.nav_statistik -> startActivity(Intent(this, StatistikActivity::class.java))
            R.id.nav_profil -> startActivity(Intent(this, ProfilActivity::class.java))
            R.id.btn_tukar_poin -> startActivity(Intent(this, RewardActivity::class.java))
        }
    }

    private fun setupAktivitasItems() {
        val itemBotol = findViewById<View>(R.id.item_botol)
        val itemKertas = findViewById<View>(R.id.item_kertas)

        service.riwayat!!.enqueue(object : Callback<MutableList<RiwayatResponse?>?> {
            override fun onResponse(
                call: Call<MutableList<RiwayatResponse?>?>,
                response: Response<MutableList<RiwayatResponse?>?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!.filterNotNull()

                    if (list.isNotEmpty()) {
                        val r1 = list[0]
                        (itemBotol.findViewById<View>(R.id.tv_nama_sampah) as TextView).text = r1.namaSampah
                        (itemBotol.findViewById<View>(R.id.tv_berat) as TextView).text = "${r1.berat} kg"
                        (itemBotol.findViewById<View>(R.id.tv_poin) as TextView).text = "+${r1.poinDidapat} poin"
                        (itemBotol.findViewById<View>(R.id.tv_tanggal_lokasi) as TextView).text = r1.tanggalLokasi
                    }

                    if (list.size > 1) {
                        val r2 = list[1]
                        (itemKertas.findViewById<View>(R.id.tv_nama_sampah) as TextView).text = r2.namaSampah
                        (itemKertas.findViewById<View>(R.id.tv_berat) as TextView).text = "${r2.berat} kg"
                        (itemKertas.findViewById<View>(R.id.tv_poin) as TextView).text = "+${r2.poinDidapat} poin"
                        (itemKertas.findViewById<View>(R.id.tv_tanggal_lokasi) as TextView).text = r2.tanggalLokasi
                    }
                }
            }

            override fun onFailure(call: Call<MutableList<RiwayatResponse?>?>, t: Throwable) {}
        })
    }

    private fun fetchEdukasiItems() {
        service.edukasi!!.enqueue(object : Callback<MutableList<EdukasiResponse?>?> {
            override fun onResponse(
                call: Call<MutableList<EdukasiResponse?>?>,
                response: Response<MutableList<EdukasiResponse?>?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!.filterNotNull()
                    edukasiList.clear()
                    edukasiList.addAll(list)
                    edukasiAdapter?.updateData(edukasiList)
                }
            }

            override fun onFailure(call: Call<MutableList<EdukasiResponse?>?>, t: Throwable) {}
        })
    }

    private fun setActiveNav() {
        setNavColor(R.id.nav_home, R.color.green_primary, Typeface.BOLD)
        setNavColor(R.id.nav_reward, R.color.gray_text, Typeface.NORMAL)
        setNavColor(R.id.nav_statistik, R.color.gray_text, Typeface.NORMAL)
        setNavColor(R.id.nav_profil, R.color.gray_text, Typeface.NORMAL)
    }

    private fun setNavColor(navId: Int, colorRes: Int, typefaceStyle: Int) {
        val tab = findViewById<View?>(navId) ?: return
        if (tab is LinearLayout) {
            for (i in 0 until tab.childCount) {
                val child = tab.getChildAt(i)
                if (child is ImageView) {
                    child.setColorFilter(getColor(colorRes))
                } else if (child is TextView) {
                    child.setTextColor(getColor(colorRes))
                    child.setTypeface(null, typefaceStyle)
                }
            }
        }
    }

    private fun bukaYoutube(videoId: String?) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
        if (appIntent.resolveActivity(packageManager) != null) {
            startActivity(appIntent)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/watch?v=$videoId")))
        }
    }
}