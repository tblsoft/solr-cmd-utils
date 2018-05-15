# Git Examples

## Git Log to Elastic
In this example i will index the git logs to elasticsearch.
To extract the git log informations as json the function gitlog2json.sh is used.

```
gitlog2json.sh
solr-pipeline -p gitlog-pipeline.yaml
```

In Kibana you can visualize and search the git logs.