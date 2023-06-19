package app.lacabra.ragdrop

interface TypeFactory {

    val name: String
    fun create(value: String): Type

}