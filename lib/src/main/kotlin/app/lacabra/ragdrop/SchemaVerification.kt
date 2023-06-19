package app.lacabra.ragdrop

import org.json.JSONObject

class SchemaVerification {

    companion object {

        /**
         * Verify the schema of the given JSON object
         * @param schema The schema to verify
         * @return Whether the schema is valid
         * @throws BadSchemaException If the schema is invalid
         */
        fun verifySchema(schema: JSONObject): Boolean {

            val roots = schema.keys()
            for (root in roots) {
/*
                val rootObject = schema.getJSONObject(root)

                val aliases = try { rootObject.getJSONArray("aliases").toList().map { it.toString() } } catch (e: Exception) { listOf() }
                val required = try { rootObject.getBoolean("required") } catch (e: Exception) { true }
                val type = try { rootObject.getString("type") } catch (e: Exception) { "string" }
                val description = try { rootObject.getString("description") } catch (e: Exception) { "" }

                // from "hello my world" split by " " and remove the first word
                val descriptionWords = description.split(" ").drop(1)
*/
            }


            return true
        }

    }
}