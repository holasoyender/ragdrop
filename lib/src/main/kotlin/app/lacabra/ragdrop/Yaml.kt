package app.lacabra.ragdrop

import app.lacabra.ragdrop.exceptions.BadYamlException
import app.lacabra.ragdrop.exceptions.BadSchemaException
import app.lacabra.ragdrop.exceptions.InvalidYamlValueException
import org.yaml.snakeyaml.Yaml

class Yaml {

    var content: String = ""
    var parsed: Map<Any, Any> = mapOf()

    /**
     * Load YAML content from a string.
     * @param content The YAML content to load.
     * @return The [Yaml] object.
     * @throws BadYamlException If the YAML is invalid.
     */
    fun load(content: String): app.lacabra.ragdrop.Yaml {
        this.content = content

        if (content.isEmpty())
            throw BadYamlException("Empty YAML file")

        this.parsed = try {
            Yaml().load(content)
        } catch (e: Exception) {
            throw BadYamlException("Invalid YAML file: ${e.message}")
        }

        return this
    }

    /**
     * Verify the YAML content against a schema.
     * @param schema The schema to verify against.
     * @return Whether the YAML is valid.
     * @throws BadSchemaException If the schema is invalid.
     * @throws BadYamlException If the YAML is invalid.
     * @throws InvalidYamlValueException If a value in the YAML is invalid.
     */
    fun validate(schema: Schema): Boolean {
        return schema.validate(this.parsed)
    }

    /**
     * Load YAML content from a file path.
     * @param path The path to the YAML file.
     * @return The [Yaml] object.
     * @throws BadYamlException If the YAML is invalid.
     * @throws java.io.FileNotFoundException If the file does not exist.
     * @throws java.io.IOException If the file cannot be read.
     * @throws java.lang.IllegalArgumentException If the file is not found.
     */
    fun loadFromPath(path: String): app.lacabra.ragdrop.Yaml {
        return this.load(java.io.File(path).readText(Charsets.UTF_8))
    }

}