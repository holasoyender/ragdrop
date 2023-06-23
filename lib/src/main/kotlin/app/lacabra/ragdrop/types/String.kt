package app.lacabra.ragdrop.types

import app.lacabra.ragdrop.Constants.requirementsRegex
import app.lacabra.ragdrop.Type
import app.lacabra.ragdrop.TypeFactory
import app.lacabra.ragdrop.exceptions.BadStringException
import kotlin.Boolean
import kotlin.collections.Map
import kotlin.reflect.KFunction1

class String(
    private val value: kotlin.String
): Type {

    private var maxLength: Int = Int.MAX_VALUE
    private var minLength: Int = 0

    private var verified = false
    private var valid = false

    init {
        verify()
    }

    override fun verify(): Boolean {

        if (verified) return valid
        verified = true

        val requirements = try { requirementsRegex.find(value)?.groupValues?.getOrNull(1) /* string[X..Y] -> X..Y */ } catch (e: Exception) { null }

        if (requirements != null) {
            val range = requirements.split("..")
            if (range.size != 2)
                throw BadStringException("Invalid string requirements: you must provide a range of length (e.g. string[1..10])")

            try {

                minLength = range[0].toInt()
                if (minLength < 0)
                    throw BadStringException("Invalid string requirements: you must provide a range of length (e.g. string[1..10])")

                maxLength = range[1].toInt()

            } catch (e: Exception) {
                throw BadStringException("Invalid string requirements: you must provide a range of length (e.g. string[1..10])")
            }
        }

        valid = true
        return true
    }

    override fun validate(value: kotlin.String): Boolean {

        if (!verify()) return false

        if (value.length < minLength) throw BadStringException("Invalid string: '$value' is too short, minimum length is $minLength")
        if (value.length > maxLength) throw BadStringException("Invalid string: '$value' is too long, maximum length is $maxLength")

        return true
    }

    override fun withTypes(types: Map<kotlin.String, KFunction1<kotlin.String, Type>>) = Unit


    companion object : TypeFactory {

        override val name = "string"


        override fun create(value: kotlin.String): Type = String(value)

    }
}