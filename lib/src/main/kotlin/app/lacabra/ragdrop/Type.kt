package app.lacabra.ragdrop

import app.lacabra.ragdrop.exceptions.BadSchemaException

interface Type {

    @Throws(BadSchemaException::class)
    fun verify(): Boolean

    fun validate(value: String): Boolean

    fun withTypes(types: Map<String, Function1<String, Type>>)

}