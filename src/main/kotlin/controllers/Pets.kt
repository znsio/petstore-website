package controllers

import org.json.JSONArray
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.net.URI

data class PetInfo(val id: Int, val name: String)
data class CreationStatus(val id: Int, val status: String)

const val base = "http://localhost:9000"

enum class API(val method: HttpMethod, val url: String) {
    CREATE_PET(HttpMethod.PUT, "/pets")
}

@RestController
class Pets {
    @GetMapping("/pets/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun details(@PathVariable id: Int) = getFromAPI("/pets/$id")

    @PostMapping("/create-pet", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createPet(@RequestBody pet: String) = CreationStatus(id = callCreatePetAPI(pet), status="success")

    @GetMapping("/findFirstAvailablePet", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findFirstAvailablePet(@RequestParam(name="type", required=true) type: String): PetInfo {
        val result = getFromAPI("/pets?type=$type&status=available&name=")
        val pets = JSONArray(result)

        val pet = pets.getJSONObject(0)

        return PetInfo(pet.getInt("id"), pet.getString("name").toUpperCase())
    }


    private fun getFromAPI(url: String): String = RestTemplate().getForEntity(URI.create("$base$url"), String::class.java).body?.trim() ?: ""
}

private fun callCreatePetAPI(pet: String): Int = writeJSONToAPI(API.CREATE_PET, pet)?.toInt() ?: error("No pet id received in response to create request.")

private fun writeJSONToAPI(api: API, body: String): String? {
    val username = "jamie"
    val password = "secure"

    val authToken = getAuthToken(username, password)

    val uri = URI.create("$base${api.url}")
    val headers = HttpHeaders()
    headers["Content-Type"] = "application/json"
    headers["Authenticate"] = authToken
    val request = RequestEntity(body, headers, api.method, uri)
    val response = RestTemplate().exchange(request, String::class.java)
    return response.body
}

fun getAuthToken(username: String, password: String): String {
    val uri = URI.create("$base/auth")
    val headers = HttpHeaders()
    headers["Content-Type"] = "application/json"

    val request = RequestEntity("{\"username\": \"$username\", \"password\": \"$password\"}", headers, HttpMethod.POST, uri)
    val response = RestTemplate().exchange(request, String::class.java)
    return response.body
}