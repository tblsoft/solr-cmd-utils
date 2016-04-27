# solr-cmd-utils

## Travis CI Build Information

[![Build Status](https://travis-ci.org/tblsoft/solr-cmd-utils.svg?branch=master)](https://travis-ci.org/tblsoft/solr-cmd-utils)

## Code coverage

[![Coverage Status](https://coveralls.io/repos/github/tblsoft/solr-cmd-utils/badge.svg?branch=master)](https://coveralls.io/github/tblsoft/solr-cmd-utils?branch=master)


## Installation
Assuming you want to install the lib to the `~/dev/solr-cmd-utils` directory, you have to do the following tasks:

    curl -L "https://github.com/tblsoft/solr-cmd-utils/releases/download/0.0.1/solr-cmd-utils-0.0.1.tar.gz" | tar xvfz - -C ~/dev

To call the util function from every location, you have to add the following exports to your `.profile`

    export SOLR_CMD_UTILS_HOME=~/dev/solr-cmd-utils
    export PATH=$SOLR_CMD_UTILS_HOME/bin:$PATH
    
### Prerequisites
* Linux, OS X, or Unix
* Java 7


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

        
## Todo's
* Documentation for installation
* improve help menu for the command line options
* documentation for all the available functions
        
## Amazon API Tools
The Amazon Api Tools are a good example to build a java command line tool.
        
        ~/dev/ec2-api-tools/bin>less ec2-cmd
        