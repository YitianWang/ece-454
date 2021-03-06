#!/bin/sh

#
# Wojciech Golab, 2016
#

JAVA_CC=/usr/lib/jvm/java-1.8.0/bin/javac
THRIFT_CC=/opt/bin/thrift

echo --- Cleaning
rm -f *.jar
rm -f *.class
rm -fr gen-java

echo --- Compiling Thrift IDL
$THRIFT_CC --version
$THRIFT_CC --gen java a1.thrift

echo --- Compiling Java
$JAVA_CC -version
$JAVA_CC gen-java/*.java -cp .:"lib/*"
$JAVA_CC *.java -cp .:gen-java/:"lib/*":"jBCrypt-0.4/*"

echo --- Done, now run your code.
 #java -cp .:gen-java/:"lib/*":"jBCrypt-0.4/*" FENode 10123
#java -cp .:gen-java/:"lib/*":"jBCrypt-0.4/*" BENode localhost 10123 10124
 java -cp .:gen-java/:"lib/*":"jBCrypt-0.4/*" MultiThreadedClient ecelinux1 $1
