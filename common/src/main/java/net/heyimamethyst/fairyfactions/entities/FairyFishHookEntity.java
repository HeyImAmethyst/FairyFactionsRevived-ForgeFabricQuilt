package net.heyimamethyst.fairyfactions.entities;

import com.mojang.logging.LogUtils;
import net.heyimamethyst.fairyfactions.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Random;

public class FairyFishHookEntity extends Projectile
{

    private static final Logger LOGGER = LogUtils.getLogger();
    private final RandomSource syncronizedRandom = RandomSource.create();
    private boolean biting;
    private int outOfWaterTime;
    private static final int MAX_OUT_OF_WATER_TIME = 10;
    private static final EntityDataAccessor<Integer> DATA_HOOKED_ENTITY = SynchedEntityData.defineId(FishingHook.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_BITING = SynchedEntityData.defineId(FishingHook.class, EntityDataSerializers.BOOLEAN);
    private int life;
    public int nibble;
    private int timeUntilLured;
    private int timeUntilHooked;
    private float fishAngle;
    private boolean openWater = true;
    @Nullable
    public Entity hookedIn;
    private FishHookState currentState = FishHookState.FLYING;
    private final int luck;
    private final int lureSpeed;

    private FairyFishHookEntity(EntityType<? extends FairyFishHookEntity> pEntityType, Level pLevel, int pLuck, int pLureSpeed)
    {
        super(pEntityType, pLevel);
        this.noCulling = true;
        this.luck = Math.max(0, pLuck);
        this.lureSpeed = Math.max(0, pLureSpeed);
    }

    public FairyFishHookEntity(EntityType<? extends FairyFishHookEntity> pEntityType, Level pLevel)
    {
        this(pEntityType, pLevel, 0, 0);
    }

    public FairyFishHookEntity(FairyEntity fairy, Level pLevel, int pLuck, int pLureSpeed)
    {
        this(ModEntities.FAIRY_FISHING_BOBBER_ENTITY.get(), pLevel, pLuck, pLureSpeed);
        //this(ModEntities.FAIRY_FISHING_BOBBER_ENTITY.get(), pLevel);

        this.setOwner(fairy);
        fairy.setFishEntity(this);

        float f = fairy.getXRot();
        float f1 = fairy.getYRot();
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        double d0 = fairy.getX() - (double)f3 * 0.3D;
        double d1 = fairy.getEyeY();
        double d2 = fairy.getZ() - (double)f2 * 0.3D;
        this.moveTo(d0, d1, d2, f1, f);
        Vec3 vec3 = new Vec3((double)(-f3), (double)Mth.clamp(-(f5 / f4), -5.0F, 5.0F), (double)(-f2));
        double d3 = vec3.length();
        vec3 = vec3.multiply(0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D);
        this.setDeltaMovement(vec3);
        this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(vec3.y, vec3.horizontalDistance()) * (double)(180F / (float)Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    protected void defineSynchedData()
    {
        this.getEntityData().define(DATA_HOOKED_ENTITY, 0);
        this.getEntityData().define(DATA_BITING, false);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_37153_)
    {
        if (DATA_HOOKED_ENTITY.equals(p_37153_))
        {
            int i = this.getEntityData().get(DATA_HOOKED_ENTITY);
            this.hookedIn = i > 0 ? this.level.getEntity(i - 1) : null;
        }

        if (DATA_BITING.equals(p_37153_))
        {
            this.biting = this.getEntityData().get(DATA_BITING);
            if (this.biting)
            {
                this.setDeltaMovement(this.getDeltaMovement().x, (double)(-0.4F * Mth.nextFloat(this.syncronizedRandom, 0.6F, 1.0F)), this.getDeltaMovement().z);
            }
        }

        super.onSyncedDataUpdated(p_37153_);
    }

    public boolean shouldRenderAtSqrDistance(double p_37125_)
    {
        double d0 = 64.0D;
        return p_37125_ < 4096.0D;
    }

    public void lerpTo(double p_37127_, double p_37128_, double p_37129_, float p_37130_, float p_37131_, int p_37132_, boolean p_37133_)
    {
    }

    public void tick()
    {
        this.syncronizedRandom.setSeed(this.getUUID().getLeastSignificantBits() ^ this.level.getGameTime());

        super.tick();

        FairyEntity fairy = this.getFairyOwner();

        if (fairy == null)
        {
            fairy.setFishEntity(null);
            fairy.setSitting(false);
            this.discard();
        }
        else if (this.level.isClientSide || !this.shouldStopFishing(fairy))
        {
            if (this.onGround)
            {
                ++this.life;
                if (this.life >= 1200)
                {
                    fairy.setFishEntity(null);
                    fairy.setSitting(false);
                    this.discard();
                    return;
                }
            }
            else
            {
                this.life = 0;
            }

            float f = 0.0F;
            BlockPos blockpos = this.blockPosition();
            FluidState fluidstate = this.level.getFluidState(blockpos);
            if (fluidstate.is(FluidTags.WATER))
            {
                f = fluidstate.getHeight(this.level, blockpos);
            }

            boolean flag = f > 0.0F;
            if (this.currentState == FishHookState.FLYING)
            {
                if (this.hookedIn != null)
                {
                    this.setDeltaMovement(Vec3.ZERO);
                    this.currentState = FishHookState.HOOKED_IN_ENTITY;
                    return;
                }

                if (flag)
                {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.3D, 0.2D, 0.3D));
                    this.currentState = FishHookState.BOBBING;
                    return;
                }

                this.checkCollision();
            }
            else
            {
                if (this.currentState == FishHookState.HOOKED_IN_ENTITY)
                {
                    if (this.hookedIn != null)
                    {
                        if (!this.hookedIn.isRemoved() && this.hookedIn.level.dimension() == this.level.dimension())
                        {
                            this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8D), this.hookedIn.getZ());
                            getFairyOwner().setFishEntity(null);
                            getFairyOwner().setTempItem(null);
                            getFairyOwner().setSitting(false);
                            discard();
                        }
                        else
                        {
                            this.setHookedEntity((Entity)null);
                            this.currentState = FishHookState.FLYING;
                        }
                    }

                    return;
                }

                if (this.currentState == FishHookState.BOBBING)
                {
                    Vec3 vec3 = this.getDeltaMovement();
                    double d0 = this.getY() + vec3.y - (double)blockpos.getY() - (double)f;

                    if (Math.abs(d0) < 0.01D)
                    {
                        d0 += Math.signum(d0) * 0.1D;
                    }

                    this.setDeltaMovement(vec3.x * 0.9D, vec3.y - d0 * (double)this.random.nextFloat() * 0.2D, vec3.z * 0.9D);

                    if (this.nibble <= 0 && this.timeUntilHooked <= 0)
                    {
                        this.openWater = true;
                    }
                    else
                    {
                        this.openWater = this.openWater && this.outOfWaterTime < 10 && this.calculateOpenWater(blockpos);
                    }

                    if (flag)
                    {
                        this.outOfWaterTime = Math.max(0, this.outOfWaterTime - 1);

                        if (this.biting)
                        {
                            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.1D * (double)this.syncronizedRandom.nextFloat() * (double)this.syncronizedRandom.nextFloat(), 0.0D));
                        }

                        if (!this.level.isClientSide)
                        {
                            this.catchingFish(blockpos);
                        }
                    }
                    else
                    {
                        this.outOfWaterTime = Math.min(10, this.outOfWaterTime + 1);
                    }
                }
            }

            if (!fluidstate.is(FluidTags.WATER))
            {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
            this.updateRotation();

            if (this.currentState == FishHookState.FLYING && (this.onGround || this.horizontalCollision))
            {
                this.setDeltaMovement(Vec3.ZERO);
            }

            double d1 = 0.92D;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.92D));
            this.reapplyPosition();
        }
    }

    public boolean shouldStopFishing(FairyEntity p_37137_)
    {
        ItemStack itemstack = p_37137_.getMainHandItem();

        boolean flag = itemstack.is(Items.STICK);

        if
        (
            !p_37137_.isRemoved()
            && p_37137_.isAlive()
            /*&& (flag)*/
            && !(this.distanceToSqr(p_37137_) > 1024.0D
            && p_37137_.getVehicle() == null)
        )
        {
            return false;
        }
        else
        {
            FairyEntity fairy = getFairyOwner();
            fairy.setFishEntity(null);
            fairy.setSitting(false);
            this.discard();
            return true;
        }
    }

    private void checkCollision()
    {
        //HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        //if (hitresult.getType() == HitResult.Type.MISS || !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) this.onHit(hitresult);

        HitResult hitResult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        this.onHit(hitResult);
    }

    protected boolean canHitEntity(Entity p_37135_)
    {
        return super.canHitEntity(p_37135_) || p_37135_.isAlive() && p_37135_ instanceof ItemEntity;
    }

    protected void onHitEntity(EntityHitResult p_37144_)
    {
        super.onHitEntity(p_37144_);

        if (!this.level.isClientSide)
        {
            this.setHookedEntity(p_37144_.getEntity());
        }

    }

    protected void onHitBlock(BlockHitResult p_37142_)
    {
        super.onHitBlock(p_37142_);
        this.setDeltaMovement(this.getDeltaMovement().normalize().scale(p_37142_.distanceTo(this)));
    }

    private void setHookedEntity(@Nullable Entity p_150158_)
    {
        this.hookedIn = p_150158_;
        this.getEntityData().set(DATA_HOOKED_ENTITY, p_150158_ == null ? 0 : p_150158_.getId() + 1);
    }

    private void catchingFish(BlockPos p_37146_)
    {
        ServerLevel serverlevel = (ServerLevel)this.level;
        int i = 1;
        BlockPos blockpos = p_37146_.above();

        if (this.random.nextFloat() < 0.25F && this.level.isRainingAt(blockpos))
        {
            ++i;
        }

        if (this.random.nextFloat() < 0.5F && !this.level.canSeeSky(blockpos))
        {
            --i;
        }

        if (this.nibble > 0)
        {
            --this.nibble;
            if (this.nibble <= 0)
            {
                this.timeUntilLured = 0;
                this.timeUntilHooked = 0;
                this.getEntityData().set(DATA_BITING, false);
            }
        }
        else if (this.timeUntilHooked > 0)
        {
            this.timeUntilHooked -= i;
            if (this.timeUntilHooked > 0)
            {
                this.fishAngle += (float)(this.random.nextGaussian() * 4.0D);
                float f = this.fishAngle * ((float)Math.PI / 180F);
                float f1 = Mth.sin(f);
                float f2 = Mth.cos(f);
                double d0 = this.getX() + (double)(f1 * (float)this.timeUntilHooked * 0.1F);
                double d1 = (double)((float)Mth.floor(this.getY()) + 1.0F);
                double d2 = this.getZ() + (double)(f2 * (float)this.timeUntilHooked * 0.1F);
                BlockState blockstate = serverlevel.getBlockState(new BlockPos(d0, d1 - 1.0D, d2));
                if (serverlevel.getBlockState(new BlockPos((int)d0, (int)d1 - 1, (int)d2)).getMaterial() == net.minecraft.world.level.material.Material.WATER)
                {
                    if (this.random.nextFloat() < 0.15F)
                    {
                        serverlevel.sendParticles(ParticleTypes.BUBBLE, d0, d1 - (double)0.1F, d2, 1, (double)f1, 0.1D, (double)f2, 0.0D);
                    }

                    float f3 = f1 * 0.04F;
                    float f4 = f2 * 0.04F;
                    serverlevel.sendParticles(ParticleTypes.FISHING, d0, d1, d2, 0, (double)f4, 0.01D, (double)(-f3), 1.0D);
                    serverlevel.sendParticles(ParticleTypes.FISHING, d0, d1, d2, 0, (double)(-f4), 0.01D, (double)f3, 1.0D);
                }
            }
            else
            {
                this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                double d3 = this.getY() + 0.5D;
                serverlevel.sendParticles(ParticleTypes.BUBBLE, this.getX(), d3, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), 0.0D, (double)this.getBbWidth(), (double)0.2F);
                serverlevel.sendParticles(ParticleTypes.FISHING, this.getX(), d3, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), 0.0D, (double)this.getBbWidth(), (double)0.2F);
                this.nibble = Mth.nextInt(this.random, 20, 40);
                this.getEntityData().set(DATA_BITING, true);
            }
        }
        else if (this.timeUntilLured > 0)
        {
            this.timeUntilLured -= i;
            float f5 = 0.15F;

            if (this.timeUntilLured < 20)
            {
                f5 += (float)(20 - this.timeUntilLured) * 0.05F;
            }
            else if (this.timeUntilLured < 40)
            {
                f5 += (float)(40 - this.timeUntilLured) * 0.02F;
            }
            else if (this.timeUntilLured < 60)
            {
                f5 += (float)(60 - this.timeUntilLured) * 0.01F;
            }

            if (this.random.nextFloat() < f5)
            {
                float f6 = Mth.nextFloat(this.random, 0.0F, 360.0F) * ((float)Math.PI / 180F);
                float f7 = Mth.nextFloat(this.random, 25.0F, 60.0F);
                double d4 = this.getX() + (double)(Mth.sin(f6) * f7) * 0.1D;
                double d5 = (double)((float)Mth.floor(this.getY()) + 1.0F);
                double d6 = this.getZ() + (double)(Mth.cos(f6) * f7) * 0.1D;
                BlockState blockstate1 = serverlevel.getBlockState(new BlockPos(d4, d5 - 1.0D, d6));

                if (serverlevel.getBlockState(new BlockPos(d4, d5 - 1.0D, d6)).getMaterial() == net.minecraft.world.level.material.Material.WATER)
                {
                    serverlevel.sendParticles(ParticleTypes.SPLASH, d4, d5, d6, 2 + this.random.nextInt(2), (double)0.1F, 0.0D, (double)0.1F, 0.0D);
                }
            }

            if (this.timeUntilLured <= 0)
            {
                this.fishAngle = Mth.nextFloat(this.random, 0.0F, 360.0F);
                this.timeUntilHooked = Mth.nextInt(this.random, 20, 80);
            }
        }
        else
        {
            this.timeUntilLured = Mth.nextInt(this.random, 100, 600);
            this.timeUntilLured -= this.lureSpeed * 20 * 5;
        }

    }

    private boolean calculateOpenWater(BlockPos p_37159_)
    {
        OpenWaterType fishinghook$openwatertype = OpenWaterType.INVALID;

        for(int i = -1; i <= 2; ++i)
        {
            OpenWaterType fishinghook$openwatertype1 = this.getOpenWaterTypeForArea(p_37159_.offset(-2, i, -2), p_37159_.offset(2, i, 2));

            switch(fishinghook$openwatertype1)
            {
                case INVALID:
                    return false;
                case ABOVE_WATER:
                    if (fishinghook$openwatertype == OpenWaterType.INVALID)
                    {
                        return false;
                    }
                    break;
                case INSIDE_WATER:
                    if (fishinghook$openwatertype == OpenWaterType.ABOVE_WATER)
                    {
                        return false;
                    }
            }

            fishinghook$openwatertype = fishinghook$openwatertype1;
        }

        return true;
    }

    private OpenWaterType getOpenWaterTypeForArea(BlockPos p_37148_, BlockPos p_37149_)
    {
        return BlockPos.betweenClosedStream(p_37148_, p_37149_).map(this::getOpenWaterTypeForBlock).reduce((p_37139_, p_37140_) -> {
            return p_37139_ == p_37140_ ? p_37139_ : OpenWaterType.INVALID;
        }).orElse(OpenWaterType.INVALID);
    }

    private OpenWaterType getOpenWaterTypeForBlock(BlockPos p_37164_)
    {
        BlockState blockstate = this.level.getBlockState(p_37164_);

        if (!blockstate.isAir() && !blockstate.is(Blocks.LILY_PAD))
        {
            FluidState fluidstate = blockstate.getFluidState();
            return fluidstate.is(FluidTags.WATER) && fluidstate.isSource() && blockstate.getCollisionShape(this.level, p_37164_).isEmpty() ? OpenWaterType.INSIDE_WATER : OpenWaterType.INVALID;
        }
        else
        {
            return OpenWaterType.ABOVE_WATER;
        }
    }

    public boolean isOpenWaterFishing()
    {
        return this.openWater;
    }

    public void addAdditionalSaveData(CompoundTag p_37161_)
    {
    }

    public void readAdditionalSaveData(CompoundTag p_37151_)
    {
    }

    public boolean gotBite()
    {
        if ((this.nibble > 0 || onGround || outOfWaterTime > 1200))
        {
            nibble -= random.nextInt(5);
            return true;
        }

        return false;
    }

    public int retrieve(ItemStack pStack)
    {
        FairyEntity fairy = this.getFairyOwner();

        if (!this.level.isClientSide && fairy != null && !this.shouldStopFishing(fairy))
        {
            int i = 0;
            //net.minecraftforge.event.entity.player.ItemFishedEvent event = null;

            if (this.hookedIn != null)
            {
                this.pullEntity(this.hookedIn);
                //CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer) entity, p_37157_, this, Collections.emptyList());
                this.level.broadcastEntityEvent(this, (byte) 31);
                i = this.hookedIn instanceof ItemEntity ? 3 : 5;
            }
            else if (this.nibble > 0)
            {
//                LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) this.level)).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.TOOL, pStack).withParameter(LootContextParams.THIS_ENTITY, this).withRandom(this.random);
//                lootcontext$builder.withParameter(LootContextParams.KILLER_ENTITY, this.getOwner()).withParameter(LootContextParams.THIS_ENTITY, this);
//                LootTable loottable = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING);
//                List<ItemStack> list = loottable.getRandomItems(lootcontext$builder.create(LootContextParamSets.FISHING));

                LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel)this.level)).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.TOOL, pStack).withParameter(LootContextParams.THIS_ENTITY, this).withRandom(this.random).withLuck((float)this.luck + fairy.getLuck());
                //lootcontext$builder.withParameter(LootContextParams.KILLER_ENTITY, this.getOwner()).withParameter(LootContextParams.THIS_ENTITY, this);
                LootTable loottable = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING);
                List<ItemStack> list = loottable.getRandomItems(lootcontext$builder.create(LootContextParamSets.FISHING));

                //event = new net.minecraftforge.event.entity.player.ItemFishedEvent(list, this.onGround ? 2 : 1, this);
                //net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);

