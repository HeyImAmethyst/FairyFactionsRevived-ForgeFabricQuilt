package net.heyimamethyst.fairyfactions.entities;

import net.heyimamethyst.fairyfactions.FairyConfigValues;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.Loc;
import net.heyimamethyst.fairyfactions.entities.ai.*;
import net.heyimamethyst.fairyfactions.entities.ai.goals.*;
import net.heyimamethyst.fairyfactions.proxy.ClientMethods;
import net.heyimamethyst.fairyfactions.proxy.CommonMethods;
import net.heyimamethyst.fairyfactions.registry.ModSounds;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.heyimamethyst.fairyfactions.world.FairyGroupGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FairyEntity extends FairyEntityBase
{

    private boolean				cower;
    public boolean				didHearts;
    public boolean				didSwing;

    private boolean				wasFishing;
    public int                  fishingSpeedBonus;
    public int                  fishingLuckBonus;

    private int					snowballin;

    private int					flyTime;
    private int					healTime;
    private int					cryTime;
    private int					loseInterest;
    private int					loseTeam;

    public int                 postedCount;	// flag for counting posted checks
    public int				   postX, postY, postZ;

    public double motionX = 0;
    public double motionY = 0;
    public double motionZ = 0;

    double vehicleMotionX = 0;
    double vehicleMotionY = 0;
    double vehicleMotionZ = 0;

    public double speedModifier = 0.3D;

    private LivingEntity	    ruler;
    public LivingEntity	        entityHeal;
    private Entity				entityFear;
    private FairyFishHookEntity fishEntity;

    public static InteractionHand MAIN_FAIRY_HAND = InteractionHand.MAIN_HAND;

    public static final ItemStack	healPotion		= PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.HEALING);
    public static final ItemStack	restPotion		= PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.REGENERATION);
    public static final ItemStack	fishingStick	= new ItemStack(Items.STICK);
    public static final ItemStack	scoutMap		= new ItemStack(Items.FILLED_MAP);

    public static final ItemStack	woodSword		= new ItemStack(Items.WOODEN_SWORD);
    public static final ItemStack	ironSword		= new ItemStack(Items.IRON_SWORD);
    public static final ItemStack	goldSword		= new ItemStack(Items.GOLDEN_SWORD);

    public ItemStack handPotion()
    {
        return (rarePotion() ? restPotion : healPotion);
    }

    public FairyAttackGoal fairyAttackGoal;
    public FairyBehavior fairyBehavior;

    private boolean isLandNavigator;

    public FairyEntity(EntityType<? extends Animal> entityType, Level level)
    {
        super(entityType, level);

        // fairy-specific init

        setSkin(getRandom().nextInt(4));
        setFaction(0);
        setFairyName(getRandom().nextInt(16), getRandom().nextInt(16));

        //setSpecialJob(this.getRandom().nextInt(2) == 0);
        setSpecialJob(false);

        setJob(getRandom().nextInt(4));

        setFlymode(false);
        this.sinage = getRandom().nextFloat();
        this.flyTime = 400 + this.getRandom().nextInt(200);
        this.cower = this.getRandom().nextBoolean();
        this.postX = this.postY = this.postZ = -1;

        fairyBehavior = new FairyBehavior(this, speedModifier);
        switchNavigator(false);

//        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0f);
//        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0f);
//        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0f);
//        this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0f);
//        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0f);

    }

    @Override
    protected void registerGoals()
    {
        this.fairyAttackGoal = new FairyAttackGoal(this, 0.3D, false);

        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(1, fairyAttackGoal);

        this.goalSelector.addGoal(4, new FairyAIGoal((this)));

        //this.goalSelector.addGoal(10, new FairyAIStroll(this, speedModifier, 1.0000001E-5F));
        this.goalSelector.addGoal(4, new FairyAIStroll(this, 0.3D));
        this.goalSelector.addGoal(12, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));
    }

