package com.example.sampahku;
import com.google.gson.annotations.SerializedName;

public class ProfilResponse {
    @SerializedName("nama") private String nama;
    @SerializedName("email") private String email;
    @SerializedName("no_telepon") private String noTelepon;
    @SerializedName("alamat") private String alamat;
    @SerializedName("total_poin") private int totalPoin;

    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public String getNoTelepon() { return noTelepon; }
    public String getAlamat() { return alamat; }
    public int getTotalPoin() { return totalPoin; }
}