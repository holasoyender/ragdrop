package app.lacabra.ragdrop.types

import app.lacabra.ragdrop.Constants
import app.lacabra.ragdrop.Importer
import app.lacabra.ragdrop.Type
import app.lacabra.ragdrop.TypeFactory
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KFunction1

class Array(
    private val value: String,
    private val types: Map<String, KFunction1<String, Type>>,
): Type {

    var type: Type? = null

    private var verified = false
    private var valid = false

    constructor(value: String): this(value, Constants.defaultTypes)

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
        return true
    }

    companion object: TypeFactory {
        override val name = "array"

        class BadArrayException(
            message: String
        ): Exception(message)

        override fun create(value: String): Type = Array(value)
        fun create(value: String, types: Map<String, KFunction1<String, Type>>): Type = Array(value, types)
        fun create(value: String, importer: Importer): Type = Array(value, importer.types)

    }

}