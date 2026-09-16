#!/bin/bash

# FastCode IDE Build Script
echo "Building FastCode IDE..."

# Create output directory
mkdir -p bin

# Compile all Java files
find src -name "*.java" > sources.txt
javac -d bin @sources.txt

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "Run with: java -cp bin com.fastcode.Main"
else
    echo "Build failed!"
    exit 1
fi

# Cleanup
rm sources.txt
