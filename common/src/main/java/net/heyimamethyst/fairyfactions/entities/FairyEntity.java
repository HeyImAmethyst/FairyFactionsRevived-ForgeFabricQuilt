package net.heyimamethyst.fairyfactions.entities;

import net.heyimamethyst.fairyfactions.FairyConfigValues;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.Loc;
import net.heyimamethyst.fairyfactions.entities.ai.*;
import net.heyimamethyst.fairyfactions.entities.ai.goals.*;
import net.heyimamethyst.fairyfactions.items.FairyWandItem;
import net.heyimamethyst.fairyfactions.proxy.ClientMethods;
import net.heyimamethyst.fairyfactions.proxy.CommonMethods;
import net.heyimamethyst.fairyfactions.registry.ModItems;
import net.heyimamethyst.fairyfactions.registry.ModSounds;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.heyimamethyst.fairyfactions.util.MathUtil;
import net.heyimamethyst.fairyfactions.util.NBTUtil;
import net.heyimamethyst.fairyfactions.world.FairyGroupGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

//Parts of chest storing code came from : https://github.com/baileyholl/Ars-Nouveau/blob/1.18.x/src/main/java/com/hollingsworth/arsnouveau/common/entity/Starbuncle.java

public class FairyEntity extends FairyEntityBase
{

    private boolean				cower;
    public boolean				didHearts;
    public boolean				didSwing;

    private boolean             hasFlapped = false;

    private boolean				wasFishing;
    public int                  fishingSpeedBonus;
    public int                  fishingLuckBonus;

    private int					snowballin;

    private int					flyTime;
    private int					requestFoodTime;
    private int					healTime;
    private int					cryTime;
    private int					loseInterest;
    private int					loseTeam;

    public int                 postedCount;	// flag for counting posted checks
    public int				   postX, postY, postZ;

    public List<BlockPos> TO_LIST = new ArrayList<>();
    public List<BlockPos> FROM_LIST = new ArrayList<>();

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

    int foodTimerBaseValue = 800000;

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

        //setWantedFoodItem(Items.AIR);

        this.sinage = getRandom().nextFloat();
        this.flyTime = 400 + this.getRandom().nextInt(200);
        this.requestFoodTime = foodTimerBaseValue + this.getRandom().nextInt(501);
        this.cower = this.getRandom().nextBoolean();

        this.postX = this.postY = this.postZ = -1;

        fairyBehavior = new FairyBehavior(this, speedModifier);
        switchNavigator(false);

        //this.maxUpStep = 1.0f;
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
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(1, fairyAttackGoal);

        this.goalSelector.addGoal(4, new FairyAIGoal((this)));

