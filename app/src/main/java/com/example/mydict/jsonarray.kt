package com.example.mydict

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class jsonarray {
    @SerializedName("results")
    @Expose
    var results: String? = null

    @SerializedName("lexicalEntries")
    @Expose
    var lexicalEntries: String? = null

    @SerializedName("entries")
    @Expose
    var entries: String? = null

    @SerializedName("senses")
    @Expose
    var senses: String? = null
    @SerializedName("definitons")
    @Expose
    var definitons: String? = null
}