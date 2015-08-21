Elasticbox
=======================

Elasticbox is a simple bare bone app for test TIKA and Elasticsearch features.

Requisites
------------

- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Git](https://git-scm.com/download/): follow installation instructions from [site](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).
- [Maven](https://maven.apache.org/download.cgi): follow installation instructions from [site](https://maven.apache.org/install.html).

> Make sure all executable programs (like **java**, **mvn** and **git**) are linked to your PATH environment variable

Technology stack
------------

+ **Main**
    
    - [ElasticSearch](https://www.elastic.co/downloads/elasticsearch)
    - [elasticsearch-head](http://mobz.github.io/elasticsearch-head/)
    - [ElasticHQ](http://www.elastichq.org/support_plugin.html) 
    - [elasticsearch-kopf](https://github.com/lmenezes/elasticsearch-kopf/) 
    - [bigdesk](http://bigdesk.org/)
    - [Kibana](https://www.elastic.co/downloads/kibana)
    
+ **Others**
    
    - [Elasticsearch.Net](http://nest.azurewebsites.net/)

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

    > Usage: 
    
    ```bash
    java -jar elasticbox-tika-indexer.jar <options>           
      
      Option                                                                 Description                     
      ------                                                                 -----------                     
      -?, -h, --help                                                         Show the help                   
      --index.name=<value>                                                   Elasticsearch index name        
                                                                              (default: elasticbox)          
      --paths=<comma-separated paths>                                        Paths for index to Elasticsearch
                                                                              (default: current directory)   
      --recursive=<true|false>                                               Index path recursively          
                                                                              (default: true)                
      --spring.data.elasticsearch.cluster-nodes=<comma-separated nodes>      Elasticsearch cluster nodes     
                                                                              (default: localhost:9300)      
      --thread-count=<number of threads>                                     Max number of threads           
                                                                              (default: 10)                  
      --error.logging.file=<value>                                           Error logging file              
                                                                              (default: elasticbox.error.log)
      
      Example:                                                           
         java -jar elasticbox-tika-indexer.jar
         java -jar elasticbox-tika-indexer.jar --recursive=false
         java -jar elasticbox-tika-indexer.jar --paths=/Documents --index.name=documents
   ```  