package com.amg.dinningphilosophers.JSon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public
class JSonController {
static     ObjectMapper objectMapper = new ObjectMapper();


    public static String objectToStringMapper(Object object){

        String string = new String();

        try {
            string = objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return string;
    }
    public static <T> T stringToObjectMapper(String string,Class<T> type){
        if(string==null)return null;

        try {
            return objectMapper.readValue(string,type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static <T> T stringToObjectMapper(String string, TypeReference <T> valueTypeRef){

        try {
            return objectMapper.readValue(string,valueTypeRef);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
