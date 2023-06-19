package app.lacabra.ragdrop

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

                val rootObject = schema.getJSONObject(root)

                val aliases = try { rootObject.getJSONArray("aliases").toList().map { it.toString() } } catch (e: Exception) { listOf() }
                val required = try { rootObject.getBoolean("required") } catch (e: Exception) { true }
                val description = try { rootObject.getString("description") } catch (e: Exception) { "" }
                if (aliases.any { it == root })
                    throw BadSchemaException("Root '$root' is an alias for itself")

                val defaultValue = if (required) {
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
                val rawDataType = try {
                    rootObject.getString("type")
                } catch (e: Exception) {
                    throw BadSchemaException("Type not specified or unknown for root '$root")
                }

                val rawType = rawDataType.split(" ").getOrNull(0)?.split("[")?.getOrNull(0) ?: throw BadSchemaException("Type '$rawDataType' not found for root '$root'")

                val dataType = types[rawType] ?: throw BadSchemaException("Type '$rawType' not found for root '$root'")
                val type = dataType(rawDataType)

                if (!type.verify())
                    throw BadSchemaException("Type '$rawType' is invalid for root '$root'")

            }

            return true
        }

    }
}