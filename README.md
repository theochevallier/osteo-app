# osteo-app
Java project implementing an osteo app (to manage patient, consultations and invoices). Technologies used : Java, JavaFx (front), Maven, postgreSQL (local database), hibernate (ORM). Only for Mac apple chip setup for the moment.

# Ostheo

Desktop application for managing an osteopathy practice, built with Java and JavaFX.

The application provides a local solution for managing **patients, consultations and invoices**, with data stored in a local PostgreSQL database.

> **Current status:** macOS Apple Silicon (ARM64) only.

## Features

* **Patient management**

  * Create and update patient records
  * Store contact and medical information
  * Archive patients
  * Search and filter patients

* **Consultation management**

  * Create and manage consultations
  * Associate consultations with patients
  * Store consultation-related information and attachments

* **Invoice management**

  * Create and manage invoices
  * Generate invoices as PDF documents
  * Associate invoices with patients and consultations

* **Data management**

  * Export data to Excel
  * Local PostgreSQL database
  * Database management through Hibernate ORM

* **Security**

  * Password hashing
  * Local data storage

* **Logging**

  * Application logging with SLF4J and Log4j 2

## Tech Stack

### Application

* **Java 21**
* **JavaFX 21**
* **Maven**
* **FXML**
* **Hibernate ORM**
* **Jakarta Persistence (JPA)**

### Database

* **PostgreSQL**
* **Hibernate ORM**

### UI

* **JavaFX**
* **FormsFX**
* **ValidatorFX**
* **Ikonli**
* **BootstrapFX**

### PDF & Documents

* **iText**
* **Thymeleaf**
* **Flying Saucer**

Used for generating PDF invoices from HTML/templates.

### Data Export

* **Apache POI**

Used for exporting application data to Excel files.

### Logging

* **SLF4J**
* **Log4j 2**

### Testing

* **JUnit 5**

## Requirements

Before running the application, make sure you have:

* macOS on **Apple Silicon (ARM64)**
* **Java 21**
* **Maven**
* **PostgreSQL**

The current JavaFX dependencies are configured specifically for Apple Silicon:

```xml
<classifier>mac-aarch64</classifier>
```

Therefore, the project is currently not configured for Intel macOS or Windows/Linux.

## Database

The application uses a **local PostgreSQL database**.

Database configuration is handled locally by the application. Make sure PostgreSQL is installed and running before launching the application.

## Installation

Clone the repository:

```bash
git clone https://github.com/<your-username>/<your-repository>.git
cd <your-repository>
```

Install the Maven dependencies and build the project:

```bash
mvn clean install
```

Then launch the application from your IDE or using the configured Maven/JavaFX launch configuration.

## Project Structure

The project follows a layered architecture separating the UI, business logic and persistence layers.

```text
src/
├── main/
│   ├── java/
│   │   └── ...
│   └── resources/
│       ├── fxml/
│       ├── css/
│       └── ...
└── test/
    └── ...
```

## Architecture

The application is built around a JavaFX desktop architecture with:

* **FXML** for UI definitions
* **Controllers** for UI interactions
* **Services** for business logic
* **DAOs** for database access
* **Hibernate/JPA** for persistence
* **PostgreSQL** for local data storage

This separation keeps the UI, business logic and persistence layers independent and easier to maintain.

## Project Goals

This project was developed as a desktop application for an osteopathy practice, with a focus on:

* Local data storage
* Maintainability
* Type-safe persistence
* A simple desktop user interface
* PDF document generation
* Data export
* Extensibility

## Current Limitations

* macOS Apple Silicon only
* PostgreSQL must currently be installed locally
* Desktop application only
* No remote/cloud database support

## License

This project is currently intended for private use.
