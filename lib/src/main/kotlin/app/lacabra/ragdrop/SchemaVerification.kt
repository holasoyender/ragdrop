package app.lacabra.ragdrop

import app.lacabra.ragdrop.types.Number
import org.json.JSONObject
import kotlin.reflect.KFunction1

class SchemaVerification {

    companion object {

        /**
         * Verify the schema of the given JSON object
         * @param schema The schema to verify
         * @return Whether the schema is valid
         * @throws BadSchemaException If the schema is invalid
         */
        fun verifySchema(schema: JSONObject, types: Map<String, KFunction1<String, Type>>): Boolean {

            val roots = schema.keys()
            for (root in roots) {

                println("Checking root '$root'")

                val rootObject = schema.getJSONObject(root)

                val aliases = try { rootObject.getJSONArray("aliases").toList().map { it.toString() } } catch (e: Exception) { listOf() }
                println("Aliases: $aliases")
                val required = try { rootObject.getBoolean("required") } catch (e: Exception) { true }
                println("Required: $required")
                val description = try { rootObject.getString("description") } catch (e: Exception) { "" }
                println("Description: $description")
                if (aliases.any { it == root })
                    throw BadSchemaException("Root '$root' is an alias for itself")

                val defaultValue = if (!required) {
                    try {
                        rootObject.getString("default")
                    } catch (e: Exception) {
                        throw BadSchemaException("Root '$root' is required but has no default value")
                    }
                } else {
                    try {
                        rootObject.getString("default")
                    } catch (e: Exception) {
                        null
                    }
                }

                println("Default value: $defaultValue")
                val rawDataType = try {
                    rootObject.getString("type")
                } catch (e: Exception) {
                    throw BadSchemaException("Type not specified or unknown for root '$root")
                }
                println("Raw data type: $rawDataType")

                val rawType = rawDataType.split(" ").getOrNull(0)?.split("[")?.getOrNull(0) ?: throw BadSchemaException("Type '$rawDataType' not found for root '$root'")
                println("Raw type: $rawType")

                val dataType = types[rawType] ?: throw BadSchemaException("Type '$rawType' not found for root '$root'")
                val type = dataType(rawDataType)
                type.withTypes(types)

                when (rawType) {
                    "string" -> {
                        (type as app.lacabra.ragdrop.types.String).let {
                            println("String min length: ${it.minLength}")
                            println("String max length: ${it.maxLength}")
                        }
                    }
                    "number" -> {
                        (type as Number).let {
                            println("Number min value: ${it.minValue}")
                            println("Number max value: ${it.maxValue}")
                        }
                    }
                    "array" -> {
                        (type as app.lacabra.ragdrop.types.Array).let {
                            println("Array of ${it.type}")
                            println("Number min value ${(it.type as Number).minValue}")
                            println("Number max value ${(it.type as Number).maxValue}")
                        }
                    }
                }

                if (!type.verify())
                    throw BadSchemaException("Type '$rawType' is invalid for root '$root'")

            }

            return true
        }

    }
}