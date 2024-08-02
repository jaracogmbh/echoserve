package de.jaraco.exception

class NoConfigDirectoryGivenException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
}