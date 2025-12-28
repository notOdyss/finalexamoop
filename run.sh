#!/bin/bash

echo "Starting Task Manager Application..."
echo "======================================"
echo ""

# Check if PostgreSQL is running
echo "1. Checking PostgreSQL connection..."
psql -U notodyss -d taskmanager_db -c "SELECT 1;" > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "   ✓ PostgreSQL is running"
else
    echo "   ✗ PostgreSQL connection failed"
    echo "   Please start PostgreSQL and create the database:"
    echo "   createdb -U notodyss taskmanager_db"
    exit 1
fi

echo ""
echo "2. Checking Java version..."
java -version 2>&1 | head -1
echo ""

echo "3. Compiling and running application..."
echo "   (This may take a minute on first run)"
echo ""

./mvnw clean javafx:run

echo ""
echo "Application stopped."
