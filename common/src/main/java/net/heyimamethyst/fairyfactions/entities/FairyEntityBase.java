package net.heyimamethyst.fairyfactions.entities;

import net.heyimamethyst.fairyfactions.FairyConfigValues;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class FairyEntityBase extends Animal
{
    protected static final EntityDataAccessor<Byte> B_FLAGS = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Byte> B_TYPE = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.BYTE);           // skin, job, faction
    protected static final EntityDataAccessor<Byte> B_NAME_ORIG = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.BYTE);      // generated original name
    protected static final EntityDataAccessor<String> S_OWNER = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.STRING);      // owner name
    protected static final EntityDataAccessor<Byte> B_FLAGS2 = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.BYTE);         // capabilities, activities,
    // etc...
    protected static final EntityDataAccessor<Byte> B_HEALTH = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.BYTE);         // NB: UNUSED currently - was used in original mod
    protected static final EntityDataAccessor<String> S_NAME_REAL = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.STRING);         // custom name
    protected static final EntityDataAccessor<Integer> I_TOOL = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.INT);         // temporary tool

    public static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Boolean> EMOTIONAL = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Integer> WANTED_FOOD = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.INT);

    public static final int	MAX_SKIN	= 3;
    public static final int	MAX_JOB		= 3;
    public static final int	MAX_FACTION	= 15;
    public static final int	MAX_NAMEIDX	= 15;

    protected static final int	NJOB_NORMAL	= 0;
    protected static final int	NJOB_GUARD	= 1;
    protected static final int	NJOB_SCOUT	= 2;
    protected static final int	NJOB_MEDIC	= 3;
    protected static final int	SJOB_QUEEN	= 0;
    protected static final int	SJOB_ROGUE	= 1;

    //B_FLAGS
    protected static final int	FLAG_ARM_SWING		= 0;
    protected static final int	FLAG_FLY_MODE		= 1;
    protected static final int	FLAG_CAN_FLAP		= 2;
    protected static final int	FLAG_TAMED			= 3;
    protected static final int	FLAG_ANGRY			= 4;
    protected static final int	FLAG_CRYING			= 5;
    protected static final int	FLAG_LIFTOFF		= 6;
    protected static final int	FLAG_HEARTS			= 7;

    //B_FLAGS2
    protected static final int	FLAG2_CAN_HEAL		= 0;
    protected static final int	FLAG2_RARE_POTION	= 1;
    protected static final int	FLAG2_SPECIAL_JOB	= 2;
    protected static final int	FLAG2_NAME_ENABLED	= 3;
    protected static final int	FLAG2_CLIMBING		= 4;
    protected static final int	FLAG2_POSTED		= 5;
    protected static final int	FLAG2_WITHERED		= 6;
    protected static final int	FLAG2_HAIR_TYPE		= 7;

    // non-persistent fields
    public float                sinage;
    public boolean				createGroup;
    public int					listActions;
    public int					witherTime;
    protected ItemStack tempItem;
    protected boolean				isSwinging;
    protected int					particleCount;
    protected boolean				flyBlocked;

    public FairyEntityBase(EntityType<? extends Animal> p_27403_, Level p_27404_)
    {
        super(p_27403_, p_27404_);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_)
    {
        return null;
    }

//    @Override
//    public float getEyeHeight(Pose pPose)
//    {
//        if (!level.isClientSide && this.onGround && getRandom().nextBoolean())
//        {
//            int a = (int)Math.floor(position().x);
//            int b = (int)Math.floor(getBoundingBox().minY);
//            int c = (int)Math.floor(position().z);
//
//            if (FairyUtils.isAirySpace(this, a, b, c) && FairyUtils.isAirySpace(this, a, b + 1, c))
//            {
//                //return this.getDimensions(p_20237_).height * 1.375F;
//                return this.getBbHeight() * 1.375F;
//            }
//        }
//
//        return super.getEyeHeight(pPose);
//    }

