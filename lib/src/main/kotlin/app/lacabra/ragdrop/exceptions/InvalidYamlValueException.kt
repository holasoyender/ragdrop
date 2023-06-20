package app.lacabra.ragdrop.exceptions

class InvalidYamlValueException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}