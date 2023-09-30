package net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyBedEntity;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSectionSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FairyBedTextureAtlas extends AbstractTexture implements Tickable
{
    private static final Logger LOGGER = LogUtils.getLogger();
    @Deprecated
    public static final ResourceLocation LOCATION_BLOCKS = InventoryMenu.BLOCK_ATLAS;
    @Deprecated
    public static final ResourceLocation LOCATION_PARTICLES = new ResourceLocation("textures/atlas/particles.png");
    private static final String FILE_EXTENSION = ".png";
    private final List<Tickable> animatedTextures = Lists.newArrayList();
    private final Set<ResourceLocation> sprites = Sets.newHashSet();
    private final Map<ResourceLocation, FairyBedTextureAtlasSprite> texturesByName = Maps.newHashMap();
    private final ResourceLocation location;
    private final int maxSupportedTextureSize;

    public FairyBedTextureAtlas(ResourceLocation resourceLocation)
    {
        this.location = resourceLocation;
        maxSupportedTextureSize = RenderSystem.maxSupportedTextureSize();
    }

    @Override
    public void load(ResourceManager resourceManager) {
    }

    public void reload(Preparations preparations) {
        this.sprites.clear();
        this.sprites.addAll(preparations.sprites);
        LOGGER.info("Created: {}x{}x{} {}-atlas", preparations.width, preparations.height, preparations.mipLevel, this.location);
        TextureUtil.prepareImage(this.getId(), preparations.mipLevel, preparations.width, preparations.height);
        this.clearTextureData();
        for (FairyBedTextureAtlasSprite textureAtlasSprite : preparations.regions) {
            this.texturesByName.put(textureAtlasSprite.getName(), textureAtlasSprite);
            try {
                textureAtlasSprite.uploadFirstFrame();
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.forThrowable(throwable, "Stitching texture atlas");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Texture being stitched together");
                crashReportCategory.setDetail("Atlas path", this.location);
                crashReportCategory.setDetail("Sprite", textureAtlasSprite);
                throw new ReportedException(crashReport);
            }
            Tickable tickable = textureAtlasSprite.getAnimationTicker();
            if (tickable == null) continue;
            this.animatedTextures.add(tickable);
        }
    }

    public Preparations prepareToStitch(ResourceManager resourceManager, Stream<ResourceLocation> stream, ProfilerFiller profilerFiller, int i) {
        int m;
        profilerFiller.push("preparing");
        Set<ResourceLocation> set = stream.peek(resourceLocation -> {
            if (resourceLocation == null) {
                throw new IllegalArgumentException("Location cannot be null!");
            }
        }).collect(Collectors.toSet());
        int j = this.maxSupportedTextureSize;
        FairyBedTextureStitcher stitcher = new FairyBedTextureStitcher(j, j, i);
        int k = Integer.MAX_VALUE;
        int l = 1 << i;
        profilerFiller.popPush("extracting_frames");
        for (FairyBedTextureAtlasSprite.Info info2 : this.getBasicSpriteInfos(resourceManager, set)) {
            k = Math.min(k, Math.min(info2.width(), info2.height()));
            m = Math.min(Integer.lowestOneBit(info2.width()), Integer.lowestOneBit(info2.height()));
            if (m < l) {
                LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", info2.name(), info2.width(), info2.height(), Mth.log2(l), Mth.log2(m));
                l = m;
            }

            int index = 9;

            for (FairyBedEntity.LogType logType: FairyBedEntity.LogType.values())
            {
                if(Objects.equals(info2.name(), new ResourceLocation(FairyFactions.MOD_ID,"block/" + logType.getName() + "_log")))
                {
                    index = 0;
                }

                if(Objects.equals(info2.name(), new ResourceLocation(FairyFactions.MOD_ID,"block/" + logType.getName() + "_log_top")))
                {
                    index = 1;
                }

            }

            if(Objects.equals(info2.name(), new ResourceLocation(FairyFactions.MOD_ID,"block/framed_white_wool")))
            {
                index = 4;
            }

            for (FairyBedEntity.WoolType woolType: FairyBedEntity.WoolType.values())
            {
                if(Objects.equals(info2.name(), new ResourceLocation(FairyFactions.MOD_ID,"block/" + woolType.getName() + "_wool")))
                {
                    index = 6;
                }
            }

            if(Objects.equals(info2.name(), new ResourceLocation(FairyFactions.MOD_ID,"block/chain")))
            {
                index = 2;
            }

            if(Objects.equals(info2.name(), new ResourceLocation(FairyFactions.MOD_ID,"block/glowstone")))
            {
                index = 3;
            }

            if(Objects.equals(info2.name(), new ResourceLocation(FairyFactions.MOD_ID,"block/iron_block")))
            {
                index = 5;
            }

            if(Objects.equals(info2.name(), new ResourceLocation("block/gold_block")))
            {
                //stitcher.registerSprite(info2, 7);
                index = 7;
            }

            stitcher.registerSprite(info2, index);
        }
        int n = Math.min(k, l);
        int o = Mth.log2(n);
        if (o < i) {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.location, i, o, n);
            m = o;
        } else {
            m = i;
        }
        profilerFiller.popPush("register");
        stitcher.registerSprite(MissingFairyBedTextureAtlasSprite.info(), 100);
        profilerFiller.popPush("stitching");
        try {
            stitcher.stitch();
        } catch (StitcherException stitcherException) {
            CrashReport crashReport = CrashReport.forThrowable(stitcherException, "Stitching");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Stitcher");
            crashReportCategory.setDetail("Sprites", stitcherException.getAllSprites().stream().map(info -> String.format("%s[%dx%d]", info.name(), info.width(), info.height())).collect(Collectors.joining(",")));
            crashReportCategory.setDetail("Max Texture Size", j);
            throw new ReportedException(crashReport);
        }
        profilerFiller.popPush("loading");
        List<FairyBedTextureAtlasSprite> list = this.getLoadedSprites(resourceManager, stitcher, m);
        profilerFiller.pop();
        return new Preparations(set, stitcher.getWidth(), stitcher.getHeight(), m, list);
    }

