Elasticbox
=======================

Elasticbox is a simple bare bone app for test TIKA and Elasticsearch features.

Requisites
------------

- [Git](https://git-scm.com/download/): follow installation instructions from [site](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).
- [Maven](https://maven.apache.org/download.cgi): follow installation instructions from [site](https://maven.apache.org/install.html).

Technology stack
------------

- [ElasticSearch](https://www.elastic.co/downloads/elasticsearch)
- [elasticsearch-head](http://mobz.github.io/elasticsearch-head/)
- [ElasticHQ](http://www.elastichq.org/support_plugin.html) 
- [elasticsearch-kopf](https://github.com/lmenezes/elasticsearch-kopf/) 
- [bigdesk](http://bigdesk.org/)
- [Kibana](https://www.elastic.co/downloads/kibana)

Installation
------------

```bash
git clone https://github.com/fvalmeida/elasticbox.git
cd elasticbox
mvn package
```

Running
------------

1. After build, copy target directory content to your desired installation path
2. From **elasticsearch** directory, start it
    Run `bin/elasticsearch` on Unix or `bin/elasticsearch.bat` on Windows
3. There are many ways to index files:
    - Copy **elasticbox-tika-indexer.jar** to desired path that will be indexed and run it
        ```bash
        java -jar elasticbox-tika-indexer.jar
        ```
    - From installation path run **elasticbox-tika-indexer.jar** with `paths` argument
        ```bash
        java -jar elasticbox-tika-indexer.jar --paths=/Users/fvalmeida/Documents
        ```
    > For help, just run `java -jar elasticbox-tika-indexer.jar -?`
