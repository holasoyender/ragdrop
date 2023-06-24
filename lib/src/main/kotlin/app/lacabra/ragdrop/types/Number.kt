package app.lacabra.ragdrop.types

import app.lacabra.ragdrop.Constants.requirementsRegex
import app.lacabra.ragdrop.Type
import app.lacabra.ragdrop.exceptions.BadNumberException
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Map

class Number(
    private val value: String
): Type {

    private var maxValue = Long.MAX_VALUE
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
                throw BadNumberException("Invalid number requirements: you must provide a range of length (e.g. number[1..10])")

            try {
                minValue = range[0].toInt()
                maxValue = range[1].toLong()
            } catch (e: Exception) {
                throw BadNumberException("Invalid number requirements: you must provide a range of length (e.g. number[1..10])")
            }
        }

        valid = true
        return true
    }

    override fun validate(value: String): Boolean {

        if (!verify()) return false

        val number = try { value.toLong() } catch (e: Exception) { null }
            ?: throw BadNumberException("Invalid number: '$value' is not a valid number")

        if (number < minValue) throw BadNumberException("Invalid number: '$value' is too small, minimum value is $minValue")
        if (number > maxValue) throw BadNumberException("Invalid number: '$value' is too big, maximum value is $maxValue")

        return true
    }

    override fun withTypes(types: Map<String, Function1<String, Type>>) = Unit

}