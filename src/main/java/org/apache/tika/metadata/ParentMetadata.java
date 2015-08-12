package org.apache.tika.metadata;

import java.util.List;

/**
 * Created by fvalmeida on 8/12/15.
 */
public class ParentMetadata extends Metadata {

    private List<Metadata> embeddedMetadatas;

    public List<Metadata> getEmbeddedMetadatas() {
        return embeddedMetadatas;
    }

    public void setEmbeddedMetadatas(List<Metadata> metadatas) {
        this.embeddedMetadatas = metadatas;
    }

}
