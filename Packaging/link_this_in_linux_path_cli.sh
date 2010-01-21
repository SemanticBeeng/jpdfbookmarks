#!/bin/sh

###############################################################################
# Create a link to this file to execute jpdfbookmarks cli on linux or other 
# Unix like systems in a folder in the PATH environment variable. For example  
# as root type:
#
# $ ln -s  link_this_in_linux_path_cli.sh /usr/local/bin/jpdfbookmarks
#
###############################################################################

JAR_NAME=jpdfbookmarks.jar
JVM_OPTIONS="-Xms64m -Xmx512m -splash: "

PATH_TO_TARGET=`readlink $0`
DIR_OF_TARGET=`dirname $PATH_TO_TARGET`

if [ -n "$JAVA_HOME" ]; then
  "$JAVA_HOME/bin/java" $JVM_OPTIONS -jar "$DIR_OF_TARGET/$JAR_NAME" "$@"
else
  java $JVM_OPTIONS -jar "$DIR_OF_TARGET/$JAR_NAME" "$@"
fi

