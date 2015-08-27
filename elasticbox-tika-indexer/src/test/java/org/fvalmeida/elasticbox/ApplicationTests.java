package org.fvalmeida.elasticbox;


import org.apache.commons.codec.digest.DigestUtils;
import org.elasticsearch.client.Client;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ApplicationTests {

    @Autowired
    private Client client;

    @Value("${index.name}")
    private String indexName;

    @Test
    public void contextLoads() {
    }

    @Test
    public void get() throws IOException {
        Assert.assertEquals(
                client.prepareGet(indexName, "metadata",
                        DigestUtils.sha256Hex(new FileInputStream("playground/test_recursive_embedded.docx")))
                        .execute().actionGet()
                        .isExists(),
                true);
//        SearchResponse response2 = client.prepareSearch().execute().actionGet();
    }
}