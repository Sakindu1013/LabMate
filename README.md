# 🧪 LabMate

**LabMate** is an Android-based laboratory equipment management system designed to simplify the management, borrowing, returning, and tracking of laboratory equipment.

The system provides different functionalities for **Administrators, Laboratory Staff, and Students**, allowing laboratory equipment and borrowing activities to be managed efficiently through a centralized mobile application.

---

## ✨ Features

### 🔐 Authentication

* Email and password authentication
* Google Sign-In
* Automatic account creation for new Google users
* Secure user session management

### 👥 Role-Based Access Control

LabMate supports three user roles:

* **Admin** – Manage users, equipment, laboratories, and borrowing activities
* **Staff** – Manage laboratory equipment and borrowing requests
* **Student** – Browse equipment and submit borrowing requests

### 🧪 Laboratory Management

* View available laboratories
* View laboratory details
* Manage laboratory information

### 📦 Equipment Management

* Add laboratory equipment
* Update equipment information
* Remove equipment
* View equipment details
* Manage equipment availability and status
* Search and manage laboratory inventory

### 📱 QR Code Integration

* Scan equipment QR codes
* Use QR codes during borrowing and returning workflows
* Quickly identify equipment without manually entering equipment information

### 📋 Borrowing Requests

Students can submit requests to borrow equipment.

Staff and administrators can:

* View borrowing requests
* Approve requests
* Reject requests
* Automatically handle requests when equipment becomes unavailable

The system also prevents conflicting borrowing requests for the same equipment.

### 🔄 Borrowing & Returning

* Borrow equipment
* Return equipment
* Track current borrowing records
* Automatically update equipment status
* Maintain borrowing history

### 📜 Borrowing History

Users can view meaningful borrowing records including:

* Equipment information
* Borrowing details
* Return information
* Borrowing status

### 👤 User Profiles

* View user information
* Manage profile details
* Display role-based information

### ⏳ Loading & UI Feedback

* Full-screen loading overlays for initial data loading
* Loading feedback during important operations
* Toast messages for errors and operation results
* Silent data refresh when returning to screens

---

## 🏗️ Architecture

LabMate follows the **MVVM (Model–View–ViewModel)** architecture.

```text
UI / Activities / Fragments
            │
            ▼
       ViewModels
            │
            ▼
       Repositories
            │
            ▼
   Firebase Authentication
            +
        Firestore
```

### Main Components

**Activities / Fragments**

* Handle user interaction
* Display application UI
* Observe ViewModel state

**ViewModels**

* Manage UI-related data
* Handle application logic
* Expose loading, success, and error states

**Repositories**

* Handle communication with Firebase
* Separate data access from UI logic

**Models**

* Represent application data such as users, equipment, borrowing records, and borrowing requests

---

## 🛠️ Technologies Used

| Technology              | Purpose                            |
| ----------------------- | ---------------------------------- |
| Java                    | Application development            |
| Android Studio          | Android development environment    |
| Android SDK             | Android application framework      |
| Firebase Authentication | User authentication                |
| Firebase Firestore      | Cloud database                     |
| MVVM                    | Application architecture           |
| Android Jetpack         | Lifecycle and ViewModel components |
| Material Design         | User interface components          |
| RecyclerView            | Dynamic lists                      |
| QR Code Scanner         | Equipment identification           |
| SharedPreferences       | Local user session management      |

---

## 📱 Application Structure

```text
LabMate
│
├── Authentication
│   ├── Login
│   ├── Registration
│   └── Google Sign-In
│
├── Dashboard
│
├── Laboratories
│   ├── Laboratory List
│   └── Laboratory Details
│
├── Equipment
│   ├── Equipment List
│   ├── Equipment Details
│   ├── Add Equipment
│   ├── Update Equipment
│   └── Remove Equipment
│
├── Borrowing
│   ├── Borrow Equipment
│   ├── Return Equipment
│   ├── Borrowing Requests
│   └── Borrowing History
│
├── QR Scanning
│
├── User Management
│
└── Profile
```

---

## 🔄 Borrowing Request Workflow

The borrowing request system follows a controlled workflow:

```text
Student
   │
   ▼
Select Equipment
   │
   ▼
Submit Borrowing Request
   │
   ▼
Staff / Admin Reviews Request
   │
   ├───────────────┐
   ▼               ▼
Approve          Reject
   │               │
   ▼               ▼
Borrowing       Request
Created         Rejected
   │
   ▼
Equipment Status Updated
```

If equipment becomes unavailable while requests are pending, the relevant requests are automatically handled to prevent conflicting borrowing operations.

---

## 🔥 Firebase

LabMate uses Firebase for backend services.

### Firebase Authentication

Used for:

* Email/password authentication
* Google authentication
* User identity management

### Cloud Firestore

Used to store application data such as:

* Users
* Laboratories
* Equipment
* Borrowing requests
* Borrowing records

---

## 🚀 Getting Started

### Prerequisites

Before running the project, install:

* Android Studio
* Android SDK
* JDK compatible with the project's Gradle configuration
* A Firebase project

### Installation

1. Clone the repository:

```bash
git clone https://github.com/Sakindu1013/LabMate.git
```

2. Open the project in **Android Studio**.

3. Create or connect a Firebase project.

4. Add the Android application to Firebase using the application's package name.

5. Download the Firebase configuration file:

```text
google-services.json
```

6. Place it inside:

```text
app/
```

7. Enable the required Firebase services:

   * Firebase Authentication
   * Google Sign-In
   * Cloud Firestore

8. Configure the required Firestore collections and security rules.

9. Sync the Gradle project.

10. Build and run the application on an Android device or emulator.

---

## 🔒 Security

The application uses Firebase Authentication for user authentication and Firestore security rules for controlling database access.

Role-based functionality is implemented for:

```text
Admin
Staff
Student
```

Users are provided access to functionality according to their assigned role.

> **Note:** Never commit sensitive Firebase credentials, API keys, signing keys, or other private configuration files to a public repository.

---

## 🎯 Project Objectives

The main objectives of LabMate are to:

* Digitize laboratory equipment management
* Reduce manual equipment tracking
* Simplify borrowing and returning processes
* Improve equipment availability tracking
* Provide controlled access based on user roles
* Reduce conflicts between borrowing requests
* Provide an efficient way to identify equipment using QR codes
* Maintain a centralized record of borrowing activities

---

## 📚 Project Status

**Status: ✅ Completed**

LabMate has been fully implemented and tested, with the major application workflows functioning correctly.

---

## 👨‍💻 Developer

**Sakindu Dinsara**

BSc Honours in Electronics and Information Technology
University of Colombo

---

## 📄 License

This project was developed as an academic/software engineering project.

If you intend to make the project publicly available for reuse, add an appropriate open-source license such as the MIT License.

---

⭐ If you find this project useful, consider giving the repository a star!
