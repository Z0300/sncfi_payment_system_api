package com.sncfi.payment_system.exception

class ResourceNotFoundException(message: String) : RuntimeException(message)
class InsufficientBalanceException(message: String) : RuntimeException(message)
class InvalidPaymentException(message: String) : RuntimeException(message)
class AccessDeniedForResourceException(message: String) : RuntimeException(message)