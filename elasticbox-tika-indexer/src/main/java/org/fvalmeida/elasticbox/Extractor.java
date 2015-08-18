package org.fvalmeida.elasticbox;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.sax.ContentHandlerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Component
public class Extractor {

    @Async("parseExecutor")
    public Future<List<Pair<Metadata, String>>> parse(File file) throws TikaException, IOException, SAXException {
        List<Pair<Metadata, String>> pairs = new ArrayList<>();
        List<Metadata> metadatas = getMetadata(file);
        if (metadatas.size() > 1) {
            Metadata parentMetadata = metadatas.get(0);
            pairs.add(new ImmutablePair<>(
                    addPath(metadatas.get(0), file),
                    DigestUtils.sha256Hex(new FileInputStream(file))));
            for (int i = 1; i < metadatas.size(); i++) {
                Metadata metadata = metadatas.get(i);
                metadata.set("resourceName", parentMetadata.get("resourceName"));
                pairs.add(new ImmutablePair<>(
                        addPath(metadata, file),
                        String.format("%s-%s", DigestUtils.sha256Hex(new FileInputStream(file)), i)));
            }
        } else {
            pairs.add(new ImmutablePair<>(
                    addPath(metadatas.get(0), file),
                    DigestUtils.sha256Hex(new FileInputStream(file))));
        }
        return new AsyncResult<>(pairs);
    }

    private Metadata addPath(Metadata metadata, File file)  {
        Path path = Paths.get(file.getAbsolutePath());
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        metadata.add("resourceRoot", fileSystemView.getSystemDisplayName(Paths.get(file.getAbsolutePath()).getRoot().toFile()));
        metadata.add("resourcePath", path.getParent().toString());
        return metadata;
    }

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