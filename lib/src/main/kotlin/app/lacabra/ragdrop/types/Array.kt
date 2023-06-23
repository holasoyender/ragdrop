package app.lacabra.ragdrop.types

import app.lacabra.ragdrop.Constants
import app.lacabra.ragdrop.Type
import app.lacabra.ragdrop.TypeFactory
import app.lacabra.ragdrop.exceptions.BadArrayException
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Map

class Array(
    private val value: String,
    private var types: Map<String, Function1<String, Type>>,
): Type {

    var type: Type? = null

    private var verified = false
    private var valid = false

    init {
        verify()
    }

    override fun verify(): Boolean {

        if (verified) return valid
        verified = true

        // example: array number[1..10], please dont try this with 'array array number[1..10]'
        val rawData = value.split(" ").drop(1).joinToString(" ")  // number[1..10]
        val rawType = rawData.split("[").getOrNull(0) ?: throw BadArrayException("Invalid array requirements: you must provide a type (e.g. array number)") // number
        val type = types[rawType] ?: throw BadArrayException("Invalid array requirements: unknown type '$rawType'") // number -> Number.create(<input>)

        val req = try {
            type(rawData) // Create a new instance of the type
        } catch (e: Exception) {
            throw BadArrayException("Invalid array requirements: unknown type '$rawType'")
        }

        if (!req.verify()) // Verify the type
            throw BadArrayException("Invalid array requirements: unknown type '$rawType'")

        this.type = req
        valid = true

        return true
    }

    override fun validate(value: String): Boolean {

        if (!verify()) return false

        // value is a string of the form: [1, 2, 3, 4]
        val values = value.drop(1).dropLast(1).split(",").map { it.trim() }
        val type = type ?: throw BadArrayException("Invalid array requirements: unknown type")

        for (v in values)
            if (!type.validate(v)) throw BadArrayException("Invalid array requirements: invalid value '$v' for type")

        return true
    }

    override fun withTypes(types: Map<String, Function1<String, Type>>) {
        this.types = types
    }

    companion object: TypeFactory {
        override val name = "array"

        override fun create(value: String): Type = Array(value, Constants.defaultTypes)

    }

}