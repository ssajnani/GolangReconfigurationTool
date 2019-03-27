#!/bin/bash
mvn clean package -X
java -jar target/variable-fetcher-0.1.0.jar
