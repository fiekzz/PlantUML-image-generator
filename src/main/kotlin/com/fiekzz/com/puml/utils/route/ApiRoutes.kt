package com.fiekzz.com.puml.utils.route

object APIROUTES {

    const val API_ROOT = "/api"

    object DEFAULT {
        const val BASE = "/"
        const val GET_BASE = "/"
        const val GET_HEALTH = "/health"
    }

    object PLANTUML {
        const val POST_FILE_GENERATE_UML = "/plantuml/file/generate"
        const val POST_TEXT_GENERATE_UML = "/plantuml/json/generate"

        const val POST_GENERATE_UML_CACHE = "/plantuml/generate/cache"
        const val GET_IMAGE_ENDPOINT = "/plantuml/image/{id}"
    }
}