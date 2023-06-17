# ragdrop
Verify the integrity of your YAML files with a simple JSON scheme

#### Maven package

Replace `x.y.z` with the latest version number: [![](https://jitpack.io/v/holasoyender/ragdrop.svg)](https://jitpack.io/#holasoyender/ragdrop)

* Repository: https://jitpack.io
* Artifact: **com.github.holasoyender:ragdrop:x.y.z**

Using in Gradle:
```gradle
repositories {
  maven {
    url 'https://jitpack.io'
  }
}

dependencies {
  implementation 'com.github.holasoyender:ragdrop:x.y.z'
}
```

Using in Maven:
```xml
<repositories>
  <repository>
    <id>jitpack</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.holasoyender</groupId>
    <artifactId>ragdrop</artifactId>
    <version>x.y.z</version>
  </dependency>
</dependencies>
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