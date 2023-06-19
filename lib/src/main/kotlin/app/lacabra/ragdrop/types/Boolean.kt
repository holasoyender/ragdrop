package app.lacabra.ragdrop.types

import app.lacabra.ragdrop.Type
import app.lacabra.ragdrop.TypeFactory
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
        return true
    }

    override fun withTypes(types: Map<String, KFunction1<String, Type>>) = Unit


    companion object : TypeFactory {
        override val name = "boolean"

        override fun create(value: String): Type = Boolean(value)
    }
}