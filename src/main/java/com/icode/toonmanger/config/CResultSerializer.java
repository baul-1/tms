package com.icode.toonmanger.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CResultSerializer  extends JsonSerializer<CResult> {

    @Override
    public void serialize(CResult cresult, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        CMap head = cresult.getHead();


        gen.writeStartObject();
            gen.writeFieldName("head");
            {
                gen.writeStartObject();
                draw(head,gen,serializers);
                gen.writeEndObject();
            }

            gen.writeFieldName("body");
            {
                gen.writeStartObject();
                draw(cresult, gen, serializers);
                gen.writeEndObject();
            }
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
                if(obj==null) gen.writeString("");
                else gen.writeString(obj.toString());
            }
        }
    }
}
