package com.example.SpringDemo.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalTime;

public class CustomLocalTimeDeserializer extends JsonDeserializer<LocalTime> {
    
    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        
        System.out.println("=== CUSTOM TIME DESERIALIZER DEBUG ===");
        System.out.println("Received node: " + node);
        System.out.println("Node type: " + node.getNodeType());
        System.out.println("Is object: " + node.isObject());
        System.out.println("Is textual: " + node.isTextual());
        
        // Handle object format: {"hour": 12, "minute": 30, "second": 0, "nano": 0}
        if (node.isObject()) {
            int hour = node.get("hour").asInt();
            int minute = node.get("minute").asInt();
            int second = node.has("second") ? node.get("second").asInt() : 0;
            int nano = node.has("nano") ? node.get("nano").asInt() : 0;
            
            LocalTime result = LocalTime.of(hour, minute, second, nano);
            System.out.println("Parsed from object: " + result);
            return result;
        }
        
        // Handle string format: "12:30:00"
        if (node.isTextual()) {
            String timeStr = node.asText();
            String[] parts = timeStr.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            int second = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
            
            LocalTime result = LocalTime.of(hour, minute, second);
            System.out.println("Parsed from string: " + result);
            return result;
        }
        
        System.err.println("Cannot deserialize LocalTime from: " + node);
        throw new IOException("Cannot deserialize LocalTime from: " + node);
    }
}
