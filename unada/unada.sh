#!/bin/bash
clear
echo "Starting uNaDa..."

if [ -z "$JETTY_HOME" ]; then
    echo "You need to configure JETTY_HOME environment variable. Otherwise run uNaDa manually following the instructions. :)"
    exit 1
fi  

cd $JETTY_HOME
java -jar start.jar etc/jetty.xml -Djetty.port=8181

echo "Finalizing uNaDa.."