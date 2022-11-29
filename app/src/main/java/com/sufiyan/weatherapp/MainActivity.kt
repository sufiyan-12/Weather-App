package com.sufiyan.weatherapp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var homeRL: RelativeLayout
    private lateinit var loadingPB: ProgressBar
    private lateinit var cityNameTV: TextView
    private lateinit var temperatureTV: TextView
    private lateinit var conditionTV: TextView
    private lateinit var weatherRV: RecyclerView
    private lateinit var cityEdt: TextInputEditText
    private lateinit var backIV: ImageView
    private lateinit var iconIV: ImageView
    private lateinit var searchIV: ImageView
    private lateinit var weatherRVModalArrayList: ArrayList<WeatherRVModal>
    private lateinit var weatherRVAdapter: WeatherRVAdapter
    private lateinit var locationManager: LocationManager
    private val PERMISSION_CODE: Int = 1
    private lateinit var cityName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_main)

        homeRL = findViewById(R.id.idRLHome)
        loadingPB = findViewById(R.id.idPBLoading)
        cityNameTV = findViewById(R.id.idTVCityName)
        temperatureTV = findViewById(R.id.idTVTemperature)
        conditionTV = findViewById(R.id.idTVCondition)
        weatherRV = findViewById(R.id.idRVWeather)
        cityEdt = findViewById(R.id.idEdtCity)
        backIV = findViewById(R.id.idIVBack)
        iconIV = findViewById(R.id.idIVIcon)
        searchIV = findViewById(R.id.idIVSearch)
        weatherRVModalArrayList = ArrayList<WeatherRVModal>()

        weatherRVAdapter = WeatherRVAdapter(this, weatherRVModalArrayList)
        weatherRV.adapter = weatherRVAdapter
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_CODE)
        }
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
        cityName = getCityName(location.longitude, location.latitude)
        getWeatherInfo(cityName)

        searchIV.setOnClickListener {
            val city: String = cityEdt.text.toString().trim()
            cityEdt.setText("")
            if(city.isEmpty()){
                Toast.makeText(this, "Please Enter City Name", Toast.LENGTH_SHORT).show()
            }else{
                getWeatherInfo(city)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Please provide the permissions..", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun getCityName(longitude: Double, latitude: Double): String{
        var cityName: String = "Not Found"
        val gcd: Geocoder = Geocoder(baseContext, Locale.getDefault())
        try{
            val addresses: List<Address> = gcd.getFromLocation(latitude, longitude, 10)!!
            for (adr in addresses){
                if(adr!=null){
                    val city: String = adr.locality
                    if(city != null && !city.equals("")){
                        cityName = city
                    }else{
                        Log.d("TAG", "City Not Found!")
                        Toast.makeText(this, "User City Not Found...", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }catch(e: IOException){
            e.printStackTrace()
        }
        return cityName
    }

    private fun getWeatherInfo(cityName: String){
        val url = "https://api.weatherapi.com/v1/forecast.json?key=4103bb5e79d54593af7152829222711&q=$cityName&aqi=yes"
        cityNameTV.text = cityName
        val requestQueue = Volley.newRequestQueue(this@MainActivity)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url,null,
            {response->
                loadingPB.visibility = View.GONE
                homeRL.visibility = View.VISIBLE
                weatherRVModalArrayList.clear()

                try {
                    val temperature: String = response.getJSONObject("current").getString("temp_c")
                    temperatureTV.text = "$temperature°c"
                    val city = response.getJSONObject("location").getString("name")
                    val region = response.getJSONObject("location").getString("region")
                    cityNameTV.text = "$city, $region"
                    val isDay: Int = response.getJSONObject("current").getString("is_day").toInt()
                    val condition: String = response.getJSONObject("current").getJSONObject("condition").getString("text")
                    val conditionIcon: String = response.getJSONObject("current").getJSONObject("condition").getString("icon")
                    Picasso.get().load("https:$conditionIcon").into(iconIV)
                    conditionTV.text = condition
                    if(isDay==1){
                        // morning
                        Picasso.get().load("https://images.unsplash.com/photo-1513002749550-c59d786b8e6c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80").into(backIV)
                    }else{
                        Picasso.get().load("https://images.unsplash.com/uploads/14116941824817ba1f28e/78c8dff1?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NjB8fGFuZHJvaWQlMjBhcHAlMjBiYWNrZ3JvdW5kfGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=500&q=60").into(backIV)
                    }

                    val forecastObj: JSONObject = response.getJSONObject("forecast")
                    val forecast0: JSONObject = forecastObj.getJSONArray("forecastday").getJSONObject(0)
                    val hourArray: JSONArray = forecast0.getJSONArray("hour")

                    for(i in 0 until hourArray.length()){
                        val hourObj: JSONObject = hourArray.getJSONObject(i)
                        val time: String = hourObj.getString("time")
                        val temper: String = hourObj.getString("temp_c")
                        val img: String = hourObj.getJSONObject("condition").getString("icon")
                        val wind: String = hourObj.getString("wind_kph")
                        weatherRVModalArrayList.add(WeatherRVModal(time, temper, img, wind))
                    }
                    weatherRVAdapter.notifyDataSetChanged()
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },{
                Toast.makeText(this, "Enter valid cityname..", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "Volley error $it")
        })
        requestQueue.add(jsonObjectRequest)
    }
}

















