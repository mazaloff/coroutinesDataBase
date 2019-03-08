package web

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.call
import io.ktor.features.origin
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.request.*
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.websocket.webSocket
import model.NewWidget
import service.WidgetService

fun Route.widget(widgetService: WidgetService) {

    route("/widget") {

        get("/") {
            call.respond(widgetService.getAllWidgets())
        }

        get("/{id}") {
            val widget = widgetService.getWidget(call.parameters["id"]?.toInt() ?: 0)
            if (widget == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(widget)
        }

        post("/") {

            /*val call = call.request.call
            val request = call.request
            val version: String = request.httpVersion // "HTTP/1.1"
            val httpMethod: HttpMethod = request.httpMethod // GET, POST...
            val uri: String = request.uri // Short cut for `origin.uri`
            val scheme: String = request.origin.scheme // "http" or "https"
            val host: String? = request.host() // The host part without the port
            val port: Int = request.port() // Port of request
            val path: String = request.path() // The uri without the query string
            val document: String = request.document() // The last component after '/' of the uri
            val remoteHost: String = request.origin.remoteHost // The IP address of the client doing the request
            val headers: Headers = request.headers*/

            val widget = call.receive<NewWidget>()
            call.respond(HttpStatusCode.Created, widgetService.addWidget(widget))
        }

        put("/") {
            val widget = call.receive<NewWidget>()
            val updated = widgetService.updateWidget(widget)
            if(updated == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, updated)
        }

        delete("/{id}") {
            val removed = widgetService.deleteWidget(call.parameters["id"]?.toInt() ?: 0)
            if (removed) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }

    }

    val mapper = jacksonObjectMapper().apply {
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    webSocket("/updates") {
        try {
            widgetService.addChangeListener(this.hashCode()) {
                outgoing.send(Frame.Text(mapper.writeValueAsString(it)))
            }
            while(true) {
                incoming.receiveOrNull() ?: break
            }
        } finally {
            widgetService.removeChangeListener(this.hashCode())
        }
    }
}
