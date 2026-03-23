<div align="center">

<br/>

```
 _      ___ ____  ____      _    ______   __  __  ____
| |    |_ _| __ )|  _ \    / \  |  _ \ \ / / |  \/  |/ ___|
| |     | ||  _ \| |_) |  / _ \ | |_) \ V /  | |\/| \___ \
| |___  | || |_) |  _ <  / ___ \|  _ < | |   | |  | |___) |
|_____|___|____/|_| \_\/_/   \_\_| \_\|_|   |_|  |_|____/
```

# 📚 Library Management System

**A fully-featured Java Desktop Application for managing libraries — built with Java Swing GUI and developed in IntelliJ IDEA.**

<br/>

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com)
[![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white)](https://www.jetbrains.com/idea/)
[![Swing GUI](https://img.shields.io/badge/GUI-Java%20Swing-blue?style=for-the-badge)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=for-the-badge)](http://makeapullrequest.com)

<br/>

[🐛 Report Bug](../../issues) · [✨ Request Feature](../../issues) · [📖 Wiki](../../wiki)

---

</div>

## 📋 Table of Contents

- [📖 Description](#-description)
- [✨ Features](#-features)
- [🖥️ Screenshots](#️-screenshots)
- [🛠️ Tech Stack](#️-tech-stack)
- [🚀 Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation & Run](#installation--run)
- [📁 Project Structure](#-project-structure)
- [🗄️ Database Schema](#️-database-schema)
- [👤 User Roles](#-user-roles)
- [🤝 Contributing](#-contributing)
- [🗺️ Roadmap](#️-roadmap)
- [📜 License](#-license)
- [📬 Contact](#-contact)

---

## 📖 Description

**Library Management System (LMS)** is a desktop application built entirely in **Java** using **Java Swing** for the graphical user interface, developed with **IntelliJ IDEA**. It is designed to digitize and automate the day-to-day operations of any library — whether a school library, university library, or public reading center.

The system replaces manual, paper-based processes with a clean, intuitive desktop interface that allows librarians and administrators to efficiently manage books, members, borrowing transactions, and overdue fines — all from one application running locally on their machine.

### 🎯 Why This System?

Managing a library manually — tracking which books are available, who borrowed what, and when things are due — is time-consuming and error-prone. This system was built to solve exactly that:

| Without LMS | With LMS |
|-------------|----------|
| Paper-based book records | Digital searchable catalog |
| Manual member registration | Instant member profiles |
| Hard-to-track borrow dates | Automatic due date calculation |
| Missed fine collections | Auto-calculated overdue fines |
| No reporting or statistics | Built-in dashboard & reports |

Whether you're managing 500 books or 50,000, this system keeps everything organized, searchable, and accurate.

---

## ✨ Features

### 📚 Book Catalog Management
- Add, edit, and delete book records
- Store title, author, ISBN, publisher, year, category, and quantity
- Search books by title, author, ISBN, or category
- Track total copies vs. available copies in real time
- Mark books as available, borrowed, or reserved

### 👥 Member Management
- Register new library members with full profile info
- Assign unique member IDs automatically
- View complete borrowing history per member
- Activate or deactivate member accounts
- Search members by name, ID, or contact details

### 🔄 Borrow & Return System
- Issue books to members with one click
- Automatic due date calculation based on library policy
- Record return transactions and update availability instantly
- View all currently borrowed books at a glance
- Flag overdue books automatically

### 💰 Fine & Penalty System
- Automatically calculate overdue fines by day
- View outstanding fines per member
- Mark fines as paid and generate payment records
- Configure fine rate per day from settings

### 📊 Dashboard & Reports
- Summary dashboard: total books, members, active borrows, overdue items
- Generate reports: most borrowed books, active members, overdue list
- Filter and export data views

### 🔐 Authentication & Security
- Admin login with username and password
- Session management (auto logout on close)
- Role-based access (Admin / Librarian)

### ⚙️ Settings & Configuration
- Configure library name, address, and contact info
- Set borrow duration limits
- Set fine rate per overdue day
- Backup and restore database

---

## 🖥️ Screenshots

> 📌 *Replace placeholders below with actual screenshots from your application.*

| Login Screen | Main Dashboard |
|:------------:|:--------------:|
| ![Login](https://via.placeholder.com/420x280?text=Login+Screen) | ![Dashboard](https://via.placeholder.com/420x280?text=Main+Dashboard) |

| Book Catalog | Add New Book |
|:------------:|:------------:|
| ![Books](https://via.placeholder.com/420x280?text=Book+Catalog) | ![Add Book](https://via.placeholder.com/420x280?text=Add+New+Book) |

| Member Management | Borrow & Return |
|:-----------------:|:---------------:|
| ![Members](https://via.placeholder.com/420x280?text=Member+Management) | ![Borrow](https://via.placeholder.com/420x280?text=Borrow+%26+Return) |

| Fine Management | Reports |
|:---------------:|:-------:|
| ![Fines](https://via.placeholder.com/420x280?text=Fine+Management) | ![Reports](https://via.placeholder.com/420x280?text=Reports+View) |

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 17+ |
| **GUI Framework** | Java Swing (JFrame, JPanel, JTable, JDialog) |
| **IDE** | IntelliJ IDEA |
| **Database** | SQLite (embedded) / MySQL |
| **DB Connector** | JDBC |
| **Build Tool** | Maven / Gradle |
| **Architecture** | MVC (Model - View - Controller) |

> ⚠️ *Update the Database row to match what you actually used (SQLite or MySQL).*

---

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed on your machine:

- ☕ **Java JDK 17+**
  ```
  https://www.oracle.com/java/technologies/downloads/
  ```
- 💡 **IntelliJ IDEA** (Community or Ultimate)
  ```
  https://www.jetbrains.com/idea/download/
  ```
- 🗄️ **MySQL** *(if using MySQL instead of SQLite)*
  ```
  https://dev.mysql.com/downloads/installer/
  ```

Verify Java is installed:
```bash
java -version
# Should show: java version "17.x.x" or higher
```

---

### Installation & Run

**1. Clone the repository**

```bash
git clone https://github.com/your-username/library-management-system.git
```

**2. Open in IntelliJ IDEA**

```
File → Open → Select the project folder → Click OK
```

**3. Configure the database**

- If using **SQLite**: The `.db` file is included — no setup needed.
- If using **MySQL**:
  - Create a new database:
    ```sql
    CREATE DATABASE library_db;
    ```
  - Import the schema:
    ```bash
    mysql -u root -p library_db < database/schema.sql
    ```
  - Update `src/config/DBConnection.java`:
    ```java
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password";
    ```

**4. Add JDBC Driver (if not already in pom.xml)**

For Maven (`pom.xml`):
```xml
<!-- MySQL -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- SQLite -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.43.0.0</version>
</dependency>
```

**5. Build and Run**

```
In IntelliJ: Run → Run 'Main'
```

Or from terminal:
```bash
mvn clean install
java -jar target/library-management-system.jar
```

**6. Default Login Credentials**

```
Username: admin
Password: admin123
```

> ⚠️ *Change the default password after first login.*

---

## 📁 Project Structure

```
library-management-system/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── config/
│   │   │   │   └── DBConnection.java         # Database connection handler
│   │   │   │
│   │   │   ├── model/                        # Data models (POJOs)
│   │   │   │   ├── Book.java
│   │   │   │   ├── Member.java
│   │   │   │   ├── BorrowRecord.java
│   │   │   │   ├── Fine.java
│   │   │   │   └── User.java
│   │   │   │
│   │   │   ├── dao/                          # Database Access Objects
│   │   │   │   ├── BookDAO.java
│   │   │   │   ├── MemberDAO.java
│   │   │   │   ├── BorrowDAO.java
│   │   │   │   ├── FineDAO.java
│   │   │   │   └── UserDAO.java
│   │   │   │
│   │   │   ├── view/                         # Swing GUI panels & frames
│   │   │   │   ├── LoginFrame.java
│   │   │   │   ├── MainFrame.java
│   │   │   │   ├── DashboardPanel.java
│   │   │   │   ├── BookPanel.java
│   │   │   │   ├── MemberPanel.java
│   │   │   │   ├── BorrowPanel.java
│   │   │   │   ├── FinePanel.java
│   │   │   │   ├── ReportsPanel.java
│   │   │   │   └── SettingsPanel.java
│   │   │   │
│   │   │   ├── controller/                   # Business logic controllers
│   │   │   │   ├── BookController.java
│   │   │   │   ├── MemberController.java
│   │   │   │   ├── BorrowController.java
│   │   │   │   ├── FineController.java
│   │   │   │   └── AuthController.java
│   │   │   │
│   │   │   └── Main.java                     # Application entry point
│   │   │
│   │   └── resources/
│   │       ├── images/                       # Icons and images
│   │       └── styles/                       # UI styling constants
│   │
│   └── test/                                 # Unit tests
│       └── java/
│
├── database/
│   ├── schema.sql                            # Database schema
│   └── seed.sql                              # Sample data (optional)
│
├── docs/
│   └── screenshots/                          # App screenshots
│
├── pom.xml                                   # Maven config
└── README.md
```

---

## 🗄️ Database Schema

### `books` table
```sql
CREATE TABLE books (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    title        VARCHAR(255) NOT NULL,
    author       VARCHAR(255) NOT NULL,
    isbn         VARCHAR(20) UNIQUE,
    publisher    VARCHAR(255),
    year         INT,
    category     VARCHAR(100),
    total_copies INT DEFAULT 1,
    available    INT DEFAULT 1,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### `members` table
```sql
CREATE TABLE members (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) UNIQUE,
    phone      VARCHAR(20),
    address    TEXT,
    joined_at  DATE,
    status     ENUM('active', 'inactive') DEFAULT 'active'
);
```

### `borrow_records` table
```sql
CREATE TABLE borrow_records (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    book_id     INT NOT NULL,
    member_id   INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date    DATE NOT NULL,
    return_date DATE,
    status      ENUM('borrowed', 'returned', 'overdue') DEFAULT 'borrowed',
    FOREIGN KEY (book_id)   REFERENCES books(id),
    FOREIGN KEY (member_id) REFERENCES members(id)
);
```

### `fines` table
```sql
CREATE TABLE fines (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    borrow_id  INT NOT NULL,
    amount     DECIMAL(10,2) NOT NULL,
    paid       BOOLEAN DEFAULT FALSE,
    paid_at    TIMESTAMP,
    FOREIGN KEY (borrow_id) REFERENCES borrow_records(id)
);
```

### `users` table
```sql
CREATE TABLE users (
    id       INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     ENUM('admin', 'librarian') DEFAULT 'librarian'
);
```

---

## 👤 User Roles

| Role | Permissions |
|------|------------|
| 🔑 **Admin** | Full access — manage books, members, fines, users, settings, and reports |
| 📚 **Librarian** | Manage books, members, borrow/return transactions, and view reports |

---

## 🤝 Contributing

Contributions are welcome and appreciated! 🎉

**Steps to contribute:**

1. **Fork** the repository
2. **Create** a feature branch
   ```bash
   git checkout -b feature/YourFeatureName
   ```
3. **Commit** your changes
   ```bash
   git commit -m "feat: add YourFeatureName"
   ```
4. **Push** to your branch
   ```bash
   git push origin feature/YourFeatureName
   ```
5. **Open a Pull Request**

### Commit Message Convention

| Prefix | Purpose |
|--------|---------|
| `feat:` | New feature |
| `fix:` | Bug fix |
| `ui:` | UI/GUI changes |
| `db:` | Database changes |
| `docs:` | Documentation updates |
| `refactor:` | Code restructuring |
| `test:` | Adding tests |

---

## 🗺️ Roadmap

- [x] Book catalog with full CRUD
- [x] Member registration and management
- [x] Borrow and return tracking
- [x] Overdue fine calculation
- [x] Admin login and role management
- [x] Dashboard with summary stats
- [ ] 🖨️ Print receipts for borrow/return transactions
- [ ] 📧 Email notifications for due dates
- [ ] 📊 Export reports to PDF / Excel
- [ ] 🔍 Barcode / ISBN scanner support
- [ ] 🌙 Dark mode UI theme
- [ ] 🌍 Multi-language support (Arabic, French)
- [ ] ☁️ Cloud database sync option

---

## 📜 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more information.

---

## 📬 Contact

> 📌 *Replace with your real information.*

**Developer** — [@your-username](https://github.com/your-username)

📧 Email: `your.email@example.com`

🔗 Project: [https://github.com/your-username/library-management-system](https://github.com/your-username/library-management-system)

---

<div align="center">

<br/>

Built with ☕ Java and a love for books

<br/>

⭐ If this project helped you, please give it a star — it means a lot!

</div>
