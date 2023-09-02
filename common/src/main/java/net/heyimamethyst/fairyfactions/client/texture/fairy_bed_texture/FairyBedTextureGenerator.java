package net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyBedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Environment(value= EnvType.CLIENT)
public class FairyBedTextureGenerator extends SimplePreparableReloadListener<Map<FairyBedTextureAtlas, TextureAtlas.Preparations>>
        implements AutoCloseable
{
    //private final TextureAtlas textureAtlas;

    public static FairyBedTextureGenerator INSTANCE = new FairyBedTextureGenerator(Minecraft.getInstance().getTextureManager(), "fairy_bed");

    public Map<Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType>, FairyBedTextureAtlas> FAIRY_BED_TEXTURE_ATLASES = new HashMap<>();

    private final String prefix;

    public FairyBedTextureGenerator(TextureManager textureManager, String string)
    {
        this.prefix = string;
        //this.textureAtlas = new TextureAtlas(resourceLocation);
        //textureManager.register(this.textureAtlas.location(), this.textureAtlas);

        for (FairyBedEntity.LogType logType : FairyBedEntity.LogType.values())
        {
            for (FairyBedEntity.WoolType woolType : FairyBedEntity.WoolType.values())
            {
                Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType> pair = Pair.of(logType, woolType);

                ResourceLocation resourceLocation = new ResourceLocation(FairyFactions.MOD_ID, "textures/atlas/entity/fairy_bed/" + logType.getName() + "_" + woolType.getName() + ".png");
                FAIRY_BED_TEXTURE_ATLASES.put(pair, new FairyBedTextureAtlas(resourceLocation));
            }
        }

        for (FairyBedTextureAtlas atlas : FAIRY_BED_TEXTURE_ATLASES.values())
        {
            textureManager.register(atlas.location(), atlas);
        }
    }

    protected Stream<ResourceLocation> getResourcesToLoad(FairyBedEntity.LogType logType, FairyBedEntity.WoolType woolType)
    {
//        Map<Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType>, ResourceLocation> map = new HashMap<>();
//
//        for (FairyBedEntity.LogType logType : FairyBedEntity.LogType.values())
//        {
//            for (FairyBedEntity.WoolType woolType : FairyBedEntity.WoolType.values())
//            {
//                Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType> pair = Pair.of(logType, woolType);
//                map.put(pair, ModModelLayers.createFairyBedModelName(logType, woolType));
//            }
//        }
//
//        return Stream.concat(Registry.MOTIVE.keySet().stream(), Stream.of(BACK_SPRITE_LOCATION));

        //return Registry.MOB_EFFECT.keySet().stream();

        ArrayList<ResourceLocation> textures = new ArrayList<>();

//        textures.add(new ResourceLocation("block/" + logType.getName() + "_log"));
//        textures.add(new ResourceLocation("block/" + logType.getName() + "_log_top"));
//        textures.add(new ResourceLocation("block/white_wool"));
//        textures.add(new ResourceLocation("block/" + woolType.getName() + "_wool"));
//        textures.add(new ResourceLocation("block/chain"));
//        textures.add(new ResourceLocation("block/glowstone"));
//        textures.add(new ResourceLocation("block/iron_block"));

//        textures.add(new ResourceLocation("block/chain"));
//        textures.add(new ResourceLocation("block/glowstone"));
//        textures.add(new ResourceLocation("block/iron_block"));
//        textures.add(new ResourceLocation("block/" + logType.getName() + "_log"));
//        textures.add(new ResourceLocation("block/" + logType.getName() + "_log_top"));
//        textures.add(new ResourceLocation("block/white_wool"));
//        textures.add(new ResourceLocation("block/" + woolType.getName() + "_wool"));

//        textures.add(new ResourceLocation("block/iron_block"));
//        textures.add(new ResourceLocation("block/glowstone"));
//        textures.add(new ResourceLocation("block/chain"));
//        textures.add(new ResourceLocation("block/" + woolType.getName() + "_wool"));
//        textures.add(new ResourceLocation("block/white_wool"));
//        textures.add(new ResourceLocation("block/" + logType.getName() + "_log_top"));
//        textures.add(new ResourceLocation("block/" + logType.getName() + "_log"));

//        textures.add(new ResourceLocation("block/white_wool"));
//        textures.add(new ResourceLocation("block/" + woolType.getName() + "_wool"));
//        textures.add(new ResourceLocation("block/" + logType.getName() + "_log_top"));
//        textures.add(new ResourceLocation("block/" + logType.getName() + "_log"));
//        textures.add(new ResourceLocation("block/iron_block"));
//        textures.add(new ResourceLocation("block/glowstone"));
//        textures.add(new ResourceLocation("block/chain"));

        textures.add(new ResourceLocation("block/" + woolType.getName() + "_wool"));
        textures.add(new ResourceLocation("block/iron_block"));
        textures.add(new ResourceLocation("block/white_wool"));
        textures.add(new ResourceLocation("block/glowstone"));
        textures.add(new ResourceLocation("block/" + logType.getName() + "_log_top"));
        textures.add(new ResourceLocation("block/chain"));
        textures.add(new ResourceLocation("block/" + logType.getName() + "_log"));

        return textures.stream();
    }

    public TextureAtlasSprite getSprite(ResourceLocation resourceLocation)
    {
        //return this.textureAtlas.getSprite(this.resolveLocation(resourceLocation));

        if(FAIRY_BED_TEXTURE_ATLASES != null)
        {
            for (TextureAtlas atlas : FAIRY_BED_TEXTURE_ATLASES.values())
            {
                return atlas.getSprite(this.resolveLocation(resourceLocation));
                //return atlas.getSprite(resourceLocation);
            }
        }

        return null;
    }

    private ResourceLocation resolveLocation(ResourceLocation resourceLocation)
    {
        System.out.println("FairyBedTextureGenerator-resolveLocation:" + resourceLocation);
        //return new ResourceLocation(resourceLocation.getNamespace(), this.prefix + "/" + resourceLocation.getPath());
        return new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath());
    }

    public static Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType> getKeyByValue(Map<Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType>, FairyBedTextureAtlas> map, TextureAtlas value)
    {
        for (Map.Entry<Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType>, FairyBedTextureAtlas> entry : map.entrySet())
        {
            if (Objects.equals(value, entry.getValue()))
            {
                return entry.getKey();
            }
        }

        return null;
    }

    protected Map<FairyBedTextureAtlas, FairyBedTextureAtlas.Preparations> prepareAtlas(ResourceManager resourceManager, ProfilerFiller profilerFiller)
    {
        profilerFiller.startTick();
        profilerFiller.push("stitching");
        //TextureAtlas.Preparations preparations = this.textureAtlas.prepareToStitch(resourceManager, this.getResourcesToLoad().map(this::resolveLocation), profilerFiller, 0);

        Map<FairyBedTextureAtlas, FairyBedTextureAtlas.Preparations> preparationsMap = new HashMap<>();

        for (FairyBedTextureAtlas atlas : FAIRY_BED_TEXTURE_ATLASES.values())
        {
            //TextureAtlas.Preparations preparations = atlas.prepareToStitch(resourceManager, this.getResourcesToLoad(getKeyByValue(FAIRY_BED_TEXTURE_ATLASES, atlas).getFirst(), getKeyByValue(FAIRY_BED_TEXTURE_ATLASES, atlas).getSecond()).map(this::resolveLocation), profilerFiller, 0);
            FairyBedTextureAtlas.Preparations preparations = atlas.prepareToStitch(resourceManager, this.getResourcesToLoad(getKeyByValue(FAIRY_BED_TEXTURE_ATLASES, atlas).getFirst(), getKeyByValue(FAIRY_BED_TEXTURE_ATLASES, atlas).getSecond()), profilerFiller, 0);
            preparationsMap.put(atlas, preparations);
        }

        profilerFiller.pop();
        profilerFiller.endTick();

        return preparationsMap;
    }

    @Override
    protected Map<FairyBedTextureAtlas, FairyBedTextureAtlas.Preparations> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller)
    {
        return this.prepareAtlas(resourceManager, profilerFiller);
    }

    @Override
    protected void apply(Map<FairyBedTextureAtlas, FairyBedTextureAtlas.Preparations> preparations, ResourceManager resourceManager, ProfilerFiller profilerFiller)
    {
        profilerFiller.startTick();
        profilerFiller.push("upload");

        //this.textureAtlas.reload(preparations);

        for (FairyBedTextureAtlas atlas : FAIRY_BED_TEXTURE_ATLASES.values())
        {
            atlas.reload(preparations.get(atlas));

//            //TextureManager textureManager = Minecraft.getInstance().getTextureManager();
//            //SimpleTexture texture = (SimpleTexture) textureManager.getTexture(atlas.location());
//
//            try
//            {
//                //SimpleTexture.TextureImage textureImage = texture.getTextureImage(resourceManager);
//                atlas.
//                NativeImage nativeImage = textureImage.getImage();
//                nativeImage.writeToFile("assets/fairyfactions/test/" + atlas.getId() + "png");
//            }
//            catch (IOException e)
//            {
//                throw new RuntimeException(e);
//            }

            //CopyAtlasTextureToDisk(resourceManager, atlas);
        }

        profilerFiller.pop();
        profilerFiller.endTick();
    }

    @Override
    public void close()
    {
        //this.textureAtlas.clearTextureData();
        for (FairyBedTextureAtlas atlas : FAIRY_BED_TEXTURE_ATLASES.values())
        {
            atlas.clearTextureData();
        }
    }

    public void CopyAtlasTextureToDisk(ResourceManager resourceManager, FairyBedTextureAtlas atlas)
    {
//        BufferedImage img = null;
//
//        try
//        {
////                var readIn = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader()
////                        .getResourceAsStream(atlas.location().getPath())), StandardCharsets.UTF_8));
//
//            var readIn = new BufferedReader(new InputStreamReader(resourceManager.getResource(atlas.location()).getInputStream(), StandardCharsets.UTF_8));
//
//            //var firstInteger = Integer.valueOf(readIn.readLine());
//
//            //img = ImageIO.read(new File(readIn.readLine()));
//
//            //File outputfile = new File("assets/fairyfactions/test/" + atlas.getId() + "png");
//            //ImageIO.write(img, "png", outputfile);
//
//            Resource resource = resourceManager.getResource(atlas.location());
//            NativeImage nativeImage = NativeImage.read(resource.getInputStream());
//            nativeImage.
//
//            img = ImageIO.read(resource.getInputStream());
//            ImageIO.write(img,"png",new File("assets/fairyfactions/test/" + atlas.getId() + "png"));
//        }
//        catch (IOException e)
//        {
//            throw new RuntimeException(e);
//        }

//        BufferedImage img = null;
//
//        Path path = Paths.get("assets/fairyfactions/test/" + atlas.getId() + "png");
//
//        InputStream inStream = null;
//
//        OutputStream outStream = null;
//
//        if(!Files.exists(path))
//        {
//            try
//            {
//                //var readIn = new BufferedReader(new InputStreamReader(Minecraft.getInstance().getResourceManager().getResource(textureAtlas.location()).getInputStream(), StandardCharsets.UTF_8));
//                //var readIn = new BufferedReader(new InputStreamReader(Minecraft.getInstance().getResourceManager().getResource(textureAtlas.location()).getInputStream(), StandardCharsets.UTF_8));
//
//                //img = ImageIO.read(new File(readIn.readLine()));
//                //img = ImageIO.read(Minecraft.getInstance().getResourceManager().getResource(textureAtlas.location()).getInputStream());
//
//                inStream = resourceManager.getResource(atlas.location()).getInputStream();
//
//                File outputfile = new File("assets/fairyfactions/test/" + atlas.getId() + "png");
//                outStream = new FileOutputStream(outputfile);
//
//                //ImageIO.write(img, "png", outputfile);
//
//                byte[] buffer = new byte[1024];
//
//                int length;
//
//                while ((length = inStream.read(buffer)) > 0)
//                {
//                    outStream.write(buffer, 0, length);
//                }
//
//                System.out.println("File is copied successful!");
//            }
//            catch (IOException e)
//            {
//                throw new RuntimeException(e);
//            }
//            finally
//            {
//
//                try {
//
//                    if (inStream != null)
//                    {
//
//                        inStream.close();
//
//                    }
//
//                    if (outStream != null)
//                    {
//
//                        outStream.close();
//
//                    }
//
//                }
//                catch (IOException e)
//                {
//
//                    e.printStackTrace();
//
//                }
//
//            }
//        }

        InputStream in = null;
        FileOutputStream out = null;

        try
        {

            //in = new FileInputStream("input.jpg");
            //out = new FileOutputStream("output.jpg");

            Resource resource = resourceManager.getResource(atlas.location());

            in = resource.getInputStream();
            out = new FileOutputStream("assets/fairyfactions/test/" + atlas.getId() + "png");

            //System.out.println(atlas.location());
            //in = resourceManager.getResource(atlas.location()).getInputStream();

//            File file = new File("assets/fairyfactions/" + atlas.location().getPath());
//
//            if (file.createNewFile())
//            {
//                System.out.println("File created: " + file.getName());
//            }
//            else
//            {
//                System.out.println("File already exists.");
//            }

 //           in = new BufferedInputStream(new FileInputStream(file.getPath()));
//            out = new FileOutputStream("assets/fairyfactions/test/" + atlas.getId() + "png");


            //NativeImage nativeImage = NativeImage.read(resource.getInputStream());

            int c;

            while ((c = in.read()) != -1)
            {
                out.write(c);
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found: " + atlas.location());
        }
        catch (IOException e)
        {
            System.out.println("I/O Error: " + e);
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }

                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
                System.out.println("Error closing file: " + e);
            }
        }
    }
}
