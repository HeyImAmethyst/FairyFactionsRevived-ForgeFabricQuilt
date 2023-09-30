package net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture;
import java.util.Collection;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@Environment(EnvType.CLIENT)
public class ModStitcherException extends RuntimeException
{
    private final Collection<FairyBedTextureAtlasSprite.Info> allSprites;

    public ModStitcherException(FairyBedTextureAtlasSprite.Info info, Collection<FairyBedTextureAtlasSprite.Info> collection) {
        super(String.format(Locale.ROOT, "Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", info.name(), info.width(), info.height()));
        this.allSprites = collection;
    }

    public Collection<FairyBedTextureAtlasSprite.Info> getAllSprites() {
        return this.allSprites;
    }
}
