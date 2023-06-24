package app.lacabra.ragdrop.types

import app.lacabra.ragdrop.Type
import app.lacabra.ragdrop.exceptions.BadMapException
import kotlin.Boolean
import kotlin.String

class Map(
    private val _value: String,
    private var types: kotlin.collections.Map<String, Function1<String, Type>>,
    ): Type {

    private var key: Type? = null
    private var value: Type? = null

    private var verified = false
    private var valid = false

    init {
        verify()
    }

    constructor(value: String): this(value, app.lacabra.ragdrop.Constants.defaultTypes)

    override fun verify(): Boolean {
        if (verified) return valid
        verified = true

        val rawData = _value.split(" ").getOrNull(1)
            ?: throw BadMapException("Invalid map requirements: you must provide a type (e.g. map number:string)") // number:string
        val rawDataKey = rawData.split(":").getOrNull(0)
            ?: throw BadMapException("Invalid map requirements: you must provide a key type (e.g. map number:string)") // number
        val rawDataValue = rawData.split(":").getOrNull(1)
            ?: throw BadMapException("Invalid map requirements: you must provide a value type (e.g. map number:string)") // string

        val key = rawDataKey.split("[").getOrNull(0)
            ?: throw BadMapException("Invalid map requirements: you must provide a key type (e.g. map number:string)") // number

        val value = rawDataValue.split("[").getOrNull(0)
            ?: throw BadMapException("Invalid map requirements: you must provide a value type (e.g. map number:string)") // string

        val keyType = types[key] ?: throw BadMapException("Invalid map requirements: unknown key type '$key'")
        val valueType = types[value] ?: throw BadMapException("Invalid map requirements: unknown value type '$value'")

        val keyReq = try {
            keyType(rawDataKey)
        } catch (e: Exception) {
            throw BadMapException("Invalid map requirements: unknown key type '$key'")
        }

        val valueReq = try {
            valueType(rawDataValue)
        } catch (e: Exception) {
            throw BadMapException("Invalid map requirements: unknown value type '$value'")
        }

        if (!keyReq.verify())
            throw BadMapException("Invalid map requirements: unknown key type '$key'")

        if (!valueReq.verify())
            throw BadMapException("Invalid map requirements: unknown value type '$value'")

        this.key = keyReq
        this.value = valueReq

        valid = true
        return true
    }

    override fun validate(value: String): Boolean {
        return true
    }

    fun validate(map: kotlin.collections.Map<Any, Any>): Boolean {
        if (!verify()) return false

        val key = key ?: throw BadMapException("Invalid map requirements: unknown key type")
        val value = value ?: throw BadMapException("Invalid map requirements: unknown value type")

        for ((k, v) in map) {
            if (!key.validate(k.toString())) throw BadMapException("Invalid map requirements: invalid key '$k' for type")
            if (!value.validate(v.toString())) throw BadMapException("Invalid map requirements: invalid value '$v' for type")
        }

        return true
    }

    override fun withTypes(types: kotlin.collections.Map<String, Function1<String, Type>>) {
        this.types = types
    }

}