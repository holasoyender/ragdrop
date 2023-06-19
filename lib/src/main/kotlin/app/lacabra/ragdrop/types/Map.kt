package app.lacabra.ragdrop.types

import app.lacabra.ragdrop.BadSchemaException
import app.lacabra.ragdrop.Constants
import app.lacabra.ragdrop.Type
import app.lacabra.ragdrop.TypeFactory
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KFunction1

class Map(
    private val value: String,
    private var types: kotlin.collections.Map<String, KFunction1<String, Type>>,
    ): Type {

    private var verified = false
    private var valid = false

    init {
        verify()
    }

    override fun verify(): Boolean {
        if (verified) return valid
        verified = true

        val rawData = value.split(" ").getOrNull(1) ?: throw BadMapException("Invalid map requirements: you must provide a type (e.g. map number)") // number:string
        val key = rawData.split(":").getOrNull(0) ?: throw BadMapException("Invalid map requirements: you must provide a key type (e.g. map number:string)") // number
        val value = rawData.split(":").getOrNull(1) ?: throw BadMapException("Invalid map requirements: you must provide a value type (e.g. map number:string)") // string

        //...
        valid = true
        return true
    }

    override fun validate(value: kotlin.String): Boolean {
        return true
    }

    override fun withTypes(types: kotlin.collections.Map<String, KFunction1<String, Type>>) {
        this.types = types
    }

    companion object : TypeFactory {
        override val name = "map"

        private class BadMapException(
            message: kotlin.String
        ): BadSchemaException(message)

        override fun create(value: kotlin.String): Type = Map(value, Constants.defaultTypes)

    }

}