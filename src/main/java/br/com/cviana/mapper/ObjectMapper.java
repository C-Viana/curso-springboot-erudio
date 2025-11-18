package br.com.cviana.mapper;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

public class ObjectMapper {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ObjectMapper.class);
    
    private static final Mapper mapper = DozerBeanMapperBuilder.buildDefault();
    
    public static <O, D> D parseObject(O origin, Class<D> destination) {
        log.info("Parse object {} to {}", origin, destination);
        return mapper.map(origin, destination);
    }

    public static <O, D> java.util.List<D> parseListObjects(java.util.List<O> origin, Class<D> destination) {
        log.info("Parse list of objects {} to {}", origin, destination);
        return origin.stream()
                     .map(element -> parseObject(element, destination))
                     .collect(java.util.stream.Collectors.toList());
    }
    
}
