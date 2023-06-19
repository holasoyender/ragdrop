package ragdrop.types

import ragdrop.BadSchemaException
import ragdrop.Constants.requirementsRegex
import ragdrop.Type
import ragdrop.TypeFactory
import kotlin.Boolean

class String(
    private val value: kotlin.String
): Type {

    var maxLength: Int = Int.MAX_VALUE
    var minLength: Int = 0

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
        return true
    }

    companion object : TypeFactory {

        override val name = "string"

        class BadStringException(
            message: kotlin.String
        ): BadSchemaException(message)

        override fun create(value: kotlin.String): Type = String(value)

    }
}