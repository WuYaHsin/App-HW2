package com.example.hw2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import okhttp3.*
import java.io.IOException
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var textView = findViewById<TextView>(R.id.textView)
        textView.movementMethod = ScrollingMovementMethod.getInstance()

        //[POST]URL取得API Token
        val client0 = OkHttpClient()
        val body = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .add("client_id", "s0853012-426d5633-231f-4d49")
            .add("client_secret", "ff6c06e5-abf7-476b-8116-3b0cd9945955")
            .build()
        val request0 = Request.Builder()
            .url("https://tdx.transportdata.tw/auth/realms/TDXConnect/protocol/openid-connect/token")
            .header("content-type", "application/x-www-form-urlencoded")
            .post(body)
            .build()


        client0.newCall(request0).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) = if (response.isSuccessful) {
                val responseBody0 = response.body?.string()
                println(responseBody0)

                //儲存Token
                val jsonObject0 = JSONObject(responseBody0)
                val token = jsonObject0.get("access_token")
                println("token=${token}")
                //查詢機場代碼
                val item = arrayOf(
                    "TPE 桃園", "TSA 松山", "CYI 嘉義", "CMJ 七美", "GNI 綠島", "HUN 花蓮",
                    "KHH 高雄", "KNH 金門", "MZG 馬公", "MFK 馬祖", "KYD 蘭嶼", "PIF 屏東", "WOT 望安", "TXG 台中",
                    "TTT 台東", "TNN 台南"
                )
                var airportindex = 0
                val button2 = findViewById<Button>(R.id.button2)
                button2.setOnClickListener {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("機場代碼")
                        .setSingleChoiceItems(item, 0) { dialogInterface, i ->
                            airportindex = i
                        }

                        .setPositiveButton("確定") { dialog, which ->
                            Toast.makeText(this@MainActivity, "${item[airportindex]}", Toast.LENGTH_LONG).show()
                            var airport = item[airportindex].substring(0, 3)
                            var input = findViewById<EditText>(R.id.inputtext)
                            input.setText("${airport}")
                        }.show()
                }


                val button = findViewById<Button>(R.id.button)
                button.setOnClickListener {
                    textView.text = ""
                    val client = OkHttpClient()

                    //[GET]URL
                    var airportID = ""
                    airportID = findViewById<EditText>(R.id.inputtext).text.toString()

                    val url =
                        "https://tdx.transportdata.tw/api/basic/v2/Air/FIDS/Airport/Departure/${airportID}?IsCargo=false&%24orderby=ScheduleDepartureTime%20desc&%24top=5000&%24format=JSON"
                    val request = Request.Builder()
                        .url(url)
                        .header(
                            "Authorization",
                            "Bearer "+token
                        )
                        .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: okhttp3.Call, e: IOException) {
                            e.printStackTrace()
                        }

                        override fun onResponse(call: okhttp3.Call, response: Response) {
                            if (response.isSuccessful) {

                                val responseBody = response.body?.string()
                                //println(responseBody)

                                val jsonObject = JSONArray(responseBody)
                                var index = 0
                                var count = 0


                                for (i in 0 until jsonObject.length()) {
                                    //更新時間
                                    var updateTime =
                                        jsonObject.getJSONObject(i).getString("UpdateTime")
                                    updateTime = updateTime.replace("-", "")
                                    updateTime = updateTime.replace(":", "")
                                    updateTime = updateTime.replace("T", "")
                                    updateTime = updateTime.substring(0, 12)
                                    //println("更新時間:${updateTime}")

                                    //預計起飛時間
                                    var departureTime =
                                        jsonObject.getJSONObject(i)
                                            .getString("ScheduleDepartureTime")
                                    departureTime = departureTime.replace("-", "")
                                    departureTime = departureTime.replace(":", "")
                                    departureTime = departureTime.replace("T", "")
                                    departureTime = departureTime.substring(0, 12)
                                    //                        println("預計起飛時間:${departureTime}")

                                    if (updateTime > departureTime) {
                                        if (jsonObject.getJSONObject(i)
                                                .has("ActualDepartureTime")
                                        ) {
                                            //實際起飛時間
                                            if (count >= 10) {
                                                break
                                            }
                                            var actdeparture =
                                                jsonObject.getJSONObject(i)
                                                    .getString("ActualDepartureTime")
                                            count += 1
                                            index = i


                                            //實際起飛時間
                                            var actTime = jsonObject.getJSONObject(index)
                                                .getString("ActualDepartureTime")
                                            //                            println("實際起飛時間:${actTime}")

                                            //航班
                                            val airlineID =
                                                jsonObject.getJSONObject(index)
                                                    .getString("AirlineID")
                                            val flightNumber =
                                                jsonObject.getJSONObject(index)
                                                    .getString("FlightNumber")
                                            val id = airlineID + flightNumber
                                            //                            println("航班:${id}")

                                            //目的地
                                            val destination = jsonObject.getJSONObject(index)
                                                .getString("ArrivalAirportID")
                                            //                            println("目的地:${destination}")

                                            //航廈
                                            val terminal =
                                                jsonObject.getJSONObject(index)
                                                    .getString("Terminal")
                                            //                            println("航廈:${terminal}")

                                            //登機門
                                            val gate =
                                                jsonObject.getJSONObject(index).getString("Gate")
                                            //                            println("登機門:${gate}")

                                            textView.append("航班:${id} \n 實際起飛時間:${actTime} \n 目的地:${destination} \n 航廈:${terminal} \n 登機門:${gate} \n\n-------------------\n\n")

                                        }
                                    }
                                }


                            } else {
                                println("Request failed")
                                runOnUiThread {
                                    findViewById<TextView>(R.id.textView).text = "資料錯誤"
                                }
                            }
                        }
                    })
                }
            } else {
                println("Request failed")
                runOnUiThread {
                    findViewById<TextView>(R.id.textView).text = "資料錯誤"
                }
            }
        })
    }
}