//    private Collection<TextureAtlasSprite.Info> getBasicSpriteInfos(ResourceManager resourceManager, Set<ResourceLocation> set) {
//        ArrayList<CompletableFuture<Void>> list = Lists.newArrayList();
//        ConcurrentLinkedQueue<TextureAtlasSprite.Info> queue = new ConcurrentLinkedQueue<TextureAtlasSprite.Info>();
//        for (ResourceLocation resourceLocation : set) {
//            if (MissingTextureAtlasSprite.getLocation().equals(resourceLocation)) continue;
//            list.add(CompletableFuture.runAsync(() -> {
//                TextureAtlasSprite.Info info;
//                ResourceLocation resourceLocation2 = this.getResourceLocation(resourceLocation);
//                try (Resource resource = resourceManager.getResource(resourceLocation2);){
//                    PngInfo pngInfo = new PngInfo(resource.toString(), resource.getInputStream());
//                    AnimationMetadataSection animationMetadataSection = resource.getMetadata(AnimationMetadataSection.SERIALIZER);
//                    if (animationMetadataSection == null) {
//                        animationMetadataSection = AnimationMetadataSection.EMPTY;
//                    }
//                    Pair<Integer, Integer> pair = animationMetadataSection.getFrameSize(pngInfo.width, pngInfo.height);
//                    info = new TextureAtlasSprite.Info(resourceLocation, pair.getFirst(), pair.getSecond(), animationMetadataSection);
//                } catch (RuntimeException runtimeException) {
//                    LOGGER.error("Unable to parse metadata from {} : {}", (Object)resourceLocation2, (Object)runtimeException);
//                    return;
//                } catch (IOException iOException) {
//                    LOGGER.error("Using missing texture, unable to load {} : {}", (Object)resourceLocation2, (Object)iOException);
//                    return;
//                }
//                queue.add(info);
//            }, Util.backgroundExecutor()));
//        }
//        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
//        return queue;
//    }

    private Collection<FairyBedTextureAtlasSprite.Info> getBasicSpriteInfos(ResourceManager resourceManager, Set<ResourceLocation> set) {
        List<CompletableFuture<?>> list = Lists.newArrayList();
        Queue<FairyBedTextureAtlasSprite.Info> queue = new ConcurrentLinkedQueue();
        Iterator var5 = set.iterator();

        while(var5.hasNext()) {
            ResourceLocation resourceLocation = (ResourceLocation)var5.next();
            if (!MissingTextureAtlasSprite.getLocation().equals(resourceLocation)) {
                list.add(CompletableFuture.runAsync(() -> {
                    ResourceLocation resourceLocation2 = this.getResourceLocation(resourceLocation);
                    Optional<Resource> optional = resourceManager.getResource(resourceLocation2);
                    if (optional.isEmpty()) {
                        LOGGER.error("Using missing texture, file {} not found", resourceLocation2);
                    } else {
                        Resource resource = (Resource)optional.get();

                        PngInfo pngInfo;
                        try {
                            InputStream inputStream = resource.open();

                            try {
                                Objects.requireNonNull(resourceLocation2);
                                pngInfo = new PngInfo(resourceLocation2::toString, inputStream);
                            } catch (Throwable var14) {
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (Throwable var12) {
                                        var14.addSuppressed(var12);
                                    }
                                }

                                throw var14;
                            }

                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (IOException var15) {
                            LOGGER.error("Using missing texture, unable to load {} : {}", resourceLocation2, var15);
                            return;
                        }

                        ModAnimationMetadataSection animationMetadataSection;
                        try {
                            animationMetadataSection = (ModAnimationMetadataSection)resource.metadata().getSection(ModAnimationMetadataSection.SERIALIZER).orElse(ModAnimationMetadataSection.EMPTY);
                        } catch (Exception var13) {
                            LOGGER.error("Unable to parse metadata from {} : {}", resourceLocation2, var13);
                            return;
                        }

                        Pair<Integer, Integer> pair = animationMetadataSection.getFrameSize(pngInfo.width, pngInfo.height);
                        FairyBedTextureAtlasSprite.Info info = new FairyBedTextureAtlasSprite.Info(resourceLocation, (Integer)pair.getFirst(), (Integer)pair.getSecond(), animationMetadataSection);
                        queue.add(info);
                    }
                }, Util.backgroundExecutor()));
            }
        }

        CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
        return queue;
    }

