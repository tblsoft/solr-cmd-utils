# Open Street Map (OSM) Example



## Usage example Germany
- Download the osm file from https://download.geofabrik.de

````
curl -L https://download.geofabrik.de/europe/germany-latest.osm.bz2 > germany-latest.osm.bz2

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

## OSM API
- https://www.openstreetmap.org/node/678255512
- https://www.openstreetmap.org/api/0.6/node/678255512
- https://nominatim.openstreetmap.org/details.php?place_id=6898401

Grenze von Darmstadt:
- https://www.openstreetmap.org/api/0.6/relation/286590
- https://www.openstreetmap.org/relation/286590

Bliederstedt:
- http://nominatim.openstreetmap.org/lookup?osm_ids=N794344325&format=json

## Nominatim
http://wiki.openstreetmap.org/wiki/Nominatim