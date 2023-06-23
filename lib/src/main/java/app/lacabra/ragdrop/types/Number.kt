package app.lacabra.ragdrop.types

import app.lacabra.ragdrop.Constants.requirementsRegex
import app.lacabra.ragdrop.Type
import app.lacabra.ragdrop.TypeFactory
import app.lacabra.ragdrop.exceptions.BadNumberException
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Map
import kotlin.reflect.KFunction1

class Number(
    private val value: String
): Type {

    private var maxValue = Int.MAX_VALUE
    private var minValue = Int.MIN_VALUE

    private var verified = false
    private var valid = false

    init {
        verify()
    }

    override fun verify(): Boolean {

        if (verified) return valid
        verified = true

        val requirements = try { requirementsRegex.find(value)?.groupValues?.getOrNull(1) /* number[X..Y] -> X..Y */ } catch (e: Exception) { null }

        if (requirements != null) {
            val range = requirements.split("..")
            if (range.size != 2)
                throw BadNumberException("Invalid int requirements: you must provide a range of length (e.g. number[1..10])")

            try {
                minValue = range[0].toInt()
                maxValue = range[1].toInt()
            } catch (e: Exception) {
                throw BadNumberException("Invalid int requirements: you must provide a range of length (e.g. number[1..10])")
            }
        }

        valid = true
        return true
    }

    override fun validate(value: String): Boolean {

        if (!verify()) return false

        val number = try { value.toInt() } catch (e: Exception) { null }
        if (number == null) throw BadNumberException("Invalid int: '$value' is not a valid number")

        if (number < minValue) throw BadNumberException("Invalid int: '$value' is too small, minimum value is $minValue")
        if (number > maxValue) throw BadNumberException("Invalid int: '$value' is too big, maximum value is $maxValue")

        return true
    }

    override fun withTypes(types: Map<String, KFunction1<String, Type>>) = Unit


    companion object: TypeFactory {
        override val name = "number"


        override fun create(value: String): Type = Number(value)
    }
}