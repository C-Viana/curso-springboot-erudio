package br.com.cviana.serialization.converters;

import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.annotation.JsonInclude;

public final class YamlJacksonToHttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    
    public YamlJacksonToHttpMessageConverter() {
        super(
            new YAMLMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL),
            MediaType.parseMediaType("application/yaml"));
    }

}
