package ragdrop

object Constants {

    val requirementsRegex = Regex("\\[(.*?)]")

    val defaultTypes = mapOf(
        "string" to ragdrop.types.String::create,
        "number" to ragdrop.types.Number::create,
        "boolean" to ragdrop.types.Boolean::create,
        "array" to ragdrop.types.Array::create,
    )
}