package com.example.sampahku

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sampahku.ApiClient.service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RewardActivity : AppCompatActivity(), View.OnClickListener {
    private var ivBack: ImageView? = null
    private var ivViewToggle: ImageView? = null
    private var rvRewards: RecyclerView? = null
    private var adapter: RewardAdapter? = null
    private var rewardList: MutableList<RewardResponse> = mutableListOf()

    // Preferences key
    private val PREFS_NAME = "RewardPrefs"
    private val KEY_VIEW_TYPE = "view_type"

    // list navbar isinya
    private var navHome: LinearLayout? = null
    private var navReward: LinearLayout? = null
    private var navQr: View? = null
    private var navStatistik: LinearLayout? = null
    private var navProfil: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward)

        if (getSupportActionBar() != null) {
            getSupportActionBar()!!.hide()
        }

        ivBack = findViewById(R.id.iv_back)
        ivBack!!.setOnClickListener { finish() }

        ivViewToggle = findViewById(R.id.iv_view_toggle)
        ivViewToggle!!.setOnClickListener { toggleViewMode() }

        rvRewards = findViewById(R.id.rv_rewards)

        // Load saved view type
        val savedViewType = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_VIEW_TYPE, RewardAdapter.VIEW_TYPE_LIST)

        updateToggleIcon(savedViewType)

        // Setup RecyclerView with saved view type
        setupRecyclerView(savedViewType)

        // inisialisasi bottom navigation bar
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

        fetchRewards()
        setActiveNav()
    }

    private fun setupRecyclerView(viewType: Int) {
        val spanCount = if (viewType == RewardAdapter.VIEW_TYPE_GRID) 2 else 1
        rvRewards!!.layoutManager = GridLayoutManager(this, spanCount)
        
        adapter = RewardAdapter(rewardList, viewType) { reward ->
            if (reward.logoResourceName?.contains("gopay") == true) {
                bukaGopay()
            } else {
                Toast.makeText(this, "Menukarkan ${reward.namaReward}", Toast.LENGTH_SHORT).show()
            }
        }
        rvRewards!!.adapter = adapter
    }

    private fun toggleViewMode() {
        val currentViewType = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_VIEW_TYPE, RewardAdapter.VIEW_TYPE_LIST)

        val nextViewType = when (currentViewType) {
            RewardAdapter.VIEW_TYPE_LIST -> RewardAdapter.VIEW_TYPE_GRID
            RewardAdapter.VIEW_TYPE_GRID -> RewardAdapter.VIEW_TYPE_CARD
            RewardAdapter.VIEW_TYPE_CARD -> RewardAdapter.VIEW_TYPE_LIST
            else -> RewardAdapter.VIEW_TYPE_LIST
        }

        // Save new view type
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_VIEW_TYPE, nextViewType)
            .apply()

        updateToggleIcon(nextViewType)

        // Update layout manager span count
        val spanCount = if (nextViewType == RewardAdapter.VIEW_TYPE_GRID) 2 else 1
        (rvRewards!!.layoutManager as GridLayoutManager).spanCount = spanCount
        
        // Update adapter view type
        adapter?.updateViewType(nextViewType)
    }

    private fun updateToggleIcon(viewType: Int) {
        val iconRes = when (viewType) {
            RewardAdapter.VIEW_TYPE_LIST -> R.drawable.ic_view_list
            RewardAdapter.VIEW_TYPE_GRID -> R.drawable.ic_view_grid
            RewardAdapter.VIEW_TYPE_CARD -> R.drawable.ic_view_card
            else -> R.drawable.ic_view_list
        }
        ivViewToggle!!.setImageResource(iconRes)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            R.id.nav_reward -> Toast.makeText(this, "Reward", Toast.LENGTH_SHORT).show()
            R.id.nav_qr -> startActivity(Intent(this, QrActivity::class.java))
            R.id.nav_statistik -> startActivity(Intent(this, StatistikActivity::class.java))
            R.id.nav_profil -> startActivity(Intent(this, ProfilActivity::class.java))
        }
    }

    private fun fetchRewards() {
        service.reward!!.enqueue(object : Callback<MutableList<RewardResponse?>?> {
            override fun onResponse(
                call: Call<MutableList<RewardResponse?>?>,
                response: Response<MutableList<RewardResponse?>?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!.filterNotNull()
                    rewardList.clear()
                    rewardList.addAll(list)
                    adapter?.updateData(rewardList)
                }
            }

            override fun onFailure(call: Call<MutableList<RewardResponse?>?>, t: Throwable) {
                Toast.makeText(this@RewardActivity, "Gagal memuat reward", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun bukaGopay() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("gojek://gopay"))
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://gopay.co.id")))
        }
    }

    private fun setActiveNav() {
        setNavColor(R.id.nav_home, R.color.gray_text, Typeface.NORMAL)
        setNavColor(R.id.nav_reward, R.color.green_primary, Typeface.BOLD)
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
}