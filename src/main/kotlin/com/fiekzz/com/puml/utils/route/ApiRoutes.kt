package com.fiekzz.com.puml.utils.route

/*
* object RoutePath {
    const val GET_ROOT = "/"
    const val GET_HEALTH = "/health"
}
* */

private const val apiRoot = "/api"

object APIROUTES {

    object DEFAULT {
        const val BASE = "/"
        const val GET_BASE = "$apiRoot"
        const val GET_HEALTH = "$apiRoot/health"
    }

    object PLANTUML {
        const val POST_FILE_GENERATE_UML = "$apiRoot/plantuml/file/generate"
        const val POST_TEXT_GENERATE_UML = "$apiRoot/plantuml/text/generate"
    }
}