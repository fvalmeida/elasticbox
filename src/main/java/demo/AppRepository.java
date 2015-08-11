package org.fvalmeida.searchbox.service;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AppRepository extends ElasticsearchRepository<String, Long> {
}
