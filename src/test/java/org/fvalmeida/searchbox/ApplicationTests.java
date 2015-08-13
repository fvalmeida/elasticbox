package org.fvalmeida.searchbox;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.index.IndexResponse;
import org.fvalmeida.searchbox.service.AppService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Slf4j
public class ApplicationTests {

    @Autowired
    private AppService service;

    @Test
    public void contextLoads() {
    }

    @Test
    public void tikaIndexTest() {

        try {
            ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            Resource[] tikaFiles = patternResolver.getResources("classpath*:tika/**");

            List<Future<List<Pair<String, IndexResponse>>>> futures = new ArrayList<>();
            for (Resource tikaFile : tikaFiles) {
                futures.add(service.parse(tikaFile.getFile()));
            }

            for (Future<List<Pair<String, IndexResponse>>> future : futures) {
                for (Pair<String, IndexResponse> pair : future.get()) {
                    log.info(pair.getLeft());
                    log.info(ToStringBuilder.reflectionToString(pair.getRight()));
                }
            }

        } catch (Exception e) {
            log.error("{}", e);
        }

    }
}
