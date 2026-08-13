package com.sncfi.payment_system.entity

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

/**
 * Common id + equals/hashCode for all entities.
 *
 * Deliberately NOT a `data class`: JPA entities shouldn't use data-class
 * equals/hashCode, since that compares every property (including lazy
 * associations, which triggers unwanted DB fetches and breaks equality
 * before an entity has an id). Equality here is id-based only, and two
 * transient (unsaved) entities are never equal to each other.
 */
@MappedSuperclass
abstract class BaseEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity) return false
        if (this::class != other::class) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String = "${javaClass.simpleName}(id=$id)"
}