        this.goalSelector.addGoal(4, new FairyAIStroll(this, 0.3D));
        this.goalSelector.addGoal(12, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));
    }

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
        this.entityData.define(EMOTIONAL, false);
        this.entityData.define(SLEEPING, false);

        this.entityData.define(DELIVERY_MODE, false);

        this.entityData.define(B_FLAGS, (byte) 0);
        this.entityData.define(B_FLAGS2, (byte) 0);
        this.entityData.define(B_TYPE, (byte) 0);
        this.entityData.define(B_HEALTH, (byte) 0);
        this.entityData.define(B_NAME_ORIG, (byte) 0);
        this.entityData.define(S_OWNER, "");
        this.entityData.define(S_NAME_REAL, "");
        this.entityData.define(I_TOOL, 0);
        this.entityData.define(WANTED_FOOD, 0);
        this.entityData.define(ITEM_INDEX, 0);
        this.entityData.define(BED_LOCATION, Optional.empty());
        this.entityData.define(POST_CHEST_LOCATION, Optional.empty());

        this.entityData.define(TO_POS_SIZE, 0);
        this.entityData.define(FROM_POS_SIZE, 0);

        this.entityData.define(HELD_STACK, ItemStack.EMPTY);
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
        tag.putShort("requestFoodTIme", (short) requestFoodTime);
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
        tag.putBoolean("emotional", this.isEmotional());
        tag.putBoolean("sleeping", this.isSleeping());

        tag.putInt("wanted_food", this.entityData.get(WANTED_FOOD));
        tag.putInt("item_index", this.entityData.get(ITEM_INDEX));

        tag.putBoolean("delivery_mode", this.getDeliveryMode());

        //tag.put("held_item", this.entityData.get(HELD_STACK));

        int counter = 0;
        for (BlockPos p : FROM_LIST) {
            NBTUtil.storeBlockPos(tag, "from_" + counter, p);
            counter++;
        }
        counter = 0;
        for (BlockPos p : TO_LIST) {
            NBTUtil.storeBlockPos(tag, "to_" + counter, p);
            counter++;
        }

        this.entityData.get(BED_LOCATION).ifPresent(blockPos ->
        {
            tag.putInt("bedPosX", blockPos.getX());
            tag.putInt("bedPosY", blockPos.getY());
            tag.putInt("bedPosZ", blockPos.getZ());
        });

        this.entityData.get(POST_CHEST_LOCATION).ifPresent(blockPos ->
        {
            tag.putInt("chestPosX", blockPos.getX());
            tag.putInt("chestPosY", blockPos.getY());
            tag.putInt("chestPosZ", blockPos.getZ());
        });
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);

        this.entityData.set(B_FLAGS, tag.getByte("flags"));
        this.entityData.set(B_FLAGS2, tag.getByte("flags2"));
        this.entityData.set(B_TYPE, tag.getByte("type"));
        this.entityData.set(B_NAME_ORIG, tag.getByte("nameOrig"));


        FROM_LIST = new ArrayList<>();
        TO_LIST = new ArrayList<>();
        int counter = 0;

        while (NBTUtil.hasBlockPos(tag, "from_" + counter))
        {
            BlockPos pos = NBTUtil.getBlockPos(tag, "from_" + counter);
            if (!this.FROM_LIST.contains(pos))
                this.FROM_LIST.add(pos);
            counter++;
        }

        counter = 0;
        while (NBTUtil.hasBlockPos(tag, "to_" + counter))
        {
            BlockPos pos = NBTUtil.getBlockPos(tag, "to_" + counter);
            if (!this.TO_LIST.contains(pos))
                this.TO_LIST.add(pos);
            counter++;
        }

        this.entityData.set(TO_POS_SIZE, TO_LIST.size());
        this.entityData.set(FROM_POS_SIZE, FROM_LIST.size());

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
        requestFoodTime = tag.getShort("requestFoodTime");
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
        setEmotional(tag.getBoolean("emotional"));
        setSleeping(tag.getBoolean("sleeping"));

        setWantedFoodItem(Item.byId(tag.getInt("wanted_food")));
        setItemIndex(tag.getInt("item_index"));

        setDeliveryMode(tag.getBoolean("delivery_mode"));

        if (tag.contains("bedPosX") && tag.contains("bedPosY") && tag.contains("bedPosZ"))
        {
            BlockPos blockPos = new BlockPos(tag.getInt("bedPosX"), tag.getInt("bedPosY"), tag.getInt("bedPosZ"));

            this.setBedLocation(blockPos);
        }

        if (tag.contains("chestPosX") && tag.contains("chestPosY") && tag.contains("chestPosZ"))
        {
            BlockPos blockPos = new BlockPos(tag.getInt("chestPosX"), tag.getInt("chestPosY"), tag.getInt("chestPosZ"));

            this.setPostChestLocation(blockPos);
        }

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
    public void onWanded(Player playerEntity)
    {
        clearPostChestLocation();
        this.FROM_LIST = new ArrayList<>();
        this.TO_LIST = new ArrayList<>();
        this.entityData.set(TO_POS_SIZE, 0);
        this.entityData.set(FROM_POS_SIZE, 0);
        //this.bedPos = null;
        clearBedLocation();

        setDeliveryMode(false);

        spawnAtLocation(getHeldItem());
        setHeldItem(ItemStack.EMPTY);

        String n = getActualName(getNamePrefix(), getNameSuffix());

        if (playerEntity.level.isClientSide)
            playerEntity.displayClientMessage( new TextComponent(n).append(new TranslatableComponent(Loc.FAIRY_CLEARED.get())), false);

        //CommonMethods.sendChat((ServerPlayer) playerEntity, new TranslatableComponent(Loc.FAIRY_CLEARED.get()));
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity)
    {
        if (storedPos == null)
            return;

        String n = getActualName(getNamePrefix(), getNameSuffix());

        if(posted())
        {
            if (level.getBlockEntity(storedPos) != null && isChestContainer(storedPos, level))
            {
                if(getDeliveryMode())
                {
                    CommonMethods.sendChat((ServerPlayer) playerEntity, new TextComponent(n).append(new TranslatableComponent(Loc.FAIRY_STORE.get())));
                    setToPos(storedPos);
                }
                else
                {
                    CommonMethods.sendChat((ServerPlayer) playerEntity, new TextComponent(n).append(new TranslatableComponent(Loc.FAIRY_STORE_AND_TAKE.get())));
                    setPostChestLocation(storedPos);
                }
            }
        }

    }

    @Override
    public void onFinishedConnectionFirst(@Nullable Entity storedEntityPos, @Nullable LivingEntity storedEntity, Player playerEntity)
    {
        if(posted())
        {
            if(storedEntityPos instanceof FairyBedEntity)
            {

                FairyBedEntity fairyBedEntity = (FairyBedEntity) storedEntityPos;

                String n = getActualName(getNamePrefix(), getNameSuffix());

                if (playerEntity.level.isClientSide)
                    playerEntity.displayClientMessage( new TranslatableComponent(Loc.FAIRY_SET_BED.get()).append(new TextComponent(n)), false);

                fairyBedEntity.setFairyOwner(n);
                setBedLocation(storedEntityPos.blockPosition());
            }
        }
    }

    public boolean isChestContainer(@Nullable BlockPos storedPos, Level level)
    {
        return level.getBlockEntity(storedPos) instanceof ChestBlockEntity;
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity)
    {
        if(posted())
        {
            if (storedPos == null)
                return;

            String n = getActualName(getNamePrefix(), getNameSuffix());

            if (level.getBlockEntity(storedPos) != null && isChestContainer(storedPos, level))
            {

                if(getDeliveryMode())
                {
                    //CommonMethods.sendChat((ServerPlayer) playerEntity, new TranslatableComponent(Loc.FAIRY_TAKE.get()));

                    if (playerEntity.level.isClientSide)
                        playerEntity.displayClientMessage(new TextComponent(n).append(new TranslatableComponent(Loc.FAIRY_TAKE.get())), false);

                    setFromPos(storedPos);
                }
                else
                {
                    //CommonMethods.sendChat((ServerPlayer) playerEntity, new TranslatableComponent(Loc.FAIRY_CANT_ASSIGN_TAKE_CHEST.get()));

                    if (playerEntity.level.isClientSide)
                        playerEntity.displayClientMessage(new TranslatableComponent(Loc.FAIRY_CANT_ASSIGN_TAKE_CHEST.get()), false);

                    return;
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        //_dump_();

        if (this.createGroup)
        {

            createGroup = false;
            int i = (int) Math.floor(position().x);
            int j = (int) Math.floor(getBoundingBox().minY) - 1;
            int k = (int) Math.floor(position().z);

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

        if (!level.isClientSide)
        {
            updateWithering();
            setHealth(getHealth());

            Path currentPath = this.getNavigation().getPath();
            setFairyClimbing(flymode() && canFlap() && currentPath != null && horizontalCollision);

            if (isSitting() && ((getVehicle() != null && !isSleeping()) || !onGround))
            {
                setSitting(false);
            }

            setPosted(postY > -1);

//            if(posted() && isEmotional() && (queen() && !isSitting()))
//            {
//                setSitting(true);
//            }
        }

        if (getHealth() > 0.0F)
        {
            // wing animations

            if (!this.onGround && !(this.getVehicle() instanceof FairyBedEntity))
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

            if (this.getVehicle() != null)
                vehicleVec3 = this.getVehicle().getDeltaMovement();

            motionX = vec3.x;
            motionY = vec3.y;
            motionZ = vec3.z;

            vehicleMotionX = vehicleVec3.x;
            vehicleMotionY = vehicleVec3.y;
            vehicleMotionZ = vehicleVec3.z;

            if (flymode())
            {
                if (!liftOff() && getVehicle() != null && !(this.getVehicle() instanceof FairyBedEntity) && !getVehicle().isOnGround()
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

                    if ((((LivingEntity) getVehicle() instanceof Player && isJumping) || !((LivingEntity) getVehicle() instanceof Player))
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

                if (this.getVehicle() != null)
                {
                    vehicleMotionX = this.getVehicle().getDeltaMovement().x;
                    vehicleMotionZ = this.getVehicle().getDeltaMovement().z;
                }

                motionX = this.getDeltaMovement().x;
                motionZ = this.getDeltaMovement().z;
            }

            if (this.getVehicle() != null)
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

                if (angry() || (crying() && queen()) || isEmotional())
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

                if(posted() && getDeliveryMode())
                {
                    this.level.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D), (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(), (random.nextDouble() - 0.5D) * 2.0D);
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

        if (FairyConfigValues.ENABLE_FAIRY_WING_SOUNDS && flymode())
        {

            if(!MathUtil.isNegative(Math.sin(sinage)))
            {
                if(!hasFlapped)
                {
                    this.playSound(ModSounds.FAIRY_FLAP.get(), 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    hasFlapped = true;
                }
            }

            if(MathUtil.isNegative(Math.sin(sinage)))
            {
                hasFlapped = false;
            }
        }

        setHeldFairyItem(MAIN_FAIRY_HAND);
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

        if (posted() && this.requestFoodTime > 0)
        {
            this.requestFoodTime--;
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

            if (this.getVehicle() != null && !(this.getVehicle() instanceof FairyBedEntity))
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
                        if ((this.getVehicle().isOnGround() || this.getVehicle().isInWater()) && !isSleeping())
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

            if (this.flyTime <= 0 || isEmotional() || (this.flyBlocked
                    && (this.getVehicle() == null || (this.getTarget() != null
                    && this.getVehicle() == this.getTarget() && this.getVehicle() instanceof FairyBedEntity))))
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
            if (this.flyTime <= 0 && !this.flyBlocked && this.getVehicle() != null && !(this.getVehicle() instanceof FairyBedEntity))
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

            if (this.getVehicle() != null && !(this.getVehicle() instanceof FairyBedEntity) && !flymode())
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

//        ++listActions;
//
//        if (listActions >= 8)
//        {
//            listActions = this.getRandom().nextInt(3);
//
//            fairyBehavior.handleRuler();
//        }

        //System.out.println(this + ": " + this.entityData.get(BED_LOCATION).toString());

        if(!posted() && isEmotional())
        {
            setEmotional(false);

            if(queen())
            {
                setSitting(false);
            }

            setWantedFoodItem(Items.AIR);
            setRequestFoodTime(foodTimerBaseValue + 500);
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

        setAngry(canGetAngryAt());

        if(this.getTarget() != null && !this.getTarget().isAlive())
        {
            this.setTarget(null);
        }

        setCanHeal(this.healTime <= 0);

        if(getHeldItem() != null && !posted())
        {
            spawnAtLocation(getHeldItem());
            setHeldItem(ItemStack.EMPTY);
        }

        //_dump_();
    }

    public float distanceToBlockPos(BlockPos blockPos)
    {
        float f = (float)(this.blockPosition().getX() - blockPos.getX());
        float g = (float)(this.blockPosition().getY() - blockPos.getY());
        float h = (float)(this.blockPosition().getZ() - blockPos.getZ());

        return Mth.sqrt(f * f + g * g + h * h);
    }

    private void setPosToBed(BlockPos blockPos)
    {
        this.setPos((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
    }

    public boolean canGetAngryAt()
    {
        if(this.getTarget() != null)
        {
            if(this.getTarget() instanceof FairyEntity)
            {
                FairyEntity fairy = (FairyEntity) this.getTarget();

                if(FairyUtils.sameTeam(this, fairy))
                {
                    return false;
                }
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }

        return false;
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
//        else if(getHeldItem() != null || !getHeldItem().isEmpty())
//        {
//            return getHeldItem();
//        }

//        if(getHeldItem() != null || !getHeldItem().isEmpty())
//        {
//            return getHeldItem();
//        }

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
        //return  ItemStack.EMPTY;

        return getHeldItem();
    }

    public void setHeldFairyItem(InteractionHand hand)
    {
        setItemInHand(hand, getItemInHand(hand));
    }

//    public void setHeldStack(ItemStack stack)
//    {
//        this.setItemSlot(EquipmentSlot.MAINHAND, stack == null ? ItemStack.EMPTY : stack);
//    }

//    public ItemStack getHeldStack()
//    {
//        return this.getMainHandItem();
//    }

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

            level.gameEvent(this, GameEvent.FISHING_ROD_CAST, this);

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

    public boolean mySecondSign(SignBlockEntity sign)
    {
        // Converts actual name
        final String actualName = getActualName(getNamePrefix(), getNameSuffix());

        if( signContains(sign, actualName))
        {
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

                if(text.getString().equalsIgnoreCase("Second Post"))
                {
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    public void abandonPost()
    {

        clearPostChestLocation();
        this.FROM_LIST = new ArrayList<>();
        this.TO_LIST = new ArrayList<>();
        this.entityData.set(TO_POS_SIZE, 0);
        this.entityData.set(FROM_POS_SIZE, 0);
        //this.bedPos = null;
        clearBedLocation();

        spawnAtLocation(getHeldItem());
        setDeliveryMode(false);
        setHeldItem(ItemStack.EMPTY);

        postX = postY = postZ = -1;

        if (posted())
        {
            String n = getActualName(getNamePrefix(), getNameSuffix());
            //CommonMethods.sendChat((ServerPlayer) ruler, Component.literal(n).append( Component.translatable(Loc.FAIRY_ABANDON_POST.get())));

//            if(ruler != null)
//                if (ruler.level().isClientSide)
//                    ((Player)ruler).displayClientMessage(Component.literal(n).append( Component.translatable(Loc.FAIRY_ABANDON_POST.get())), false);
//

            setPosted(false);
        }
    }

    public void setFromPos(BlockPos fromPos)
    {
        if (!this.FROM_LIST.contains(fromPos))
            this.FROM_LIST.add(fromPos.immutable());

        this.entityData.set(FROM_POS_SIZE, FROM_LIST.size());
    }

    public void setToPos(BlockPos toPos)
    {
        if (!this.TO_LIST.contains(toPos))
            this.TO_LIST.add(toPos.immutable());

        this.entityData.set(TO_POS_SIZE, TO_LIST.size());
    }

    //endregion

    //endregion

    // region ---------- name ----------

    @Nullable
    public String getActualName(int prefix, int suffix)
    {


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

    public MutableComponent getFactionNameComponent(int faction)
    {
        if (getFaction() < 0 || getFaction() > MAX_FACTION)
            return new TextComponent("Error-faction");

        return new TextComponent(FairyUtils.faction_colors[faction]).withStyle(FairyUtils.faction_colors_formatting[getFaction()]).append("<").append(new TranslatableComponent(FairyUtils.faction_names[faction]).append(">"));
    }

    @Override
    public Component getDisplayName()
    {
        if (getFaction() != 0)
        {

            if (queen())
            {
                String q = Loc.QUEEN.get();

                Component queenName = new TranslatableComponent(q).withStyle(FairyUtils.faction_colors_formatting[getFaction()]).append(getQueenName2(getNamePrefix(), getNameSuffix(), getFaction()));

                return queenName;
            }
            else
            {

                Component factionName = new TextComponent(getFactionName(getFaction()));

                //return factionName;
                return getFactionNameComponent(getFaction());
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
                name = new TextComponent(( posted() ? "§a" : "§c") + "@").append(getDeliveryMode() ? "§b§l§o * §f" : "").append(new TranslatableComponent(q)).append(woosh).append(( posted() ? "§a" : "§c") + "@");
            }
            else
            {
                //name = new TranslatableComponent(q).append(woosh);

                name = PlayerTeam.formatNameForTeam(this.getTeam(), new TranslatableComponent(q).append(woosh)).withStyle((p_185975_) ->
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

    @Override
    public void setTarget(@Nullable LivingEntity entity)
    {

        if (entity == null || this.getTarget() == null || entity != this.getTarget() || (entity instanceof FairyEntity && FairyUtils.sameTeam(this, (FairyEntity)entity)))
        {
            loseInterest = 0;
        }

        super.setTarget(entity);
    }

    public void clear(ItemStack stack, Player player)
    {
        FairyWandItem.FairyWandData data = new FairyWandItem.FairyWandData(stack);
        data.setStoredPos(null);
        data.setStoredEntityID(-1);
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
                if(stack.getItem() == ModItems.FAIRY_WAND.get())
                {
                    return ModItems.FAIRY_WAND.get().interactLivingWithEntity(stack, player, this, hand);
                }
                else if(stack.is(Items.FEATHER))
                {
                    stack.shrink(1);
                    toggleDeliveryMode(player);

                    return InteractionResult.SUCCESS;
                }
                else
                {
                    return handleFairyInteractions(player, stack);
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

    private InteractionResult handleFairyInteractions(Player player, ItemStack stack)
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

            return InteractionResult.PASS;
        }
        else if(stack != null && stack.is(getWantedFoodItem()) && stack.getCount() > 0)
        {
            stack.shrink(1);

            setHearts(!hearts());
            setEmotional(false);

            if(queen())
            {
                setSitting(false);
            }

            if(getHealth() < getMaxHealth())
            {
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
            }

            setWantedFoodItem(Items.AIR);
            setRequestFoodTime(foodTimerBaseValue + 500);

            return InteractionResult.SUCCESS;
        }
        else if (stack != null && FairyUtils.haircutItem(stack)
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
            if (isSitting() && !isEmotional())
            {
                if (FairyUtils.realFreshOysterBars(stack.getItem()) && stack.getCount() > 0)
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
            else if (player.isShiftKeyDown() && !isEmotional() && stack.isEmpty())
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

                    this.getNavigation().moveTo((Path) null, speedModifier);
                    setTarget(null);
                    entityFear = null;
                }

                return InteractionResult.SUCCESS;
            }
            else if (stack.isEmpty()
                    /*(stack == null || !FairyUtils.snowballItem(stack.getItem()))*/
                    && !player.isShiftKeyDown())
            {
                // otherwise, right-clicking wears a fairy hat

                if (isEmotional())
                {
                    return InteractionResult.PASS;
                }
                else
                {
                    CommonMethods.sendFairyMount(this, player);

                    setFlymode(true);
                    flyTime = 200;
                    setCanFlap(true);

                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    public void toggleDeliveryMode(Player player)
    {
        //name = new TextComponent(( posted() ? "§a" : "§c") + "@§f").append(new TranslatableComponent(q)).append(woosh).append(( posted() ? "§a" : "§c") + "@");
        String n = getActualName(getNamePrefix(), getNameSuffix());

        if(posted())
        {
            setDeliveryMode(!getDeliveryMode());

            if(getDeliveryMode())
            {

                if( getPostChestLocation().isPresent())
                {
                    clearPostChestLocation();

                    //CommonMethods.sendChat((ServerPlayer) ruler, new TextComponent("Delivery mode toggled on. ").append(new TextComponent(n).append(new TranslatableComponent(Loc.FAIRY_CLEARED.get()))));
                    //player.displayClientMessage( new TextComponent("Delivery mode toggled on. ").append(new TextComponent(n).append(new TranslatableComponent(Loc.FAIRY_CLEARED.get()))), false);
                }
                else
                {
                    //.sendChat((ServerPlayer) ruler, new TextComponent("Delivery mode toggled on"));
                    //player.displayClientMessage( new TextComponent("Delivery mode toggled on"), false);
                }

            }

            if(!getDeliveryMode())
            {
                if((TO_LIST.size() > 0 || FROM_LIST.size() > 0))
                {
                    this.FROM_LIST = new ArrayList<>();
                    this.TO_LIST = new ArrayList<>();
                    this.entityData.set(TO_POS_SIZE, 0);
                    this.entityData.set(FROM_POS_SIZE, 0);

                    spawnAtLocation(getHeldItem());
                    setHeldItem(ItemStack.EMPTY);

                    //CommonMethods.sendChat((ServerPlayer) ruler, new TextComponent("Delivery mode toggled off. ").append(new TextComponent(n).append(new TranslatableComponent(Loc.FAIRY_CLEARED.get()))));
                    //player.displayClientMessage(new TextComponent("Delivery mode toggled off. ").append(new TextComponent(n).append(new TranslatableComponent(Loc.FAIRY_CLEARED.get()))), false);
                }
                else
                {
                    //CommonMethods.sendChat((ServerPlayer) ruler, new TextComponent("Delivery mode toggled off"));
                    //player.displayClientMessage(new TextComponent("Delivery mode toggled off"), false);
                }
            }
        }
//        else
//        {
//            player.displayClientMessage(new TextComponent(n + " is not posted. Assign her a post before toggling delivery mode"), false);
//        }

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

            BlockPos pos = roamBlockPos(
                    ruler.blockPosition().getX(),
                    flymode() ? blockPosition().getY() : ruler.blockPosition().getY(),
                    ruler.blockPosition().getZ(), this, (float) Math.PI);

            if (pos != null)
                this.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);

            if (ruler instanceof Player player)
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

//                if (player instanceof ServerPlayer)
//                {
//                    CommonMethods.sendChat((ServerPlayer) player, Component.translatable(finalQueenString).append(name).append(Component.translatable(finalS)));
//                }

                if (player.level.isClientSide)
                    player.displayClientMessage(new TranslatableComponent(finalQueenString).append(name).append(new TranslatableComponent(finalS)), false);

            }
        }

        setFairyCustomName("");
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

            CommonMethods.sendChat((ServerPlayer) ruler, new TranslatableComponent(finalS).append(n).append(new TranslatableComponent(finalMessage)));
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

            CommonMethods.sendChat((ServerPlayer) ruler, new TranslatableComponent(finalS).append(n).append(new TranslatableComponent(finalS1)));
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
                && ruler instanceof ServerPlayer)
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
            if (entity instanceof ServerPlayer
                    && isRuler((ServerPlayer) entity))
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
                    && isRuler((ServerPlayer) ((Wolf) entity).getOwner()))
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
            CommonMethods.sendChat((ServerPlayer) player, new TranslatableComponent(s).append(new TranslatableComponent(finalS)));

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

    public int getFlyTime()
    {
        return this.flyTime;
    }

    public void setRequestFoodTime(int requestFoodTime)
    {
        this.requestFoodTime = requestFoodTime;
    }

    public int getRequestFoodTime()
    {
        return requestFoodTime;
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

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType mobSpawnType, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag)
    {
        if(Animal.checkAnimalSpawnRules((EntityType<? extends Animal>) this.getType(), pLevel, mobSpawnType, this.blockPosition(), this.random))
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

        return super.finalizeSpawn(pLevel, pDifficulty, mobSpawnType, pSpawnData, pDataTag);
    }

    //endregion
}