//                if (event.isCanceled())
//                {
//                    this.discard();
//                    return event.getRodDamage();
//                }

                //CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer) entity, p_37157_, this, list);

                for (ItemStack itemstack : list)
                {
                    ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), itemstack);
                    double d0 = fairy.getX() - this.getX();
                    double d1 = fairy.getY() - this.getY();
                    double d2 = fairy.getZ() - this.getZ();

                    double d3 = 0.065D;
                    itementity.setDeltaMovement(d0 * d3, d1 * d3 + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * d3);
                    this.level.addFreshEntity(itementity);

                    //entity.level.addFreshEntity(new ExperienceOrb(entity.level, entity.getX(), entity.getY() + 0.5D, entity.getZ() + 0.5D, this.random.nextInt(6) + 1));
                }

                i = 1;
            }

            if (this.onGround)
            {
                i = 2;
            }

            fairy.setFishEntity(null);
            fairy.setSitting(false);
            this.discard();

            //return event == null ? i : event.getRodDamage();
            return i;
        }
        else
        {
            return 0;
        }
    }

    public void handleEntityEvent(byte p_37123_)
    {
        if (p_37123_ == 31 && this.level.isClientSide && this.hookedIn instanceof Player && ((Player)this.hookedIn).isLocalPlayer())
        {
            this.pullEntity(this.hookedIn);
        }

        super.handleEntityEvent(p_37123_);
    }

    protected void pullEntity(Entity p_150156_)
    {
        Entity entity = this.getOwner();

        if (entity != null)
        {
            Vec3 vec3 = (new Vec3(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ())).scale(0.1D);
            p_150156_.setDeltaMovement(p_150156_.getDeltaMovement().add(vec3));
        }
    }

    protected MovementEmission getMovementEmission()
    {
        return MovementEmission.NONE;
    }

    public void remove(RemovalReason p_150146_)
    {
        this.updateOwnerInfo((FairyFishHookEntity) null);
        super.remove(p_150146_);
    }

    public void onClientRemoval()
    {
        this.updateOwnerInfo((FairyFishHookEntity) null);
    }

    public void setOwner(@Nullable Entity p_150154_)
    {
        super.setOwner(p_150154_);
        this.updateOwnerInfo(this);
    }

    private void updateOwnerInfo(@Nullable FairyFishHookEntity p_150148_)
    {
        FairyEntity fairy = this.getFairyOwner();

        if (fairy != null)
        {
            //fairy.was = p_150148_;
        }

    }

    @Nullable
    public FairyEntity getFairyOwner()
    {
        Entity entity = this.getOwner();
        return entity instanceof FairyEntity ? (FairyEntity)entity : null;
    }

    @Nullable
    public Entity getHookedIn()
    {
        return this.hookedIn;
    }

    public boolean canChangeDimensions()
    {
        return false;
    }

    public Packet<?> getAddEntityPacket()
    {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? this.getId() : entity.getId());
    }

    public void recreateFromPacket(ClientboundAddEntityPacket p_150150_)
    {
        super.recreateFromPacket(p_150150_);

        if (this.getFairyOwner() == null)
        {
            int i = p_150150_.getData();
            LOGGER.error("Failed to recreate fishing hook on client. {} (id: {}) is not a valid owner.", this.level.getEntity(i), i);
            this.kill();
        }

    }

    static enum FishHookState
    {
        FLYING,
        HOOKED_IN_ENTITY,
        BOBBING;
    }

    static enum OpenWaterType
    {
        ABOVE_WATER,
        INSIDE_WATER,
        INVALID;
    }
}
