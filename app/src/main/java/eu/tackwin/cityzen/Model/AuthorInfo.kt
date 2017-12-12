package eu.tackwin.cityzen.Model

import org.json.JSONObject

/**
 * Created by tackw on 12/12/2017.
 */
class AuthorInfo(
		val pseudo: String,
		val id: String
) {

	companion object {
		fun createFromJson(json: JSONObject): AuthorInfo {
			return AuthorInfo(
					(json["pseudo"] ?: "pseudo_default") as String,
					(json["id"] ?: "id_default") as String
				)
		}
	}
}