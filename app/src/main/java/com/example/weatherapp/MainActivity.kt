package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var textView: TextView
    private lateinit var searchView: SearchView

    //запускаем функцию
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textView = binding.textView
        searchView = binding.searchView
        binding.btnExit.setOnClickListener {
            finish()
        }

        //строка поиска города
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //поиск во время отправки запроса
                searchCity(query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                // Handle text changes, if needed
                return false
            }
        }
        )
        binding.btEnter.setOnClickListener {
            // передает данные с сервера при нажатии кнопки
            val query = searchView.query.toString()
            searchCity(query)
        }
    }
    private fun searchCity(cityName: String) {
        val weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&units=metric&appid=820f3cf1558a68db5403f5d407db9051"
        // передача данных погоды с api
        val queue = Volley.newRequestQueue(this)
        val stringReq = StringRequest(
            Request.Method.GET, weatherUrl,
            { response ->
                // лог успешного запроса
                Log.e("lat", "Запрос: $response")
                parseJsonResponse(response)
            },
            { error ->
                // лог ошибочного запроса
                Log.e("lat", "Ошибка: ${error.message}", error)
                textView.text = "Ошибка: ${error.message}"
            }
        )
        queue.add(stringReq)
    }
    private fun parseJsonResponse(response: String) {
        try {
            // забираем из JSON нужную информацию
            val obj = JSONObject(response)
            val main = obj.getJSONObject("main")
            val temperature = main.getDouble("temp")
            val humidity = main.getInt("humidity")
            val pressure = main.getInt("pressure")
            val weatherArray = obj.getJSONArray("weather")
            val weatherObj = weatherArray.getJSONObject(0)
            var description = weatherObj.getString("description")
            val wind = obj.getJSONObject("wind")
            val windSpeed = wind.getDouble("speed")
            val windDirection = wind.getDouble("deg")
            val clouds = obj.getJSONObject("clouds")
            val cloudiness = clouds.getInt("all")
            val name = obj.getString("name")
            val sys = obj.getJSONObject("sys")
            val country = sys.getString("country")

            // передаем информацию через функцию getString()
            textView.text = "Погода в $name, $country:\n" +
                    "Описание: $description\n" +
                    "Температура: ${temperature}°C\n" +
                    "Влажность: $humidity%\n" +
                    "Давление: $pressure hPa\n" +
                    "Скорость ветра: $windSpeed m/s\n" +
                    "Направление ветра: $windDirection°\n" +
                    "Обачность: $cloudiness%\n"
        } catch (e: JSONException) {
            // лог ошибки JSON
            Log.e("lat", "Ошибка передачи JSON: ${e.message}", e)
            textView.text = "Ошибка передачи JSON"
        }
    }
}

