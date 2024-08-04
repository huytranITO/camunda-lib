package com.abb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@UtilityClass
public class JsonUtil {

  private static final ModelMapper MODEL_MAPPER = new ModelMapper();


  public <T> String convertObject2String(T t, ObjectMapper objectMapper) {
    try {
      return objectMapper.writeValueAsString(t);
    } catch (JsonProcessingException e) {
      log.error("Parse object to string fail: ", e);
    }
    return null;
  }

  public <T> T convertObject2Object(Object s, Class<T> tClass, ObjectMapper objectMapper) {
    return objectMapper.convertValue(s, tClass);
  }

  public <T> T convertString2Object(String s, Class<T> tClass, ObjectMapper objectMapper) {
    try {
      return objectMapper.readValue(s, tClass);
    } catch (JsonProcessingException e) {
      log.error("Parse string to object fail: ", e);
    }
    return null;
  }

  public <T> T convertInputStream2Object(InputStream in, Class<T> tClass, ObjectMapper objectMapper) {
    try {
      return objectMapper.readValue(in, tClass);
    } catch (IOException e) {
      log.error("Parse string to object fail: ", e);
      return null;
    }
  }

  public <T> Set<T> convertString2Set(String s, Class<T> tClass, ObjectMapper objectMapper) {
    return readList(s, HashSet.class, tClass, objectMapper);
  }

  private <T> Set<T> readList(String str, Class<? extends Collection> type, Class<T> elementType,
      ObjectMapper objectMapper) {
    try {
      return objectMapper.readValue(str,
          objectMapper.getTypeFactory().constructCollectionType(type, elementType));
    } catch (IOException e) {
      log.error("Read list fail: ", e);
    }
    return Collections.emptySet();
  }

  public static <T> T toObject(Object obj, Class<T> type) {
    T t = null;
    if (obj != null) {
      try {
        t = MODEL_MAPPER.map(obj, type);
      } catch (Exception ex) {
        log.error("", ex);
      }

    }
    return t;
  }

  public <T> T convertBytes2Object(byte[] bytes, Class<T> tClass, ObjectMapper objectMapper) {
    try {
      if(bytes != null && bytes.length > 0) {
        return objectMapper.readValue(bytes, tClass);
      }
    } catch (IOException e) {
      log.error("Parse string to object fail: ", e);
    }
    return null;
  }

  public String convertBytes2String(byte[] bytes) {
    try {
      return new String(bytes, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.error("Parse bytes to string fail: ", e);
    }
    return null;
  }

  public static byte[] convertObject2Bytes(Object object, ObjectMapper objectMapper) {
    if (Objects.nonNull(object)) {
      return convertObject2String(object, objectMapper).getBytes(StandardCharsets.UTF_8);
    }
    return null;
  }
}
