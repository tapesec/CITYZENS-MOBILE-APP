package eu.tackwin.cityzen

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import eu.tackwin.cityzen.api.Auth
import eu.tackwin.cityzen.api.AuthInfo
import eu.tackwin.cityzen.api.AuthListener
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity(), AuthListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_sign_in)

		Common.dpi = resources.displayMetrics.density

		checkPermissions()

		if (!isConnected()){
			AlertDialog.Builder(this)
				.setTitle("No connection")
				.setMessage("Check your internet connection")
				.setPositiveButton("Ok") { _, _ -> }
				.show()
		}
	}

	private fun checkPermissions(){
		if (ContextCompat.checkSelfPermission(
				this,
				Manifest.permission.ACCESS_NETWORK_STATE
			) != PackageManager.PERMISSION_GRANTED
		) {
			ActivityCompat.requestPermissions(
					this,
					arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
					0
			)
		}
		if (ContextCompat.checkSelfPermission(
				this,
				Manifest.permission.INTERNET
			) != PackageManager.PERMISSION_GRANTED
		) {
			ActivityCompat.requestPermissions(
					this,
					arrayOf(Manifest.permission.INTERNET),
					0
			)
		}

	}

	private fun isConnected() :  Boolean {
		val connectivityMngr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val networkInfo = connectivityMngr.activeNetworkInfo
		return (networkInfo != null && networkInfo.isConnected)
	}

	fun signinPressed(view: View) {
		Auth(getString(R.string.base_url) + "/auth/token", email_input.text.toString(),
			password_input.text.toString(), this
		)
	}

	override fun authComplete(
			access_token: String,
			refresh_token: String,
			id_token: String,
			scope: String,
			expires_in: Int,
			token_type: String
	) {
		Log.i("access_token", access_token)
		Log.i("refresh_token", refresh_token)
		Log.i("id_token", id_token)
		Log.i("scope", scope)
		Log.i("expires_in", expires_in.toString())
		Log.i("token_type", token_type)

		AuthInfo.ACCESS_TOKEN = access_token
		AuthInfo.REFRESH_TOKEN = refresh_token
		AuthInfo.ID_TOKEN = id_token

		val intent = Intent(this, MapsActivity::class.java)
		startActivity(intent)
	}

	override fun authFailure(error_code: Int, message: String) {
		Log.e("error_code", error_code.toString())
		Log.e("message", message)
	}
}
