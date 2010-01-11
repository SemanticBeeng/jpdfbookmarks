#!/bin/sh

JAR_NAME=jpdfbookmarks.jar
JVM_OPTIONS="-Xms256m -Xmx512m"

PATH_TO_TARGET=`readlink $0`
DIR_OF_TARGET=`dirname $PATH_TO_TARGET`

if [ -n "$JAVA_HOME" ]; then
  "$JAVA_HOME/bin/java" $JVM_OPTIONS -jar "$DIR_OF_TARGET/$JAR_NAME" "$@"
else
  java $JVM_OPTIONS -jar "$DIR_OF_TARGET/$JAR_NAME" "$@"
fi

