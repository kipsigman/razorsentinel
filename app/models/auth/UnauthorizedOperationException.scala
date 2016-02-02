package models.auth

/**
 * Exception thrown when a User attempts an operation they do not have Authorization for.
 */
class UnauthorizedOperationException(message: String) extends RuntimeException(message)