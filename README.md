# SampahKu - Kotlin

SampahKu adalah aplikasi manajemen sampah yang membantu pengguna dalam mengelola sampah, mendapatkan poin, dan menukarkan reward.

## Setup Development Environment

### Prerequisite
- Android Studio (sudah include JDK 21) **[Recommended]**
- ATAU JDK 21 manual + VSCode

### Persiapan Backend (Database)
Aplikasi ini menggunakan backend REST API untuk mengelola data. Sebelum menjalankan aplikasi, pastikan server backend sudah aktif.
- **Base URL Default:** `http://10.0.2.2:8000/` (Akses localhost dari Emulator Android).
- Jika menggunakan perangkat fisik, pastikan perangkat berada dalam jaringan yang sama dengan server dan ubah `BASE_URL` di `ApiClient.kt` menggunakan alamat IP server Anda.

---

## Menjalankan Aplikasi

### Untuk Developer (Android Studio)
1. Buka folder project di Android Studio.
2. Tunggu hingga proses **Gradle Sync** selesai.
3. Hubungkan perangkat Android atau jalankan Emulator.
4. Klik tombol **Run** (Ikon Play) di toolbar atas atau tekan `Shift + F10`.

### Untuk Pengguna (Instalasi APK)
1. Build APK melalui Android Studio: `Build > Build Bundle(s) / APK(s) > Build APK(s)`.
2. Temukan file APK di `app/build/outputs/apk/debug/app-debug.apk`.
3. Pindahkan APK ke perangkat Android dan instal secara manual.

---

## Menjalankan Pengujian (Testing)

### 1. Local Unit Tests
Digunakan untuk menguji logika fungsi secara mandiri.
- **Lokasi:** `app/src/test/java/com/example/sampahku/`
- **Cara Menjalankan:**
  - **Android Studio:** Klik kanan pada direktori `test` -> **Run 'Tests in...'**.
  - **Terminal:** `./gradlew test`

### 2. Instrumented Tests
Digunakan untuk menguji UI dan integrasi dengan sistem Android di perangkat/emulator.
- **Lokasi:** `app/src/androidTest/java/com/example/sampahku/`
- **Cara Menjalankan:**
  - **Android Studio:** Klik kanan pada direktori `androidTest` -> **Run 'Tests in...'**.
  - **Terminal:** `./gradlew connectedAndroidTest`

---

## Struktur Proyek
- `ApiClient.kt` & `ApiService.kt`: Konfigurasi koneksi ke server/backend.
- `LoginActivity.kt` & `RegisterActivity.kt`: Autentikasi pengguna.
- `MainActivity.kt`: Dashboard utama aplikasi.
- `ProfilActivity.kt`: Informasi akun dan total poin.
- `QrActivity.kt`: Fitur pemindaian QR Code.