//    @Override
//    public double getEyeY()
//    {
//        if (!level.isClientSide && this.onGround && getRandom().nextBoolean())
//        {
//            int a = (int)Math.floor(position().x);
//            int b = (int)Math.floor(getBoundingBox().minY);
//            int c = (int)Math.floor(position().z);
//
//            if (FairyUtils.isAirySpace(this, a, b, c) && FairyUtils.isAirySpace(this, a, b + 1, c))
//            {
//                return this.getEyeHeight() * 1.375D;
//            }
//        }
//
//        return super.getEyeY();
//    }


//    @Override
//    public boolean isInWall()
//    {
//        //WalkNodeEvaluator.getBlockPathTypeStatic(level, new BlockPos(i + l, k - 1, j + i1).mutable()) == BlockPathTypes.WALKABLE
//
//        for (int i = 0; i < 8; ++i)
//        {
//            float f = ( (float) ( ( i >> 0 ) % 2 ) - 0.5F ) * getBbWidth() * 0.8F;
//            float f1 = ( (float) ( ( i >> 1 ) % 2 ) - 0.5F ) * 0.1F;
//            float f2 = ( (float) ( ( i >> 2 ) % 2 ) - 0.5F ) * getBbWidth() * 0.8F;
//            int j = (int)Math.floor(position().x + (double) f);
//            int k = (int)Math.floor(position().y + (double) super.getEyeY() + (double) f1);
//            int l = (int)Math.floor(position().z + (double) f2);
//
//            if (!level.getBlockState(new BlockPos(j ,k, l)).isAir())
//            {
//                return true;
//            }
//        }
//
//        return false;
//    }

    // Fixes the head shaking glitch.

