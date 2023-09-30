package net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ModAnimationMetadataSectionSerializer implements MetadataSectionSerializer<ModAnimationMetadataSection>
{
    public ModAnimationMetadataSectionSerializer() {
    }

    public ModAnimationMetadataSection fromJson(JsonObject jsonObject) {
        ImmutableList.Builder<AnimationFrame> builder = ImmutableList.builder();
        int i = GsonHelper.getAsInt(jsonObject, "frametime", 1);
        if (i != 1) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid default frame time");
        }

        int j;
        if (jsonObject.has("frames")) {
            try {
                JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "frames");

                for(j = 0; j < jsonArray.size(); ++j) {
                    JsonElement jsonElement = jsonArray.get(j);
                    AnimationFrame animationFrame = this.getFrame(j, jsonElement);
                    if (animationFrame != null) {
                        builder.add(animationFrame);
                    }
                }
            } catch (ClassCastException var8) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonObject.get("frames"), var8);
            }
        }

        int k = GsonHelper.getAsInt(jsonObject, "width", -1);
        j = GsonHelper.getAsInt(jsonObject, "height", -1);
        if (k != -1) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)k, "Invalid width");
        }

        if (j != -1) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)j, "Invalid height");
        }

        boolean bl = GsonHelper.getAsBoolean(jsonObject, "interpolate", false);
        return new ModAnimationMetadataSection(builder.build(), k, j, i, bl);
    }

    @Nullable
    private AnimationFrame getFrame(int i, JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return new AnimationFrame(GsonHelper.convertToInt(jsonElement, "frames[" + i + "]"));
        } else if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "frames[" + i + "]");
            int j = GsonHelper.getAsInt(jsonObject, "time", -1);
            if (jsonObject.has("time")) {
                Validate.inclusiveBetween(1L, 2147483647L, (long)j, "Invalid frame time");
            }

            int k = GsonHelper.getAsInt(jsonObject, "index");
            Validate.inclusiveBetween(0L, 2147483647L, (long)k, "Invalid frame index");
            return new AnimationFrame(k, j);
        } else {
            return null;
        }
    }

    public String getMetadataSectionName() {
        return "animation";
    }
}
