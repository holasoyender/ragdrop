# ragdrop
Verify the integrity of your YAML files with a simple JSON scheme

# ⚠️⚠️ NOT READY YET ⚠️⚠️
```This is currently in heavy development and is not ready for production use.```

#### Maven package

Replace `x.y.z` with the latest version number: https://github.com/holasoyender/ragdrop/packages

* Repository: https://lacabra.app
* Artifact: **app.lacabra:ragdrop:x.y.z**

Using in Gradle:
```gradle
dependencies {
  implementation 'app.lacabra:ragdrop:x.y.z'
}
```

Using in Maven:
```xml
<dependency>
  <groupId>app.lacabra</groupId>
  <artifactId>ragdrop</artifactId>
  <version>x.y.z</version>
</dependency>
```

#### Usage

This is a simple example of how to use the library. Let's imagine we have a YAML file with the following content:

```yaml
name: John Doe
age: 30
house:
  address: 123 Main Street
  city: Springfield
  state: NY
  zip: 12345
```

Our verification scheme will be a JSON file with the following content:

```json
{
  "name": {
    "type": "string",
    "required": true,
    "description": "Name of the person"
  },
  "age": {
    "type": "number",
    "required": false,
    "default": 18,
    "description": "Age of the person"
  },
  "house": {
    "type": "map",
    "required": true,
    "description": "Information about the house of the person",
    "object": {
      "address": {
        "type": "string",
        "required": true,
        "description": "Address of the house"
      },
      "city": {
        "type": "string",
        "required": true,
        "description": "City of the house"
      },
      "state": {
        "type": "string",
        "required": true,
        "description": "State of the house"
      },
      "zip": {
        "type": "number",
        "required": true,
        "description": "Zip code of the house"
      }
    }
  }
}
```

Now we can verify the integrity of the YAML file with the following code:

```kotlin
import app.lacabra.ragdrop.Importer
import app.lacabra.ragdrop.Yaml

fun main() {

    val schema = Importer("path/to/verification/scheme.json") // Import the verification scheme
    
    if (schema.verify()) { // Verify the integrity of the scheme
        val yaml = Yaml("path/to/yaml/file.yaml") // Import the YAML file
        if (yaml.verify(schema)) { // Verify the integrity of the YAML file compared to the scheme
            println("The YAML file is valid")
        } else {
            println("The YAML file is invalid")
        }
    }
}
```


## Custom types
ragdrop supports custom types. You can create your own types and use them in your verification schemes. For example, let's imagine we want to create a type called `email` that verifies that the value is a valid email address. We can do it like this:

```kotlin
/* FILE: EmailType.kt */

import app.lacabra.ragdrop.Type
import app.lacabra.ragdrop.TypeFactory

class EmailType(
    private val value: String
): Type {

    private var verified = false
    private var valid = false

    init {
        verify()
    }

    override fun verify(): Boolean {

        if (verified) return valid
        verified = true

        // Verify that type specified in the scheme is correct, for example if you want to add a requirement like
        // 'email[<domain>]' you should verify that. You can see an example of this in the 'types/String.kt' file

        valid = true
        return true
    }

    override fun validate(value: String): Boolean {
    
        // Verify that the value in the YAML file is correct
        // Here you should compare the input value with a regular expression or something like that
        
        return true
    }

    companion object : TypeFactory {

        override val name = "email"
        override fun create(value: String): Type = EmailType(value)

    }
}
```

Then, we need to register the type in the `Importer`:

```kotlin
import app.lacabra.ragdrop.Importer
import app.lacabra.ragdrop.Yaml

fun main() {

    val schema = Importer("path/to/verification/scheme.json") // Import the verification scheme
    
    schema.addType("email", EmailType::create) // Register the type 'email' in the schema
    
    if (schema.verify()) { // Verify the integrity of the scheme
        val yaml = Yaml("path/to/yaml/file.yaml") // Import the YAML file
        if (yaml.verify(schema)) { // Verify the integrity of the YAML file compared to the scheme
            println("The YAML file is valid")
        } else {
            println("The YAML file is invalid")
        }
    }
}
```