//    @Override
//    public void lookAt(Entity pEntity, float pMaxYRotIncrease, float pMaxXRotIncrease)
//    {
//        double d0 = pEntity.getX() - this.getX();
//        double d2 = pEntity.getZ() - this.getZ();
//        double d1;
//        if (pEntity instanceof LivingEntity)
//        {
//            LivingEntity livingentity = (LivingEntity)pEntity;
//            d1 = livingentity.getEyeY() - this.getEyeY();
//        }
//        else
//        {
//            d1 = (pEntity.getBoundingBox().minY + pEntity.getBoundingBox().maxY) / 2.0D - this.getEyeY();
//        }
//
//        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
//        float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
//        float f1 = (float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
//        this.setXRot(this.rotlerp(this.getXRot(), f1, pMaxXRotIncrease));
//        this.setYRot(this.rotlerp(this.getYRot(), f, pMaxYRotIncrease));
//    }
//
//    private float rotlerp(float pAngle, float pTargetAngle, float pMaxIncrease)
//    {
//        float f = Mth.wrapDegrees(pTargetAngle - pAngle);
//        if (f > pMaxIncrease) {
//            f = pMaxIncrease;
//        }
//
//        if (f < -pMaxIncrease) {
//            f = -pMaxIncrease;
//        }
//
//        return pAngle + f;
//    }

    // ---------- behaviors ----------

    // maximum number of times to try pathfinding
    public static final int		MAX_PATHING_TRIES	= 32;
    public static final float	PATH_TOWARD			= 0F;
    public static final float	PATH_AWAY			= (float) Math.PI;

    /**
     * if griniscule is 0F, entity2 will roam towards entity1. if griniscule is
     * pi, entity2 will roam away from entity1. Also, a griniscule is a
     * portmanteau of grin and miniscule.
     *
     * @param target
     * @param actor
     * @param griniscule
     * @return
     */

    public BlockPos roamBlockPos(Entity target, Entity actor, float griniscule)
    {
        return roamBlockPos(target.getBlockX(), target.getBlockY(), target.getBlockZ(), actor, griniscule);
    }

    public BlockPos roamBlockPos(double t, double u, double v, Entity actor, float griniscule)
    {
        // t is an X coordinate, u is a Y coordinate, v is a Z coordinate.
        // Griniscule of 0.0 means towards, 3.14 means away.

        double a = t - actor.position().x;
        double b = v - actor.position().z;
        double crazy = Math.atan2(a, b);
        crazy += ( getRandom().nextFloat() - getRandom().nextFloat() ) * 0.25D;
        crazy += griniscule;
        double c = actor.position().x + ( Math.sin(crazy) * 8F );
        double d = actor.position().z + ( Math.cos(crazy) * 8F );
        int x = (int)Math.floor(c);
        int y = (int)Math.floor(actor.getBoundingBox().minY
                + ( getRandom().nextFloat() * ( u - actor.getBoundingBox().minY ) ));
        int z = (int)Math.floor(d);

        for (int q = 0; q < MAX_PATHING_TRIES; q++)
        {
            int i = x + getRandom().nextInt(5) - getRandom().nextInt(5);
            int j = y + getRandom().nextInt(5) - getRandom().nextInt(5);
            int k = z + getRandom().nextInt(5) - getRandom().nextInt(5);

            if (j > 4 && j < level().getHeight() - 1 && FairyUtils.isAirySpace(this, i, j, k)
                    && !FairyUtils.isAirySpace(this, i, j - 1, k))
            {

                return new BlockPos(i, y, k);
            }
        }

        return (BlockPos) null;
    }

    // Can teleport to the ruler if they have an enderman drop in his inventory.
    public void teleportToRuler(LivingEntity entity)
    {
        int i = (int)Math.floor(entity.position().x) - 2;
        int j = (int)Math.floor(entity.position().z) - 2;
        int k = (int)Math.floor(entity.getBoundingBox().minY);

        for (int l = 0; l <= 4; l++)
        {
            for (int i1 = 0; i1 <= 4; i1++)
            {
                if (( l < 1 || i1 < 1 || l > 3 || i1 > 3 )
                        && level().getBlockState(new BlockPos(i + l, k - 1, j + i1)).isSolidRender(level(), new BlockPos(i + l, k - 1, j + i1))
                        && !level().getBlockState(new BlockPos(i + l, k, j + i1)).isSolidRender(level(), new BlockPos(i + l, k, j + i1))
                        && !level().getBlockState(new BlockPos(i + l, k + 1, j + i1)).isSolidRender(level(), new BlockPos(i + l, k + 1, j + i1))
                        && FairyUtils.isAirySpace(this, i + l, k, j + i1))
                {
                    moveTo((float) ( i + l ) + 0.5F, k, (float) ( j + i1 ) + 0.5F, getYRot(), getXRot());
                    return;
                }
            }
        }
    }

    public boolean canTeleportToRuler(Player player)
    {
        return player.getInventory() != null
                && (( player.getInventory().contains(new ItemStack(Items.ENDER_PEARL))
                || player.getInventory().contains(new ItemStack(Items.ENDER_EYE)) || FairyConfigValues.ALWAYS_FAIRY_TELEPORT));
    }

    // region ---------- B_TYPE ----------

    public int getSkin()
    {
        return this.entityData.get(B_TYPE) & 0x03;
    }

    public void setSkin(int skin)
    {
        if (skin < 0)
        {
            skin = 0;
        }
        else if (skin > MAX_SKIN)
        {
            skin = MAX_SKIN;
        }

        byte byte0 = this.entityData.get(B_TYPE);
        byte0 = (byte) ( byte0 & 0xfc );
        byte0 = (byte)(byte0 | (byte)skin);

        this.entityData.set(B_TYPE, Byte.valueOf(byte0));
    }

    public int getJob()
    {
        return (this.entityData.get(B_TYPE) >> 2 ) & 0x03;
    }

    public void setJob(int job)
    {
        if (job < 0)
        {
            job = 0;
        }
        else if (job > MAX_JOB)
        {
            job = MAX_JOB;
        }

        byte byte0 = this.entityData.get(B_TYPE);
        byte0 = (byte) ( byte0 & 0xf3 );
        byte0 = (byte)(byte0 | (byte)job << 2);

        this.entityData.set(B_TYPE, Byte.valueOf(byte0));
    }

    public boolean normal()
    {
        return getJob() == NJOB_NORMAL && !specialJob();
    }

    public boolean guard()
    {
        return getJob() == NJOB_GUARD && !specialJob();
    }

    public boolean scout()
    {
        return getJob() == NJOB_SCOUT && !specialJob();
    }

    public boolean medic()
    {
        return getJob() == NJOB_MEDIC && !specialJob();
    }

    public boolean queen()
    {
        return getJob() == SJOB_QUEEN && specialJob();
    }

    public boolean rogue()
    {
        return getJob() == SJOB_ROGUE && specialJob();
    }

    public int getFaction()
    {
        return (this.entityData.get(B_TYPE) >> 4 ) & 0x0f;
    }

    public void setFaction(int faction)
    {
        if (faction < 0)
        {
            faction = 0;
        }
        else if (faction > MAX_FACTION)
        {
            faction = MAX_FACTION;
        }

        byte byte0 = this.entityData.get(B_TYPE);
        byte0 = (byte) ( byte0 & 0x0f );
        byte0 |= (byte) faction << 4;

        this.entityData.set(B_TYPE, Byte.valueOf(byte0));
    }

    //endregion

    // region ---------- name ----------

    protected void setFairyName(int prefix, int suffix)
    {
        if (prefix < 0) {
            prefix = 0;
        } else if (prefix > 15) {
            prefix = 15;
        }

        if (suffix < 0) {
            suffix = 0;
        } else if (suffix > 15) {
            suffix = 15;
        }

        byte byte0 = (byte) ( ( (byte) prefix & 0x0f )
                | ( ( (byte) suffix & 0x0f ) << 4 ) );

        this.entityData.set(B_NAME_ORIG, Byte.valueOf(byte0));
    }

    public int getNamePrefix()
    {
        return (byte) this.entityData.get(B_NAME_ORIG) & 0x0f;
    }

    public int getNameSuffix()
    {
        return (byte) ( this.entityData.get(B_NAME_ORIG) >> 4 ) & 0x0f;
    }

    // Custom name of the fairy, enabled by paper.

    public String getFairyCustomName()
    {
        return this.entityData.get(S_NAME_REAL);
    }

    public void setFairyCustomName(@Nullable String s)
    {

        if(s != null && !s.isEmpty())
            FairyFactions.LOGGER.info("setFairyCustomName: "+this.getId()+" = "+ s);

        this.entityData.set(S_NAME_REAL, s);
    }

    @Override
    public void setCustomName(@Nullable Component component)
    {
        if(component != null && !component.getString().isEmpty())
            FairyFactions.LOGGER.info("setCustomName: "+this.getId()+" = "+ component.getString());

        super.setCustomName(component);
    }

    //endregion

    // region ---------- B_FLAGS | B_FLAGS2 -----------

    protected boolean getFairyFlag(EntityDataAccessor<Byte> object, int offset)
    {
        return (this.entityData.get(object) & ( 1 << offset )) != 0;
    }

    protected void setFairyFlag(EntityDataAccessor<Byte> object, int offset, boolean flag)
    {

        byte byte0 = this.entityData.get(object);

        if (flag)
        {
            byte0 |= 1 << offset;
        }
        else
        {
            byte0 &= ~( 1 << offset );
        }

        this.entityData.set(object, Byte.valueOf(byte0));
    }

    public boolean getArmSwing()
    {
        return getFairyFlag(B_FLAGS, FLAG_ARM_SWING);
    }

    public void armSwing(boolean flag)
    {
        setFairyFlag(B_FLAGS, FLAG_ARM_SWING, flag);
        setTempItem(null);
    }

    public boolean flymode()
    {
        return getFairyFlag(B_FLAGS, FLAG_FLY_MODE);
    }

    public void setFlymode(boolean flag)
    {
        setFairyFlag(B_FLAGS, FLAG_FLY_MODE, flag);
    }

    public boolean canFlap()
    {
        return getFairyFlag(B_FLAGS, FLAG_CAN_FLAP);
    }

    public void setCanFlap(boolean flag)
    {
        setFairyFlag(B_FLAGS, FLAG_CAN_FLAP, flag);
    }

    public boolean tamed()
    {
        return getFairyFlag(B_FLAGS, FLAG_TAMED);
    }

    public void setTamed(boolean flag)
    {
        setFairyFlag(B_FLAGS, FLAG_TAMED, flag);
    }

    public void setEmotional(boolean flag)
    {
        this.entityData.set(EMOTIONAL, flag);
    }

    public boolean isEmotional()
    {
        return this.entityData.get(EMOTIONAL);
    }

    public boolean angry()
    {
        return getFairyFlag(B_FLAGS, FLAG_ANGRY);
    }

    public void setAngry(boolean flag)
    {
        setFairyFlag(B_FLAGS, FLAG_ANGRY, flag);
    }

    public boolean crying()
    {
        return getFairyFlag(B_FLAGS, FLAG_CRYING);
    }

    public void setCrying(boolean flag)
    {
        setFairyFlag(B_FLAGS, FLAG_CRYING, flag);
    }

    public boolean liftOff()
    {
        return getFairyFlag(B_FLAGS, FLAG_LIFTOFF);
    }

    public void setLiftOff(boolean flag)
    {
        setFairyFlag(B_FLAGS, FLAG_LIFTOFF, flag);
    }

    public boolean hearts()
    {
        return getFairyFlag(B_FLAGS, FLAG_HEARTS);
    }

    public void setHearts(boolean flag)
    {
        setFairyFlag(B_FLAGS, FLAG_HEARTS, flag);
    }

    public boolean canHeal()
    {
        return getFairyFlag(B_FLAGS2, FLAG2_CAN_HEAL);
    }

    public void setCanHeal(boolean flag)
    {
        setFairyFlag(B_FLAGS2, FLAG2_CAN_HEAL, flag);
    }

    public boolean rarePotion()
    {
        return getFairyFlag(B_FLAGS2, FLAG2_RARE_POTION);
    }

    public void setRarePotion(boolean flag)
    {
        setFairyFlag(B_FLAGS2, FLAG2_RARE_POTION, flag);
    }

    public boolean specialJob()
    {
        return getFairyFlag(B_FLAGS2, FLAG2_SPECIAL_JOB);
    }

    public void setSpecialJob(boolean flag)
    {
        setFairyFlag(B_FLAGS2, FLAG2_SPECIAL_JOB, flag);
    }

    public boolean nameEnabled()
    {
        return getFairyFlag(B_FLAGS2, FLAG2_NAME_ENABLED);
    }

    public void setNameEnabled(boolean flag)
    {
        setFairyFlag(B_FLAGS2, FLAG2_NAME_ENABLED, flag);
    }

    public boolean climbing()
    {
        return getFairyFlag(B_FLAGS2, FLAG2_CLIMBING);
    }

    public void setClimbing(boolean flag)
    {
        setFairyFlag(B_FLAGS2, FLAG2_CLIMBING, flag);
    }

    public boolean posted()
    {
        return getFairyFlag(B_FLAGS2, FLAG2_POSTED);
    }

    public void setPosted(boolean flag)
    {
        //postedCount = 0;
        setFairyFlag(B_FLAGS2, FLAG2_POSTED, flag);
    }

    public boolean withered()
    {
        return getFairyFlag(B_FLAGS2, FLAG2_WITHERED);
    }

    public void setWithered(boolean flag)
    {
        setFairyFlag(B_FLAGS2, FLAG2_WITHERED, flag);
    }

    public boolean hairType()
    {
        return getFairyFlag(B_FLAGS2, FLAG2_HAIR_TYPE);
    }

    public void setHairType(boolean flag)
    {
        setFairyFlag(B_FLAGS2, FLAG2_HAIR_TYPE, flag);
    }

    //endregion

    public String rulerName()
    {
        final String name = this.entityData.get(S_OWNER);
        return name;
    }

    public void setRulerName(String s)
    {
        if( !s.isEmpty() )
            FairyFactions.LOGGER.info("setRulerName: "+this.getUUID() +" = "+s);

        this.entityData.set(S_OWNER, s);
    }

    // A temporary item shown while arm is swinging, related to jobs.
    public Item getTempItem()
    {
        return Item.byId(this.entityData.get(I_TOOL));
    }

    public void setTempItem(Item item)
    {
        this.entityData.set(I_TOOL, Item.getId(item));
    }

    public Item getWantedFoodItem()
    {
        return Item.byId(this.entityData.get(WANTED_FOOD));
    }

    public void setWantedFoodItem(Item item)
    {
        this.entityData.set(WANTED_FOOD, Item.getId(item));
    }
}
