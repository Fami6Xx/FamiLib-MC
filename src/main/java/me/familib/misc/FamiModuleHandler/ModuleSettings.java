package me.familib.misc.FamiModuleHandler;

import org.json.simple.JSONObject;

import java.lang.reflect.Field;

public class ModuleSettings {
    public JSONObject getAllValues() throws IllegalAccessException {
        JSONObject obj = new JSONObject();
        for (Field field :
                this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            obj.put(field.getName(), field.get(this));
        }
        return obj;
    }
}
