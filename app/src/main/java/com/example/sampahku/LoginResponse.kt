package com.example.sampahku;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("nama")
    private String nama;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public int getUserId() { return userId; }
    public String getNama() { return nama; }
}