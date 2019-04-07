#!/bin/bash
mvn clean package -X
java -jar target/pojo_model-1.0-SNAPSHOT.jar
