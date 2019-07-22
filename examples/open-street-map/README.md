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

https://nominatim.openstreetmap.org/reverse?format=json&osm_type=W&osm_id=478814183

## Administrative Boundaries
https://wiki.openstreetmap.org/wiki/Tag:boundary=administrative

## Streets
https://help.openstreetmap.org/questions/9816/the-best-way-to-extract-street-list

## Postal Codes
https://wiki.openstreetmap.org/wiki/DE:Key:postal%20code?uselang=de
    type=boundary
    boundary=postal_code
    postal_code=Zahl
    
## Elastic Polygon API
https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-polygon-query.html


## OSM Relation Analyzer
- http://ra.osmsurround.org/