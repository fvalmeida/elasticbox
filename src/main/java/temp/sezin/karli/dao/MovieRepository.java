package temp.sezin.karli.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import temp.sezin.karli.model.Movie;

import java.util.List;

/**
 * @author sezin karli
 * @since 2/28/15 1:56 PM
 *        User: Sezin Karli
 */
public interface MovieRepository extends ElasticsearchRepository<Movie, Long> {
    public List<Movie> findByName(String name);

    public List<Movie> findByRatingBetween(Double beginning, Double end);
}
