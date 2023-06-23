package app.lacabra.ragdrop

import app.lacabra.ragdrop.exceptions.BadSchemaException
import app.lacabra.ragdrop.exceptions.BadYamlException
import org.json.JSONObject

class SchemaVerification {

    companion object {

        /**
         * Verify the schema of the given JSON object
         * @param schema The schema to verify
         * @return Whether the schema is valid
         * @throws BadSchemaException If the schema is invalid
         */
        fun verifySchema(schema: JSONObject, types: Map<String, Function1<String, Type>>): Boolean {

            val roots = schema.keys()
            for (root in roots) {

                val rootObject = schema.getJSONObject(root)
                val aliases = try {
                    rootObject.getJSONArray("aliases").toList().map { it.toString() }
                } catch (e: Exception) {
                    listOf()
                }
                val required = try {
                    rootObject.getBoolean("required")
                } catch (e: Exception) {
                    true
                }
                //val description = try { rootObject.getString("description") } catch (e: Exception) { "" }
                if (aliases.any { it == root })
                    throw BadSchemaException("Root '$root' is an alias for itself")

                if (!required) {
                    try {
                        rootObject.get("default")
                    } catch (e: Exception) {
                        throw BadSchemaException("Root '$root' is not required but has no default value")
                    }
                } else {
                    try {
                        rootObject.getString("default")
                    } catch (_: Exception) {
                    }
                }

                val rawDataType = try {
                    rootObject.getString("type")
                } catch (e: Exception) {
                    throw BadSchemaException("Type not specified or unknown for root '$root")
                }

                val rawType = rawDataType.split(" ").getOrNull(0)?.split("[")?.getOrNull(0) ?: throw BadSchemaException(
                    "Type '$rawDataType' not found for root '$root'"
                )

                val obj = if (rawType == "map") {
                    try {
                        rootObject.getJSONObject("object")
                    } catch (e: Exception) {
                        null
                    }
                } else null

                if (obj != null)
                    return verifySchema(obj, types)

                val dataType = types[rawType] ?: throw BadSchemaException("Type '$rawType' not found for root '$root'")
                val type = dataType(rawDataType)
                type.withTypes(types)

                if (!type.verify())
                    throw BadSchemaException("Type '$rawType' is invalid for root '$root'")

            }
            return true
        }

        /**
         * Verify the given YAML against the given JSON object
         * @param yaml The YAML to verify
         * @param json The JSON object to verify against
         * @param types The types to use
         * @return Whether the YAML is valid
         * @throws BadSchemaException If the YAML is invalid
         */
        fun verifyYaml(
            yaml: Map<Any, Any>,
            json: JSONObject,
            types: Map<String, Function1<String, Type>>,
            rootName: String = "default"
        ): Boolean {

            val yamlRoots = yaml.keys
            val jsonRoots = json.keySet()


            /** -----------------
             *  PHASE 1:
             *  Check if all required roots are present in the YAML
             */

            // list of required roots and their aliases
            val requiredRoots: List<Pair<String, List<String>>> =
                jsonRoots.filter { json.getJSONObject(it).getBoolean("required") }.toList().let { mp ->
                    mp.map { root ->
                        val aliases = try {
                            json.getJSONObject(root).getJSONArray("aliases").toList()
                        } catch (e: Exception) {
                            listOf()
                        }.map { it.toString() }
                        Pair(root, aliases)
                    }
                }

            val missingRoots = requiredRoots.filter { (root, aliases) ->
                !yamlRoots.contains(root) && aliases.none { alias ->
                    yamlRoots.contains(alias)
                }
            }

            if (missingRoots.isNotEmpty())
                throw BadYamlException("Missing required roots: ${missingRoots.joinToString(", ") { (root, _) -> "'$root'" }} ${if (rootName == "default") "" else "in root '$rootName'"}")

            /** -----------------
             * PHASE 2:
             * Compare the types of the roots in the YAML to the types in the JSON schema
             */

            for (yamlRoot in yamlRoots) {

                // yamlRoot is the root name to check
                // yaml[yamlRoot] is the value of the root

                /** -----------------
                 *  PHASE 2.1:
                 *  Check if the root is in the JSON schema
                 */

                val schemaObject = if (!jsonRoots.contains(yamlRoot)) {
                    // The root is not in the JSON schema, so we need to check if it's an alias

                    val obj = jsonRoots.firstOrNull { rt ->
                        try {
                            json.getJSONObject(rt).getJSONArray("aliases").toList()
                        } catch (e: Exception) {
                            listOf()
                        }.map { it.toString().lowercase() }
                            .contains(yamlRoot.toString().lowercase()) || rt.lowercase() == yamlRoot.toString()
                            .lowercase()
                    }
                        ?: throw BadSchemaException("Root '$yamlRoot' is not in the schema ${if (rootName == "default") "" else "of root '$rootName'"}")

                    json.getJSONObject(obj)
                } else
                    json.getJSONObject(yamlRoot.toString())

                /** -----------------
                 *  PHASE 2.2:
                 *  At this point we already checked that all required roots are present in the YAML, so we can start verifying the types
                 *  Get the JSON schema type
                 */

                // The root is in the JSON schema, now we need to check if the value is valid for the type

                val rawDataType = schemaObject.getString("type")
                val rawType = rawDataType.split(" ").getOrNull(0)?.split("[")?.getOrNull(0) ?: throw BadSchemaException(
                    "Type '$rawDataType' not found for root '$yamlRoot' ${if (rootName == "default") "" else "in root '$rootName'"}"
                )
                val dataType = types[rawType] ?: throw BadSchemaException("Type '$rawDataType' not found for root '$yamlRoot' ${if (rootName == "default") "" else "in root '$rootName'"}")

                val obj = if (rawType == "map") {
                    try {
                        schemaObject.getJSONObject("object")
                    } catch (e: Exception) {
                        null
                    }
                } else null

                @Suppress("UNCHECKED_CAST")
                if (obj != null) {
                    val t = try {
                        yaml[yamlRoot] as Map<Any, Any>
                    } catch (e: Exception) {
                        throw BadYamlException("Value '${yaml[yamlRoot]}' is invalid for root '$yamlRoot': ${e.message} ${if (rootName == "default") "" else "in root '$rootName'"}")
                    }

                    verifyYaml(t, obj, types, yamlRoot.toString())
                } else {

                    /** -----------------
                     *  PHASE 2.3:
                     *  Check if the value is valid for the type
                     */

                    val type = dataType(rawDataType)
                    type.withTypes(types)

                    val validate =
                        try {
                            if (yaml[yamlRoot] is Map<*, *>) {
                                val t = yaml[yamlRoot] as Map<Any, Any>
                                (type as app.lacabra.ragdrop.types.Map).validate(t)
                            } else
                                type.validate(yaml[yamlRoot].toString())
                        } catch (e: BadYamlException) {
                            throw BadYamlException("Value '${yaml[yamlRoot]}' is invalid for root '$yamlRoot': ${e.message} ${if (rootName == "default") "" else "in root '$rootName'"}")
                        }

                    if (!validate)
                        throw BadYamlException("Value '${yaml[yamlRoot]}' is invalid for root '$yamlRoot', expected type '$rawType' ${if (rootName == "default") "" else "in root '$rootName'"}")

                }
            }

            return true
        }
    }
}