//    private List<TextureAtlasSprite> getLoadedSprites(ResourceManager resourceManager, FairyBedTextureStitcher stitcher, int i) {
//        ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
//        ArrayList list = Lists.newArrayList();
//        stitcher.gatherSprites((info, j, k, l, m) -> {
//            if (info == MissingTextureAtlasSprite.info()) {
//                MissingTextureAtlasSprite missingTextureAtlasSprite = MissingTextureAtlasSprite.newInstance(this, i, j, k, l, m);
//                queue.add(missingTextureAtlasSprite);
//            } else {
////                list.add(CompletableFuture.runAsync(() -> {
////                    TextureAtlasSprite textureAtlasSprite = null;
////                    try
////                    {
////                        textureAtlasSprite = this.load(resourceManager, info, j, k, i, l, m);
////                    }
////                    catch (IOException e)
////                    {
////                        throw new RuntimeException(e);
////                    }
////                    if (textureAtlasSprite != null) {
////                        queue.add(textureAtlasSprite);
////                    }
////                }, Util.backgroundExecutor()));
//
//                list.add(CompletableFuture.runAsync(() -> {
//                    TextureAtlasSprite textureatlassprite = null;
//                    try
//                    {
//                        textureatlassprite = this.load(resourceManager, info, j, k, i, l, m);
//                    }
//                    catch (IOException e)
//                    {
//                        throw new RuntimeException(e);
//                    }
//                    if (textureatlassprite != null) {
//                        queue.add(textureatlassprite);
//                    }
//
//                }, Util.backgroundExecutor()));
//            }
//        });
//        //CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
//        CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
//        return Lists.newArrayList(queue);
//    }

    private List<FairyBedTextureAtlasSprite> getLoadedSprites(ResourceManager resourceManager, FairyBedTextureStitcher stitcher, int i) {
        Queue<FairyBedTextureAtlasSprite> queue = new ConcurrentLinkedQueue();
        List<CompletableFuture<?>> list = Lists.newArrayList();
        stitcher.gatherSprites((info, j, k, l, m) -> {
            if (info == MissingFairyBedTextureAtlasSprite.info()) {
                MissingFairyBedTextureAtlasSprite missingTextureAtlasSprite = MissingFairyBedTextureAtlasSprite.newInstance(this, i, j, k, l, m);
                queue.add(missingTextureAtlasSprite);
            } else {
                list.add(CompletableFuture.runAsync(() -> {
                    FairyBedTextureAtlasSprite textureAtlasSprite = this.load(resourceManager, info, j, k, i, l, m);
                    if (textureAtlasSprite != null) {
                        queue.add(textureAtlasSprite);
                    }

                }, Util.backgroundExecutor()));
            }

        });
        CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
        return Lists.newArrayList(queue);
    }

