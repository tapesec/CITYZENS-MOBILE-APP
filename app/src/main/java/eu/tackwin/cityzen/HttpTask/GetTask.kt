package eu.tackwin.cityzen.HttpTask

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Created by Tackwin on 29/11/2017.
 */

class GetTask(
		private val getListener: GetListener,
		private val headers: Map<String, String>? = null
) : AsyncTask<JSONObject, Int, ByteArray>() {

	override fun doInBackground(vararg args: JSONObject?): ByteArray {
		if (args.isEmpty()) return ByteArray(0)

		val arg = args[0] as JSONObject

		if (!arg.has("url")) return ByteArray(0)

		val u = arg["url"] as String
		arg.remove("url")

		return sendGet(u, arg)
	}

	private fun sendGet(baseUrl: String, params: JSONObject): ByteArray {

		var connection: HttpURLConnection?
		connection = null

		try {
			var fullUrl = baseUrl

			if (params.length() != 0){
				val getData = StringBuilder()
				getData.append('?')

				var first = true
				for (it in params.keys()){
					if (first)
						first = false
					else
						getData.append('&')

					getData.append(URLEncoder.encode(it as String, "UTF-8"))
					getData.append("=")
					getData.append(URLEncoder.encode(params[it] as String, "UTF-8"))
				}
				fullUrl += getData.toString()

				Log.d("GET", fullUrl)
			}

			val url = URL(fullUrl)
			connection = url.openConnection() as HttpURLConnection

			connection.requestMethod = "GET"

			if (headers != null){
				for (it in headers.asIterable()){
					connection.setRequestProperty(
						URLEncoder.encode(it.key, "UTF-8"),
						URLEncoder.encode(it.value, "UTF-8")
					)
				}
			}

			connection.connect()
			BufferedReader(InputStreamReader(connection.inputStream)).use {
				val response = it.readText()
				val responseBytes = response.toByteArray()
				getListener.getComplete(responseBytes)
				return responseBytes
			}

		} catch (e: IOException) {
			Log.e("sendGet", e.message)

			getListener.getFailure(
					connection?.responseCode ?: 1,
					connection?.responseMessage ?: ""
			)
			return ByteArray(0)
		}
	}
}
