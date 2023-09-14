package org.mbte.mdds.tests

import org.json.JSONArray
import org.json.JSONObject
import org.mbte.mdds.util.DatabaseHandler
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


fun main(args: Array<String>) {
	val test = ADSBackendTest1()
	val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
	val userHome = File(System.getProperty("user.home"))
	val time = dateFormatter.format(LocalDateTime.now()).replace(" ", "_").replace(":", ";")
	val dbFile = File(userHome, "${test.javaClass.simpleName}-$time.db")
	dbFile.createNewFile()
	val dbHandler = DatabaseHandler("jdbc:sqlite:${dbFile.absolutePath}")
	dbHandler.initContactsTable()

    val xmlInputStream = test.javaClass.classLoader.getResourceAsStream("ab.xml")
    val document = test.loadXml(xmlInputStream)
    if (document != null) {
        val addressBook = test.loadAddressBook(document)
        // Insert each contact into the Database
        for (contact in addressBook.contacts) {
            dbHandler.insertContact(contact)
        }
    } else {
        println("Failed to load XML.")
    }

    // Retrieve all contacts from the Database
    val contactsFromDB = dbHandler.getAllContacts()

    // Convert the contacts from the Database into Json
    val jsonContacts = JSONArray()
    for (contact in contactsFromDB) {
        val jsonContact = JSONObject()
        jsonContact.put("id", contact.id)
        jsonContact.put("companyName", contact.companyName)
        jsonContact.put("name", contact.name)
        jsonContact.put("title", contact.title)
        jsonContact.put("address", contact.address)
        jsonContact.put("city", contact.city)
        jsonContact.put("email", contact.email)
        jsonContact.put("region", contact.region)
        jsonContact.put("zip", contact.zip)
        jsonContact.put("country", contact.country)
        jsonContact.put("phone", contact.phone)
        jsonContact.put("fax", contact.fax)
        jsonContacts.put(jsonContact)
    }

    // Create a JSON object to hold the contacts
    val jsonOutput = JSONObject()
    jsonOutput.put("contacts", jsonContacts)

    // Print the JSON output to a file
    val outputJsonFile = File("output.json")
    test.printOutput(jsonOutput, outputJsonFile)

    println("Assessment complete.")
    println("Database file located at ${dbFile.absolutePath}")
    println("JSON output located at ${outputJsonFile.absolutePath}")
}

data class AddressBook(val contacts: List<Contact>)
data class Contact(
	val id: String, 
	val companyName: String, 
	val name: String, 
	val title: String, 
	val address: String, 
	val city: String, 
	val email: String,
	val region: String?,
	val zip: String?, 
	val country: String, 
	val phone: String, 
	val fax: String?
)

interface AddressBookInterface {
	fun loadXml(inputStream: InputStream): Document?
	fun loadAddressBook(doc: Document): AddressBook
	fun convertToJson(addressBook: AddressBook): JSONObject
	fun printOutput(json: JSONObject, output: File)
}

class ADSBackendTest1(): AddressBookInterface {
	override fun loadXml(inputStream: InputStream): Document? {
        val docBuilderFactory = DocumentBuilderFactory.newInstance()
        val docBuilder: DocumentBuilder
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder()
            return docBuilder.parse(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
	}
    override fun loadAddressBook(doc: Document): AddressBook {
		val contactList = mutableListOf<Contact>()
        val root = doc.documentElement
        val contactElements = root.getElementsByTagName("Contact")
        for (i in 0 until contactElements.length) {
            val contactElement = contactElements.item(i) as Element
            val id = contactElement.getElementsByTagName("CustomerID").item(0).textContent
            val companyName = contactElement.getElementsByTagName("CompanyName").item(0).textContent
            val name = contactElement.getElementsByTagName("ContactName").item(0).textContent
            val title = contactElement.getElementsByTagName("ContactTitle").item(0).textContent
            val address = contactElement.getElementsByTagName("Address").item(0).textContent
            val city = contactElement.getElementsByTagName("City").item(0).textContent
            val email = contactElement.getElementsByTagName("Email").item(0).textContent
            val region = contactElement.getElementsByTagName("PostalCode").item(0)?.textContent
            val zip = contactElement.getElementsByTagName("PostalCode").item(0)?.textContent
            val country = contactElement.getElementsByTagName("Country").item(0).textContent
            val phone = contactElement.getElementsByTagName("Phone").item(0).textContent
            val fax = contactElement.getElementsByTagName("Fax").item(0)?.textContent

            val contact = Contact(
                id, companyName, name, title, address, city, email, region, zip, country, phone, fax
            )
            contactList.add(contact)
        }
        return AddressBook(contactList)
	}

	override fun convertToJson(addressBook: AddressBook): JSONObject {
		val jsonContacts = JSONArray()
        for (contact in addressBook.contacts) {
            val jsonContact = JSONObject()
            jsonContact.put("id", contact.id)
            jsonContact.put("companyName", contact.companyName)
            jsonContact.put("name", contact.name)
            jsonContact.put("title", contact.title)
            jsonContact.put("address", contact.address)
            jsonContact.put("city", contact.city)
            jsonContact.put("email", contact.email)
            jsonContact.put("region", contact.region)
            jsonContact.put("zip", contact.zip)
            jsonContact.put("country", contact.country)
            jsonContact.put("phone", contact.phone)
            jsonContact.put("fax", contact.fax)
            jsonContacts.put(jsonContact)
        }

        val jsonObject = JSONObject()
        jsonObject.put("contacts", jsonContacts)
        return jsonObject
	}

	override fun printOutput(json: JSONObject, output: File) {
		try {
            Files.write(output.toPath(), json.toString(4).toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
	}

}