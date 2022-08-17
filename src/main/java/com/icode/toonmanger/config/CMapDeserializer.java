package com.icode.toonmanger.config;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CMapDeserializer extends StdDeserializer<CMap> {
    ObjectMapper mapper = new ObjectMapper();
    public CMapDeserializer() {
        this(null);
    }

    public CMapDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CMap deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        Iterator<String> stringIterator = node.fieldNames();
        CMap ret = new CMap();
        while(stringIterator.hasNext()){
            String key = stringIterator.next();
            JsonNode item = node.get(key);
            if(item.isArray()){
                //List<CMap> oo = mapper.readValue(item, new TypeReference<List<CMap>>(){};
                List<CMap> oo = jp.getCodec().treeToValue(item, List.class);
                ret.put(key,oo);
            }else{
                if(item.isContainerNode()){
                    CMap oo = jp.getCodec().treeToValue(item, CMap.class);
                    ret.put(key,oo);
                }else{
                    ret.put(key,item.textValue());
                }
            }
        }

        return ret;
    }
}


