# Setup Guide

## Prerequisites

1. **Java Development Kit (JDK) 21+**
   - Download: https://www.oracle.com/java/technologies/downloads/

2. **PostgreSQL 13+**
   - Download: https://www.postgresql.org/download/

3. **Git**
   - Download: https://git-scm.com/downloads/

## Installation Steps

### 1. Clone the Repository

```bash
git clone https://github.com/notOdyss/finalexamoop.git
cd finalexamoop
```

### 2. Set Up PostgreSQL Database

Open PostgreSQL terminal (psql) or pgAdmin and run:

```sql
CREATE DATABASE taskmanager_db;
```

Then run the database setup script:

```bash
psql -U postgres -d taskmanager_db -f database-setup.sql
```

Or manually execute the SQL from `database-setup.sql` file.

If you need the start_date column (for calendar features), also run:

```bash
psql -U postgres -d taskmanager_db -f migration_add_start_date.sql
```

### 3. Configure Database Connection

Open this file:
```
src/main/java/org/example/workspace/database/DatabaseManager.java
```

Update these lines with your PostgreSQL credentials:

```java
private static final String DB_URL = "jdbc:postgresql://localhost:5432/taskmanager_db";
private static final String DB_USER = "postgres";
private static final String DB_PASSWORD = "your_password_here";
```

Also update:
```
src/main/java/org/example/workspace/db/Database.java
```

Change the password on this line:
```java
private static final String password = "your_password_here";
```

### 4. Run the Application

**Option 1 - Using Maven Wrapper (Recommended):**

On macOS/Linux:
```bash
./mvnw clean javafx:run
```

On Windows:
```bash
mvnw.cmd clean javafx:run
```

**Option 2 - Using Maven (if installed):**
```bash
mvn clean javafx:run
```

**Option 3 - Using IDE (IntelliJ IDEA):**
1. Open the project in IntelliJ IDEA
2. Wait for Maven dependencies to download
3. Find `Launcher.java`
4. Right-click → Run 'Launcher.main()'

## Troubleshooting

### Database Connection Issues

If you get "Connection refused" error:
- Make sure PostgreSQL is running
- Check if port 5432 is available
- Verify database credentials

### Maven Build Issues

If build fails:
```bash
./mvnw clean install -U
```

### JavaFX Issues

If JavaFX modules not found, make sure you're using JDK 21 and Maven is using the correct Java version:
```bash
java -version
./mvnw -version
```

## First Run

1. Click "Register" to create an account
2. Enter username (min 3 characters), email, and password (min 6 characters)
3. Login with your credentials
4. Start creating tasks!

## Features to Test

- Dashboard: View and manage all tasks
- Calendar View: See tasks on calendar with trails
- Statistics: View task analytics
- Profile: Update user information
- Task Management: Create, edit, delete tasks with priorities and deadlines
