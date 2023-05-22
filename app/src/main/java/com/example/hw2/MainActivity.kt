package com.example.hw2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException
import com.google.gson.JsonParser
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
                //查詢機場代碼
                val item = arrayOf(
                    "TPE 桃園", "TSA 松山", "CYI 嘉義", "CMJ 七美", "GNI 綠島", "HUN 花蓮",
                    "KHH 高雄", "KNH 金門", "MZG 馬公", "MFK 馬祖", "KYD 蘭嶼", "PIF 屏東", "WOT 望安", "TXG 台中",
                    "TTT 台東", "TNN 台南"
                )
                var airportindex = 0
                val button2 = findViewById<Button>(R.id.button2)
                button2.setOnClickListener {
                    AlertDialog.Builder(getApplicationContext())
                        .setTitle("機場代碼")
                        .setSingleChoiceItems(item, 0) { dialogInterface, i ->
                            airportindex = i
                        }

                        .setPositiveButton("確定") { dialog, which ->
                            Toast.makeText(getApplicationContext(), "${item[airportindex]}", Toast.LENGTH_LONG).show()
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
                            "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJER2lKNFE5bFg4WldFajlNNEE2amFVNm9JOGJVQ3RYWGV6OFdZVzh3ZkhrIn0.eyJleHAiOjE2ODQ4MjczNTcsImlhdCI6MTY4NDc0MDk1NywianRpIjoiMGI1NDgxOGItM2Y5Yy00YjkxLWE5MGItOGY4YjJjNjY3MjIyIiwiaXNzIjoiaHR0cHM6Ly90ZHgudHJhbnNwb3J0ZGF0YS50dy9hdXRoL3JlYWxtcy9URFhDb25uZWN0Iiwic3ViIjoiOGViMTFlMDUtNTc1MC00NmE2LThkNWMtZGMyNjYxZGQ4MTllIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiczA4NTMwMTItNDI2ZDU2MzMtMjMxZi00ZDQ5IiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJzdGF0aXN0aWMiLCJwcmVtaXVtIiwibWFhcyIsImFkdmFuY2VkIiwidmFsaWRhdG9yIiwiaGlzdG9yaWNhbCIsImJhc2ljIl19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJ1c2VyIjoiZWI2NzE3YTAifQ.fY0c4PgfS1Il_GVsTs3YOn3tHSYK5-TGafuq5YwTF5ZERl0VPjT1Tu3pTgPj8Fmva7Yo58krf3_Kd4ftgItrA4tHlyMuP02s_aCp0OILh23JVtLF0NcN-hqCBsmmd3q6x47EMdN-Yi1eWjbEiZTuTIANbfJqXcOV5iRQO6A-IVX6GfHK28MBfCAvmA5CKs0XDtGOO7NLdPF1DCq91xR8Zt6QYe74QTakCWdsNV4NO1eW4nowpLRyVzUhgVjhyme2XmBYutbqjSUWM_aXQah3Xu2OXmE__GkFt6135h-UL5Vp_0Rnvm6YYqPIEK1ehdNwgwHEUQ9zOCs3hysjW-cX-Q"
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
                                var j = 0


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