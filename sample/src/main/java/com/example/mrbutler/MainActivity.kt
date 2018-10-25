package com.example.mrbutler

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jetradar.permissions.mrButler
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.requestLocationPermissionButton

class MainActivity : AppCompatActivity() {
  private val disposables = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    requestLocationPermissionButton.setOnClickListener {
      requestLocationPermission()
    }
  }

  private fun requestLocationPermission() {
    disposables.add(mrButler(this).request(Manifest.permission.ACCESS_FINE_LOCATION)
        .subscribe(
            { granted ->
              if (granted) {
                Toast.makeText(this, R.string.toast_location_permission_granted, Toast.LENGTH_LONG).show()
              }
            },
            { error -> Log.e(TAG, "Failed to get location permission", error) }
        )
    )
  }

  override fun onDestroy() {
    super.onDestroy()
    disposables.dispose()
  }

  private companion object {
    private const val TAG = "MainActivity"
  }
}
