# Changelog
All notable changes for the solr-cmd-utils project will be documented in this file.


## [3.29]
### Changed


### Deprecated



## [Unreleased]
### Changed
- update dependencies
### Deprecated
- Javascript filters are not supported anymore in newer java versions

## [3.19]
### Added
- add a datype list.string to ensure arrays in the elastic index

## [3.18]
### Added
- implement a qsc data push writer
- add a datype list.string to ensure arrays in the elastic index

### Changed


### Fixed
- include the seperator for the elastic prefix index search, to avoid finding indexes with the same prefix


### Deprecated


## [3.17]
### Added
- make it possible to log documents fields in the status filter
- implement a document reader
- implement a document writer
- add a mapping function for base65 encoding and decoding
- make it possible to read multiple csv files
- implement a SearchQueryAnalyzerFilter
- implement a document joiner filter
- implement a html reader for testing

### Changed


### Fixed


### Deprecated


## [3.16] - 2021-02-20
### Added
- add a cache max age parameter to the http filter

### Changed
- improve error handling in HttpHelper
- CSVWriter always delete the file if append is false

### Fixed
- fix nullpointer in ElasticScrollQuery

### Deprecated

## [3.13] - 2020-07-13
### Added
- SquadReader to read squad data 
- SquadWriter to write squad data 
- implement a filter to add static values
- implement a random iban generator
- implement a QSF Data reader
- implement a qsf data repository manager
- ElasticWriter: add a alias at the beginning of the feeding, if no alias exists
- read variables from a settings file in the home directory
- allow classpath references in IOUTILS
- implement the expectedDocumentCount feature
- implement a lowercase filter
- implement a RemoveHtmlFilter
- implement a ShingleFilter

### Changed


### Fixed

### Deprecated
- ElasticHelper.getAliaslUrl() is renamed to getAliasesUrl()


## [3.9]
### Added
- add CONTRIBUTING.md
- add pull_request_template.md
- add CODE_OF_CONDUCT.md
- allow streaming of gzip files of http locations
- add a processing time filter
- add a file caching for http filter
- add a html filter to extract data from html
- add a mulitpoint datatype for elastic writer
- allow the processing of multiple filenames in xml reader
- allow the usage of variables in reader properties
- CSVReader - add a property to configure the quote
- implement a raw value for the fields 
- extend the elastic writer to process raw json value
- add regex find filter
- add stopword filter
- add HtmlTextExtractorFilter
- add json input parameter for JsonPathReader
- add splitRegex to AggregationCountFilter
- add a date pattern for the json serializer in the elastic writer
- add maven central badge
- add processing time filter
- ElasticWriter - check if the index exists before creating a new one
- ElasticWriter - implement delete for bulk methods
- ElasticWriter - improve error handling of the elastic writer
- CSVReader - add a property to configure the quote
- HTTPFilter - add a property to configure follow redirects
- HTMLFilter - implement a custom mapping with jsoup
- IOUtils - add a method to read the content of a file to a list of string
- add a feeding queue reader for the quasiris search cloud
- improve error handling of the pipeline executer
- add a webhook which reports errors
- MappinFilter - add a remove html method
- MappinFilter - add a remove url method
- CsvWriter - add a option to append to an existing csv file
- add a elastic reader, that supports the elastic bulk format - ElasticdumpJsonReader 
- add a elastic writer, that supports the elastic bulk format - ElasticdumpFileWriter 
- add support for subfields
- log the pipeline
- make the elastic reader ready for elastic 7
- support nested objects in the elastic scroll api

### Changed
- add jaxb dependencies to enable jdk 11 support
- Xml Sitemap Writer - create the final directory if it does not exists
- remove solr dependencies from the statistic filter
- increase version of jackson
- remove duplicate jaxb-api definition in the pom.xml

### Fixed



## [3.9]
### Added
- add snyk.io badge
- add some new travis jdk's

### Changed
- significantly improved performacne for JavaScriptFilter
- remove versioneye.com badge
- remove minio dependency
- increase version of jsoup
- increase version of guava
- increase version of httpclient
- increase version of solrj
- increase version of junit
- increase version of jcommander
- remove google-collections dependency
- increase version of snakeyaml
- remove commons-math3 dependency
- increase version of grok
- increase version of json-path
- increase version of slf4j-simple
- increase version of rhino
- remove async-http-client dependency
- remove mustache dependency
- increase version of coveralls-maven-plugin
- increase version of maven-compiler-plugin
- increase version of maven-assembly-plugin

### Fixed


## [3.3] and previous- 2019-05-09
### Added

### Changed

### Fixed
