# Εφαρμογή Διαχείρισης Ακινήτων

## Περιγραφή
Η εφαρμογή αυτή είναι μια πλατφόρμα διαχείρισης ακινήτων που επιτρέπει σε ιδιοκτήτες να καταχωρούν τα ακίνητά τους και σε ενοικιαστές να τα αναζητούν.

## Προαπαιτούμενα
Για να τρέξετε την εφαρμογή χρειάζεστε:
- Java JDK 17 ή νεότερη έκδοση
- Maven
- MySQL Server
- Git (προαιρετικό, για κλωνοποίηση του repository)

## Οδηγίες Εγκατάστασης

### 1. Λήψη του Κώδικα
```bash
git clone https://github.com/AnastasisAndrik/rentalapp2025.git
cd rentalapp2025
```

### 2. Ρύθμιση Βάσης Δεδομένων
1. Δημιουργήστε μια νέα βάση δεδομένων MySQL
```sql
CREATE DATABASE rentalapp;
```

2. Ανοίξτε το αρχείο `src/main/resources/application.properties` και προσαρμόστε τις ρυθμίσεις της βάσης:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rentalapp
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Εγκατάσταση Εξαρτήσεων και Μεταγλώττιση
```bash
mvn clean install
```

## Εκτέλεση της Εφαρμογής

### Εκτέλεση μέσω Maven
```bash
mvn spring-boot:run
```

### Εκτέλεση του JAR αρχείου
```bash
java -jar target/rentalapp-0.0.1-SNAPSHOT.jar
```

Μετά την εκτέλεση, η εφαρμογή θα είναι διαθέσιμη στη διεύθυνση:
http://localhost:8080

## Χρήση της Εφαρμογής

### Διαθέσιμοι Ρόλοι
1. **Διαχειριστής (Admin)**
   - Πρόσβαση στο dashboard διαχείρισης
   - Διαχείριση χρηστών
   - Εποπτεία όλων των ακινήτων

2. **Ιδιοκτήτης (Landlord)**
   - Διαχείριση των ακινήτων του
   - Προσθήκη/επεξεργασία ακινήτων
   - Προβολή αιτημάτων ενοικίασης

3. **Ενοικιαστής (Tenant)**
   - Αναζήτηση ακινήτων
   - Προβολή διαθέσιμων ακινήτων
   - Διαχείριση του προφίλ του


