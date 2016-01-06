# solr-cmd-utils

## Installation
TODO 

## Dump
    solr-dump -in "http://localhost:8983/solr/source-core/select?q=*&rows=1000" \
        -out "http://localhost:8983/solr/dest-core" \
        -ignore-fields ".*Stemmed_.*_stemmed.*,.*Facet_.*_facet.*,.*Facet_facet,.*Sorted_.*_sorted.*"
        
        
## Amazon API Tools
        
        ~/dev/ec2-api-tools/bin>less ec2-cmd
        