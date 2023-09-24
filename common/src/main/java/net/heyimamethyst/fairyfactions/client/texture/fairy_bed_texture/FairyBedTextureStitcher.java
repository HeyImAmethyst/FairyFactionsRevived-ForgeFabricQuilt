package net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.StitcherException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

@Environment(value=EnvType.CLIENT)

public class FairyBedTextureStitcher
{
    //private static final Comparator<Holder> HOLDER_COMPARATOR = Comparator.comparing(holder -> -holder.height).thenComparing(holder -> -holder.width);
    //.thenComparing(holder -> holder.spriteInfo.name());
    //private static final Comparator<Holder> HOLDER_COMPARATOR = Comparator.comparing(Holder::getHeight).thenComparing(Holder::getWidth).thenComparing(Holder::getSpriteInfoName);
    //private static final Comparator<Holder> HOLDER_COMPARATOR = Comparator.comparing(Holder::getHeight).thenComparing(Holder::getWidth);
//    private static final Comparator<Holder> HOLDER_COMPARATOR = Comparator.comparing((arg) -> {
//        return -arg.height;
//    }).thenComparing((arg) -> {
//        return -arg.width;
//    }).thenComparing((arg) -> {
//        return arg.spriteInfo.name();
//    });

    private static final Comparator<Holder> HOLDER_COMPARATOR = Comparator.comparing(Holder::getHeight).thenComparing(Holder::getWidth).thenComparingInt(Holder::getIndex);

    private final int mipLevel;
    private final Set<Holder> texturesToBeStitched = Sets.newHashSetWithExpectedSize(256);
    private final List<Region> storage = Lists.newArrayListWithCapacity(256);
    private int storageX;
    private int storageY;
    private final int maxWidth;
    private final int maxHeight;

    public FairyBedTextureStitcher(int i, int j, int k)
    {
        this.mipLevel = k;
        this.maxWidth = i;
        this.maxHeight = j;
    }

    public int getWidth()
    {
        return this.storageX;
    }

    public int getHeight()
    {
        return this.storageY;
    }

    public void registerSprite(TextureAtlasSprite.Info info, int index)
    {
        Holder holder = new Holder(info, this.mipLevel, index);
        this.texturesToBeStitched.add(holder);
    }

    public void stitch()
    {
        ArrayList<FairyBedTextureStitcher.Holder> list = Lists.newArrayList(this.texturesToBeStitched);
        list.sort(HOLDER_COMPARATOR);
        //System.out.println(list);
        for (FairyBedTextureStitcher.Holder holder2 : list)
        {
            if (this.addToStorage(holder2)) continue;
            throw new StitcherException(holder2.spriteInfo, list.stream().map(holder -> holder.spriteInfo).collect(ImmutableList.toImmutableList()));
        }
        this.storageX = Mth.smallestEncompassingPowerOfTwo(this.storageX);
        this.storageY = Mth.smallestEncompassingPowerOfTwo(this.storageY);
    }

    public void gatherSprites(SpriteLoader spriteLoader)
    {
        for (Region region2 : this.storage)
        {
            region2.walk(region ->
            {
                Holder holder = region.getHolder();
                TextureAtlasSprite.Info info = holder.spriteInfo;
                spriteLoader.load(info, this.storageX, this.storageY, region.getX(), region.getY());
            });
        }
    }

    static int smallestFittingMinTexel(int i, int j)
    {
        return (i >> j) + ((i & (1 << j) - 1) == 0 ? 0 : 1) << j;
    }

    private boolean addToStorage(Holder holder)
    {
        for (Region region : this.storage)
        {
            if (!region.add(holder)) continue;
            return true;
        }
        return this.expand(holder);
    }

