package app.lacabra.ragdrop

import kotlin.reflect.KFunction1

interface Type {

    @Throws(BadSchemaException::class)
    fun verify(): Boolean

    fun validate(value: String): Boolean

    fun withTypes(types: Map<String, KFunction1<String, Type>>)

}