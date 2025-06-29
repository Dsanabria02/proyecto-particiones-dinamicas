package server.fs;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class NodeAdapter implements JsonSerializer<Node>, JsonDeserializer<Node> {

    @Override
    public JsonElement serialize(Node node, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();

        json.addProperty("name", node.getName());
        json.addProperty("created", node.getCreated().toString());
        json.addProperty("modified", node.getModified().toString());
        json.addProperty("size", node.getSize());

        if (node instanceof FileNode fileNode) {
            json.addProperty("type", "file");
            json.addProperty("extension", fileNode.getExtension());
            json.addProperty("content", fileNode.getContent());
        } else if (node instanceof DirectoryNode dirNode) {
            json.addProperty("type", "directory");

            // Serializar los hijos (evitando ciclos)
            JsonArray children = new JsonArray();
            for (Node child : dirNode.getChildren()) {
                children.add(serialize(child, child.getClass(), context));
            }
            json.add("children", children);
        }

        return json;
    }

    @Override
    public Node deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        String type = obj.get("type").getAsString();

        switch (type) {
            case "directory":
                return context.deserialize(jsonElement, DirectoryNode.class);
            case "file":
                return context.deserialize(jsonElement, FileNode.class);
            default:
                throw new JsonParseException("Tipo desconocido: " + type);
        }
    }
}
