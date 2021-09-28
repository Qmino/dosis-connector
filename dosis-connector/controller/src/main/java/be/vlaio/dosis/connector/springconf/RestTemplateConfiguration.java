package be.vlaio.dosis.connector.springconf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({JacksonConfig.class})
public class RestTemplateConfiguration {

    private final int timeOut = 5000;

    @Autowired
    private ObjectMapper mapper;


    @Bean
    public RestTemplate getResttemplate() {
        RestTemplate template = new RestTemplate(getClientHttpRequestFactory());
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);
        template.getMessageConverters().add(0, converter);
        return template;
    }

    /**
     * @return een clientrequestfactory aan op basis van de gespecificeerde timeout.
     */
    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeOut);
        clientHttpRequestFactory.setReadTimeout(timeOut);
        clientHttpRequestFactory.setConnectionRequestTimeout(timeOut);
        return clientHttpRequestFactory;
    }
}
