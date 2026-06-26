package com.example.sampahku;
import com.google.gson.annotations.SerializedName;

public class RiwayatResponse {
    @SerializedName("nama_sampah") private String namaSampah;
    @SerializedName("berat") private double berat;
    @SerializedName("poin_didapat") private int poinDidapat;
    @SerializedName("tanggal_lokasi") private String tanggalLokasi;

    public String getNamaSampah() { return namaSampah; }
    public double getBerat() { return berat; }
    public int getPoinDidapat() { return poinDidapat; }
    public String getTanggalLokasi() { return tanggalLokasi; }
}