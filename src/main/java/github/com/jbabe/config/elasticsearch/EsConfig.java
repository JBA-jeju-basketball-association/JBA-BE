package github.com.jbabe.config.elasticsearch;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EsConfig extends AbstractElasticsearchConfiguration {

    private final EsProperties esProperties;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        // https://discuss.elastic.co/t/localhost-nodename-nor-servname-provided-or-not-known/186173/11
        return new RestHighLevelClient(RestClient.builder(esProperties.httpHost()));
    }
}