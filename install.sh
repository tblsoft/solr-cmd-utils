#!/bin/bash
set -e
mvn clean install -DskipTests=true
rm -rf ~/dev/solr-cmd-utils/
tar xvfz target/solr-cmd-utils-3.26-SNAPSHOT-cmd.tar.gz -C ~/dev
