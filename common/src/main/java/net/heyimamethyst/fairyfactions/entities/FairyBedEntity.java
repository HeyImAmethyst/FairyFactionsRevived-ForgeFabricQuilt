package net.heyimamethyst.fairyfactions.entities;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class FairyBedEntity extends Entity
{
    private static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_HURTDIR = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);

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
        this.entityData.define(DATA_ID_TYPE, Type.OAK_WHITE.ordinal());
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
        switch (this.getFairyBedType())
        {
            default:
            {
                return  ModItems.OAK_WHITE_FAIRY_BED.get();
            }
            case OAK_ORANGE:
            {
                return ModItems.OAK_ORANGE_FAIRY_BED.get();
            }
            case OAK_MAGENTA:
            {
                return ModItems.OAK_MAGENTA_FAIRY_BED.get();
            }
            case OAK_LIGHT_BLUE:
            {
                return ModItems.OAK_LIGHT_BLUE_FAIRY_BED.get();
            }
            case OAK_YELLOW:
            {
                return ModItems.OAK_YELLOW_FAIRY_BED.get();
            }
            case OAK_LIME:
            {
                return ModItems.OAK_LIME_FAIRY_BED.get();
            }
            case OAK_PINK:
            {
                return ModItems.OAK_PINK_FAIRY_BED.get();
            }
            case OAK_GRAY:
            {
                return ModItems.OAK_GRAY_FAIRY_BED.get();
            }
            case OAK_LIGHT_GRAY:
            {
                return ModItems.OAK_LIGHT_GRAY_FAIRY_BED.get();
            }
            case OAK_CYAN:
            {
                return ModItems.OAK_CYAN_FAIRY_BED.get();
            }
            case OAK_PURPLE:
            {
                return ModItems.OAK_PURPLE_FAIRY_BED.get();
            }
            case OAK_BLUE:
            {
                return ModItems.OAK_BLUE_FAIRY_BED.get();
            }
            case OAK_BROWN:
            {
                return ModItems.OAK_BROWN_FAIRY_BED.get();
            }
            case OAK_GREEN:
            {
                return ModItems.OAK_GREEN_FAIRY_BED.get();
            }
            case OAK_RED:
            {
                return ModItems.OAK_RED_FAIRY_BED.get();
            }
            case OAK_BLACK:
        }

        return ModItems.OAK_BLACK_FAIRY_BED.get();
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
        if (compoundTag.contains("Type", 8))
        {
            this.setType(FairyBedEntity.Type.byName(compoundTag.getString("Type")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag)
    {
        compoundTag.putString("Type", this.getFairyBedType().getName());
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

    public void setType(FairyBedEntity.Type type)
    {
        this.entityData.set(DATA_ID_TYPE, type.ordinal());
    }

    public FairyBedEntity.Type getFairyBedType()
    {
        return FairyBedEntity.Type.byId(this.entityData.get(DATA_ID_TYPE));
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

    public static enum Type
    {
        OAK_WHITE(WoodType.OAK, DyeColor.WHITE),
        OAK_ORANGE(WoodType.OAK, DyeColor.ORANGE),
        OAK_MAGENTA(WoodType.OAK, DyeColor.MAGENTA),
        OAK_LIGHT_BLUE(WoodType.OAK, DyeColor.LIGHT_BLUE),
        OAK_YELLOW(WoodType.OAK, DyeColor.YELLOW),
        OAK_LIME(WoodType.OAK, DyeColor.LIME),
        OAK_PINK(WoodType.OAK, DyeColor.PINK),
        OAK_GRAY(WoodType.OAK, DyeColor.GRAY),
        OAK_LIGHT_GRAY(WoodType.OAK, DyeColor.LIGHT_GRAY),
        OAK_CYAN(WoodType.OAK, DyeColor.CYAN),
        OAK_PURPLE(WoodType.OAK, DyeColor.PURPLE),
        OAK_BLUE(WoodType.OAK, DyeColor.BLUE),
        OAK_BROWN(WoodType.OAK, DyeColor.BROWN),
        OAK_GREEN(WoodType.OAK, DyeColor.GREEN),
        OAK_RED(WoodType.OAK, DyeColor.RED),
        OAK_BLACK(WoodType.OAK, DyeColor.BLACK);

        //SPRUCE(Blocks.SPRUCE_LOG, "spruce"),
        //BIRCH(Blocks.BIRCH_LOG, "birch"),
        //JUNGLE(Blocks.JUNGLE_LOG, "jungle"),
        //ACACIA(Blocks.ACACIA_LOG, "acacia"),
        //DARK_OAK(Blocks.DARK_OAK_LOG, "dark_oak");

        private final String name;
        private final WoodType log;

        private final DyeColor dyeColor;

        private Type(WoodType woodType, DyeColor dyeColor)
        {
            this.name = woodType.name() + "_" + dyeColor.getName();
            this.log = woodType;
            this.dyeColor = dyeColor;
        }

        public String getName()
        {
            return this.name;
        }

        public WoodType getWoodLog()
        {
            return this.log;
        }

        public DyeColor getDyeColor()
        {
            return this.dyeColor;
        }

        public String toString()
        {
            return this.name;
        }

        public static FairyBedEntity.Type byId(int i)
        {
            FairyBedEntity.Type[] types = FairyBedEntity.Type.values();

            if (i < 0 || i >= types.length)
            {
                i = 0;
            }

            return types[i];
        }

        public static FairyBedEntity.Type byName(String string)
        {
            FairyBedEntity.Type[] types = FairyBedEntity.Type.values();

            for (int i = 0; i < types.length; ++i)
            {
                if (!types[i].getName().equals(string)) continue;
                return types[i];
            }

            return types[0];
        }
    }
}