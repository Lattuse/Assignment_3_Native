package org.bonfire.lattuse


interface AccessPolicy {
    fun canAccess(person: Person, facility: Facility): Boolean
}

// base class
abstract class Person(
    val id: Int,
    val name: String,
    val role: String
) {
    abstract fun getAccessLevel(): Int

    override fun toString(): String {
        return "$role(name=$name, id=$id)"
    }
}

// subclasses
class Student(
    id: Int,
    name: String,
    val group: String
) : Person(id, name, "Student") {

    override fun getAccessLevel(): Int = 1
}

class Lecturer(
    id: Int,
    name: String,
    val department: String
) : Person(id, name, "Lecturer") {

    override fun getAccessLevel(): Int = 2
}

class Staff(
    id: Int,
    name: String,
    val position: String
) : Person(id, name, "Staff") {

    override fun getAccessLevel(): Int = 3
}

// facilities
open class Facility(
    val name: String,
    private val requiredAccessLevel: Int
) {
    fun getRequiredAccessLevel(): Int = requiredAccessLevel

    override fun toString(): String {
        return "Facility(name=$name)"
    }
}

class Building(name: String) : Facility(name, 1)
class Room(name: String) : Facility(name, 2)
class Laboratory(name: String) : Facility(name, 3)

// access policy
class LevelBasedAccessPolicy : AccessPolicy {
    override fun canAccess(person: Person, facility: Facility): Boolean {
        return person.getAccessLevel() >= facility.getRequiredAccessLevel()
    }
}

// access manager (composition)
class AccessManager(private val policy: AccessPolicy) {

    private val revokedAccess = mutableSetOf<Pair<Int, String>>()

    fun grantAccess(person: Person, facility: Facility): Boolean {
        if (revokedAccess.contains(person.id to facility.name)) {
            return false
        }
        return policy.canAccess(person, facility)
    }

    fun revokeAccess(person: Person, facility: Facility) {
        revokedAccess.add(person.id to facility.name)
    }
}


fun main() {
    val student = Student(1, "Alice", "SE-2422")
    val lecturer = Lecturer(2, "Dr. Bob", "Computer Science")
    val staff = Staff(3, "Charlie", "Security")

    val building = Building("Main Building")
    val room = Room("Lecture Room 101")
    val lab = Laboratory("AI Laboratory")

    val accessManager = AccessManager(LevelBasedAccessPolicy())

    val people = listOf(student, lecturer, staff)
    val facilities = listOf(building, room, lab)

    for (person in people) {
        for (facility in facilities) {
            val result = accessManager.grantAccess(person, facility)
            println("${person.name} -> ${facility.name}: $result")
        }
        println()
    }

    println("Revoking Staff access to AI Laboratory...")
    accessManager.revokeAccess(staff, lab)
    println("Staff -> AI Laboratory: ${accessManager.grantAccess(staff, lab)}")
}
