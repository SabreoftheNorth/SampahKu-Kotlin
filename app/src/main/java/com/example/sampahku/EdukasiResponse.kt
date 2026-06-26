package com.example.sampahku;
import com.google.gson.annotations.SerializedName;

public class EdukasiResponse {
    @SerializedName("video_id_youtube") private String videoIdYoutube;
    @SerializedName("judul") private String judul;
    @SerializedName("deskripsi") private String deskripsi;

    public String getVideoIdYoutube() { return videoIdYoutube; }
    public String getJudul() { return judul; }
    public String getDeskripsi() { return deskripsi; }
}