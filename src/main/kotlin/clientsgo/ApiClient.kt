package clientsgo

import kotlinx.coroutines.Deferred

interface ApiClient {

    fun loginAsync(auth: Authorization) : Deferred<GithubUser?>

}
