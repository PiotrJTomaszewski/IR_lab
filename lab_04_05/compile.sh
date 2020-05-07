#!/bin/bash
echo "Cleaning"
mvn clean
rm -rf index
echo "Compiling"
mvn compile
