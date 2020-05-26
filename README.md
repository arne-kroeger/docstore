# docstore-server

## Requirements

Java: >= 1.8
PostgreSQL-Server: >= 9

## Installation & Usage

PostgreSQL server installation has to be done to launch the server.

Server can be started with:

```shell
cd /PATH/TO/INSTALLATION
mkdir config
curl -o https://github.com/arne-kroeger/docstore/raw/dev/config/application.example.properties -o config/application.properties
vi config/application.properties # edit properties
java -jar docstore.jar
```

## Clients

The clients are sending documents or document data to server and it will be historized on server side.
The document will be added to store:

![alt text](https://github.com/arne-kroeger/docstore/raw/dev/doc/docstore.png "Logo Title Text 1")

The server checks on differences between the latest and the incoming version and will historize it in cases of new data. It also tracks the latest update received date and the date of change. 

The document has a unique identifier (for space and environment) which needs to be defined on client side. Whenever the identifier is the same the server will check for changes. If it is unknown to the server it will add a new document.


The following meta information can be added to the document:

|Meta-Info      |Description                                    |
|---------------|-----------------------------------------------|
|Tags           |Tagging of the document                        |
|Space          |Space of the document where it belongs         |
|Environment    |Environment of the document where it belongs   |

### Tags

The tagging is used to group the documents in logical contexts. There is no limitation of used chars.

### Space

The space is a logical category where the documents belongs to (e.g. Product, Customer, Server-Type).

### Environment

The environment specifies the source of the created document. It is possible to send the same document from different environments (e.g. DEV, STAGE, PRODUCTION) to visualize different setups.

## Templates

To use standard templates for visualisation the client can add templates to the server. The templates are maintained in [Freemarker](https://freemarker.apache.org/). The clients then use the idenfication of the template and send the variables to the server which creates the final document. 

Templating can be used to ensure that all generated documents for the same topic are structurally identical.

## Archiving

The server is checking for updates of the documents. If no updates are received in a defined period of time the service will:

- outdate the document
- archive the document

The time limits can be defined in the configuration file. 

## API

The API can be used to create an own visualization (client) or you can use the standard vue client: [docstore-vue-client](https://github.com/arne-kroeger/docstore-vue-client)

Details on the API spec can be found in OpenAPI Spec: [OpenAPI-Spec](https://github.com/arne-kroeger/docstore/blob/dev/src/main/resources/perfact.documentation.openapi.yml)

## Clients

Clients can be generated by the [OpenAPI generator](https://github.com/OpenAPITools/openapi-generator). 

The following clients are published automaticaly by this project:

|Client |Url                                                            |
|-------|---------------------------------------------------------------|
|Java   |https://github.com/arne-kroeger/docstore-client-java-feign     |
|PHP    |https://github.com/arne-kroeger/docstore-client-php            |
|Python |https://github.com/arne-kroeger/docstore-client-python         |


## Author

Arne Kröger


