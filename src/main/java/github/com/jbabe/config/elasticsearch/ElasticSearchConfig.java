package github.com.jbabe.config.elasticsearch;


import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.web.client.RestClient;

public class ElasticSearchConfig extends  AbstractElasticsearchConfiguration{

    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();
        return RestClient.create(clientConfiguration).rest();
    }
}
