package com.example.mydict

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
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
         try{
             var jsnresp = JSONObject(json)
             val featureResults = jsnresp.optJSONArray("results")
             val firstObject = featureResults.getJSONObject(0)
             val lexicalEntries = firstObject.getJSONArray("lexicalEntries")
             val firstlexical = lexicalEntries.getJSONObject(0)
             val entries = firstlexical.getJSONArray("entries")
             val firstentry = entries.getJSONObject(0)
             val senses = firstentry.getJSONArray("senses")
             val firstsense = senses.getJSONObject(0)
             val definition = firstsense.getJSONArray("definitions")
             Log.d("definition","it is"+definition[0])
             return Data(definition[0].toString())

         }catch (e:JSONException){
             Log.d("MainActivity", "Error in parsing" + e)
         }
        return null

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
