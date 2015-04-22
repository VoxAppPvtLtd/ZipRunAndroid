package com.ziprun.consumer.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CleanJsonObject {
    private static final String TAG = CleanJsonObject.class.getCanonicalName();

    private JsonObject jsonObject;

    public CleanJsonObject(JsonObject jsonObject){
        this.jsonObject = jsonObject;
    }
        
    public boolean getBoolean(String memberName, boolean defValue){
        JsonElement value = this.jsonObject.get(memberName);
        if(value == null || value.isJsonNull()){
            return defValue;
        }else{
            return value.getAsBoolean();
        }        
    }
    
    public String getString(String memberName, String defValue){
        JsonElement value = this.jsonObject.get(memberName);
        if(value == null || value.isJsonNull()){
            return defValue;
        }else{
            return value.getAsString();
        }        
    }
    
    public int getInt(String memberName, int defValue){
        JsonElement value = this.jsonObject.get(memberName);
        if(value == null || value.isJsonNull()){
            return defValue;
        }else{
            return value.getAsInt();
        }
    }

    public long getLong(String memberName, long defValue){
        JsonElement value = this.jsonObject.get(memberName);
        if(value == null || value.isJsonNull()){
            return defValue;
        }else{
            return value.getAsLong();
        }
    }

    public float getFloat(String memberName, float defValue){
        JsonElement value = this.jsonObject.get(memberName);
        if(value == null || value.isJsonNull()){
            return defValue;
        }else{
            return value.getAsFloat();
        }
    }

    public double getDouble(String memberName, double defValue){
        JsonElement value = this.jsonObject.get(memberName);
        if(value == null || value.isJsonNull()){
            return defValue;
        }else{
            return value.getAsDouble();
        }
    }

    public byte getByte(String memberName, byte defValue){
        JsonElement value = this.jsonObject.get(memberName);
        if(value == null || value.isJsonNull()){
            return defValue;
        }else{
            return value.getAsByte();
        }
    }

    public JsonObject getJsonObject(String memberName){
        JsonElement value = this.jsonObject.get(memberName);
        if(value == null || value.isJsonNull()){
            return null;
        }else{
            return value.getAsJsonObject();
        }

    }

    public JsonArray getJsonArray(String memberName){
        JsonElement value = this.jsonObject.get(memberName);
        if(value == null || value.isJsonNull()){
            return null;
        }else{
            return value.getAsJsonArray();
        }
    }
}
