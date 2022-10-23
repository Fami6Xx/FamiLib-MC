package me.familib.misc.FamiModuleHandler;

import org.json.simple.JSONObject;

import java.lang.reflect.Field;

public class ModuleSettings {
    // I highly recommend that you put in settings only primitive data types!

    /**
     * Gets all Fields of this settings and returning JSONObject of them.
     * @return JSONObject which contains all Fields of settings
     * @throws IllegalAccessException if this Field object is enforcing Java language access control and the underlying field is inaccessible.
     */
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
