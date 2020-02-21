#!/usr/bin/env bash
set -e

cp ../../../target/solr-cmd-utils-3.11-SNAPSHOT-cmd.tar.gz .
sudo docker build -t solr-cmd-utils .
sudo docker tag solr-cmd-utils tblsoft/solr-cmd-utils
sudo docker push tblsoft/solr-cmd-utils:latest