#!/bin/bash

repo=$(pwd)
services=$(ls | grep '-service$')

for service in $services; do
  cd "$repo/$service"
  mvn clean install -DskipTests
done