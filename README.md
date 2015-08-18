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
mvn clean package
```

<!---
- to run unit tests using Chrome put this JVM parameters "-Dbrowser=chrome -Dwebdriver.chrome.driver=/path/to/your/chromedriver"
    - eg.: /Users/fvalmeida/Downloads/chromedriver
-->