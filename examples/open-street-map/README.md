# Open Street Map (OSM) Example

## Usage
- Download the osm file from https://download.geofabrik.de

````
curl https://download.geofabrik.de/europe/germany-latest.osm.bz2 > germany-latest.osm.bz2
bunzip2 germany-latest.osm.bz2

solr-pipeline -p osm-pipeline.yaml
````