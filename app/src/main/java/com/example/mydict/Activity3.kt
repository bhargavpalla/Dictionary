package com.example.mydict

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Array.get
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.Paths.get


class Activity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun findfun(view: View){

        var stringUrl = "https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/"+editext.text.toString()
        var asynctask  = Async()
        asynctask.execute(stringUrl)

    }
    inner class Async: AsyncTask<String, Void, Data>() {
        override fun doInBackground(vararg p0: String?): Data? {
            var url: URL ? = null
            try {
                url = URL(p0[0])

            }catch (exception: MalformedURLException){
                Log.d( "MainActivity","ERROR")
            }
            var jsonresp :String?
            try {
                jsonresp = makehttpconnectin(url)
                val data = extract(jsonresp)
                return data
            }catch(e:IOException){
                Log.d("MainActivity","Error in making http connection")
            }
            return null


        }

        override fun onPostExecute(result: Data?) {
            super.onPostExecute(result)
            if(result == null){
                return
            }
            showdef(result.definition)

        }
    }
    fun showdef(definition:String?){
        val intent = Intent(this, Second::class.java)
        intent.putExtra("mydef", definition)
        startActivity(intent)

    }
    fun extract(json: String): Data?{

        val gson = Gson()
        val jsonobj: jsonarray = gson.fromJson(json, jsonarray::class.java)
        val definiton: String = jsonobj.get("results").get(0).get("lexicalEntries").get(0).get("entries").get(0).get("senses").get(0).get("definitions")


    }
    fun makehttpconnectin(url:URL?): String{
        var response = ""
        var inputstream: InputStream? = null
        var urlconnection: HttpURLConnection
        try {
            urlconnection = url?.openConnection() as HttpURLConnection
            urlconnection.requestMethod = "GET"
            urlconnection.setRequestProperty("Accept", "Application/json")
            urlconnection.setRequestProperty("app_id", "a600c867")
            urlconnection.setRequestProperty("app_key", "ed4376e805a27314498f1d4311ab6bc5")
            urlconnection.readTimeout = 10000
            urlconnection.connectTimeout = 15000
            urlconnection.connect()
            if (urlconnection.responseCode == 200) {
                inputstream = urlconnection.inputStream
                response = readfrominputstream(inputstream)
            } else {
                Log.d("MainActivity", "ERROR" + urlconnection.responseCode)
            }

            urlconnection.disconnect()
            inputstream?.close()
        }catch (e:IOException){
            Log.d("MainActivity","Connection Error"+e)
        }
        return response

    }
    fun readfrominputstream(inputstream: InputStream?) :String{
        val output= StringBuilder()
        if(inputstream!=null){
            val inputstreamreader = InputStreamReader(inputstream, charset("UTF-8"))
            val reader = BufferedReader(inputstreamreader)
            var line = reader.readLine()
            while(line!=null){
                output.append(line)
                line = reader.readLine()
            }

        }
        return output.toString()



    }

}
