package app.lacabra.ragdrop

interface Type {

    @Throws(BadSchemaException::class)
    fun verify(): Boolean

    fun validate(value: String): Boolean

}