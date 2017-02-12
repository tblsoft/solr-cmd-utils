========================
solr-cmd-utils |release|
========================


Pipelines
=========
The following command executes the pipeline csv-writer-pipeline.yaml and set the variable filename.
The file will be written to the location test.csv::

    solr-pipeline -p examples/unittest/csv-writer-pipeline.yaml -Vfilename=test.csv


TODO
====