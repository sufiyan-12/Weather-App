package com.sufiyan.weatherapp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import org.w3c.dom.Text

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
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
        weatherRVModalArrayList = ArrayList<WeatherRVModal>();
        weatherRVAdapter = WeatherRVAdapter(this, weatherRVModalArrayList)
        weatherRV.adapter = weatherRVAdapter

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, Manifest)
        }
    }

    private fun getWeatherInfo(cityName: String{
        val url: String = "http://api.weatherapi.com/v1/current.json?key=4103bb5e79d54593af7152829222711&q="+cityName+""&aqi=yes"

    }
}

















