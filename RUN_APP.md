# How to Run the Application

## Quick Start

### Option 1: Using Maven (Recommended)
```bash
./mvnw clean javafx:run
```

### Option 2: Using IDE (IntelliJ IDEA)
1. Open the project in IntelliJ IDEA
2. Wait for Maven to download dependencies
3. Find `Launcher.java` in the project explorer
4. Right-click on `Launcher.java` → **Run 'Launcher.main()'**

### Option 3: Direct Java Execution
```bash
./mvnw clean package
java --module-path target/classes --add-modules javafx.controls,javafx.fxml \
     -cp "target/classes:$(./mvnw dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" \
     org.example.workspace.Launcher
```

---

## Troubleshooting

### Issue 1: "Nothing happens" when running ./mvnw javafx:run

**Possible causes:**
1. **Database not configured** - Check DatabaseManager.java credentials
2. **PostgreSQL not running** - Start PostgreSQL service
3. **Port already in use** - Close other instances

**Solution:**
```bash
# Check if PostgreSQL is running
psql -U notodyss -d taskmanager_db -c "SELECT 1;"

# If error, create database:
createdb -U notodyss taskmanager_db

# Run SQL setup:
psql -U notodyss -d taskmanager_db -f database-setup.sql
```

### Issue 2: "JavaFX not found" error

**Solution:**
Make sure you're using JDK 21 or higher with JavaFX included, or Maven will download it automatically.

```bash
java -version  # Should show version 21+
```

### Issue 3: Application starts but immediately closes

**Check console output for errors:**
```bash
./mvnw clean javafx:run 2>&1 | tee app.log
```

Look for:
- Database connection errors
- Missing FXML files
- Class loading issues

---

## Current Configuration

**Database:**
- URL: `jdbc:postgresql://localhost:5432/taskmanager_db`
- User: `notodyss`
- Password: `` (empty)

**If you need to change credentials:**
Edit: `src/main/java/org/example/workspace/database/DatabaseManager.java`

Lines 11-13:
```java
private static final String DB_URL = "jdbc:postgresql://localhost:5432/taskmanager_db";
private static final String DB_USER = "notodyss";
private static final String DB_PASSWORD = "";
```

---

## macOS Specific

### If you get "App is damaged and can't be opened"

```bash
xattr -cr /Applications/YourApp.app
```

### If you need to grant permissions

System Preferences → Security & Privacy → Allow app to run

---

## Verification Steps

1. **Check Java version:**
   ```bash
   java -version
   ```
   Should be 21 or higher

2. **Check PostgreSQL:**
   ```bash
   psql -U notodyss -d taskmanager_db -c "\dt"
   ```
   Should show `users` and `tasks` tables

3. **Check Maven:**
   ```bash
   ./mvnw --version
   ```

4. **Compile without running:**
   ```bash
   ./mvnw clean compile
   ```
   Should complete without errors

---

## Expected Behavior

When you run `./mvnw clean javafx:run`:

1. Maven downloads dependencies (first time only)
2. Compiles the project
3. **Application window opens** showing Welcome Page
4. You can click **Register** or **Login**
5. After login, you see the Calendar View

**If the window doesn't open**, there's an error. Check the console output.

---

## Alternative: Run from IDE

**IntelliJ IDEA** is the easiest way to run and debug:

1. File → Open → Select `Workspace` folder
2. Wait for indexing to complete
3. Open `src/main/java/org/example/workspace/Launcher.java`
4. Click the green play button next to `main` method
5. Or right-click file → Run 'Launcher'

**Benefits:**
- See errors immediately
- Easy debugging
- Auto-completion works
- Database inspector built-in

---

## Contact

If none of these work, check:
1. Console output for errors
2. Database connection
3. Java version compatibility
4. FXML files exist in `src/main/resources/org/example/workspace/`
