# ragdrop
Verify the integrity of your YAML files with a simple JSON scheme

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
    "type": "number[1..100]",
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
        "type": "string[3..50]",
        "required": true,
        "description": "City of the house"
      },
      "state": {
        "type": "string[2..50]",
        "required": true,
        "description": "State of the house"
      },
      "zip": {
        "type": "number[10000..99999]",
        "required": true,
        "description": "Zip code of the house"
      }
    }
  }
}
```

Now we can verify the integrity of the YAML file with the following code:

```java
import app.lacabra.ragdrop.Schema;
import app.lacabra.ragdrop.Yaml;

public class Main {

    public static void main(String[] args) {

        Schema schema = new Schema("path/to/verification/scheme.json"); // Import the verification scheme
        Yaml yaml = new Yaml().loadFromPath("path/to/yaml/file.yaml"); // Import the YAML file
        
        if (schema.verify()) { // Verify the integrity of the scheme
            try {
                schema.validate(yaml); // Verify the integrity of the YAML file compared to the scheme
            } catch (Exception e) {
                System.out.println("The YAML file is invalid: " + e.getMessage());
            }
        } else {
            System.out.println("The verification scheme is invalid: " + schema.getErrorMessage());
        }


    }
}
```


## Custom types
ragdrop supports custom types. You can create your own types and use them in your verification schemes. For example, let's imagine we want to create a type called `email` that verifies that the value is a valid email address. We can do it like this:

```java
/* FILE: EmailType.java */
import app.lacabra.ragdrop.Type;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailType implements Type {

    EmailType(String value) {
        // This method is not used in this example
    }

    @Override
    public boolean verify() {
        // This method is not used in this example
        return true;
    }

    @Override
    public boolean validate(@NotNull String value) {
        // Here you should compare the value with an email pattern and return true if it matches
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }


    @NotNull
    public static EmailType create(@NotNull String value) {
        return new EmailType(value);
    }

    @Override
    public void withTypes(@NotNull Map<String, ? extends Function1<? super String, ? extends Type>> types) {
        // This method is not used in this example
    }
}
```

Then, we need to register the type in the `Importer`:

```java
import app.lacabra.ragdrop.Schema;
import app.lacabra.ragdrop.Yaml;
import app.lacabra.ragdrop.exceptions.BadYamlException;

public class Main {

    public static void main(String[] args) {

        Schema schema = new Schema("path/to/verification/scheme.json"); // Import the verification scheme
        Yaml yaml = new Yaml().loadFromPath("path/to/yaml/file.yaml"); // Import the YAML file
        
        schema.addType(EmailType::create); // Register the type

        if (schema.verify()) { // Verify the integrity of the scheme
            try {
                schema.validate(yaml); // Verify the integrity of the YAML file compared to the scheme
            } catch (Exception e) {
                System.out.println("The YAML file is invalid: " + e.getMessage());
            }
        } else {
            System.out.println("The verification scheme is invalid: " + schema.getErrorMessage());
        }


    }
}
```