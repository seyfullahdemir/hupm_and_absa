package tr.edu.metu.ceng.absa.aspectextraction.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JSonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static JSonUtil instance = null;

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JSonUtil() {

    }

    public static JSonUtil getInstance() {
        if(instance == null) {
            instance = new JSonUtil();
        }
        return instance;
    }

    public String convertToJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    public <T> T convertJsonStringToObject(Class<T> clazz, String jsonStr) throws IOException {
        return (T)objectMapper.readValue(jsonStr, clazz);
    }


    public String getControlCharacterFreeString(final String param) {
        return param == null ? null : param.replaceAll("\\p{Cntrl}", "");
    }
}