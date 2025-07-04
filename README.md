# solr-cmd-utils

[![Build Status](https://travis-ci.org/tblsoft/solr-cmd-utils.svg?branch=master)](https://travis-ci.org/tblsoft/solr-cmd-utils)
[![Coverage Status](https://coveralls.io/repos/github/tblsoft/solr-cmd-utils/badge.svg?branch=master)](https://coveralls.io/github/tblsoft/solr-cmd-utils?branch=master)
[![Maven Centeral](https://img.shields.io/maven-central/v/io.github.tblsoft.solr/solr-cmd-utils.svg)](https://repo.maven.apache.org/maven2/io/github/tblsoft/solr/solr-cmd-utils)
[![MIT License](https://img.shields.io/npm/l/check-dependencies.svg?style=flat-square)](http://opensource.org/licenses/MIT)
[![Documentation Status](http://readthedocs.org/projects/solr-cmd-utils/badge/?version=latest)](http://solr-cmd-utils.readthedocs.io/en/latest/?badge=latest)
[![Join the chat at https://gitter.im/solr-cmd-utils/Lobby](https://badges.gitter.im/solr-cmd-utils/Lobby.svg)](https://gitter.im/solr-cmd-utils/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/478cf34208e148e78e862f233f2a7cd5)](https://www.codacy.com/app/tbl/solr-cmd-utils?utm_source=github.com&utm_medium=referral&utm_content=tblsoft/solr-cmd-utils&utm_campaign=badger)
[![codebeat badge](https://codebeat.co/badges/d9a8307b-c8e9-4a70-9ba5-e05eb1ca1a43)](https://codebeat.co/projects/github-com-tblsoft-solr-cmd-utils-master)
[![Stories in Ready](https://badge.waffle.io/tblsoft/solr-cmd-utils.svg?label=ready&title=Ready)](http://waffle.io/tblsoft/solr-cmd-utils) 
[![Code Climate](https://codeclimate.com/github/codeclimate/codeclimate/badges/gpa.svg)](https://codeclimate.com/github/codeclimate/codeclimate)
[![Known Vulnerabilities](https://snyk.io/test/github/tblsoft/solr-cmd-utils/badge.svg)](https://snyk.io/test/github/tblsoft/solr-cmd-utils) 

## Installation
Assuming you want to install the lib to the `~/dev/solr-cmd-utils` directory, you have to do the following tasks:

    curl -L "https://github.com/tblsoft/solr-cmd-utils/releases/download/1.0/solr-cmd-utils-1.0-cmd.tar.gz" | tar xvfz - -C ~/dev

To call the util function from every location, you have to add the following exports to your `.profile`

    export SOLR_CMD_UTILS_HOME=~/dev/solr-cmd-utils
    export PATH=$SOLR_CMD_UTILS_HOME/bin:$PATH
    
### Prerequisites
* Linux, OS X, or Unix
* Java 17

## solr-pipeline
The following command executes the pipeline csv-writer-pipeline.yaml and set the variable filename. The file will be written to the location test.csv.

    solr-pipeline -p examples/unittest/csv-writer-pipeline.yaml -Vfilename=test.csv
## solr-dump
    solr-dump -in "http://localhost:8983/solr/source-core/select?q=*&rows=1000" \
        -out "http://localhost:8983/solr/dest-core" \
        -ignore-fields ".*Stemmed_.*_stemmed.*,.*Facet_.*_facet.*,.*Facet_facet,.*Sorted_.*_sorted.*"
        
Download the solr data from the specified input and writes the data to the specified output.
The input and output can be a url or a file.

## solr-extract-nouns
    solr-extract-nouns -in "http://localhost:8983/solr/source-core/select?q=*&rows=1000" \
        -out "nouns.txt"
        

        
Extract all nouns from the given input and list the nouns. The output is deduplicated and sortet. 
Each noun is in one row. Example

    noun1
    noun2
    ...

The algorithm is specific for the german language.

## solr-numfound
    solr-numfound "http://localhost:8983/solr/solr-core/select?q=*"

Retrieve the data from the specified url and return the numFound of the resultset. This method can be uses for Monitoring.
If you have a full feed every night, you can check the number of results that were processed yesterday. If numFound > 0 you might have a problem.

    solr-numfound -in "http://localhost:8983/solr/solr-core/select?q=*&fq=processingtime:[*+TO+NOW/DAY-1DAY]"


## Logging
For logging the slf4j simple logger is used. The log level can be configured with the following java opts parameter:
```
export SOLR_CMD_UTILS_JAVA_OPTS="-Dorg.slf4j.simpleLogger.defaultLogLevel=debug" 
```

To enable debug in a docker container:
```
-e "SOLR_CMD_UTILS_JAVA_OPTS=-Dorg.slf4j.simpleLogger.defaultLogLevel=debug"
```

## Todo's
* Documentation for installation
* improve help menu for the command line options
* documentation for all the available functions
        
## Amazon API Tools
The Amazon Api Tools are a good example to build a java command line tool.
        
        ~/dev/ec2-api-tools/bin>less ec2-cmd
        
## License
[MIT](https://github.com/tblsoft/solr-cmd-utils/blob/master/LICENSE)
        