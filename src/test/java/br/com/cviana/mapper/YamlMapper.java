package br.com.cviana.mapper;


import io.restassured.mapper.ObjectMapper;

public class YamlMapper {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ObjectMapper.class);

    /* 
     * SOLUÇÃO IMPLEMENTADA NO CURSO.
     * PARA UTILIZAR ESSA SOLUÇÃO, ADICIONE A SEGUINTE INTERFACE A ESSA CLASSE [implements io.restassured.mapper.ObjectMapper]
     * 
    private com.fasterxml.jackson.databind.ObjectMapper objMapper;
    protected com.fasterxml.jackson.databind.type.TypeFactory typeFactory;

    public YamlMapper() {
        this.objMapper = new com.fasterxml.jackson.databind.ObjectMapper(new com.fasterxml.jackson.dataformat.yaml.YAMLFactory())
            .disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.typeFactory = TypeFactory.defaultInstance();
    }

    @Override
    public Object deserialize(io.restassured.mapper.ObjectMapperDeserializationContext context) {
        var content = context.getDataToDeserialize().asString();
        Class<?> type = (Class<?>) context.getType();
        try {
            return objMapper.readValue(content, typeFactory.constructType(type));
        } catch (Exception e) {
            log.error("Error deserializing YAML to object: {}", e.getMessage());
            throw new IllegalArgumentException("Error deserializing YAML to object: {}" + e.getMessage());
        }
    }

    @Override
    public Object serialize(io.restassured.mapper.ObjectMapperSerializationContext context) {
        String content = "";
        try {
            content = objMapper.writeValueAsString(context.getObjectToSerialize());
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
        }
        return content;
    }
    */
    
    //SOLUÇÃO SUGERIDA PELA IA (Copilot)
    public static String toStringYaml(Object obj) {
        try {
            com.fasterxml.jackson.dataformat.yaml.YAMLMapper yamlMapper = new com.fasterxml.jackson.dataformat.yaml.YAMLMapper();
            return yamlMapper.writeValueAsString(obj);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Error converting object to YAML string: {}", e.getMessage());
            return null;
        }
    }
    
}
