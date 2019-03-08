package clientsgo

import kotlinx.coroutines.Deferred
import khttp.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

object ApiClientImp: ApiClient {

    override fun loginAsync(auth: Authorization) : Deferred<GithubUser> = GlobalScope.async {
        val response = get("https://api.github.com/user", auth = auth)
        if (response.statusCode != 200) {
            throw RuntimeException("Incorrect login or password")
        }

        val jsonObject = response.jsonObject
        with (jsonObject) {
            return@async GithubUser(getString("login"), getInt("id"),
                    getString("repos_url"), getString("name"))
        }
    }

}