set -e
mvn clean install
tar xvfz target/solr-cmd-utils-1.0-SNAPSHOT-cmd.tar.gz -C ~/dev
