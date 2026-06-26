package com.example.sampahku;
import com.google.gson.annotations.SerializedName;

public class RewardResponse {
    @SerializedName("nama_reward") private String namaReward;
    @SerializedName("deskripsi") private String deskripsi;
    @SerializedName("poin_dibutuhkan") private int poinDibutuhkan;
    @SerializedName("logo_resource_name") private String logoResourceName;

    public String getNamaReward() { return namaReward; }
    public String getDeskripsi() { return deskripsi; }
    public int getPoinDibutuhkan() { return poinDibutuhkan; }
    public String getLogoResourceName() { return logoResourceName; }
}