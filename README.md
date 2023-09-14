# AddressBookApplication
This is a Kotlin application for managing an address book. It loads contact information from an XML file, stores it in an SQLite database, and provides functionality to retrieve and export contacts in JSON format.

## Getting Started

These instructions will help you set up and run the application on your local machine.

### Prerequisites

To run this application, you will need:

- Java Development Kit (JDK) installed
- Kotlin Compiler (Kotlinc) installed
- SQLite

### Installing

1. Clone the repository to your local machine.
```bash
git clone https://github.com/HarryUka/ads-back-end-dev-test.git
2. Compile the Kotlin source files.
```bash
kotlinc src -include-runtime -d address-book.jar

3. Run the application.
```bash
java -jar address-book.jar

### Usage
The application will load the ab.xml file located in the resources directory.

Contact information will be inserted into an SQLite database.

You can retrieve all contacts from the database, convert them to JSON, and save the output in output.json.
