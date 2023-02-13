package me.familib.apis;

import me.familib.FamiLib;
import me.familib.apis.modules.HoloAPI.HoloAPI;
import me.familib.apis.modules.MenuManager.MenuManager;
import me.familib.apis.modules.Trees.Trees;
import me.familib.misc.FamiModuleHandler.AModuleHandler;
import me.familib.misc.FamiModuleHandler.ModuleSettings;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Level;

public class FamiModuleHandler {
    private final ArrayList<AModuleHandler> modules = new ArrayList<>();
    private final String path = FamiLib.getInstance().getDataFolder().getPath() + "/settings.json";
    private JSONObject settings = new JSONObject();

    public FamiModuleHandler(){
        // Initialize all modules and start those, whom are configured to do so

        modules.add(new HoloAPI());
        modules.add(new Trees());
        modules.add(new MenuManager());

        try {
            JSONObject obj  = getSavedSettings();
            if(obj != null) {
                settings = obj;
                initializeSettings();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initialize();
        tryEnabling("Trees");
    }


    /**
     * Run at start of ModuleHandler, it enables modules which can't be disabled
     */
    private void initialize(){
        modules.forEach(module -> {
            if(!module.canBeDisabled())
                tryEnabling(module.getName());
        });
    }

    /**
     * Force disables all modules
     */
    public void disableAll(){
        modules.forEach(handler -> {
            if(handler.isEnabled())
                handler.setEnabled(false, true);
        });
        updateSettings();
        saveSettings();
    }

    /**
     * Tries to find and enable module
     *
     * @param name Name of module you want to enable
     * @return True if module was successfully enabled or has already been enabled ,or false if the module can't be initialized or wasn't found
     */
    public boolean tryEnabling(String name){
        Optional<AModuleHandler> optional =
                modules.stream()
                        .filter(handler -> handler.getName().equals(name))
                        .filter(AModuleHandler::canBeDisabled)
                        .findFirst();

        if(!optional.isPresent()) return false;

        if(optional.get().isEnabled()) return true;

        return optional.get().setEnabled(true);
    }

    /**
     * Tries to find and disable module
     *
     * @param name Name of module you want to disable
     * @return True if module was successfully disabled or has already been disabled ,or false if module can't be disabled or wasn't found
     */
    public boolean tryDisabling(String name){
        Optional<AModuleHandler> optional =
                modules.stream()
                        .filter(handler -> handler.getName().equals(name))
                        .filter(AModuleHandler::canBeDisabled)
                        .findFirst();

        if(!optional.isPresent()) return false;

        if(!optional.get().isEnabled()) return true;

        return optional.get().setEnabled(false);
    }

    /**
     * Finds module you specified
     *
     * @param name Name of module you want to query
     * @return The module you wanted if it finds one, otherwise null
     */
    @Nullable
    public AModuleHandler getModule(String name){
        Optional<AModuleHandler> optional =
                modules.stream()
                    .filter(handler -> handler.getName().equals(name))
                    .findFirst();

        return optional.orElse(null);
    }

    /**
     * Updates fields in setting classes to gathered settings from json file.
     */
    private void initializeSettings(){
        Iterator<String> names = settings.keys();
        while(names.hasNext()){
            String moduleName = names.next();

            AModuleHandler module = getModule(moduleName);
            if(module == null) continue;

            ModuleSettings moduleSettings = module.getModuleSettings();
            if(moduleSettings == null) continue;

            JSONObject fieldSettings = (JSONObject) settings.get(moduleName);

            for(Field field : moduleSettings.getClass().getDeclaredFields()){
                if(!fieldSettings.has(field.getName())) continue;

                try {
                    field.setAccessible(true);
                    Object fieldValue = fieldSettings.get(field.getName());
                    if(fieldValue.getClass().getName().equalsIgnoreCase("java.math.BigDecimal")){
                        fieldValue = ((BigDecimal) fieldValue).doubleValue();
                    }
                    field.set(moduleSettings, fieldValue);
                }catch (IllegalAccessException exc){
                    exc.printStackTrace();
                }
            }
        }
    }

    /**
     * Updates all modules settings to settings object in this file
     */
    public void updateSettings(){
        for(AModuleHandler moduleHandler : modules){
            updateLocalSettings(moduleHandler);
        }
    }

    /**
     * Updates settings in this class from given module
     * @param moduleHandler ModuleHandler which has some modified fields and needs to update them in this class
     */
    public void updateSettings(AModuleHandler moduleHandler){
        updateLocalSettings(moduleHandler);
    }

    private void updateLocalSettings(AModuleHandler moduleHandler) {
        if(moduleHandler.getModuleSettings() == null) return;

        try{
            if(settings.has(moduleHandler.getName())) {
                settings.remove(moduleHandler.getName());
            }
            settings.put(moduleHandler.getName(), moduleHandler.getModuleSettings().getAllValues());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Saves settings to JSON file
     */
    private void saveSettings(){
        FileWriter file = null;

        if(!FamiLib.getFamiLib().getDataFolder().exists())
            FamiLib.getFamiLib().getDataFolder().mkdir();

        try{
            file = new FileWriter(path);
            file.write(settings.toString());
        }catch (IOException exc){
            exc.printStackTrace();
        }finally {
            try{
                if(file != null) {
                    file.flush();
                    file.close();
                }
            }catch (IOException exc){
                exc.printStackTrace();
            }
        }
    }

    /**
     * Gets all saved settings for modules
     * @return JSONObject which contains saved settings for modules
     * @throws IOException If file is not found, then it throws IOException
     * @throws JSONException If there was a syntax error in settings.json
     */
    private JSONObject getSavedSettings() throws IOException, JSONException {
        File file = new File(path);
        if(file.exists()){
            InputStream is = new FileInputStream(path);
            String jsonText = IOUtils.toString(is, StandardCharsets.UTF_8);
            return new JSONObject(jsonText);
        }else{
            if(!FamiLib.getFamiLib().getDataFolder().exists())
                FamiLib.getFamiLib().getDataFolder().mkdir();
            file.createNewFile();
            return null;
        }
    }
}
