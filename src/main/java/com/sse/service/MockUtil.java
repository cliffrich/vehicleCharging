package com.sse.service;

import com.sse.rest.EntityType;

public class MockUtil {
    public static String getMock(EntityType type){
        if(type.equals(EntityType.location))
            return getLocation();
        else if(type.equals(EntityType.asset))
            return getAsset();
        return null;
    }
    private static String getLocation(){
        return "{\n" +
                "\t\"id\":1,\n" +
                "\t\"name\":\"ABC Services\",\n" +
                "\t\"postcode\":\"AA1 1AA\",\n" +
                "\t\"assets\":[],\n" +
                "\t\"_links\":\n" +
                "\t\t{\n" +
                "\t\t\t\"self\":\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"href\":\"http://localhost/v1/api/location/1\"\n" +
                "\t\t\t},\n" +
                "\t\t\t\"locations\":\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"href\":\"http://localhost/v1/api/locations\"\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "}";
    }
    private static String getAsset(){
        return "{\n" +
                "\t\"id\":3,\n" +
                "\t\"name\":\"Insta volt\",\n" +
                "\t\"chargeType\":\"RAPID_AC\",\n" +
                "\t\"status\":\"AVAILABLE\",\n" +
                "\t\"location\":\n" +
                "\t{\n" +
                "\t\t\"id\":1,\n" +
                "\t\t\"name\":\"ABC Services\",\n" +
                "\t\t\"postcode\":\"AA1 1AA\",\n" +
                "\t\t\"assets\":[]\n" +
                "\t},\n" +
                "\t\"_links\":\n" +
                "\t{\n" +
                "\t\t\"self\":\n" +
                "\t\t{\n" +
                "\t\t\t\"href\":\"http://localhost/v1/api/asset/3\"\n" +
                "\t\t},\n" +
                "\t\t\"location\":\n" +
                "\t\t{\n" +
                "\t\t\t\"href\":\"http://localhost/v1/api/location/1\"\n" +
                "\t\t},\n" +
                "\t\t\"assets\":\n" +
                "\t\t{\n" +
                "\t\t\t\"href\":\"http://localhost/v1/api/assets\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";
    }
}
