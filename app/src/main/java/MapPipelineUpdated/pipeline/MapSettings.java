package MapPipelineUpdated.pipeline;

import android.util.Log;

import com.mapgenerator.android.util.LoggerConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class MapSettings {

    private JSONObject jsonObj;

    public MapSettings(String file){
        try {
            jsonObj = new JSONObject(file);
        } catch (JSONException e){
            if(LoggerConfig.ON){
                Log.w("JSON Parser", "Invalid JSON File");
                e.printStackTrace();
            }
        }
    }

    private MapSettings(JSONObject jsonObj){
        this.jsonObj = jsonObj;
    }

    public MapSettings pop(String key){
        try {
            JSONObject setting = jsonObj.getJSONObject(key);

            jsonObj.remove(key);
            return new MapSettings(setting);

        } catch(JSONException e){
            return null;
        }
    }

    public boolean isEmpty(){
        return jsonObj.length()==0;
    }

    public int length(){
        return jsonObj.length();
    }

    public Object get(String key) throws JSONException {
        return jsonObj.get(key);
    }

    public MapSettings getObjectSettings(String key){
        try{
            if(jsonObj.has(key) && jsonObj.get(key) instanceof JSONObject) {
                return new MapSettings((JSONObject) jsonObj.get(key));
            }else{
                if(LoggerConfig.ON){
                    if(!(jsonObj.has(key) && jsonObj.get(key) instanceof JSONObject)){
                        Log.w("Map Settings", "getJSONObject failed to get non-JSONObject by key: " + key);
                    }else{
                        Log.w("Map Settings", "getJSONObject failed to get key: " + key);
                    }
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        return null;
    }

    public Object smartGet(String key, Object defaultBounce){
        if(jsonObj.has(key)) {
            try {
                return jsonObj.get(key);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        if(LoggerConfig.ON){
            Log.w("Map Settings", "smartGet failed to get key: " + key + ". Defaulted to " + defaultBounce.toString());
        }
        return defaultBounce;
    }

}
