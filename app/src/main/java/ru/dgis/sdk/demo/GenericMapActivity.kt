package ru.dgis.sdk.demo

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.dgis.sdk.Context
import ru.dgis.sdk.geometry.GeoPointWithElevation
import ru.dgis.sdk.map.Anchor
import ru.dgis.sdk.map.BearingSource
import ru.dgis.sdk.map.Map
import ru.dgis.sdk.map.MapView
import ru.dgis.sdk.map.MyLocationControllerSettings
import ru.dgis.sdk.map.MyLocationMapObjectSource
import ru.dgis.sdk.map.SnapToMapLayout

data class Point(
    val id: Int,
    val order: Int,
    val lat: Double,
    val lon: Double,
    val isUrgent: Boolean
)

class GenericMapActivity : AppCompatActivity() {
    private val sdkContext: Context by lazy { application.sdkContext }
    lateinit var mapSource: MyLocationMapObjectSource

    private val closeables = mutableListOf<AutoCloseable?>()

    private var map: Map? = null

    private lateinit var mapView: MapView
    private lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map_generic)

        root = findViewById(R.id.content)
        mapView = findViewById<MapView>(R.id.mapView).also {
            it.getMapAsync(this::onMapReady)
            it.showApiVersionInCopyrightView = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closeables.forEach { it?.close() }
    }

    private fun onMapReady(map: Map) {
        this.map = map
        closeables.add(map)

        mapSource = MyLocationMapObjectSource(
            sdkContext,
            MyLocationControllerSettings(BearingSource.MAGNETIC)
        )
        map.addSource(mapSource)

        // создание маркеров
        val snapToMapLayout = findViewById<SnapToMapLayout>(R.id.snapToMapLayout)

        val json = assets.open("points.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val listType = object : TypeToken<List<Point>>() {}.type
        val list = gson.fromJson<List<Point>>(json, listType)

        list.forEach { point ->
            val params = SnapToMapLayout.LayoutParams(
                width = ViewGroup.LayoutParams.WRAP_CONTENT,
                height = ViewGroup.LayoutParams.WRAP_CONTENT,
                position = GeoPointWithElevation(point.lat, point.lon),
                anchor = Anchor(0.5f, 1.0f),
            )

            val pointPin = PointPin(this, point)
            snapToMapLayout.addView(pointPin.view, params)
        }
    }
}
