package clientsgo

import kotlinx.coroutines.Deferred

interface ApiClient {

    fun login(auth: Authorization) : Deferred<GithubUser?>
    fun getRepositories(reposUrl: String, auth: Authorization) : Deferred<List<GithubRepository>>

}
