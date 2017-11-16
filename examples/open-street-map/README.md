# Open Street Map (OSM) Example



## Usage example Germany
- Download the osm file from https://download.geofabrik.de

````
curl https://download.geofabrik.de/europe/germany-latest.osm.bz2 > germany-latest.osm.bz2

solr-pipeline -p osm-pipeline.yaml -Vfilename=germany-latest.osm.bz2
````

## Usage example World
- Download the osm file http://wiki.openstreetmap.org/wiki/Planet.osm

````
curl -L -o planet-latest.osm.bz2 -C - https://planet.openstreetmap.org/planet/planet-latest.osm.bz2
solr-pipeline -p osm-pipeline.yaml

````

## OSM Downloads
- http://wiki.openstreetmap.org/wiki/Downloading_data
