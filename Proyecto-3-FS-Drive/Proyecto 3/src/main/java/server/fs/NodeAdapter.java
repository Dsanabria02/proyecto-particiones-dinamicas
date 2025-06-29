package server.fs;

import com.google.gson.*;
import java.lang.reflect.Type;

public class NodeAdapter implements JsonSerializer<Node>, JsonDeserializer<Node> {

    @Override
    public JsonElement serialize(Node src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = context.serialize(src).getAsJsonObject();
        obj.addProperty("type", src instanceof DirectoryNode ? "directory" : "file");
        return obj;
    }

    @Override
    public Node deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String type = obj.get("type").getAsString();

        switch (type) {
            case "directory":
                return context.deserialize(json, DirectoryNode.class);
            case "file":
                return context.deserialize(json, FileNode.class);
            default:
                throw new JsonParseException("Tipo desconocido: " + type);
        }
    }
}
