# solr-cmd-utils

## Todo's
* Documentation for installation
* improve help menu for the command line options
* documentation for all the available functions

## Installation
TODO 

## solr-dump
    solr-dump -in "http://localhost:8983/solr/source-core/select?q=*&rows=1000" \
        -out "http://localhost:8983/solr/dest-core" \
        -ignore-fields ".*Stemmed_.*_stemmed.*,.*Facet_.*_facet.*,.*Facet_facet,.*Sorted_.*_sorted.*"
        
Download the solr data from the specified input and writes the data to the specified output.
The input and output can be a url or a file.

## solr-numfound
    solr-numfound -in "http://localhost:8983/solr/solr-core/select?q=*"

Retrieve the data from the specified url and return the numFound of the resultset. This method can be uses for Monitoring.
If you have a full feed every night, you can check the number of results that were processed yesterday. If numFound > 0 you might have a problem.

    solr-numfound -in "http://localhost:8983/solr/solr-core/select?q=*&fq=processingtime:[*+TO+NOW/DAY-1DAY]"
        
        
## Amazon API Tools
The Amazon Api Tools are a good example to build a java command line tool.
        
        ~/dev/ec2-api-tools/bin>less ec2-cmd
        