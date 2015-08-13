package org.fvalmeida.searchbox.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.serialization.JsonMetadata;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
@Slf4j
public class AppService {

    @Autowired
    private Client client;

    @Value("elasticsearch.index.name")
    private String indexName;

    @PostConstruct
    public void init() {
        try {
            client.admin().indices().prepareCreate(indexName).get();
        } catch (IndexAlreadyExistsException ignored) {
        }
    }

    @Async
    public Future<List<Pair<String, IndexResponse>>> parse(File file) throws TikaException, IOException, SAXException {
        List<Pair<String, IndexResponse>> pairs = new ArrayList<>();
        List<Metadata> metadatas = getMetadata(file);
        if (metadatas.size() > 1) {
            Metadata parentMetadata = metadatas.get(0);
            pairs.add(store(metadatas.get(0), DigestUtils.sha256Hex(new FileInputStream(file))));
            for (int i = 1; i < metadatas.size(); i++) {
                Metadata metadata = metadatas.get(i);
                metadata.set("resourceName", parentMetadata.get("resourceName"));
                store(metadata, String.format("%s-%s", DigestUtils.sha256Hex(new FileInputStream(file)), i));
            }
        } else {
            pairs.add(store(metadatas.get(0), DigestUtils.sha256Hex(new FileInputStream(file))));
        }
        return new AsyncResult<>(pairs);
    }

    private Pair<String, IndexResponse> store(Metadata metadata, String id) throws TikaException {
        StringWriter writer = new StringWriter();
        JsonMetadata.toJson(metadata, writer);
        IndexResponse response = client.prepareIndex(indexName, Metadata.class.getName(), id)
                .setSource(writer.toString())
                .execute()
                .actionGet();
        return new ImmutablePair<>(writer.toString(), response);
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
    private List<Metadata> getMetadata(File file) throws IOException, SAXException, TikaException {
        Parser p = new AutoDetectParser();
        ContentHandlerFactory factory = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.TEXT, -1);
        RecursiveParserWrapper wrapper = new RecursiveParserWrapper(p, factory);
        InputStream stream = new FileInputStream(file);
        Metadata metadata = new Metadata();
        metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
        ParseContext context = new ParseContext();
        try {
            wrapper.parse(stream, null, metadata, context);
        } finally {
            stream.close();
        }
        return wrapper.getMetadata();
    }

}
