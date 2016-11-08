package icap.vv.de.subtitlepresenter.dao;

import com.activeandroid.serializer.TypeSerializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.verbavoice.parser.Subtitle;

/**
 * Created by Marko Nalis on 29.10.2015.
 */
public class SubtitleSerializer extends TypeSerializer{

    private final static Gson gson = new Gson();
    private final static  Type listType = new TypeToken<ArrayList<Subtitle>>(){}.getType();
    @Override
    public Class<?> getDeserializedType() {
        return List.class;
    }

    @Override
    public Class<?> getSerializedType() {
        return String.class;
    }

    @Override
    public Object serialize(Object data) {
        if(data == null)return null;
        return gson.toJson(data,listType);
    }

    @Override
    public List<Subtitle> deserialize(Object data) {
        if(null == data)return null;
        return gson.fromJson(data.toString(),listType);
    }
}
