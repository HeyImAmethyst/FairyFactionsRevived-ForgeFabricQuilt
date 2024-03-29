package net.heyimamethyst.fairyfactions.entities;

import com.mojang.datafixers.util.Pair;
import net.heyimamethyst.fairyfactions.client.model.ModModelLayers;
import net.heyimamethyst.fairyfactions.items.IWandable;
import net.heyimamethyst.fairyfactions.registry.ModEntities;
import net.heyimamethyst.fairyfactions.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class FairyBedEntity extends Entity implements IWandable
{
    private static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_HURTDIR = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_LOG_TYPE = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_WOOL_TYPE = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<String> FAIRY_OWNER = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.STRING);
    private double lastYd;

    public FairyBedEntity(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public FairyBedEntity(Level level, double d, double e, double f)
    {
        this((EntityType<? extends FairyBedEntity>) ModEntities.FAIRY_BED_ENTITY.get(), level);
        this.setPos(d, e, f);
        this.xo = d;
        this.yo = e;
        this.zo = f;
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return entityDimensions.height;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData()
    {
        this.entityData.define(DATA_ID_HURT, 0);
        this.entityData.define(DATA_ID_HURTDIR, 1);
        this.entityData.define(DATA_ID_DAMAGE, Float.valueOf(0.0f));

        //this.entityData.define(DATA_ID_TYPE, Type.OAK_WHITE.ordinal());
        this.entityData.define(DATA_ID_LOG_TYPE, LogType.OAK.ordinal());
        this.entityData.define(DATA_ID_WOOL_TYPE, WoolType.WHITE.ordinal());
        this.entityData.define(FAIRY_OWNER, "");
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return FairyBedEntity.canVehicleCollide(this, entity);
    }

    public static boolean canVehicleCollide(Entity entity, Entity entity2)
    {
        return (entity2.canBeCollidedWith() || entity2.isPushable()) && !entity.isPassengerOfSameVehicle(entity2);
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    public boolean isPushable()
    {
        return false;
    }

    @Override
    public double getPassengersRidingOffset()
    {
        return -0.4;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f)
    {
        boolean bl;

        if (this.isInvulnerableTo(damageSource))
        {
            return false;
        }

        if (this.level.isClientSide || this.isRemoved())
        {
            return true;
        }

        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() + f * 10.0f);
        this.markHurt();
        this.gameEvent(GameEvent.ENTITY_DAMAGED, damageSource.getEntity());

        boolean bl2 = bl = damageSource.getEntity() instanceof Player && ((Player)damageSource.getEntity()).getAbilities().instabuild;

        if (bl || this.getDamage() > 40.0f)
        {
            if (!bl && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
            {
                this.spawnAtLocation(this.getDropItem());
            }
            this.discard();
        }

        return true;
    }

    public Item getDropItem()
    {
//        for (FairyBedEntity.Type type: FairyBedEntity.Type.values())
//        {
//            if(type == this.getFairyBedType())
//            {
//                return ModItems.FAIRY_BED_ITEMS.get(type).get();
//            }
//        }
//
//        return ModItems.FAIRY_BED_ITEMS.get(Type.OAK_WHITE).get();

//        for (FairyBedEntity.LogType logType : FairyBedEntity.LogType.values())
//        {
//            for (FairyBedEntity.WoolType woolType : FairyBedEntity.WoolType.values())
//            {
//                if(logType == this.getFairyBedLogType() && woolType == this.getFairyBedWoolType())
//                {
//
//                    Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType> pair = Pair.of(logType, woolType);
//                    return ModItems.FAIRY_BED_ITEMS.get(pair).get();
//                }
//            }
//        }

        return ModItems.FAIRY_BED_ITEMS.get(Pair.of(this.getFairyBedLogType(), this.getFairyBedWoolType())).get();
    }

    @Override
    public void animateHurt()
    {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() * 11.0f);
    }

    @Override
    public boolean isPickable()
    {
        return !this.isRemoved();
    }

    @Override
    public void tick()
    {
        if (this.getHurtTime() > 0)
        {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0.0f)
        {
            this.setDamage(this.getDamage() - 1.0f);
        }

        super.tick();
        this.setDeltaMovement(Vec3.ZERO);

        //this.checkInsideBlocks();
//        List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(0.2f, -0.01f, 0.2f), EntitySelector.pushableBy(this));
//
//        if (!list.isEmpty())
//        {
//            boolean bl = !this.level.isClientSide && !(this.getControllingPassenger() instanceof Player);
//
//            for (int j = 0; j < list.size(); ++j)
//            {
//                Entity entity = list.get(j);
//                if (entity.hasPassenger(this)) continue;
//                if (bl && this.getPassengers().size() < 2 && !entity.isPassenger() && entity.getBbWidth() < this.getBbWidth() && entity instanceof LivingEntity && !(entity instanceof WaterAnimal) && !(entity instanceof Player))
//                {
//                    entity.startRiding(this);
//                    continue;
//                }
//
//                this.push(entity);
//            }
//        }
    }

    @Override
    protected void checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos)
    {
        this.lastYd = this.getDeltaMovement().y;

        if (this.isPassenger())
        {
            return;
        }

        if (bl)
        {
            if (this.fallDistance > 3.0f)
            {
                this.causeFallDamage(this.fallDistance, 1.0f, DamageSource.FALL);

                if (!this.level.isClientSide && !this.isRemoved())
                {
                    this.kill();
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
                    {
//                        int i;
//                        for (i = 0; i < 3; ++i)
//                        {
//                            this.spawnAtLocation(this.getBoatType().getWoodLog());
//                        }
//                        for (i = 0; i < 2; ++i)
//                        {
//                            this.spawnAtLocation(Items.STICK);
//                        }
                    }
                }
            }
            this.resetFallDistance();
        }
        else if (!this.level.getFluidState(this.blockPosition().below()).is(FluidTags.WATER) && d < 0.0)
        {
            this.fallDistance -= (float)d;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag)
    {
//        if (compoundTag.contains("Type", 8))
//        {
//            this.setType(FairyBedEntity.Type.byName(compoundTag.getString("Type")));
//        }

//        if (compoundTag.contains("LogType", 8))
//        {
//            this.setLogType(FairyBedEntity.LogType.byName(compoundTag.getString("LogType")));
//        }
//
//        if (compoundTag.contains("WoolType", 9))
//        {
//            this.setWoolType(FairyBedEntity.WoolType.byName(compoundTag.getString("WoolType")));
//        }

        this.setLogType(FairyBedEntity.LogType.byName(compoundTag.getString("LogType")));
        this.setWoolType(FairyBedEntity.WoolType.byName(compoundTag.getString("WoolType")));

        this.setFairyOwner(compoundTag.getString("fairy_owner"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag)
    {
        //compoundTag.putString("Type", this.getFairyBedType().getName());

        compoundTag.putString("LogType", this.getFairyBedLogType().getName());
        compoundTag.putString("WoolType", this.getFairyBedWoolType().getName());

        compoundTag.putString("fairy_owner", getFairyOwner());
    }

    public void setDamage(float f)
    {
        this.entityData.set(DATA_ID_DAMAGE, Float.valueOf(f));
    }

    public float getDamage()
    {
        return this.entityData.get(DATA_ID_DAMAGE).floatValue();
    }

    public void setHurtTime(int i)
    {
        this.entityData.set(DATA_ID_HURT, i);
    }

    public int getHurtTime()
    {
        return this.entityData.get(DATA_ID_HURT);
    }

    public void setHurtDir(int i)
    {
        this.entityData.set(DATA_ID_HURTDIR, i);
    }

    public int getHurtDir()
    {
        return this.entityData.get(DATA_ID_HURTDIR);
    }

//    public void setType(FairyBedEntity.Type type)
//    {
//        this.entityData.set(DATA_ID_TYPE, type.ordinal());
//    }
//
//    public FairyBedEntity.Type getFairyBedType()
//    {
//        return FairyBedEntity.Type.byId(this.entityData.get(DATA_ID_TYPE));
//    }

    public void setLogType(FairyBedEntity.LogType type)
    {
        this.entityData.set(DATA_ID_LOG_TYPE, type.ordinal());
    }

    public FairyBedEntity.LogType getFairyBedLogType()
    {
        return FairyBedEntity.LogType.byId(this.entityData.get(DATA_ID_LOG_TYPE));
    }

    public void setWoolType(FairyBedEntity.WoolType type)
    {
        this.entityData.set(DATA_ID_WOOL_TYPE, type.ordinal());
    }

    public FairyBedEntity.WoolType getFairyBedWoolType()
    {
        return FairyBedEntity.WoolType.byId(this.entityData.get(DATA_ID_WOOL_TYPE));
    }

    public void setFairyOwner(String fairyOwner)
    {
        this.entityData.set(FAIRY_OWNER, fairyOwner);
    }

    public String getFairyOwner()
    {
        return this.entityData.get(FAIRY_OWNER);
    }

    @Override
    protected boolean canAddPassenger(Entity entity)
    {
        return this.getPassengers().size() < 1;
    }

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public ItemStack getPickResult()
    {
        return new ItemStack(this.getDropItem());
    }

//    private void addLight()
//    {
//        this.levelEnumSkyBlock.Block, (int) this.posX,
//                (int) this.posY, (int) this.posZ, 16);
//        this.worldObj.markBlockRangeForRenderUpdate((int) this.posX,
//                (int) this.posY, (int) this.posX, 12, 12, 12);
//        this.worldObj.markBlockForUpdate((int) this.posX, (int) this.posY,
//                (int) this.posZ);
//        this.worldObj.updateLightByType(EnumSkyBlock.Block, (int) this.posX,
//                (int) this.posY + 1, (int) this.posZ);
//        this.worldObj.updateLightByType(EnumSkyBlock.Block, (int) this.posX,
//                (int) this.posY - 1, (int) this.posZ);
//        this.worldObj.updateLightByType(EnumSkyBlock.Block,
//                (int) this.posX + 1, (int) this.posY, (int) this.posZ);
//        this.worldObj.updateLightByType(EnumSkyBlock.Block,
//                (int) this.posX - 1, (int) this.posY, (int) this.posZ);
//        this.worldObj.updateLightByType(EnumSkyBlock.Block, (int) this.posX,
//                (int) this.posY, (int) this.posZ + 1);
//        this.worldObj.updateLightByType(EnumSkyBlock.Block, (int) this.posX,
//                (int) this.posY, (int) this.posZ - 1);
//    }

    public static enum LogType
    {
        OAK(WoodType.OAK),
        SPRUCE(WoodType.SPRUCE),
        BIRCH(WoodType.BIRCH),
        JUNGLE(WoodType.JUNGLE),
        ACACIA(WoodType.ACACIA),
        DARK_OAK(WoodType.DARK_OAK);

        private final String name;
        private final WoodType log;

        private LogType(WoodType woodType)
        {
            this.name = woodType.name();
            this.log = woodType;
        }

        public String getName()
        {
            return this.name;
        }

        public WoodType getWoodLog()
        {
            return this.log;
        }

        public String toString()
        {
            return this.name;
        }

        public static FairyBedEntity.LogType byId(int i)
        {
            FairyBedEntity.LogType[] types = FairyBedEntity.LogType.values();

            if (i < 0 || i >= types.length)
            {
                i = 0;
            }

            return types[i];
        }

        public static FairyBedEntity.LogType byName(String string)
        {
            FairyBedEntity.LogType[] types = FairyBedEntity.LogType.values();

            for (int i = 0; i < types.length; ++i)
            {
                if (!types[i].getName().equals(string)) continue;
                return types[i];
            }

            return types[0];
        }
    }

    public static enum WoolType
    {
        WHITE(DyeColor.WHITE),
        ORANGE(DyeColor.ORANGE),
        MAGENTA(DyeColor.MAGENTA),
        LIGHT_BLUE(DyeColor.LIGHT_BLUE),
        YELLOW(DyeColor.YELLOW),
        LIME(DyeColor.LIME),
        PINK(DyeColor.PINK),
        GRAY(DyeColor.GRAY),
        LIGHT_GRAY(DyeColor.LIGHT_GRAY),
        CYAN(DyeColor.CYAN),
        PURPLE(DyeColor.PURPLE),
        BLUE(DyeColor.BLUE),
        BROWN(DyeColor.BROWN),
        GREEN(DyeColor.GREEN),
        RED(DyeColor.RED),
        BLACK(DyeColor.BLACK);

        private final String name;
        private final DyeColor dyeColor;

        private WoolType(DyeColor dyeColor)
        {
            this.name = dyeColor.name();
            this.dyeColor = dyeColor;
        }

        public String getName()
        {
            return this.name.toLowerCase();
        }

        public DyeColor getDyeColor()
        {
            return this.dyeColor;
        }

        public String toString()
        {
            return this.name;
        }

        public static FairyBedEntity.WoolType byId(int i)
        {
            FairyBedEntity.WoolType[] types = FairyBedEntity.WoolType.values();

            if (i < 0 || i >= types.length)
            {
                i = 0;
            }

            return types[i];
        }

        public static FairyBedEntity.WoolType byName(String string)
        {
            FairyBedEntity.WoolType[] types = FairyBedEntity.WoolType.values();

            for (int i = 0; i < types.length; ++i)
            {
                if (!types[i].getName().equals(string)) continue;
                return types[i];
            }

            return types[0];
        }
    }
}
