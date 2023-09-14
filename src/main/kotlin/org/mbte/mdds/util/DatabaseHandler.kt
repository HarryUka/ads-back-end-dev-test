package org.mbte.mdds.util

import org.mbte.mdds.tests.Contact
import java.sql.Connection
import java.sql.DriverManager

class DatabaseHandler(private val url: String) {
	init {
        // Register the SQLite JDBC driver
        Class.forName("org.sqlite.JDBC")
    }

    fun initContactsTable() {
        getConnection()?.use { connection ->
            val statement = connection.createStatement()
            val sql = """
                CREATE TABLE IF NOT EXISTS Contacts (
                    id TEXT PRIMARY KEY,
                    companyName TEXT,
                    name TEXT,
                    title TEXT,
                    address TEXT,
                    city TEXT,
                    email TEXT,
                    region TEXT,
                    zip TEXT,
                    country TEXT,
                    phone TEXT,
                    fax TEXT
                )
            """.trimIndent()
            statement.executeUpdate(sql)
        }
    }

    fun insertContact(contact: Contact) {
        getConnection()?.use { connection ->
            val sql = """
                INSERT INTO Contacts (id, companyName, name, title, address, city, email, region, zip, country, phone, fax)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()
            val statement = connection.prepareStatement(sql)
            statement.setString(1, contact.id)
            statement.setString(2, contact.companyName)
            statement.setString(3, contact.name)
            statement.setString(4, contact.title)
            statement.setString(5, contact.address)
            statement.setString(6, contact.city)
            statement.setString(7, contact.email)
            statement.setString(8, contact.region)
            statement.setString(9, contact.zip)
            statement.setString(10, contact.country)
            statement.setString(11, contact.phone)
            statement.setString(12, contact.fax)
            kotlin.runCatching { statement.executeUpdate() }
                .onFailure { 
                    System.err.println("Failed to execute SQL: $sql")
                    it.printStackTrace()
                }     
        }
    }
    
    fun getAllContacts(): List<Contact> {
		getConnection()?.use { connection ->
            val statement = connection.createStatement()
            val sql = "SELECT * FROM Contacts"
            val result = kotlin.runCatching { statement.executeQuery(sql) }
            return if (result.isFailure) {
                System.err.println("Failed to execute SQL: $sql")
                result.exceptionOrNull()?.printStackTrace()
                emptyList()
            } else {
                val contacts = mutableListOf<Contact>()
                while (result.getOrNull()?.next() == true) {
                    val contact = Contact(
                        id = result.getOrNull()?.getString("id") ?: "",
                        companyName = result.getOrNull()?.getString("companyName") ?: "",
                        name = result.getOrNull()?.getString("name") ?: "",
                        title = result.getOrNull()?.getString("title") ?: "",
                        address = result.getOrNull()?.getString("address") ?: "",
                        city = result.getOrNull()?.getString("city") ?: "",
                        email = result.getOrNull()?.getString("email") ?: "",
                        region = result.getOrNull()?.getString("region"),
                        zip = result.getOrNull()?.getString("zip"),
                        country = result.getOrNull()?.getString("country") ?: "",
                        phone = result.getOrNull()?.getString("phone") ?: "",
                        fax = result.getOrNull()?.getString("fax")
                    )
                    contacts.add(contact)
                }
                contacts
            }
        }
        return emptyList()
	}

    private fun getConnection(): Connection? {
        return try {
            DriverManager.getConnection(url)
        } catch (e: Exception) {
            System.err.println("Failed to get connection to SQLite!!")
            e.printStackTrace()
            null
        }
    }
}