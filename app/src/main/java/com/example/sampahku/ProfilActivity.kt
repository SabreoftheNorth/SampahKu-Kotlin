package com.example.sampahku

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sampahku.ApiClient.service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilActivity : AppCompatActivity(), View.OnClickListener {
    // ini adalah navbar, navbar punya home, reward, qr, statistik, profil
    private var navHome: LinearLayout? = null
    private var navReward: LinearLayout? = null
    private var navQr: View? = null
    private var navStatistik: LinearLayout? = null
    private var navProfil: LinearLayout? = null

    private fun tampilkanDialogEdit() {
        // 1. Memanggil layout XML yang baru kita buat
        val dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profil, null)
        val etNama = dialogView.findViewById<EditText>(R.id.et_edit_nama)
        val etTelepon = dialogView.findViewById<EditText>(R.id.et_edit_telepon)
        val etAlamat = dialogView.findViewById<EditText>(R.id.et_edit_alamat)

        // 2. Mengambil teks yang sedang tampil di layar untuk dijadikan teks awal (supaya user tidak mengetik dari nol)
        val itemNama = findViewById<View>(R.id.item_nama_pengguna)
        val itemTelepon = findViewById<View>(R.id.item_telepon)
        val itemAlamat = findViewById<View>(R.id.item_alamat)

        etNama.setText(
            (itemNama.findViewById<View?>(R.id.tv_value) as TextView).getText().toString()
        )
        etTelepon.setText(
            (itemTelepon.findViewById<View?>(R.id.tv_value) as TextView).getText().toString()
        )
        etAlamat.setText(
            (itemAlamat.findViewById<View?>(R.id.tv_value) as TextView).getText().toString()
        )

        // 3. Membuat Pop-up Dialog
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setPositiveButton("Simpan", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val namaBaru = etNama.getText().toString().trim { it <= ' ' }
                val teleponBaru = etTelepon.getText().toString().trim { it <= ' ' }
                val alamatBaru = etAlamat.getText().toString().trim { it <= ' ' }

                // Eksekusi API Update
                eksekusiUpdateProfil(namaBaru, teleponBaru, alamatBaru)
            }
        })
        builder.setNegativeButton("Batal", null)
        builder.show()
    }

    private fun eksekusiUpdateProfil(nama: String?, telepon: String?, alamat: String?) {
        // MENGAMBIL ID DARI BRANKAS
        val sharedPref = getSharedPreferences("SampahkuPrefs", MODE_PRIVATE)
        val currentUserId = sharedPref.getInt("USER_ID", -1)

        if (currentUserId == -1) return  // Cegah eksekusi jika tidak ada user login


        Toast.makeText(this, "Menyimpan...", Toast.LENGTH_SHORT).show()

        // Memanggil API PATCH yang kita buat di ApiService
        service.updateProfil(currentUserId, nama, telepon, alamat)!!
            .enqueue(object : Callback<ProfilResponse?> {
                override fun onResponse(
                    call: Call<ProfilResponse?>,
                    response: Response<ProfilResponse?>
                ) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(
                            this@ProfilActivity,
                            "Berhasil diperbarui!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Segarkan ulang data di layar dengan memanggil method setupProfilData() Anda lagi
                        setupProfilData()

                        // Opsional: Update nama di header (tulisan Rakha Atha Muhammad)
                        val tvNamaHeader = findViewById<TextView>(R.id.tv_nama)
                        tvNamaHeader.setText(response.body()!!.nama)
                    } else {
                        Toast.makeText(this@ProfilActivity, "Gagal mengupdate.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ProfilResponse?>, t: Throwable) {
                    Toast.makeText(this@ProfilActivity, "Error Jaringan", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        if (getSupportActionBar() != null) {
            getSupportActionBar()!!.hide()
        }

        // tombol backnya BISA YESS!!!
        val ivBack = findViewById<ImageView>(R.id.iv_back)
        ivBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })

        // tombol edit profil
        val ivEdit = findViewById<ImageView>(R.id.iv_edit)
        ivEdit.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Memanggil fungsi pop-up edit profil
                tampilkanDialogEdit()
            }
        })

        // findviewbyid tapi ini depresi :(
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

        // setup data-data profil dan menu
        setupProfilData()
        setupMenuItems()
        setActiveNav()
    }

    // pokoknya patokannya ada di MainActivity untuk intent intent ini
    override fun onClick(v: View) {
        if (v.getId() == R.id.nav_home) {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        } else if (v.getId() == R.id.nav_reward) {
            val intent = Intent(this@ProfilActivity, RewardActivity::class.java)
            startActivity(intent)
        } else if (v.getId() == R.id.nav_qr) {
            val intent = Intent(this@ProfilActivity, QrActivity::class.java)
            startActivity(intent)
        } else if (v.getId() == R.id.nav_statistik) {
            val intent = Intent(this@ProfilActivity, StatistikActivity::class.java)
            startActivity(intent)
        } else if (v.getId() == R.id.nav_profil) {
            Toast.makeText(this, "Profil", Toast.LENGTH_SHORT).show()
        }
    }

    // set data-data informasi profil
    private fun setupProfilData() {
        val itemNama = findViewById<View>(R.id.item_nama_pengguna)
        val itemEmail = findViewById<View>(R.id.item_email)
        val itemTelepon = findViewById<View>(R.id.item_telepon)
        val itemAlamat = findViewById<View>(R.id.item_alamat)

        (itemNama.findViewById<View?>(R.id.tv_label) as TextView).setText("Nama Pengguna :")
        (itemEmail.findViewById<View?>(R.id.tv_label) as TextView).setText("Email :")
        (itemTelepon.findViewById<View?>(R.id.tv_label) as TextView).setText("No. Telepon :")
        (itemAlamat.findViewById<View?>(R.id.tv_label) as TextView).setText("Alamat :")

        // Tampilkan tulisan "Loading..." sementara data diambil
        (itemNama.findViewById<View?>(R.id.tv_value) as TextView).setText("Loading...")
        (itemEmail.findViewById<View?>(R.id.tv_value) as TextView).setText("Loading...")

        val sharedPref = getSharedPreferences("SampahkuPrefs", MODE_PRIVATE)
        val currentUserId =
            sharedPref.getInt("USER_ID", -1) // -1 adalah nilai default jika gagal/kosong

        if (currentUserId == -1) {
            Toast.makeText(this, "Sesi login tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Tarik data profil dari API Django
        service.getProfil(currentUserId)!!.enqueue(object : Callback<ProfilResponse?> {
            override fun onResponse(
                call: Call<ProfilResponse?>,
                response: Response<ProfilResponse?>
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    val profil: ProfilResponse = response.body()!!
                    (itemNama.findViewById<View?>(R.id.tv_value) as TextView).setText(profil.nama)
                    (itemEmail.findViewById<View?>(R.id.tv_value) as TextView).setText(profil.email)
                    (itemTelepon.findViewById<View?>(R.id.tv_value) as TextView).setText(profil.noTelepon)
                    (itemAlamat.findViewById<View?>(R.id.tv_value) as TextView).setText(profil.alamat)
                }
            }

            override fun onFailure(call: Call<ProfilResponse?>, t: Throwable) {
                Toast.makeText(this@ProfilActivity, "Gagal memuat profil", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun konfirmasiHapusAkun() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Akun Permanen")
            .setMessage("Apakah Anda yakin ingin menghapus akun ini? Semua poin dan data riwayat Anda akan musnah dan tidak bisa dikembalikan.")
            .setPositiveButton("Ya, Hapus!", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    eksekusiHapusAkun()
                }
            })
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun eksekusiHapusAkun() {
        // Ambil ID dari brankas memori
        val sharedPref = getSharedPreferences("SampahkuPrefs", MODE_PRIVATE)
        val currentUserId = sharedPref.getInt("USER_ID", -1)

        if (currentUserId == -1) return  // Cegah jika ID tidak ada


        Toast.makeText(this, "Menghapus data ke server...", Toast.LENGTH_SHORT).show()

        // Tembak API DELETE ke Django
        service.hapusProfil(currentUserId)!!.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                // Di Retrofit, response 204 (No Content) dihitung sebagai isSuccessful()
                if (response.isSuccessful()) {
                    Toast.makeText(
                        this@ProfilActivity,
                        "Akun berhasil dihapus selamanya.",
                        Toast.LENGTH_LONG
                    ).show()

                    // 1. Bersihkan memori login dari HP
                    sharedPref.edit().clear().apply()

                    // 2. Tendang user kembali ke halaman Login
                    val intent = Intent(this@ProfilActivity, LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ProfilActivity, "Gagal menghapus akun.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(
                    this@ProfilActivity,
                    "Error Jaringan: " + t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // ini untuk setiap ikon dan label yg ada di menu halaman
    private fun setupMenuItems() {
        setMenuItem(R.id.menu_pin, R.drawable.ic_pin, "Atur Pin Penukaran Reward")
        setMenuItem(R.id.menu_password, R.drawable.ic_lock, "Ubah Password")
        setMenuItem(R.id.menu_privasi, R.drawable.ic_shield, "Hapus Akun Permanen")
        setMenuItem(R.id.menu_bahasa, R.drawable.ic_globe, "Bahasa")
        setMenuItem(R.id.menu_bantuan, R.drawable.ic_help, "Bantuan")
        setMenuItem(R.id.menu_keluar, R.drawable.ic_logout, "Keluar")

        // khusus utk tombol Keluar, bisa kembali ke LoginActivity dan clear back stack
        // WAAAAAAAAAAH HEBAT
        val menuKeluar = findViewById<RelativeLayout>(R.id.menu_keluar)
        menuKeluar.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val sharedPref = getSharedPreferences("SampahkuPrefs", MODE_PRIVATE)
                sharedPref.edit().clear().apply()

                val intent = Intent(this@ProfilActivity, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        })

        val menuHapus = findViewById<RelativeLayout>(R.id.menu_privasi)
        menuHapus.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Jangan langsung hapus! Tampilkan konfirmasi peringatan dulu
                konfirmasiHapusAkun()
            }
        })

        // menu lain yg belum diimplementasi: "coming sooooonnn..."
        val menuIds = intArrayOf(
            R.id.menu_pin, R.id.menu_password,
            R.id.menu_privasi, R.id.menu_bahasa, R.id.menu_bantuan
        )
        for (id in menuIds) {
            val menu = findViewById<RelativeLayout>(id)
            menu.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Toast.makeText(
                        this@ProfilActivity,
                        getString(R.string.coming_soon), Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    // helper utk set icon dan label setiap item menu
    private fun setMenuItem(menuId: Int, iconRes: Int, label: String?) {
        val menu = findViewById<View>(menuId)
        (menu.findViewById<View?>(R.id.iv_menu_icon) as ImageView).setImageResource(iconRes)
        (menu.findViewById<View?>(R.id.tv_menu_label) as TextView).setText(label)
    }

    // lebih buat navbar, highlight bagian profil dan yg lain abu2
    private fun setActiveNav() {
        setNavColor(R.id.nav_home, R.color.gray_text, Typeface.NORMAL)
        setNavColor(R.id.nav_reward, R.color.gray_text, Typeface.NORMAL)
        setNavColor(R.id.nav_statistik, R.color.gray_text, Typeface.NORMAL)
        setNavColor(R.id.nav_profil, R.color.green_primary, Typeface.BOLD)
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
