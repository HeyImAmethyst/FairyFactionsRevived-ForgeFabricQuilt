package net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class FairyBedTextureAtlasSprite implements AutoCloseable
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private final FairyBedTextureAtlas atlas;
    private final ResourceLocation name;
    final int width;
    final int height;
    protected final NativeImage[] mainImage;
    @Nullable
    private final AnimatedTexture animatedTexture;
    private final int x;
    private final int y;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;

    public FairyBedTextureAtlasSprite(FairyBedTextureAtlas textureAtlas, Info info, int i, int j, int k, int l, int m, NativeImage nativeImage) {
        this.atlas = textureAtlas;
        this.width = info.width;
        this.height = info.height;
        this.name = info.name;
        this.x = l;
        this.y = m;
        this.u0 = (float)l / (float)j;
        this.u1 = (float)(l + this.width) / (float)j;
        this.v0 = (float)m / (float)k;
        this.v1 = (float)(m + this.height) / (float)k;
        this.animatedTexture = this.createTicker(info, nativeImage.getWidth(), nativeImage.getHeight(), i);

        CrashReport crashReport;
        CrashReportCategory crashReportCategory;
        try {
            try {
                this.mainImage = MipmapGenerator.generateMipLevels(new NativeImage[]{nativeImage}, i);
            } catch (Throwable var12) {
                crashReport = CrashReport.forThrowable(var12, "Generating mipmaps for frame");
                crashReportCategory = crashReport.addCategory("Frame being iterated");
                crashReportCategory.setDetail("First frame", () -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }

                    stringBuilder.append(nativeImage.getWidth()).append("x").append(nativeImage.getHeight());
                    return stringBuilder.toString();
                });
                throw new ReportedException(crashReport);
            }
        } catch (Throwable var13) {
            crashReport = CrashReport.forThrowable(var13, "Applying mipmap");
            crashReportCategory = crashReport.addCategory("Sprite being mipmapped");
            ResourceLocation var10002 = this.name;
            Objects.requireNonNull(var10002);
            crashReportCategory.setDetail("Sprite name", var10002::toString);
            crashReportCategory.setDetail("Sprite size", () -> {
                return this.width + " x " + this.height;
            });
            crashReportCategory.setDetail("Sprite frames", () -> {
                return this.getFrameCount() + " frames";
            });
            crashReportCategory.setDetail("Mipmap levels", i);
            throw new ReportedException(crashReport);
        }
    }

    private int getFrameCount() {
        return this.animatedTexture != null ? this.animatedTexture.frames.size() : 1;
    }

    @Nullable
    private AnimatedTexture createTicker(Info info, int i, int j, int k) {
        ModAnimationMetadataSection animationMetadataSection = info.metadata;
        int l = i / animationMetadataSection.getFrameWidth(info.width);
        int m = j / animationMetadataSection.getFrameHeight(info.height);
        int n = l * m;
        List<FrameInfo> list = Lists.newArrayList();
        animationMetadataSection.forEachFrame((ix, jx) -> {
            list.add(new FrameInfo(ix, jx));
        });
        int o;
        if (list.isEmpty()) {
            for(o = 0; o < n; ++o) {
                list.add(new FrameInfo(o, animationMetadataSection.getDefaultFrameTime()));
            }
        } else {
            o = 0;
            IntSet intSet = new IntOpenHashSet();

            for(Iterator<FrameInfo> iterator = list.iterator(); iterator.hasNext(); ++o) {
                FrameInfo frameInfo = (FrameInfo)iterator.next();
                boolean bl = true;
                if (frameInfo.time <= 0) {
                    LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", new Object[]{this.name, o, frameInfo.time});
                    bl = false;
                }

                if (frameInfo.index < 0 || frameInfo.index >= n) {
                    LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", new Object[]{this.name, o, frameInfo.index});
                    bl = false;
                }

                if (bl) {
                    intSet.add(frameInfo.index);
                } else {
                    iterator.remove();
                }
            }

            int[] is = IntStream.range(0, n).filter((ix) -> {
                return !intSet.contains(ix);
            }).toArray();
            if (is.length > 0) {
                LOGGER.warn("Unused frames in sprite {}: {}", this.name, Arrays.toString(is));
            }
        }

        if (list.size() <= 1) {
            return null;
        } else {
            InterpolationData interpolationData = animationMetadataSection.isInterpolatedFrames() ? new InterpolationData(info, k) : null;
            return new AnimatedTexture(ImmutableList.copyOf(list), l, interpolationData);
        }
    }

    void upload(int i, int j, NativeImage[] nativeImages) {
        for(int k = 0; k < this.mainImage.length; ++k) {
            nativeImages[k].upload(k, this.x >> k, this.y >> k, i >> k, j >> k, this.width >> k, this.height >> k, this.mainImage.length > 1, false);
        }

    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float getU0() {
        return this.u0;
    }

    public float getU1() {
        return this.u1;
    }

    public float getU(double d) {
        float f = this.u1 - this.u0;
        return this.u0 + f * (float)d / 16.0F;
    }

    public float getUOffset(float f) {
        float g = this.u1 - this.u0;
        return (f - this.u0) / g * 16.0F;
    }

    public float getV0() {
        return this.v0;
    }

    public float getV1() {
        return this.v1;
    }

    public float getV(double d) {
        float f = this.v1 - this.v0;
        return this.v0 + f * (float)d / 16.0F;
    }

    public float getVOffset(float f) {
        float g = this.v1 - this.v0;
        return (f - this.v0) / g * 16.0F;
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public FairyBedTextureAtlas atlas() {
        return this.atlas;
    }

    public IntStream getUniqueFrames() {
        return this.animatedTexture != null ? this.animatedTexture.getUniqueFrames() : IntStream.of(1);
    }

    public void close() {
        NativeImage[] var1 = this.mainImage;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            NativeImage nativeImage = var1[var3];
            if (nativeImage != null) {
                nativeImage.close();
            }
        }

        if (this.animatedTexture != null) {
            this.animatedTexture.close();
        }

    }

    public String toString() {
        ResourceLocation var10000 = this.name;
        return "TextureAtlasSprite{name='" + var10000 + "', frameCount=" + this.getFrameCount() + ", x=" + this.x + ", y=" + this.y + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.u0 + ", u1=" + this.u1 + ", v0=" + this.v0 + ", v1=" + this.v1 + "}";
    }

    public boolean isTransparent(int i, int j, int k) {
        int l = j;
        int m = k;
        if (this.animatedTexture != null) {
            l = j + this.animatedTexture.getFrameX(i) * this.width;
            m = k + this.animatedTexture.getFrameY(i) * this.height;
        }

        return (this.mainImage[0].getPixelRGBA(l, m) >> 24 & 255) == 0;
    }

    public void uploadFirstFrame() {
        if (this.animatedTexture != null) {
            this.animatedTexture.uploadFirstFrame();
        } else {
            this.upload(0, 0, this.mainImage);
        }

    }

    private float atlasSize() {
        float f = (float)this.width / (this.u1 - this.u0);
        float g = (float)this.height / (this.v1 - this.v0);
        return Math.max(g, f);
    }

    public float uvShrinkRatio() {
        return 4.0F / this.atlasSize();
    }

    @Nullable
    public Tickable getAnimationTicker() {
        return this.animatedTexture;
    }

    public VertexConsumer wrap(VertexConsumer vertexConsumer) {
        return new ModSpriteCoordinateExpander(vertexConsumer, this);
    }

    @Environment(EnvType.CLIENT)
    public static final class Info {
        final ResourceLocation name;
        final int width;
        final int height;
        final ModAnimationMetadataSection metadata;

        public Info(ResourceLocation resourceLocation, int i, int j, ModAnimationMetadataSection animationMetadataSection) {
            this.name = resourceLocation;
            this.width = i;
            this.height = j;
            this.metadata = animationMetadataSection;
        }

        public ResourceLocation name() {
            return this.name;
        }

        public int width() {
            return this.width;
        }

        public int height() {
            return this.height;
        }
    }

    @Environment(EnvType.CLIENT)
    private class AnimatedTexture implements Tickable, AutoCloseable {
        int frame;
        int subFrame;
        final List<FrameInfo> frames;
        private final int frameRowSize;
        @Nullable
        private final InterpolationData interpolationData;

        AnimatedTexture(List<FrameInfo> list, int i, @Nullable InterpolationData interpolationData) {
            this.frames = list;
            this.frameRowSize = i;
            this.interpolationData = interpolationData;
        }

        int getFrameX(int i) {
            return i % this.frameRowSize;
        }

        int getFrameY(int i) {
            return i / this.frameRowSize;
        }

        private void uploadFrame(int i) {
            int j = this.getFrameX(i) * FairyBedTextureAtlasSprite.this.width;
            int k = this.getFrameY(i) * FairyBedTextureAtlasSprite.this.height;
            FairyBedTextureAtlasSprite.this.upload(j, k, FairyBedTextureAtlasSprite.this.mainImage);
        }

        public void close() {
            if (this.interpolationData != null) {
                this.interpolationData.close();
            }

        }

        public void tick() {
            ++this.subFrame;
            FrameInfo frameInfo = (FrameInfo)this.frames.get(this.frame);
            if (this.subFrame >= frameInfo.time) {
                int i = frameInfo.index;
                this.frame = (this.frame + 1) % this.frames.size();
                this.subFrame = 0;
                int j = ((FrameInfo)this.frames.get(this.frame)).index;
                if (i != j) {
                    this.uploadFrame(j);
                }
            } else if (this.interpolationData != null) {
                if (!RenderSystem.isOnRenderThread()) {
                    RenderSystem.recordRenderCall(() -> {
                        this.interpolationData.uploadInterpolatedFrame(this);
                    });
                } else {
                    this.interpolationData.uploadInterpolatedFrame(this);
                }
            }

        }

        public void uploadFirstFrame() {
            this.uploadFrame(((FrameInfo)this.frames.get(0)).index);
        }

        public IntStream getUniqueFrames() {
            return this.frames.stream().mapToInt((frameInfo) -> {
                return frameInfo.index;
            }).distinct();
        }
    }

    @Environment(EnvType.CLIENT)
    static class FrameInfo {
        final int index;
        final int time;

        FrameInfo(int i, int j) {
            this.index = i;
            this.time = j;
        }
    }

    @Environment(EnvType.CLIENT)
    private final class InterpolationData implements AutoCloseable {
        private final NativeImage[] activeFrame;

        InterpolationData(Info info, int i) {
            this.activeFrame = new NativeImage[i + 1];

            for(int j = 0; j < this.activeFrame.length; ++j) {
                int k = info.width >> j;
                int l = info.height >> j;
                if (this.activeFrame[j] == null) {
                    this.activeFrame[j] = new NativeImage(k, l, false);
                }
            }

        }

        void uploadInterpolatedFrame(AnimatedTexture animatedTexture) {
            FrameInfo frameInfo = (FrameInfo)animatedTexture.frames.get(animatedTexture.frame);
            double d = 1.0 - (double)animatedTexture.subFrame / (double)frameInfo.time;
            int i = frameInfo.index;
            int j = ((FrameInfo)animatedTexture.frames.get((animatedTexture.frame + 1) % animatedTexture.frames.size())).index;
            if (i != j) {
                for(int k = 0; k < this.activeFrame.length; ++k) {
                    int l = FairyBedTextureAtlasSprite.this.width >> k;
                    int m = FairyBedTextureAtlasSprite.this.height >> k;

                    for(int n = 0; n < m; ++n) {
                        for(int o = 0; o < l; ++o) {
                            int p = this.getPixel(animatedTexture, i, k, o, n);
                            int q = this.getPixel(animatedTexture, j, k, o, n);
                            int r = this.mix(d, p >> 16 & 255, q >> 16 & 255);
                            int s = this.mix(d, p >> 8 & 255, q >> 8 & 255);
                            int t = this.mix(d, p & 255, q & 255);
                            this.activeFrame[k].setPixelRGBA(o, n, p & -16777216 | r << 16 | s << 8 | t);
                        }
                    }
                }

                FairyBedTextureAtlasSprite.this.upload(0, 0, this.activeFrame);
            }

        }

        private int getPixel(AnimatedTexture animatedTexture, int i, int j, int k, int l) {
            return FairyBedTextureAtlasSprite.this.mainImage[j].getPixelRGBA(k + (animatedTexture.getFrameX(i) * FairyBedTextureAtlasSprite.this.width >> j), l + (animatedTexture.getFrameY(i) * FairyBedTextureAtlasSprite.this.height >> j));
        }

        private int mix(double d, int i, int j) {
            return (int)(d * (double)i + (1.0 - d) * (double)j);
        }

        public void close() {
            NativeImage[] var1 = this.activeFrame;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                NativeImage nativeImage = var1[var3];
                if (nativeImage != null) {
                    nativeImage.close();
                }
            }

        }
    }
}
