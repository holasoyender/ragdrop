package app.lacabra.ragdrop

object Constants {

    val requirementsRegex = Regex("\\[(.*?)]")

    val defaultTypes = mapOf(
        "string" to app.lacabra.ragdrop.types.String::create,
        "number" to app.lacabra.ragdrop.types.Number::create,
        "boolean" to app.lacabra.ragdrop.types.Boolean::create,
        "array" to app.lacabra.ragdrop.types.Array::create,
        "map" to app.lacabra.ragdrop.types.Map::create
    )
}