//    @Nullable
//    private TextureAtlasSprite load(ResourceManager resourceManager, TextureAtlasSprite.Info info, int i, int j, int k, int l, int m) throws IOException
//    {
//        TextureAtlasSprite textureAtlasSprite;
//        block9: {
//            ResourceLocation resourceLocation = this.getResourceLocation(info.name());
//            Resource resource = resourceManager.getResource(resourceLocation);
//            try {
//                NativeImage nativeImage = NativeImage.read(resource.getInputStream());
//                textureAtlasSprite = new TextureAtlasSprite(this, info, k, i, j, l, m, nativeImage);
//                if (resource == null) break block9;
//            } catch (Throwable throwable) {
//                try {
//                    if (resource != null) {
//                        try {
//                            resource.close();
//                        } catch (Throwable throwable2) {
//                            throwable.addSuppressed(throwable2);
//                        }
//                    }
//                    throw throwable;
//                } catch (RuntimeException runtimeException) {
//                    LOGGER.error("Unable to parse metadata from {}", (Object)resourceLocation, (Object)runtimeException);
//                    return null;
//                } catch (IOException iOException) {
//                    LOGGER.error("Using missing texture, unable to load {}", (Object)resourceLocation, (Object)iOException);
//                    return null;
//                }
//            }
//            resource.close();
//        }
//        return textureAtlasSprite;
//    }

    @Nullable
    private FairyBedTextureAtlasSprite load(ResourceManager resourceManager, FairyBedTextureAtlasSprite.Info info, int i, int j, int k, int l, int m) {
        ResourceLocation resourceLocation = this.getResourceLocation(info.name());

        try {
            InputStream inputStream = resourceManager.open(resourceLocation);

            FairyBedTextureAtlasSprite var11;
            try {
                NativeImage nativeImage = NativeImage.read(inputStream);
                var11 = new FairyBedTextureAtlasSprite(this, info, k, i, j, l, m, nativeImage);
            } catch (Throwable var13) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                    }
                }

                throw var13;
            }

            if (inputStream != null) {
                inputStream.close();
            }

            return var11;
        } catch (RuntimeException var14) {
            LOGGER.error("Unable to parse metadata from {}", resourceLocation, var14);
            return null;
        } catch (IOException var15) {
            LOGGER.error("Using missing texture, unable to load {}", resourceLocation, var15);
            return null;
        }
    }

    private ResourceLocation getResourceLocation(ResourceLocation resourceLocation) {
        return new ResourceLocation(resourceLocation.getNamespace(), String.format("textures/%s%s", resourceLocation.getPath(), FILE_EXTENSION));
    }

    public void cycleAnimationFrames() {
        this.bind();
        for (Tickable tickable : this.animatedTextures) {
            tickable.tick();
        }
    }

    @Override
    public void tick() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::cycleAnimationFrames);
        } else {
            this.cycleAnimationFrames();
        }
    }

    public FairyBedTextureAtlasSprite getSprite(ResourceLocation resourceLocation) {
        FairyBedTextureAtlasSprite textureAtlasSprite = this.texturesByName.get(resourceLocation);
        if (textureAtlasSprite == null) {
            return this.texturesByName.get(MissingTextureAtlasSprite.getLocation());
        }
        return textureAtlasSprite;
    }

    public void clearTextureData() {
        for (FairyBedTextureAtlasSprite textureAtlasSprite : this.texturesByName.values()) {
            textureAtlasSprite.close();
        }
        this.texturesByName.clear();
        this.animatedTextures.clear();
    }

    public ResourceLocation location() {
        return this.location;
    }

    public void updateFilter(Preparations preparations)
    {
        this.setFilter(false, preparations.mipLevel > 0);
    }

    @Environment(EnvType.CLIENT)
    public static class Preparations
    {
        public final Set<ResourceLocation> sprites;
        public final int width;
        public final int height;
        public final int mipLevel;
        public final List<FairyBedTextureAtlasSprite> regions;

        public Preparations(Set<ResourceLocation> set, int i, int j, int k, List<FairyBedTextureAtlasSprite> list) {
            this.sprites = set;
            this.width = i;
            this.height = j;
            this.mipLevel = k;
            this.regions = list;
        }
    }
}