    private boolean expand(Holder holder)
    {
        Region region;
        boolean bl5;
        boolean bl4;
        boolean bl2;
        int i = Mth.smallestEncompassingPowerOfTwo(this.storageX);
        int j = Mth.smallestEncompassingPowerOfTwo(this.storageY);
        int k = Mth.smallestEncompassingPowerOfTwo(this.storageX + holder.width);
        int l = Mth.smallestEncompassingPowerOfTwo(this.storageY + holder.height);
        boolean bl = k <= this.maxWidth;
        boolean bl3 = bl2 = l <= this.maxHeight;
        if (!bl && !bl2)
        {
            return false;
        }
        boolean bl32 = bl && i != k;
        boolean bl6 = bl4 = bl2 && j != l;
        if (bl32 ^ bl4)
        {
            bl5 = bl32;
        }
        else
        {
            boolean bl7 = bl5 = bl && i <= j;
        }
        if (bl5)
        {
            if (this.storageY == 0)
            {
                this.storageY = holder.height;
            }
            region = new Region(this.storageX, 0, holder.width, this.storageY);
            this.storageX += holder.width;
        }
        else
        {
            region = new Region(0, this.storageY, this.storageX, holder.height);
            this.storageY += holder.height;
        }
        region.add(holder);
        this.storage.add(region);
        return true;
    }

    @Environment(value= EnvType.CLIENT)
    static class Holder
    {
        public final TextureAtlasSprite.Info spriteInfo;
        public int width;
        public int height;

        public int index;

        public Holder(TextureAtlasSprite.Info info, int i, int index)
        {
            this.spriteInfo = info;
            width = smallestFittingMinTexel(info.width(), i);
            height = smallestFittingMinTexel(info.height(), i);
            this.index = index;
        }

        public String toString()
        {
            return "Holder{width=" + width + ", height=" + height + ", name=" + spriteInfo.name() + "}";
        }

        public int getHeight()
        {
            return height;
        }

        public int getWidth()
        {
            return width;
        }

        public int getIndex()
        {
            return index;
        }

        public TextureAtlasSprite.Info getSpriteInfo()
        {
            return spriteInfo;
        }

        public ResourceLocation getSpriteInfoName()
        {
            return spriteInfo.name();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Region
    {
        private final int originX;
        private final int originY;
        private final int width;
        private final int height;
        private List<Region> subSlots;
        private Holder holder;

        public Region(int i, int j, int k, int l)
        {
            this.originX = i;
            this.originY = j;
            this.width = k;
            this.height = l;
        }

        public Holder getHolder()
        {
            return this.holder;
        }

        public int getX()
        {
            return this.originX;
        }

        public int getY()
        {
            return this.originY;
        }

        public boolean add(Holder holder)
        {
            if (this.holder != null)
            {
                return false;
            }
            int i = holder.width;
            int j = holder.height;
            if (i > this.width || j > this.height)
            {
                return false;
            }
            if (i == this.width && j == this.height)
            {
                this.holder = holder;
                return true;
            }
            if (this.subSlots == null)
            {
                this.subSlots = Lists.newArrayListWithCapacity(1);
                this.subSlots.add(new Region(this.originX, this.originY, i, j));
                int k = this.width - i;
                int l = this.height - j;
                if (l > 0 && k > 0)
                {
                    int n;
                    int m = Math.max(this.height, k);
                    if (m >= (n = Math.max(this.width, l)))
                    {
                        this.subSlots.add(new Region(this.originX, this.originY + j, i, l));
                        this.subSlots.add(new Region(this.originX + i, this.originY, k, this.height));
                    }
                    else
                    {
                        this.subSlots.add(new Region(this.originX + i, this.originY, k, j));
                        this.subSlots.add(new Region(this.originX, this.originY + j, this.width, l));
                    }
                }
                else if (k == 0)
                {
                    this.subSlots.add(new Region(this.originX, this.originY + j, i, l));
                }
                else if (l == 0)
                {
                    this.subSlots.add(new Region(this.originX + i, this.originY, k, j));
                }
            }
            for (Region region : this.subSlots)
            {
                if (!region.add(holder)) continue;
                return true;
            }
            return false;
        }

        public void walk(Consumer<Region> consumer)
        {
            if (this.holder != null)
            {
                consumer.accept(this);
            }
            else if (this.subSlots != null)
            {
                for (Region region : this.subSlots)
                {
                    region.walk(consumer);
                }
            }
        }

        public String toString()
        {
            return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + "}";
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface SpriteLoader
    {
        public void load(TextureAtlasSprite.Info var1, int var2, int var3, int var4, int var5);
    }
}
