package net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.heyimamethyst.fairyfactions.entities.ai.fairy_job.FairyJobManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MissingFairyBedTextureAtlasSprite extends FairyBedTextureAtlasSprite
{
    private static final int MISSING_IMAGE_WIDTH = 16;
    private static final int MISSING_IMAGE_HEIGHT = 16;
    private static final String MISSING_TEXTURE_NAME = "missingno";
    private static final ResourceLocation MISSING_TEXTURE_LOCATION = new ResourceLocation("missingno");
    @Nullable
    private static DynamicTexture missingTexture;
    private static final LazyLoadedValue<NativeImage> MISSING_IMAGE_DATA = new LazyLoadedValue(() -> {
        NativeImage nativeImage = new NativeImage(16, 16, false);
        int i = -16777216;
        int j = -524040;

        for(int k = 0; k < 16; ++k) {
            for(int l = 0; l < 16; ++l) {
                if (k < 8 ^ l < 8) {
                    nativeImage.setPixelRGBA(l, k, -524040);
                } else {
                    nativeImage.setPixelRGBA(l, k, -16777216);
                }
            }
        }

        nativeImage.untrack();
        return nativeImage;
    });
    private static final FairyBedTextureAtlasSprite.Info INFO;

    private MissingFairyBedTextureAtlasSprite(FairyBedTextureAtlas textureAtlas, int i, int j, int k, int l, int m) {
        super(textureAtlas, INFO, i, j, k, l, m, (NativeImage)MISSING_IMAGE_DATA.get());
    }

    public static MissingFairyBedTextureAtlasSprite newInstance(FairyBedTextureAtlas textureAtlas, int i, int j, int k, int l, int m) {
        return new MissingFairyBedTextureAtlasSprite(textureAtlas, i, j, k, l, m);
    }

    public static ResourceLocation getLocation() {
        return MISSING_TEXTURE_LOCATION;
    }

    public static FairyBedTextureAtlasSprite.Info info() {
        return INFO;
    }

    public void close() {
        for(int i = 1; i < this.mainImage.length; ++i) {
            this.mainImage[i].close();
        }

    }

    public static DynamicTexture getTexture() {
        if (missingTexture == null) {
            missingTexture = new DynamicTexture((NativeImage)MISSING_IMAGE_DATA.get());
            Minecraft.getInstance().getTextureManager().register(MISSING_TEXTURE_LOCATION, missingTexture);
        }

        return missingTexture;
    }

    static {
        INFO = new FairyBedTextureAtlasSprite.Info(MISSING_TEXTURE_LOCATION, 16, 16, new ModAnimationMetadataSection(ImmutableList.of(new AnimationFrame(0, -1)), 16, 16, 1, false));
    }
}
