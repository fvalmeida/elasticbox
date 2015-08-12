package org.fvalmeida.searchbox.service;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.metadata.serialization.JsonMetadataList;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.sax.ContentHandlerFactory;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

@Service
public class AppService {

    @Autowired
    private Client client;
//
//    @Autowired
//    private ElasticsearchTemplate elasticsearchTemplate;

    @PostConstruct
    public String test() throws IOException,
            SAXException, TikaException {

        ParsingExample parsingExample = new ParsingExample();
        System.out.println(parsingExample.serializedRecursiveParserWrapperExample());

        return null;
    }

//    public void indexJokes() throws Exception {
//        // create an index name "jokes" to store the jokes in
//        try {
//            client.admin().indices().prepareCreate("jokes").get();
//        } catch (IndexAlreadyExistsException ignored) {
//        }
//
//        storeJoke(1, "Why are teddy bears never hungry? ", "They are always stuffed!");
//        storeJoke(2, "Where do polar bears vote? ", "The North Poll!");
//    }
//
//    private void storeJoke(int id, String question, String answer) throws IOException {
//        // index a document ID  of type "joke" in the "jokes" index
//        client.prepareIndex("jokes", "joke", String.valueOf(id))
//                .setSource(
//                        XContentFactory.jsonBuilder()
//                                .startObject()
//                                .field("question", question)
//                                .field("answer", answer)
//                                .endObject()
//                )
//                .get();
//    }
//
//    public SearchHit[] search(String query) {
//        return client.prepareSearch("jokes")
//                .setTypes("joke")
//                .setQuery(QueryBuilders.multiMatchQuery(query, "question", "answer"))
//                .get().getHits().getHits();
//    }
}
