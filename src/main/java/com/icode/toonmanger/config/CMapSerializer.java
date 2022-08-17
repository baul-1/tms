package com.icode.toonmanger.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CMapSerializer extends JsonSerializer<CMap> {



    @Override
    public void serialize(CMap map, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        gen.writeStartObject();
        draw(map, gen, serializers);
        gen.writeEndObject();
    }

    private void draw(CMap map, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        Set<String> keys = map.keySet();
        for(String key : keys){
            gen.writeFieldName(key);
            Object obj = map.get(key);
            if(obj instanceof Map){
                gen.writeObject(obj);
            }else if(obj instanceof List){
                gen.writeObject(obj);
            }else if(obj instanceof Object[]){
                gen.writeObject(obj);
            }else{

                gen.writeString(obj.toString());
            }
        }
    }


}