//    @Override
//    protected PathNavigation createNavigation(Level pLevel)
//    {
//        return new FairyNavigation(this, pLevel);
//    }

    //method came from https://github.com/AlexModGuy/AlexsMobs/blob/45e9351d567a26310b62ead7f10536c09782abd4/src/main/java/com/github/alexthe666/alexsmobs/entity/EntityBlueJay.java#L162
    private void switchNavigator(boolean flymode)
    {
        if (flymode)
        {
            this.moveControl = new FairyFlyingMoveControl(this);
            this.navigation = new FairyNavigation(this, level);
            this.isLandNavigator = false;
        }
        else
        {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level);
            this.isLandNavigator = true;
        }
    }

    private void _dump_()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getUUID());
        sb.append(" : ");
        byte b0 = this.entityData.get(B_TYPE);
        byte b1 = this.entityData.get(B_FLAGS);
        byte b2 = this.entityData.get(B_FLAGS2);

        sb.append( String.format("%8s", Integer.toBinaryString(b0)).replace(' ', '0') );
        sb.append("-");
        sb.append( String.format("%8s", Integer.toBinaryString(b1)).replace(' ', '0') );
        sb.append("-");
        sb.append( String.format("%8s", Integer.toBinaryString(b2)).replace(' ', '0') );

        System.out.println(sb.toString());
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();

        this.entityData.define(SITTING, false);

        this.entityData.define(B_FLAGS, (byte) 0);
        this.entityData.define(B_FLAGS2, (byte) 0);
        this.entityData.define(B_TYPE, (byte) 0);
        this.entityData.define(B_HEALTH, (byte) 0);
        this.entityData.define(B_NAME_ORIG, (byte) 0);
        this.entityData.define(S_OWNER, "");
        this.entityData.define(S_NAME_REAL, "");
        this.entityData.define(I_TOOL, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);

        tag.putByte("flags", this.entityData.get(B_FLAGS));
        tag.putByte("flags2", this.entityData.get(B_FLAGS2));
        tag.putByte("type", this.entityData.get(B_TYPE));
        tag.putByte("nameOrig", this.entityData.get(B_NAME_ORIG));

        tag.putString("rulerName", rulerName());
        tag.putString("customName", getFairyCustomName());
        tag.putIntArray("post", new int[] { postX, postY, postZ });

        tag.putShort("flyTime", (short) flyTime);
        tag.putShort("healTime", (short) healTime);
        tag.putShort("cryTime", (short) cryTime);
        tag.putShort("loseInterest", (short) loseInterest);
        tag.putShort("loseTeam", (short) loseTeam);

        tag.putBoolean("cower", this.cower);
        tag.putBoolean("didHearts", this.didHearts);
        tag.putBoolean("didSwing", this.didSwing);

        this.wasFishing = ( this.fishEntity != null );
        tag.putBoolean("wasFishing", this.wasFishing);

        tag.putShort("snowballin", (short) snowballin);

        tag.putBoolean("sitting", this.isSitting());

    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);

        this.entityData.set(B_FLAGS, tag.getByte("flags"));
        this.entityData.set(B_FLAGS2, tag.getByte("flags2"));
        this.entityData.set(B_TYPE, tag.getByte("type"));
        this.entityData.set(B_NAME_ORIG, tag.getByte("nameOrig"));

        setRulerName(tag.getString("rulerName"));
        setFairyCustomName(tag.getString("customName"));

        final int[] post = tag.getIntArray("post");

        if (post.length > 0)
        {
            postX = post[0];
            postY = post[1];
            postZ = post[2];
        }

        flyTime = tag.getShort("flyTime");
        healTime = tag.getShort("healTime");
        cryTime = tag.getShort("cryTime");
        loseInterest = tag.getShort("loseInterest");
        loseTeam = tag.getShort("loseTeam");

        cower = tag.getBoolean("cower");
        didHearts = tag.getBoolean("didHearts");
        didSwing = tag.getBoolean("didSwing");
        wasFishing = tag.getBoolean("wasFishing");
        snowballin = tag.getShort("snowballin");

        setSitting(tag.getBoolean("sitting"));

        if (!this.level.isClientSide)
        {
            setCanHeal(healTime <= 0);
            setPosted(postY > -1);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor)
    {
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    public static AttributeSupplier.Builder createAttributes()
    {

        //.add(Attributes.FLYING_SPEED, (double)0.6F).add(Attributes.FOLLOW_RANGE);
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, FairyConfigValues.GENERAL_HEALTH_BASE)
                .add(Attributes.MOVEMENT_SPEED, FairyConfigValues.GENERAL_SPEED_BASE)
                .add(Attributes.FLYING_SPEED, (double)0.6F)
                .add(Attributes.FOLLOW_RANGE)
                .add(Attributes.LUCK);
    }

    protected SoundEvent getAmbientSound()
    {
        if(queen())
        {
           if(angry())
           {
               return ModSounds.FAIRY_QUEEN_ANGRY.get();
           }
           else
           {
               return ModSounds.FAIRY_QUEEN_IDLE.get();
           }
        }
        else
        {
            if(angry())
            {
                return ModSounds.FAIRY_ANGRY.get();
            }
            else
            {
                return ModSounds.FAIRY_IDLE.get();
            }
        }
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource)
    {

        if(queen())
        {
            return ModSounds.FAIRY_QUEEN_HURT.get();
        }
        else
        {
            return ModSounds.FAIRY_HURT.get();
        }
    }

    protected SoundEvent getDeathSound()
    {

        if(queen())
        {
            return ModSounds.FAIRY_QUEEN_DEATH.get();
        }
        else
        {
            return ModSounds.FAIRY_DEATH.get();
        }
    }

    public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier)
    {
        if (!pState.is(Blocks.SWEET_BERRY_BUSH))
        {
            super.makeStuckInBlock(pState, pMotionMultiplier);
        }
    }

    @Override
    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel)
    {
        return pLevel.getBlockState(pPos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    public double getMyRidingOffset()
    {
        //return super.getMyRidingOffset(p_20228_);
        //return 0.14D;
        //return 0.24D;
        //return 0.45D;
        return 0.27D;
    }

    @Override
    protected double followLeashSpeed()
    {
        return speedModifier;
    }

//    @Override
//    public double getY(double pScale)
//    {
//        //return this.position().y + (double)this.getBbHeight() * p_20228_;
//        //super.getY()
//
//        double yOffset = this.position().y + (double)this.getBbHeight() * 1.0F;
//
//        if (getVehicle() != null)
//        {
//            if (this.level.isClientSide)
//            {
//                return yOffset - ( flymode() ? 1.15F : 1.35f );
//            }
//
//            return  yOffset + ( flymode() ? 0.65F : 0.55F )
//                    - ( getVehicle() instanceof Chicken ? 0.0F : 0.15F );
//        }
//        else
//        {
//            return yOffset;
//        }
//    }

    @Override
    public void tick()
    {
        super.tick();
        //_dump_();

        if (this.createGroup)
        {

            createGroup = false;
            int i = (int)Math.floor(position().x);
            int j = (int)Math.floor(getBoundingBox().minY) - 1;
            int k = (int)Math.floor(position().z);

            final FairyGroupGenerator group = new FairyGroupGenerator(
                    FairyConfigValues.SPAWN_FACTION_MIN_SIZE,
                    FairyConfigValues.SPAWN_FACTION_MAX_SIZE,
                    getFaction());
            if (group.generate(level, random, i, j, k))
            {
                // This is good.
            }
            else
            {
                // issue a kill
                if (!level.isClientSide)
                {
                    //FairyFactions.proxy.sendFairyDespawn(this);
                    this.discard();
                }
                // NB: the original had this in client side
                // setDead(); // For singleplayer mode
            }
        }

        this.getAttribute(Attributes.FOLLOW_RANGE)
                .setBaseValue(FairyConfigValues.BEHAVIOR_PATH_RANGE);

        if (scout())
        {
            this.getAttribute(Attributes.MOVEMENT_SPEED)
                    .setBaseValue(FairyConfigValues.GENERAL_SPEED_SCOUT);
        }
        else
        {
            this.getAttribute(Attributes.MOVEMENT_SPEED)
                    .setBaseValue(FairyConfigValues.GENERAL_SPEED_BASE);
        }

        if (withered())
        {
            AttributeInstance speed = this
                    .getAttribute(Attributes.MOVEMENT_SPEED);

            speed.setBaseValue(speed.getValue() * FairyConfigValues.GENERAL_SPEED_WITHER_MULT);

        }

        if (flymode())
        {
            AttributeInstance speed = this
                    .getAttribute(Attributes.MOVEMENT_SPEED);

            speed.setBaseValue(speed.getValue() * 2.55D);

        }

        if(!level.isClientSide)
        {
            updateWithering();
            setHealth(getHealth());

            Path currentPath = this.getNavigation().getPath();
            setFairyClimbing(flymode() && canFlap() && currentPath != null && horizontalCollision);

            if (isSitting() && (getVehicle() != null || !onGround ))
            {
                setSitting(false);
            }

            setPosted(postY > -1);
        }

        if (getHealth() > 0.0F)
        {
            // wing animations

            if (!this.onGround)
            {
                this.sinage += 0.75F;
            }
            else
            {
                this.sinage += 0.15F;
            }

            if (this.sinage > Math.PI * 2F)
            {
                this.sinage -= Math.PI * 2F;
            }

            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vehicleVec3 = Vec3.ZERO;

            if(this.getVehicle() != null)
                vehicleVec3 = this.getVehicle().getDeltaMovement();

            motionX = vec3.x;
            motionY = vec3.y;
            motionZ = vec3.z;

            vehicleMotionX = vehicleVec3.x;
            vehicleMotionY = vehicleVec3.y;
            vehicleMotionZ = vehicleVec3.z;

            if (flymode())
            {
                if (!liftOff() && getVehicle() != null && !getVehicle().isOnGround()
                        && getVehicle() instanceof LivingEntity)
                {
                    getVehicle().fallDistance = 0F;

                    if (vehicleMotionY < FairyConfigValues.DEF_FLOAT_RATE)
                    {
                          vehicleMotionY = FairyConfigValues.DEF_FLOAT_RATE;
                    }

                    boolean isJumping = ((LivingEntity) getVehicle()).jumping;

                    if (isJumping && vehicleMotionY < FairyConfigValues.DEF_FLAP_RATE && canFlap())
                    {
                        vehicleMotionY = FairyConfigValues.DEF_FLAP_RATE;
                    }

//                    if (((getVehicle() instanceof Player && isJumping)
//                            && vehicleMotionY < FairyConfig.DEF_FLAP_RATE.get() && canFlap())
//                    || !(getVehicle() instanceof Player) && vehicleMotionY < FairyConfig.DEF_FLAP_RATE.get() && canFlap())
//                    {
//                        vehicleMotionY = FairyConfig.DEF_FLAP_RATE.get();
//                    }

                    if( (((LivingEntity)getVehicle() instanceof Player && isJumping) || !((LivingEntity) getVehicle() instanceof Player))
                        && vehicleMotionY < FairyConfigValues.DEF_FLAP_RATE && canFlap())
                    {
                        vehicleMotionY = FairyConfigValues.DEF_FLAP_RATE;
                    }
                }
                else
                {
                    if (motionY < FairyConfigValues.DEF_FLOAT_RATE)
                    {
                        motionY = FairyConfigValues.DEF_FLOAT_RATE;
                    }

                    if (canFlap() && FairyUtils.checkGroundBelow(this) && motionY < 0D)
                    {
                        motionY = FairyConfigValues.DEF_FLOAT_RATE * FairyConfigValues.DEF_SOLO_FLAP_MULT;
                        //motionY = 0.1875D;
                    }

                    if (liftOff() && getVehicle() != null)
                    {
                        getVehicle().fallDistance = 0F;
                        motionY = vehicleMotionY = FairyConfigValues.DEF_FLAP_RATE * FairyConfigValues.DEF_LIFTOFF_MULT;
                        //motionY = vehicleMotionY = 0.3D;
                    }
                }

                if(this.getVehicle() != null)
                {
                    vehicleMotionX = this.getVehicle().getDeltaMovement().x;
                    vehicleMotionZ = this.getVehicle().getDeltaMovement().z;
                }

                motionX = this.getDeltaMovement().x;
                motionZ = this.getDeltaMovement().z;
            }

            if(this.getVehicle() != null)
                this.getVehicle().setDeltaMovement(vehicleMotionX, vehicleMotionY, vehicleMotionZ);

            this.setDeltaMovement(motionX, motionY, motionZ);

            if (hearts() != this.didHearts)
            {
                this.didHearts = !this.didHearts;
                showHeartsOrSmokeFX(tamed());
            }

            this.particleCount++;

            if (this.particleCount >= FairyConfigValues.DEF_MAX_PARTICLES)
            {
                this.particleCount = getRandom().nextInt(FairyConfigValues.DEF_MAX_PARTICLES >> 1);
                //this.particleCount = this.getRandom().nextInt(2);

                if (angry() || (crying() && queen()))
                {
                    // anger smoke, queens don't cry :P
                    this.level.addParticle(ParticleTypes.SMOKE, position().x, getBoundingBox().maxY, position().z, 0D, 0D, 0D);
                }
                else if (crying())
                {
                    // crying effect
                    this.level.addParticle(ParticleTypes.SPLASH, position().x, getBoundingBox().maxY, position().z, 0D, 0D, 0D);
                }

                if (liftOff())
                {
                    // liftoff effect below feet
                    this.level.addParticle(ParticleTypes.EXPLOSION, position().x, getBoundingBox().minY, position().z, 0D, 0D, 0D);
                }

                if (withered() || (rogue() && canHeal()))
                {

                    double a = position().x - 0.2D + (0.4D * random.nextDouble());
                    double b = position().y + 0.45D + (0.15D * random.nextDouble());
                    double c = position().z - 0.2D + (0.4D * random.nextDouble());

                    //SmokeParticle s = new SmokeParticle(level, a, b, c,0D, 0D, 0D, 1.0F);
                    this.level.addParticle(ParticleTypes.SMOKE, a, b, c, 0D, 0D, 0D);
                }
            }

            // NB: this was only on the client in the original
            processSwinging();

        }
    }

//    @Override
//    public boolean isPersistenceRequired()
//    {
//        return isRuler(Minecraft.getInstance().player) && tamed();
//    }

    @Override
    public void checkDespawn()
    {
        super.checkDespawn();

        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful())
        {
            this.discard();
        }
        else if (!this.isPersistenceRequired() && !this.requiresCustomPersistence() && !tamed())
        {
            Player player = level.getNearestPlayer(this, -1D);

//            net.minecraftforge.eventbus.api.Event.Result result = net.minecraftforge.event.ForgeEventFactory.canEntityDespawn(this);
//
//            if (result == net.minecraftforge.eventbus.api.Event.Result.DENY)
//            {
//                noActionTime = 0;
//                player = null;
//            }
//            else if (result == net.minecraftforge.eventbus.api.Event.Result.ALLOW)
//            {
//                this.discard();
//                player = null;
//            }

            if (player != null)
            {
//            double d = ( (Entity) ( player ) ).position().x - position().x;
//            double d1 = ( (Entity) ( player ) ).position().y - position().y;
//            double d2 = ( (Entity) ( player ) ).position().z - position().z;
//            double d3 = d * d + d1 * d1 + d2 * d2;

                double d0 = player.distanceToSqr(this);
                int i = this.getType().getCategory().getDespawnDistance();
                int j = i * i;

                if (d0 > (double)j && this.removeWhenFarAway(d0))
                {
                    //this.dead = true;
                    // mod_FairyMod.fairyMod.sendFairyDespawn(this);

                    if (queen())
                    {
                        despawnFollowers();
                    }

                    this.discard();
                }

                int k = this.getType().getCategory().getNoDespawnDistance();
                int l = k * k;

                if (this.noActionTime > 600 && this.random.nextInt(800) == 0 && d0 > (double)l && this.removeWhenFarAway(d0))
                {
                    // TODO: proxy
                    //this.dead = true;

                    // mod_FairyMod.fairyMod.sendFairyDespawn(this);
                    if (queen())
                    {
                        despawnFollowers();
                    }

                    this.discard();
                }
                else if (d0 < (double)l)
                {
                    this.noActionTime = 0;
                }
            }
        }
        else
        {
            this.noActionTime = 0;
        }
    }

    public void despawnFollowers()
    {
        if (queen() && getFaction() > 0)
        {
            List<FairyEntity> list = level.getEntitiesOfClass(FairyEntity.class,
                    getBoundingBox().expandTowards(40D, 40D, 40D));

            for (FairyEntity fairyEntity : list)
            {

                if (fairyEntity != this && (fairyEntity).getHealth() > 0 && FairyUtils.sameTeam(this, fairyEntity)
                        && ((fairyEntity).ruler == null || (fairyEntity).ruler == this))
                {
                    // TODO: proxy
                    fairyEntity.discard();
                    // mod_FairyMod.fairyMod.sendFairyDespawn(fairy);
                }
            }
        }
    }

    // region ---------- behaviors ----------

    @Override
    public void aiStep()
    {
        super.aiStep();
        setHeldItem(MAIN_FAIRY_HAND);

//        if(getTarget() != null)
//            FairyFactions.LOGGER.debug(getItemInHand(InteractionHand.MAIN_HAND).getItem().toString());
    }

    @Override
    public void customServerAiStep()
    {
        super.customServerAiStep();

        //_dump_();

        if (this.wasFishing)
        {
            this.wasFishing = false;

            if (isSitting() && this.fishEntity == null)
            {
                setSitting(false);
            }
        }

        if (isSitting())
        {
            fairyBehavior.handlePosted(this.level, false);
            return;
        }

        this.flyBlocked = FairyUtils.checkFlyBlocked(this);

        if (this.flyTime > 0)
        {
            this.flyTime--;
        }

        boolean liftFlag = false;

        if (flymode() && this.isLandNavigator)
        {
            switchNavigator(true);
        }

        if (!flymode() && !this.isLandNavigator)
        {
            switchNavigator(false);
        }

        if (flymode())
        {
            fallDistance = 0F;

            if (this.getVehicle() != null)
            {
                if(this.getTarget() != null && this.getVehicle() == this.getTarget())
                {
                    this.flyTime = 200;
                    liftFlag = true;

                    if ((this.attackAnim <= 0) || this.flyBlocked)
                    {
                        this.attackAnim = 0;
                        //doHurtTarget(this.getVehicle());
                        this.getVehicle().hurt(DamageSource.mobAttack(this), 0);
                        liftFlag = false;
                    }
                    else if (tamed())
                    {
                        if (this.getVehicle().isOnGround() || this.getVehicle().isInWater())
                        {
                            //setFlyTime((queen() || scout() ? 60 : 40 ));
                            setFlyTime((!queen() && !scout()) ? 40 : 60);

                            if (withered())
                            {
                                this.flyTime -= 10;
                            }
                        }
                    }
                }
            }

            if (this.flyTime <= 0 || (this.flyBlocked
                    && (this.getVehicle() == null || (this.getTarget() != null
                    && this.getVehicle() == this.getTarget()))))
            {
                setCanFlap(false);
            }
            else
            {
                setCanFlap(true);
            }

            if(this.getVehicle() == null && (this.onGround || isInWater()))
            {
                setFlymode(false);
                this.flyTime = 400 + this.getRandom().nextInt(200);

                // Scouts are less likely to want to walk.
                if (scout())
                {
                    this.flyTime /= 3;
                }
            }
        }
        else
        {
            if (this.flyTime <= 0 && !this.flyBlocked)
            {
                this.jumpFromGround();
                setFlymode(true);
                setCanFlap(true);
                this.flyTime = 400 + getRandom().nextInt(200);

                // Scouts are more likely to want to fly.
                if (scout())
                {
                    this.flyTime *= 3;
                }
            }

            if (this.getVehicle() != null && !flymode())
            {
                setFlymode(true);
                setCanFlap(true);
            }

            if (!flymode() && !this.onGround && this.fallDistance >= 0.5F
                    && this.getVehicle() == null)
            {
                setFlymode(true);
                setCanFlap(true);
                this.flyTime = 400 + getRandom().nextInt(200);
            }
        }

        setLiftOff(liftFlag);

        if (this.healTime > 0)
        {
            this.healTime--;
        }

        if (this.cryTime > 0)
        {
            this.cryTime--;

            if (this.cryTime <= 0)
            {
                entityFear = null;
            }

            if (this.cryTime > 600)
            {
                this.cryTime = 600;
            }
        }

        ++listActions;

        if (listActions >= 8)
        {
            listActions = this.getRandom().nextInt(3);

            fairyBehavior.handleRuler();

//            if(isSitting())
//            {
//                return;
//            }
//
//            if (angry())
//            {
//                fairyBehavior.handleAnger();
//            }
//            else if (crying())
//            {
//                fairyBehavior.handleFear();
//            }
//            else
//            {
//                fairyBehavior.handleRuler();
//
//                if (medic())
//                {
//                    fairyBehavior.handleHealing();
//                }
//                else if (rogue())
//                {
//                    fairyBehavior.handleRogue();
//                }
//                else
//                {
//                    fairyBehavior.handleSocial();
//                }
//
//                fairyBehavior.handlePosted(this.level,true);
//            }
        }

        // fairies run away from players in peaceful

        if (this.level.getDifficulty() == Difficulty.PEACEFUL
                && this.getTarget() != null
                && this.getTarget() instanceof Player)
        {
            this.entityFear = this.getTarget();
            this.cryTime = Math.max(this.cryTime, 100);
            setTarget(null);
        }

        setCrying(this.cryTime > 0);

        setAngry(this.getTarget() != null);

        if(this.getTarget() != null && !this.getTarget().isAlive())
        {
            this.setTarget(null);
        }

        setCanHeal(this.healTime <= 0);

        //_dump_();
    }

    // Checks if a damage source is a snowball.
    // TODO: give this a real name
    public boolean snowballFight(DamageSource damagesource)
    {
        if (damagesource instanceof EntityDamageSource)
        {
            EntityDamageSource snowdamage = (EntityDamageSource) damagesource;

            if (snowdamage.getDirectEntity() != null && snowdamage
                    .getDirectEntity() instanceof Snowball)
            {
                this.snowballin++;

                if (attackAnim < 10)
                {
                    attackAnim = 10;
                }
            }
        }

        return this.snowballin <= 0;
    }

    public void tossSnowball(LivingEntity attackTarget)
    {
        Snowball entitysnowball = new Snowball(level, this);
        double d = attackTarget.position().x - position().x;
        double d1 = ( attackTarget.position().y + (double) attackTarget.getEyeHeight() )
                - 1.1000000238418579D - entitysnowball.position().y;
        double d2 = attackTarget.position().z - position().z;
        float f = (float)Math.sqrt(d * d + d2 * d2) * 0.2F;
        entitysnowball.shoot(d, d1 + (double) f, d2, 1.6F, 12F);

        //level.playLocalSound(position().x, position().y, position().z, SoundEvents.SNOW_GOLEM_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.0F / ( getRandom().nextFloat() * 0.4F + 0.8F ), false);
        level.playSound( null, position().x, position().y, position().z, SoundEvents.SNOW_GOLEM_SHOOT, SoundSource.NEUTRAL,  1.0F, 1.0F / ( getRandom().nextFloat() * 0.4F + 0.8F ));

        level.addFreshEntity(entitysnowball);
        attackAnim = 30;
        armSwing(!didSwing);
        //mob.faceEntity(attackTarget, 180F, 180F);
        this.snowballin--;

        if (this.snowballin < 0)
        {
            this.snowballin = 0;
        }
    }

    // region Item Hold Logic

    @Override
    public ItemStack getItemInHand(InteractionHand hand)
    {

        if (tempItem != null)
        {
            return tempItem;
        }
        else if(getTempItem() == Items.STICK)
        {
            return new ItemStack(Items.STICK);
        }

        if (queen()) // Queens always carry the gold/iron sword, guards
                     // always have the wooden sword.
        {
            if (getSkin() % 2 == 1)
            {
                return ironSword;
            }

            return goldSword;
        }

        if (guard())
        {
            return woodSword;
        }

        if (medic() && canHeal() && !angry()) // Medics carry potions
        {
            return handPotion();
        }

        if (scout()) // Scouts have maps now.
        {
            return scoutMap;
        }

        //return super.getItemInHand(hand);
        return  ItemStack.EMPTY;
    }

    public void setHeldItem(InteractionHand hand)
    {
        setItemInHand(hand, getItemInHand(hand));
    }

    //endregion

    public void doHeal(LivingEntity guy)
    {
        armSwing(!this.didSwing); // Swings arm and heals the specified person.

        ThrownPotion potion = new ThrownPotion(this.level, this);
        potion.setItem(handPotion());

        Vec3 vec3 = guy.getDeltaMovement();
        double d0 = guy.getX() + vec3.x - this.getX();
        double d1 = guy.getEyeY() - (double)1.1F - this.getY();
        double d2 = guy.getZ() + vec3.z - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        potion.setXRot(potion.getXRot() - -20.0F);
        potion.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);

        level.addFreshEntity(potion);

        //this.getNavigation().stop();
        this.getNavigation().moveTo((Path) null, speedModifier);

        this.healTime = 200;
        setRarePotion(getRandom().nextInt(4) == 0);
    }

    public void castRod()
    {
        if (fishEntity != null)
        {
            ItemStack itemstack = this.getItemInHand(InteractionHand.MAIN_HAND);

            if (!level.isClientSide)
                fishEntity.retrieve(itemstack);

            //FairyFactions.LOGGER.debug(this +": catching fish");

            armSwing(!this.didSwing);
            setSitting(false);
        }
        else
        {

            //FairyFactions.LOGGER.debug(this +": casting fishing rod");

            FairyFishHookEntity hook = new FairyFishHookEntity(this, level, fishingLuckBonus, fishingLuckBonus);

            if (!level.isClientSide)
                this.level.addFreshEntity(hook);

            //level.gameEvent(this, GameEvent.FISHING_ROD_CAST, this);

            armSwing(!this.didSwing);
            setTempItem(Items.STICK);

            setSitting(true);
            this.jumping = false;
            //this.getNavigation().stop();
            this.getNavigation().moveTo((Path) null, speedModifier);
            setTarget(null);
            this.entityFear = null;
        }
    }

    // region Post Detection

    private boolean signContains(SignBlockEntity sign, String str)
    {

        // makes the subsequence
        final CharSequence mySeq = str.subSequence(0, str.length());

        // loops through for all sign lines
        for (int i = 0; i < 4; i++)
        {
            // If the sign's text is messed up or something
            if (sign.getMessage(i, false) == null)
            {
                return false;
            }

            // name just has to be included in full on one of the lines.
            Component text = sign.getMessage(i, true);

//            if(text.getString() != null)
//                FairyFactions.LOGGER.debug(this.toString()+": " + text.getString());

            if(text.getString().contains(mySeq))
            {
                return true;
            }
        }

        return false;
    }

    public boolean canRoamFar(SignBlockEntity sign)
    {
        return signContains(sign, "~f");
    }

    public boolean mySign(SignBlockEntity sign)
    {
        // Converts actual name
        final String actualName = getActualName(getNamePrefix(), getNameSuffix());
        return signContains(sign, actualName);
    }

    public void abandonPost()
    {
        postX = postY = postZ = -1;
        setPosted(false);
    }

    //endregion

    //endregion

    // region ---------- name ----------

    @Nullable
    public String getActualName(int prefix, int suffix)
    {

        //final String custom = getCustomName() != null ? getCustomName().getString() : null;

        Component customComponent = getCustomName();
        String custom = getFairyCustomName();

        if(customComponent != null)
        {
            final String nameTagCustom = customComponent.getString();

            if (nameTagCustom != null && !nameTagCustom.isEmpty())
            {
                return nameTagCustom;
            }
            else
            {
                return null;
            }
        }
//        else if(custom != null && !custom.isEmpty())
//        {
//            return custom;
//        }

        if (prefix < 0 || prefix > MAX_NAMEIDX || suffix < 0
                || suffix > MAX_NAMEIDX)
        {
            return "Error-name";
        }
        else
        {
            return FairyUtils.name_prefixes[prefix] + "-" + FairyUtils.name_suffixes[suffix];
        }
    }

    public String getQueenName(int prefix, int suffix, int faction)
    {
        if (faction < 0 || faction > MAX_FACTION)
            return Loc.QUEEN + " Error-faction";

        return FairyUtils.faction_colors[faction] + Loc.QUEEN + " "
                + getActualName(prefix, suffix);
    }

    public String getQueenName2(int prefix, int suffix, int faction)
    {
        if (faction < 0 || faction > MAX_FACTION)
            return " Error-faction";

        return FairyUtils.faction_colors[faction] + getActualName(prefix, suffix);
        //return getActualName(prefix, suffix);
    }

    public String getFactionName(int faction)
    {
        if (faction < 0 || faction > MAX_FACTION)
            return "Error-faction";

        return FairyUtils.faction_colors[faction] + "<" + FairyUtils.faction_names[faction] + ">";
    }

    @Override
    public Component getDisplayName()
    {
        if (getFaction() != 0)
        {

            if (queen())
            {
                String q = Loc.QUEEN.get();

                Component queenName = Component.translatable(q).withStyle(FairyUtils.faction_colors_formatting[getFaction()]).append(getQueenName2(getNamePrefix(), getNameSuffix(), getFaction()));

                return queenName;
            }
            else
            {
                Component factionName = Component.translatable(getFactionName(getFaction()));

                return factionName;
            }
        }
        else if (tamed())
        {

            MutableComponent name = null;

            String woosh = getActualName(getNamePrefix(), getNameSuffix());
            String q = "";

            if (queen())
            {
                q = Loc.QUEEN.get();
            }

            if (isRuler(ClientMethods.getCurrentPlayer()))
            {
                name = Component.translatable(( posted() ? "§a" : "§c") + "@§f").append(Component.translatable(q)).append(woosh).append(( posted() ? "§a" : "§c") + "@");
            }
            else
            {
                //name = new TranslatableComponent(q).append(woosh);

                name = PlayerTeam.formatNameForTeam(this.getTeam(), Component.translatable(q).append(woosh)).withStyle((p_185975_) ->
                {
                    return p_185975_.withHoverEvent(this.createHoverEvent()).withInsertion(this.getStringUUID());
                });
            }

            return name;
        }
        else
        {
            // wild fairies should not display a nametag

            String woosh = getActualName(getNamePrefix(), getNameSuffix());

            if(woosh == "Fairy")
                FairyUtils.nameFairyEntity(this, "");

            return super.getDisplayName();
        }

        //return super.getDisplayName();
    }

    public String toString()
    {
        return getActualName(getNamePrefix(), getNameSuffix());
    }

    // ----------
    //endregion

    public boolean isRuler(Player player)
    {
        if (player == null)
            return false;

        return tamed() && rulerName().equals(player.getGameProfile().getName());
    }

    public LivingEntity getRuler()
    {
        return ruler;
    }

    public void setRuler(LivingEntity entity)
    {
        ruler = entity;
    }

    public Team getTeam()
    {
        if (tamed())
        {
            LivingEntity livingentity = this.getRuler();

            if (livingentity != null)
            {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    public boolean isAlliedTo(Entity pEntity)
    {
        if (tamed())
        {
            LivingEntity livingentity = this.getRuler();

            if (pEntity == livingentity)
            {
                return true;
            }

            if (livingentity != null)
            {
                return livingentity.isAlliedTo(pEntity);
            }
        }

        return super.isAlliedTo(pEntity);
    }

    public int getSnowballin()
    {
        return snowballin;
    }

    public void setSnowballin(int v)
    {
        snowballin = v;
    }

    // region ---------- stubs ----------

    // region swing

//    @Override
//    public void swing(InteractionHand pHand, boolean pUpdateSelf)
//    {
//        ItemStack stack = this.getItemInHand(pHand);
//        if (!stack.isEmpty() && stack.onEntitySwing(this)) return;
//
//        if (!isSwinging || this.swingTime >= this.getCurrentSwingDuration() / 2 || this.swingTime < 0)
//        {
//            this.swingTime = -1;
//            isSwinging = true;
//            this.swingingArm = pHand;
//            if (this.level instanceof ServerLevel)
//            {
//                ClientboundAnimatePacket clientboundanimatepacket = new ClientboundAnimatePacket(this, 0);
//                ServerChunkCache serverchunkcache = ((ServerLevel)this.level).getChunkSource();
//                if (pUpdateSelf)
//                {
//                    serverchunkcache.broadcastAndSend(this, clientboundanimatepacket);
//                }
//                else
//                {
//                    serverchunkcache.broadcast(this, clientboundanimatepacket);
//                }
//            }
//        }
//    }

    private void processSwinging()
    {
        int i = this.getCurrentSwingDuration();

        if (getArmSwing() != didSwing)
        {
            didSwing = !didSwing;

            this.swingTime = -1;
            isSwinging = true;
            tempItem = null;

            InteractionHand pHand = InteractionHand.MAIN_HAND;

            this.swingingArm = pHand;

//            boolean pUpdateSelf = true;

//            if (this.level instanceof ServerLevel)
//            {
//                ClientboundAnimatePacket clientboundanimatepacket = new ClientboundAnimatePacket(this, 0);
//                ServerChunkCache serverchunkcache = ((ServerLevel)this.level).getChunkSource();
//                if (pUpdateSelf)
//                {
//                    serverchunkcache.broadcastAndSend(this, clientboundanimatepacket);
//                }
//                else
//                {
//                    serverchunkcache.broadcast(this, clientboundanimatepacket);
//                }
//            }
        }

        if (isSwinging)
        {
            ++this.swingTime;

            if (this.swingTime >= i)
            {
                this.swingTime = 0;
                isSwinging = false;

                if (tempItem != null && tempItem != fishingStick)
                {
                    tempItem = null;
                }
            }
            else if (this.tempItem == null)
            {
                if (getTempItem() != Items.AIR)
                {
                    this.tempItem = new ItemStack(getTempItem());
                }
                else if(getTempItem() == Items.STICK)
                {
                    this.tempItem = fishingStick;
                }
            }
        }

        this.attackAnim = (float) this.swingTime / i;

        if (!isSitting() && tempItem != null && tempItem == fishingStick)
        {
            tempItem = null;
        }
    }

    private int getCurrentSwingDuration()
    {
        if (MobEffectUtil.hasDigSpeed(this))
        {
            return 6 - (1 + MobEffectUtil.getDigSpeedAmplification(this));
        }
        else
        {
            return this.hasEffect(MobEffects.DIG_SLOWDOWN) ? 6 + (1 + this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) * 2 : 6;
        }
    }

    //endregion

    private void showHeartsOrSmokeFX(boolean flag)
    {
        //final String s = ( flag ? "heart" : "smoke" );

        final ParticleOptions s = (flag ? ParticleTypes.HEART : ParticleTypes.SMOKE);

        for (int i = 0; i < 7; i++) {
            double d = getRandom().nextGaussian() * 0.02D;
            double d1 = getRandom().nextGaussian() * 0.02D;
            double d2 = getRandom().nextGaussian() * 0.02D;
            level.addParticle(s,
                    ( position().x + (double) ( getRandom().nextFloat() * getBbWidth() * 2.0F ) )
                            - (double) getBbWidth(),
                    position().y + 0.5D + (double) ( getRandom().nextFloat() * getBbHeight() ),
                    ( position().z + (double) ( getRandom().nextFloat() * getBbWidth() * 2.0F ) )
                            - (double) getBbWidth(),
                    d, d1, d2);
        }
    }

    public boolean isSitting()
    {
        return this.entityData.get(SITTING);
    }

    public void setSitting(boolean flag)
    {
        this.entityData.set(SITTING, flag);
    }

    /**
     * This is really confusing. The original reads from byte 19
     * then writes out to byte 22.
     *
     * This must have been a bug.
     *
     * TODO: figure out what this was supposed to do
     */
    protected void setFairyHealth(int i)
    {
        byte byte0 = this.entityData.get(B_HEALTH);

        if (i < 0)
        {
            i = 0;
        }
        else if (i > 255)
        {
            i = 255;
        }

        byte0 = (byte)((byte)i & 0xff);

        this.entityData.set(B_HEALTH, Byte.valueOf(byte0));
    }

    public int fairyHealth()
    {
        return (byte)this.entityData.get(B_HEALTH) & 0xff;
    }
    protected void setFairyClimbing(boolean flag)
    {
        setClimbing(flag);
    }

    @Override
    public void heal(float pHealAmount)
    {
        //pHealAmount = net.minecraftforge.event.ForgeEventFactory.onLivingHeal(this, pHealAmount);

        //if (pHealAmount <= 0) return;

        float f = this.getHealth();

        if (f > 0.0F)
        {
            this.setHealth(f + pHealAmount);
        }
    }

    private void updateWithering()
    {
        if (rogue())
        {
            return;
        }

        witherTime++;

        if (withered())
        {
            // Deplete Health Very Quickly.
            if (witherTime >= 8)
            {
                witherTime = 0;

                if (getHealth() > 1)
                {
                    heal(-1);
                }

                if (level.isDay())
                {
                    int a = (int)Math.floor(position().x);
                    int b = (int)Math.floor(getBoundingBox().minY);
                    int c = (int)Math.floor(position().z);
                    float f = getBrightness();

                    if (f > 0.5F && level.canSeeSky(new BlockPos(a, b, c))
                            && getRandom().nextFloat() * 5F < ( f - 0.4F ) * 2.0F)
                    {
                        setWithered(false);

                        if (tamed())
                        {
                            setHearts(!didHearts);
                        }

                        witherTime = 0;
                        return;
                    }
                }
            }

            setWithered(true);
        }
        else
        {
            if (witherTime % 10 == 0)
            {
                int a = (int)Math.floor(position().x);
                int b = (int)Math.floor(getBoundingBox().minY);
                int c = (int)Math.floor(position().z);
                float f = getBrightness();

                if (f > 0.05F || level.canSeeSky(new BlockPos(a, b, c)) )
                {
                    witherTime = getRandom().nextInt(3);
                }
                else if (witherTime >= 900)
                {
                    setWithered(true);
                    witherTime = 0;
                    return;
                }
            }

            setWithered(false);
        }
    }

    public float getBrightness()
    {
        return this.level.hasChunkAt(this.getBlockX(), this.getBlockZ()) ? this.level.getBrightness(LightLayer.SKY, new BlockPos(this.getX(), this.getEyeY(), this.getZ())) : 0.0F;
    }

    @Override
    public void setTarget(@Nullable LivingEntity entity)
    {

        if (entity == null || this.getTarget() == null || entity != this.getTarget() || (entity instanceof FairyEntity && FairyUtils.sameTeam(this, (FairyEntity)entity)))
        {
            loseInterest = 0;
        }

        //this.target = entity;
        super.setTarget(entity);
    }

    /* TAMEABLE */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {

        //_dump_();

        if (getVehicle() == null || getVehicle() == player || getVehicle() instanceof Minecart )
        {
            ItemStack stack = player.getItemInHand(hand);
            Item item = stack.getItem();

            // TODO: spawn eggs aren't producing queens.  Add a EntityAgeable style interaction
            // to get them, the way you can spawn baby cows...

            if (isRuler(player))
            {
                if (stack != null && getHealth() < getMaxHealth()
                        && FairyUtils.acceptableFoods(this, stack)
                        && stack.getCount() > 0)
                {

                    // right-clicking sugar or glistering melons will heal
                    stack.shrink(1);

                    setHearts(!hearts());

                    if (stack.getItem() == Items.SUGAR)
                    {
                        heal(5);
                    }
                    else
                    {
                        heal(99);

                        if (stack.getItem() == Items.GLISTERING_MELON_SLICE)
                        {
                            setWithered(false);
                            witherTime = 0;
                        }
                    }

                    return InteractionResult.SUCCESS;
                }
                else if (stack != null && FairyUtils.haircutItem(item)
                        && stack.getCount() > 0 && !rogue())
                {
                    // right-clicking with shears will toggle haircut on non-rogues

                    setHairType(!hairType());
                    return InteractionResult.SUCCESS;
                }
                else if (stack != null && getVehicle() == null && !isSitting()
                        && FairyUtils.vileSubstance(stack.getItem())
                        && stack.getCount() > 0)
                {
                    // right-clicking with something nasty will untame

                    this.spawnAtLocation(stack.getItem());

                    stack.shrink(1);

                    disband("(ruler gave vile substance)");

                    return InteractionResult.SUCCESS;
                }
                else if (onGround && stack != null
                        && FairyUtils.namingItem(stack.getItem()) && stack.getCount() > 0)
                {

                    FairyFactions.LOGGER.info("EntityFairy.interract: consuming paper and setting name enabled");

                    // right-clicking with paper will open the rename gui

                    stack.shrink(1);

                    setSitting(true);
                    setNameEnabled(true);

                    this.jumping = false;
                    //this.navigation.stop();
                    this.getNavigation().moveTo((Path) null, speedModifier);
                    setTarget(null);
                    entityFear = null;

                    if(player.level.isClientSide )
                    {
                        if (nameEnabled() && tamed())
                        {
                            if (!rulerName().equals(""))
                            {
                                FairyFactions.LOGGER.info("FairyEntity.tick: calling proxy.openRenameGUI");
                                ClientMethods.openRenameGUI(this);
                            }
                            else
                            {
                                FairyFactions.LOGGER.info("FairyEntity.tick: tame but no ruler...");
                            }
                        }
                    }

                    return InteractionResult.sidedSuccess(player.level.isClientSide);
                }
                else
                {
                    if (isSitting())
                    {
                        if (stack != null
                                && FairyUtils.realFreshOysterBars(stack.getItem())
                                && stack.getCount() > 0)
                        {
                            // right-clicking magma cream on seated fairy invokes "hydra"
                            //hydraFairy();
                        }
                        else
                        {
                            // right-clicking a seated fairy makes it stand up
                            setSitting(false);
                        }

                        return InteractionResult.SUCCESS;
                    }
                    else if (player.isShiftKeyDown())
                    {

                        if (flymode() || !onGround)
                        {
                            // shift-right-clicking while flying aborts flight
                            this.flyTime = 0;
                        }
                        else
                        {
                            // shift-right-clicking otherwise makes fairy sit down
                            setSitting(true);
                            jumping = false;
                            //this.navigation.stop();
                            this.getNavigation().moveTo((Path) null, speedModifier);
                            setTarget(null);
                            entityFear = null;
                        }

                        return InteractionResult.SUCCESS;
                    }
                    else if ((stack == null || !FairyUtils.snowballItem(stack.getItem())) && !player.isShiftKeyDown())
                    {
                        // otherwise, right-clicking wears a fairy hat

                        CommonMethods.sendFairyMount(this, player);

                        setFlymode(true);
                        flyTime = 200;
                        setCanFlap(true);

                        return InteractionResult.SUCCESS;
                    }
                }
            }
            else
            {
                // faction members can be tamed in peaceful
                if (( getFaction() == 0
                        || level.getDifficulty() == Difficulty.PEACEFUL )
                        && !( (queen() || posted() || tamed()) && tamed()) && !crying()
                        && !angry() && stack != null
                        && FairyUtils.acceptableFoods(this, stack)
                        && stack.getCount() > 0)
                {
                    stack.shrink(1);

                    if ((stack.getItem() != Items.SUGAR
                            || getRandom().nextInt(4) == 0))
                    {
                        if (stack.getItem() == Items.SUGAR)
                        {
                            heal(5);
                        }
                        else
                        {
                            heal(99);
                        }

                        tameMe(player);

                        return InteractionResult.SUCCESS;
                    }
                    else
                    {
                        setHearts(!hearts());
                        return InteractionResult.SUCCESS;
                    }
                }
                else if (!tamed())
                {
                    setHearts(!hearts());
                }

                tameFailMessage(player);

                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    //region Faction Logic

    public void disband(String disbandReason)
    {

        FairyFactions.LOGGER.info("disband: " + rulerName() + ": " + this + " " + disbandReason);

        setRulerName("");
        setFaction(0);
        setHearts(!didHearts);
        cryTime = 200;
        setNameEnabled(false);	// Leaving this bit set causes strange behavior
        setTamed(false);
        abandonPost();
        this.snowballin = 0;

        if (ruler != null)
        {

//            Path dest = roam(ruler, this, (float) Math.PI);
//
//            if (dest != null)
//                this.getNavigation().moveTo(dest, speedModifier);

            BlockPos pos = roamBlockPos(
                    ruler.blockPosition().getX(),
                    flymode() ? blockPosition().getY() : ruler.blockPosition().getY(),
                    ruler.blockPosition().getZ(), this, (float) Math.PI);

            if (pos != null)
                this.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);

            if (ruler instanceof Player)
            {
                String name = getActualName(getNamePrefix(), getNameSuffix());

                String queenString = "";

                if (queen())
                {
                    queenString = Loc.QUEEN.get();
                }

                int i = getRandom().nextInt(6);

                String s2 = "";

                if (queen() && i < 3)
                {
                    s2 = Loc.DISBAND_QUEEN_1.get();
                }
                else if (queen())
                {
                    s2 = Loc.DISBAND_QUEEN_2.get();
                }
                else if (i == 0)
                {
                    s2 = Loc.DISBAND_OTHER_1.get();
                }
                else if (i == 1)
                {
                    s2 = Loc.DISBAND_OTHER_2.get();
                }
                else if (i == 2)
                {
                    s2 = Loc.DISBAND_OTHER_3.get();
                }
                else if (i == 3)
                {
                    s2 = Loc.DISBAND_OTHER_4.get();
                }
                else if (i == 4)
                {
                    s2 = Loc.DISBAND_OTHER_5.get();
                }
                else
                {
                    s2 = Loc.DISBAND_OTHER_6.get();
                }

                String finalQueenString = queenString;
                String finalS = s2;

                if (ruler instanceof ServerPlayer)
                {
                    CommonMethods.sendChat((ServerPlayer) ruler, Component.translatable(finalQueenString).append(name).append(Component.translatable(finalS)));
                }
            }
        }

        setFairyCustomName("");
        //setCustomName(null);
        FairyUtils.nameFairyEntity(this, "");

        ruler = null;
    }

    public void tameMe(Player player)
    {
        if (player == null)
        {
            return;
        }

        setFaction(0);
        setNameEnabled(false);	// Leaving this bit set causes strange behavior
        setTamed(true);
        setRulerName(player.getGameProfile().getName());
        setHearts(!hearts());
        abandonPost();
        this.snowballin = 0;
        ruler = player;

        this.navigation.stop();
        this.setTarget(null);

        if (scout())
        {
            cower = false;
        }

        String n = getActualName(getNamePrefix(), getNameSuffix());

        String s = "";

        if (queen())
        {
            s = Loc.QUEEN.get();
        }

        String message = "";

        int i = getRandom().nextInt(6);

        if (queen() && i < 2)
        {
            message = Loc.TAME_QUEEN_1.get();
        }
        else if (queen() && i > 3)
        {
            message = Loc.TAME_QUEEN_2.get();
        }
        else if (queen())
        {
            message = Loc.TAME_QUEEN_3.get();
        }
        else if (i == 0)
        {
            message = Loc.TAME_OTHER_1.get();
        }
        else if (i == 1)
        {
            message = Loc.TAME_OTHER_2.get();
        }
        else if (i == 2)
        {
            message = Loc.TAME_OTHER_3.get();
        }
        else if (i == 3)
        {
            message = Loc.TAME_OTHER_4.get();
        }
        else if (i == 4)
        {
            message = Loc.TAME_OTHER_5.get();
        }
        else
        {
            message = Loc.TAME_OTHER_6.get();
        }

        if (player instanceof ServerPlayer)
        {
            String finalS = s;
            String finalMessage = message;

            CommonMethods.sendChat((ServerPlayer) ruler, Component.translatable(finalS).append(n).append(Component.translatable(finalMessage)));
        }

        FairyFactions.LOGGER.info("tameMe: " + rulerName() + ": " + this);
    }

    public void alertFollowers(Entity entity)
    {
        if (queen() && getFaction() > 0)
        {
            List list = this.level.getEntitiesOfClass(FairyEntity.class,
                    getBoundingBox().inflate(40D, 40D, 40D));

            for (int j = 0; j < list.size(); j++)
            {
                FairyEntity fairy = (FairyEntity) list.get(j);

                if (fairy != this && fairy.getHealth() > 0 && FairyUtils.sameTeam(this, fairy)
                        && ( fairy.ruler == null || fairy.ruler == this ))
                {
                    if (fairy.getVehicle() != null)
                    {
                        CommonMethods.sendFairyMount(fairy, fairy.getVehicle());
                    }

                    fairy.setTarget(null);
                    fairy.cryTime = 300;
                    fairy.setFaction(0);
                    // if(entity != null && entity instanceof EntityLiving) {
                    fairy.entityFear = null;
                    // }
                    fairy.ruler = null;
                }
            }
        }
    }

    public void alertRuler(Entity entity)
    {
        if (getFaction() > 0 && ruler != null && ruler instanceof FairyEntity
                && FairyUtils.sameTeam(this, (FairyEntity) ruler))
        {
            FairyEntity queen = ( (FairyEntity) ruler );
            boolean flag = false;
            List list = this.level.getEntitiesOfClass(FairyEntity.class,
                    queen.getBoundingBox().inflate(40D, 40D, 40D));

            for (int j = 0; j < list.size(); j++)
            {
                FairyEntity fairy = (FairyEntity) list.get(j);

                if (fairy != queen && fairy.getHealth() > 0
                        && FairyUtils.sameTeam(queen, fairy)
                        && ( fairy.ruler == null || fairy.ruler == queen ))
                {
                    flag = true;
                    break;
                }
            }

            if (!flag)
            {
                queen.setTarget(null);
                queen.cryTime = 600;
                queen.setFaction(0);
                // if(entity != null && entity instanceof EntityLiving) {
                queen.entityFear = null;
                // }
            }
        }
        else if (tamed() && ruler != null
                && ruler instanceof ServerPlayer)
        {
            // EntityPlayerMP player = (EntityPlayerMP)ruler;
            String n = getActualName(getNamePrefix(), getNameSuffix());

            String s = "";

            if (queen())
            {
                s = Loc.QUEEN.get();
            }

            String s2 = "";

            int i = random.nextInt(7);

            if (i == 0)
            {
                s2 = Loc.DEATH_1.get();
            }
            else if (i == 1)
            {
                s2 = Loc.DEATH_2.get();
            }
            else if (i == 2)
            {
                s2 = Loc.DEATH_3.get();
            }
            else if (i == 3)
            {
                s2 = Loc.DEATH_4.get();
            }
            else if (i == 4)
            {
                s2 = Loc.DEATH_5.get();
            }
            else if (i == 5)
            {
                s2 = Loc.DEATH_6.get();
            }
            else
            {
                s2 = Loc.DEATH_7.get();
            }

            String finalS = s;
            String finalS1 = s2;

            CommonMethods.sendChat((ServerPlayer) ruler, Component.translatable(finalS).append(n).append(Component.translatable(finalS1)));
        }
    }

    // Don't let that spider bite you, spider bite hurt.
//    public void hydraFairy()
//    {
//        double a = ( getBoundingBox().minX + getBoundingBox().maxX ) / 2D;
//        double b = ( getBoundingBox().minY + (double) this.getY() ) - (double) ySize;
//        double c = ( getBoundingBox().minZ + getBoundingBox().maxZ ) / 2D;
//        motionX = 0D;
//        motionY = -0.1D;
//        motionZ = 0D;
//
//        // Anthony stopped to tie his shoe, and they all went marching on.
//        jumping = false;
//        moveForward = 0F;
//        moveStrafing = 0F;
//        navigation.moveTo((Path) null, speedModifier);
//        setSitting(true);
//        onGround = true;
//        List list = level.getEntitiesOfClass(FairyEntity.class,
//                getBoundingBox().inflate(80D, 80D, 80D));
//
//        for (int j = 0; j < list.size(); j++) {
//            FairyEntity fairy = (FairyEntity) list.get(j);
//
//            if (fairy != this && fairy.getHealth() > 0 && FairyUtils.sameTeam(this, fairy)
//                    && fairy.getVehicle() == null
//                    && fairy.getPassengers() == null)
//            {
//                fairy.setTarget((LivingEntity) null);
//                fairy.cryTime = 0;
//                fairy.entityFear = null;
//                // I'll pay top dollar for that Gidrovlicheskiy in the window.
//                fairy.setPos(a, b, c);
//                fairy.motionX = 0D;
//                fairy.motionY = -0.1D;
//                fairy.motionZ = 0D;
//                fairy.jumping = false;
//                fairy.moveForward = 0F;
//                fairy.moveStrafing = 0F;
//                fairy.getNavigation().moveTo((Path) null, speedModifier);
//                fairy.setSitting(true);
//                fairy.onGround = true;
//                // It feels like I'm floating but I'm not
//                fairy.setFlymode(false);
//            }
//        }
//    }

    //endregion

    @Override
    public boolean onClimbable()
    {
        return climbing();
    }

    //attackEntityFrom(DamageSource damagesource, float damage)
    @Override
    public boolean hurt(DamageSource damagesource, float damage)
    {
        boolean ignoreTarget = false;
        Entity entity = damagesource.getEntity();

        if (ruler != null && getVehicle() != null && getVehicle() == ruler)
        {
            // Prevents the fairy from recieving any damage if on the tamer's
            // head.
            return false;
        }

        if (ruler != null && entity != null && ruler == entity
                && ruler instanceof Player)
        {
            if (snowballFight(damagesource))
            {
                // Prevents a fairy from being damaged by its ruler at all if
                // it's a player.
                return false;
            }
            else
            {
                ignoreTarget = true;
            }
        }

        if(damagesource == DamageSource.SWEET_BERRY_BUSH)
        {
            return false;
        }

        if (tamed() && !rulerName().equals("") && entity != null)
        {
            if (entity instanceof Player
                    && isRuler((Player) entity))
            {
                if (!ignoreTarget && snowballFight(damagesource))
                {
                    // Another handler made for sitting fairies just in case.
                    return false;
                }
                else
                {
                    ignoreTarget = true;
                }
            }
            else if (entity instanceof Wolf
                    && ( (Wolf) entity ).isTame()
                    && isRuler((Player) ((Wolf) entity).getOwner()))
            {
                // Protects against ruler-owned wolves.
                Wolf wolf = (Wolf) entity;
                wolf.setTarget(null);
                return false;
            }
        }

        if (( guard() || queen() ) && damage > 1)
        {
            // Guards and queens receive two thirds damage, won't reduce to 0 if
            // it was at least 1 to begin with.
            damage *= 2;
            damage /= 3;
            damage = Math.max(damage, 1);
        }

        boolean flag = super.hurt(damagesource, damage);

        // Stop them from running really fast
        //fleeingTick = 0;

        if (flag && getHealth() > 0)
        {
            if (entity != null)
            {
                if (entity instanceof LivingEntity && !ignoreTarget)
                {
                    if (getTarget() == null && cower
                            && getRandom().nextInt(2) == 0)
                    {
                        // Cowering fairies will have a chance of becoming
                        // offensive.
                        cryTime += 120;
                        entityFear = entity;

//                        Path dest = roam(entity, this, (float) Math.PI);
//
//                        if (dest != null)
//                        {
//                            this.getNavigation().moveTo(dest, speedModifier);
//                        }

                        BlockPos pos = roamBlockPos(
                                entity.blockPosition().getX(),
                                flymode() ? blockPosition().getY() : entity.blockPosition().getY(),
                                entity.blockPosition().getZ(), this, (float) Math.PI);

                        if (pos != null)
                            this.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);

                    }
                    else
                    {
                        // Become aggressive - no more screwing around.
                        setTarget((LivingEntity) entity);
                        entityFear = null;
                        cryTime = 0;
                    }
                }
                else
                {
                    // This just makes fairies run from inanimate objects that
                    // hurt them.
//                    Path dest = roam(entity, this, (float) Math.PI);
//
//                    if (dest != null)
//                    {
//                        this.getNavigation().moveTo(dest, speedModifier);
//                    }

                    BlockPos pos = roamBlockPos(
                            entity.blockPosition().getX(),
                            flymode() ? blockPosition().getY() : entity.blockPosition().getY(),
                            entity.blockPosition().getZ(), this, (float) Math.PI);

                    if (pos != null)
                        this.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);
                }
            }

            // A fairy will get up if hurt while sitting.
            if (isSitting())
            {
                setSitting(false);
            }

            if (getVehicle() != null && getRandom().nextInt(2) == 0)
            {
                CommonMethods.sendFairyMount(this, getVehicle());
            }
        }
        else if (flag)
        {
            if (getVehicle() != null)
            {
                CommonMethods.sendFairyMount(this, getVehicle());
            }

            if (queen() && !tamed())
            {
                alertFollowers(entity);
            }
            else
            {
                alertRuler(entity);
            }
        }

        return flag;
    }

    public boolean doAttack(Entity entity)
    {
        // Swings arm and attacks.
        armSwing(!this.didSwing);
        //swing(InteractionHand.MAIN_HAND, true);

        if (rogue() && this.healTime <= 0 && entity != null && entity instanceof LivingEntity)
        {
            boolean flag = entity.hurt(DamageSource.mobAttack(this), attackStrength());
            if (flag)
                applyPoison((LivingEntity)entity);

            return flag;
        }

        return entity.hurt(DamageSource.mobAttack(this), attackStrength());
    }

    protected int attackStrength()
    {
        // Self explanatory.
        if (queen())
        {
            return 5;
        }
        else if (guard())
        {
            return 4;
        }
        else if (rogue())
        {
            return 3;
        }
        else
        {
            return 2;
        }
    }
    public void applyPoison(LivingEntity entityliving)
    {
        byte duration = 0;

        switch (this.level.getDifficulty())
        {
            case NORMAL:
                duration = 7;
                break;
            case HARD:
                duration = 15;
                break;
            case PEACEFUL:
            case EASY:
            default:
                // no poison in peaceful or normal
                return;
        }

        int effect = this.random.nextInt(3);
        MobEffect mobEffect;

        switch (effect)
        {
            case 0:
                mobEffect = MobEffects.POISON;
                break;
            case 1:
                mobEffect = MobEffects.WEAKNESS;
                break;
            default:
                mobEffect = MobEffects.BLINDNESS;
                return;
        }

        entityliving.addEffect(new MobEffectInstance(mobEffect, duration * 20, 0));

        this.healTime = 100 + this.random.nextInt(100);
        setTarget(null);
        this.entityFear = entityliving;
        this.cryTime = this.healTime;
    }

    public void tameFailMessage(Player player)
    {
        String s = Loc.TAME_FAIL_PREFIX.get();
        String s2 = "";

        if (angry())
        {
            s2 = Loc.TAME_FAIL_ANGRY.get();
        }
        else if (crying())
        {
            s2 = Loc.TAME_FAIL_CRYING.get();
        }
        else if (getFaction() > 0)
        {
            if (queen())
            {
                s2 = Loc.TAME_FAIL_HAS_FOLLOWERS.get();
            }
            else
            {
                s2 = Loc.TAME_FAIL_HAS_QUEEN.get();
            }
        }
        else if (tamed() )
        {
            if(queen())
            {
                s2 = Loc.TAME_FAIL_TAME_QUEEN.get();
            }
            else
            {
                s2 = Loc.TAME_FAIL_TAME_FAIRY.get();
            }

        }
        else if (posted())
        {
            s2 = Loc.TAME_FAIL_POSTED.get();
        }
        else
        {
            ItemStack stack = (ItemStack) null;

            if (player.getItemInHand(InteractionHand.MAIN_HAND) != null)
            {
                stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            }

            if (stack != null && stack.getCount() > 0
                    && stack.getItem() == Items.GLOWSTONE_DUST)
            {
                s2 = Loc.TAME_FAIL_GLOWSTONE.get();
            }
            else if (queen())
            {
                s2 = Loc.TAME_FAIL_NOT_MELON.get();
            }
            else
            {
                s2 = Loc.TAME_FAIL_NOT_SNACK.get();
            }
        }

        if (player instanceof ServerPlayer)
        {
            String finalS = s2;
            CommonMethods.sendChat((ServerPlayer) player, Component.translatable(s).append(Component.translatable(finalS)));

        }
    }

    public FairyFishHookEntity getFishEntity()
    {
        return this.fishEntity;
    }

    public float getLuck()
    {
        return (float)this.getAttributeValue(Attributes.LUCK);
    }

    public void setFishEntity(FairyFishHookEntity fishEntity)
    {
        this.fishEntity = fishEntity;
    }

    public Entity getEntityFear()
    {
        return this.entityFear;
    }

    public void setEntityFear(Entity entityFear)
    {
        this.entityFear = entityFear;
    }

    public int getCryTime()
    {
        return this.cryTime;
    }

    public void setCryTime(int cryTime)
    {
        this.cryTime = cryTime;
    }

    public int getLooseInterest()
    {
        return this.loseInterest;
    }

    public void setLooseInterest(int loseInterest)
    {
        this.loseInterest = loseInterest;
    }

    public int getFlyTime()
    {
        return this.flyTime;
    }

    public void setHealTime(int healTime)
    {
        this.healTime = healTime;
    }

    public int getHealTime()
    {
        return this.healTime;
    }

    public void setFlyTime(int flyTime)
    {
        this.flyTime = flyTime;
    }

    public boolean willCower()
    {
        return this.cower;
    }

    public void setCower(boolean cower)
    {
        this.cower = cower;
    }

    public void setLoseTeam(int value)
    {
        this.loseTeam = value;
    }

    public int getLoseTeam()
    {
        return this.loseTeam;
    }

    public static boolean checkFairySpawnRules(EntityType<? extends FairyEntity> pFairy, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom)
    {
        return pLevel.getBlockState(pPos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && isBrightEnoughToSpawn(pLevel, pPos);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType mobSpawnType, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag)
    {
        if(FairyEntity.checkFairySpawnRules((EntityType<? extends FairyEntity>) this.getType(), pLevel, mobSpawnType, this.blockPosition(), this.random))
        {
            if(ruler == null && !tamed() && getFaction() == 0)
            {
                boolean doCreateGroup = false; //= fairy.getRandom().nextInt(2) != 0;

                //int a = new Random().nextInt(11);

                if (FairyUtils.percentChance(this, 0.6))
                {
                    doCreateGroup = true;
                }

                if(doCreateGroup)
                {
                    List list = this.level.getEntitiesOfClass(FairyEntity.class,
                            this.getBoundingBox().inflate(32D, 32D, 32D));

                    if (( list == null || list.size() < 1 ) && !this.level.isClientSide)
                    {
                        setJob(0);
                        setSpecialJob(true);
                        heal(30);
                        setHealth(30);
                        int i = random.nextInt(15) + 1;
                        setFaction(i);
                        setSkin(random.nextInt(4));
                        setCower(false);
                        createGroup = true;
                    }
                }
            }
        }

//        else if(ruler == null && !isRuler(Minecraft.getInstance().player) && getFaction() != 0)
//        {
//            List list = this.level.getEntitiesOfClass(FairyEntity.class,
//                    this.getBoundingBox().inflate(32D, 32D, 32D));
//
//            for (int j = 0; j < list.size(); j++)
//            {
//                FairyEntity fairy = (FairyEntity) list.get(j);
//
//                if (fairy != this && FairyUtils.sameTeam(fairy, this)
//                        && fairy.getHealth() > 0)
//                {
//                    flag = false;
//                    break;
//                }
//            }
//        }

        return super.finalizeSpawn(pLevel, pDifficulty, mobSpawnType, pSpawnData, pDataTag);
    }

//    /**
//     * Static predicate for determining whether or not fairy can spawn at the provided location.
//     * @param pAnimal The animal entity to be spawned
//     */
//    public static boolean checkFairySpawnRules(EntityType<? extends FairyEntity> pAnimal, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, Random pRandom)
//    {
//        //return pLevel.getBlockState(pPos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && isBrightEnoughToSpawn(pLevel, pPos);
//
//        if(Animal.checkAnimalSpawnRules(pAnimal, pLevel, pSpawnType, pPos, pRandom))
//        {
//            if(this.ruler == null && !isRuler(Minecraft.getInstance().player) && getFaction() == 0)
//            {
//                List list = this.level.getEntitiesOfClass(FairyEntity.class,
//                        this.getBoundingBox().inflate(32D, 32D, 32D));
//
//                if ((list == null || list.size() < 1) && !this.level.isClientSide)
//                {
//                    setJob(0);
//                    setSpecialJob(true);
//                    heal(30);
//                    setHealth(30);
//                    int i = random.nextInt(15) + 1;
//                    setFaction(i);
//                    setSkin(random.nextInt(4));
//                    setCower(false);
//                    createGroup = true;
//                }
//
//                return true;
//            }
//        }
//
//        return false;
//    }

    //endregion
}
