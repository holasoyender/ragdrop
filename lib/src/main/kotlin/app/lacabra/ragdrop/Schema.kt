package app.lacabra.ragdrop

import app.lacabra.ragdrop.exceptions.BadSchemaException
import app.lacabra.ragdrop.exceptions.BadYamlException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Import a JSON template from the given path
 * @param path The path to the JSON template
 * @throws IOException If the file cannot be read
 * @throws IllegalArgumentException If the file is not found
 * @constructor Create a new Importer
 */
class Schema(
    path: String
) {

    private var isResource: Boolean = false

    /**
     * Raw content of the JSON template
     */
    var content: String = ""
    private var verified: Boolean = false

    /**
     * Whether the JSON template is valid
     */
    var valid: Boolean = false

    /**
     * The error message if the JSON template is invalid
     */
    var errorMessage: String? = null
    private var json: JSONObject? = null

    private val types = Constants.defaultTypes.toMutableMap()

    init {

        if (path.startsWith("classpath:")) {
            isResource = true
            content = path.substring(10)
        } else {
            content = path
        }

        content = if (isResource) {
            val file = getFileAsIOStream(content)
            fileContent(file)
        } else {
            val file = java.io.File(content)
            file.readText()
        }

        json = try {
            JSONObject(content)
        } catch (e: Exception) {
            throw BadSchemaException("Invalid JSON provided: ${e.message ?: "Unknown reason"}}")
        }

        /*
        INFO!: File shouldn't be verified here, because user may want to add types before verifying

        val (valid, errorMessage) = verifyWithReason()

        this.verified = true
        this.valid = valid
        this.errorMessage = errorMessage

        if (!valid && errorMessage != null)
            throw BadSchemaException(errorMessage)*/

    }

    /**
     * Add a type to the importer
     * @param type The type to add
     * @param clazz The class to add
     * @throws IllegalArgumentException If the type already exists
     */
    @kotlin.jvm.Throws(IllegalArgumentException::class)
    fun addType(type: String, clazz: (String) -> Type) {
        if (types.containsKey(type))
            throw IllegalArgumentException("Type '$type' already exists")

        types[type] = clazz
    }

    /**
     * Add multiple types to the importer
     * @param types The types to add
     * @throws IllegalArgumentException If any of the types already exists
     * @see addType
     */
    @kotlin.jvm.Throws(IllegalArgumentException::class)
    fun addTypes(types: Map<String, (String) -> Type>) {
        types.forEach { (type, clazz) ->
            addType(type, clazz)
        }
    }

    /**
     * Verify the JSON schema
     * @return Whether the JSON schema is valid
     * @throws IOException If the file cannot be read
     */
    fun verify(): Boolean {
        if (verified)
            return valid
        return verifyWithReason().first
    }

    /**
     * Verify the JSON schema
     * @return A pair of whether the JSON schema is valid and the error message
     * @throws IOException If the file cannot be read
     */
    private fun verifyWithReason(): Pair<Boolean, String?> {
        this.verified = true
        return try {
            val valid = SchemaVerification.verifySchema(json!!, this.types)

            this.valid = valid
            this.errorMessage = null

            Pair(valid, null)
        } catch (e: BadSchemaException) {

            this.valid = false
            this.errorMessage = e.message

            Pair(false, e.message)
        }
    }

    /**
     * Validate a YAML file
     * @param yaml The YAML file to validate
     * @return Whether the YAML file is valid
     * @throws BadYamlException If the YAML file is invalid
     */
    @kotlin.jvm.Throws(BadYamlException::class)
    fun validate(yaml: Yaml): Boolean {
        return verify() && SchemaVerification.verifyYaml(yaml.parsed, json!!, this.types)
    }

    /**
     * Validate a YAML file
     * @param yaml The YAML file to validate
     * @return Whether the YAML file is valid
     * @throws BadYamlException If the YAML file is invalid
     */
    @kotlin.jvm.Throws(BadYamlException::class)
    fun validate(yaml: Map<Any, Any>): Boolean {
        return verify() && SchemaVerification.verifyYaml(yaml, json!!, this.types)
    }

    /**
     * Get a file as an InputStream
     * @param fileName The name of the file
     * @return The file as an InputStream
     * @throws IllegalArgumentException If the file is not found
     * @throws IOException If the file cannot be read
     */
    @Throws(IOException::class, IllegalArgumentException::class)
    private fun getFileAsIOStream(fileName: String): InputStream {
        return javaClass
            .classLoader
            .getResourceAsStream(fileName) ?: throw IllegalArgumentException("$fileName is not found")
    }

    /**
     * Get the content of a file as a String
     * @param `is` The InputStream of the file
     * @return The content of the file as a String
     * @throws IOException If the file cannot be read
     * @throws IllegalArgumentException If the file is not found
     */
    @Throws(IOException::class, IllegalArgumentException::class)
    private fun fileContent(`is`: InputStream): String {
        var lines = ""
        InputStreamReader(`is`).use { isr ->
            BufferedReader(isr).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    lines += line
                }
                `is`.close()
            }
        }
        return lines
    }
}