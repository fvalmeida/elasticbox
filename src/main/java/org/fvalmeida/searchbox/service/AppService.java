package org.fvalmeida.searchbox.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.ParentMetadata;
import org.apache.tika.metadata.serialization.JsonMetadata;
import org.apache.tika.metadata.serialization.JsonMetadataList;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.sax.ContentHandlerFactory;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AppService {

    @Autowired
    private Client client;
//
//    @Autowired
//    private ElasticsearchTemplate elasticsearchTemplate;

    @PostConstruct
    public void tikaIndexTest() {

        try {
            ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            Resource[] tikaFiles = patternResolver.getResources("classpath*:tika/**");

            try {
                client.admin().indices().prepareCreate("playground").get();
            } catch (IndexAlreadyExistsException ignored) {
            }

            for (Resource tikaFile : tikaFiles) {
                parse(tikaFile.getFile());
            }
        } catch (Exception e) {
            log.error("{}", e);
        }

    }

    private void parse(File file) throws TikaException, IOException, SAXException {
        List<Metadata> metadatas = getMetadata(file);
        if (metadatas.size() > 1) {
            ParentMetadata parentMetadata = (ParentMetadata) metadatas.get(0);
            parentMetadata.setEmbeddedMetadatas(metadatas.subList(1, metadatas.size()));

            Map<String, Object> metadataMap = new HashMap<String, Object>();
            metadataMap.put("", metadatas.get(0));
//            metadataMap.put("embeddedFiles", );
        } else {
            store(metadatas.get(0), DigestUtils.sha256Hex(new FileInputStream(file)));
        }
    }

    private void store(Metadata metadata, String id) throws TikaException {
        StringWriter writer = new StringWriter();
        JsonMetadata.toJson(metadata, writer);

//        JsonMetadataList.toJson();

        log.info(writer.toString());
        IndexResponse response = client.prepareIndex("playground", "tikametadata", id)
                .setSource(writer.toString())
                .execute()
                .actionGet();
        log.info(ToStringBuilder.reflectionToString(response));
    }

    /**
     * For documents that may contain embedded documents, it might be helpful
     * to create list of metadata objects, one for the container document and
     * one for each embedded document.  This allows easy access to both the
     * extracted content and the metadata of each embedded document.
     * Note that many document formats can contain embedded documents,
     * including traditional container formats -- zip, tar and others -- but also
     * common office document formats including: MSWord, MSExcel,
     * MSPowerPoint, RTF, PDF, MSG and several others.
     * <p>
     * The "content" format is determined by the ContentHandlerFactory, and
     * the content is stored in {@link RecursiveParserWrapper#TIKA_CONTENT}
     * <p>
     * The drawback to the RecursiveParserWrapper is that it caches metadata and contents
     * in memory.  This should not be used on files whose contents are too big to be handled
     * in memory.
     *
     * @param file
     * @return a list of metadata object, one each for the container file and each embedded file
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    public List<Metadata> getMetadata(File file) throws IOException, SAXException, TikaException {
        Parser p = new AutoDetectParser();
        ContentHandlerFactory factory = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.TEXT, -1);
        RecursiveParserWrapper wrapper = new RecursiveParserWrapper(p, factory);
        InputStream stream = new FileInputStream(file);
        Metadata metadata = new Metadata();
        metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
        ParseContext context = new ParseContext();
        try {
            wrapper.parse(stream, new DefaultHandler(), metadata, context);
        } finally {
            stream.close();
        }
        return wrapper.getMetadata();
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
