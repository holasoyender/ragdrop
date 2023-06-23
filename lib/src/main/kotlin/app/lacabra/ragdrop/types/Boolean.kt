package app.lacabra.ragdrop.types

import app.lacabra.ragdrop.Type
import app.lacabra.ragdrop.TypeFactory
import app.lacabra.ragdrop.exceptions.BadBooleanException
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Map
import kotlin.reflect.KFunction1

@Suppress("UNUSED_PARAMETER")
class Boolean(
    value: String
): Type {
    override fun verify(): Boolean {
        return true
    }

    override fun validate(value: String): Boolean {

        if (value == "true" || value == "false") return true
        if (value == "1" || value == "0") return true
        if (value == "yes" || value == "no") return true
        if (value == "y" || value == "n") return true
        if (value == "on" || value == "off") return true
        if (value == "enabled" || value == "disabled") return true
        if (value == "enable" || value == "disable") return true
        if (value == "t" || value == "f") return true

        throw BadBooleanException("Invalid boolean value: '$value' should be true/false")
    }

    override fun withTypes(types: Map<String, KFunction1<String, Type>>) = Unit


    companion object : TypeFactory {
        override val name = "boolean"

        override fun create(value: String): Type = Boolean(value)
    }
}