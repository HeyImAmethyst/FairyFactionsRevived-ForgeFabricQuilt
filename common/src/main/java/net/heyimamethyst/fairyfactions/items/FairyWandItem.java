package net.heyimamethyst.fairyfactions.items;

import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.Loc;
import net.heyimamethyst.fairyfactions.entities.FairyBedEntity;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.items.nbt.ItemstackData;
import net.heyimamethyst.fairyfactions.proxy.CommonMethods;
import net.heyimamethyst.fairyfactions.util.EntityHelper;
import net.heyimamethyst.fairyfactions.util.NBTUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

//Code for wand functionality came from : https://github.com/baileyholl/Ars-Nouveau/blob/main/src/main/java/com/hollingsworth/arsnouveau/common/items/DominionWand.java

public class FairyWandItem extends Item
{
    public FairyWandItem(Properties properties)
    {
        super(properties);
    }

    //@Override
    public InteractionResult interactLivingWithEntity(ItemStack doNotUseStack, Player playerEntity, LivingEntity target, InteractionHand hand)
    {

        //if (playerEntity.level.isClientSide || hand != InteractionHand.MAIN_HAND)
            //return InteractionResult.FAIL;

        ItemStack stack = playerEntity.getItemInHand(hand);
        FairyWandData data = new FairyWandData(stack);

        if (playerEntity.isShiftKeyDown() && target instanceof IWandable wandable)
        {
            wandable.onWanded(playerEntity);

            clear(stack, playerEntity);

            return InteractionResult.SUCCESS;
        }

        // If the wand has nothing, store it
        if (!data.hasStoredData())
        {
            data.setStoredEntityID(target.getId());

            if (playerEntity.level().isClientSide || hand != InteractionHand.MAIN_HAND)
                playerEntity.displayClientMessage(Component.translatable(Loc.FAIRY_WAND_STORED_ENTITY.get()), false);

            //CommonMethods.sendChat((ServerPlayer) playerEntity, new TranslatableComponent(Loc.FAIRY_WAND_STORED_ENTITY.get()));

            //playerEntity.displayClientMessage(new TranslatableComponent(Loc.FAIRY_WAND_STORED_ENTITY.get()), false);
            return InteractionResult.SUCCESS;
        }

        Level world = playerEntity.getCommandSenderWorld();

        if (data.getStoredPos() != null && world.getBlockEntity(data.getStoredPos()) instanceof IWandable wandable)
        {
            wandable.onFinishedConnectionFirst(data.getStoredPos(), target, playerEntity);
        }

        if (target instanceof IWandable wandable)
        {
            wandable.onFinishedConnectionLast(data.getStoredPos(), target, playerEntity);
            clear(stack, playerEntity);
        }

        return InteractionResult.SUCCESS;
    }

    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player)
    {
        return false;
    }

    public void clear(ItemStack stack, Player player)
    {
        FairyWandData data = new FairyWandData(stack);
        data.setStoredPos(null);
        data.setStoredEntityID(-1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand interactionHand)
    {
        ItemStack stack = playerEntity.getItemInHand(interactionHand);
        FairyWandData data = new FairyWandData(stack);

        //Code from https://github.com/V0idWa1k3r/ExPetrum/blob/e6a31dff9bd1b55cf65119bba6537c74c5da55fd/src/main/java/v0id/exp/combat/impl/ShieldSlam.java#L45

        Vec3 look = playerEntity.getViewVector(0).scale(100);
        Vec3 pos = playerEntity.getPosition(0);

        List<Entity> targets = EntityHelper.rayTraceEntities(playerEntity.level(), pos.add(0, playerEntity.getEyeHeight(), 0), look, Optional.of(e -> e != playerEntity), Entity.class);

        Entity assumedToBeLookedAt = EntityHelper.getClosest(targets, playerEntity);

        if (assumedToBeLookedAt != null)
        {
            //FairyFactions.LOGGER.debug("found closet entity " + assumedToBeLookedAt);

//            if(assumedToBeLookedAt instanceof FairyBedEntity)
//            {
//                FairyBedEntity fairyBed = (FairyBedEntity) assumedToBeLookedAt;
//
////                if(data.getStoredEntityID() != -1)
////                    fairyBed.onFinishedConnectionLast(data.getStoredPos(), (LivingEntity) world.getEntity(data.getStoredEntityID()), playerEntity);
//
//
//            }

            if (data.getStoredEntityID() != -1 && !playerEntity.isShiftKeyDown())
            {
                IWandable wandable = (IWandable) world.getEntity(data.getStoredEntityID());

                if(wandable != null)
                {
                    wandable.onFinishedConnectionFirst(assumedToBeLookedAt, (LivingEntity) world.getEntity(data.getStoredEntityID()), playerEntity);
                    clear(stack, playerEntity);
                }
            }
        }


        return super.use(world, playerEntity, interactionHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        if (context.getLevel().isClientSide || context.getPlayer() == null)
            return super.useOn(context);

        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        Player playerEntity = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        FairyWandData data = new FairyWandData(stack);

        if (playerEntity.isShiftKeyDown() && world.getBlockEntity(pos) instanceof IWandable wandable && !data.hasStoredData())
        {
            wandable.onWanded(playerEntity);

            clear(stack, playerEntity);
            return InteractionResult.CONSUME;
        }

        if (!data.hasStoredData())
        {
            data.setStoredPos(pos.immutable());
            CommonMethods.sendChat((ServerPlayer) playerEntity, Component.translatable(Loc.FAIRY_WAND_POSITION_SET.get()));
            return InteractionResult.SUCCESS;
        }

        if (data.getStoredPos() != null && world.getBlockEntity(data.getStoredPos()) instanceof IWandable wandable)
        {
            wandable.onFinishedConnectionFirst(pos, (LivingEntity) world.getEntity(data.getStoredEntityID()), playerEntity);
        }

        if (world.getBlockEntity(pos) instanceof IWandable wandable)
        {
            wandable.onFinishedConnectionLast(data.getStoredPos(), (LivingEntity) world.getEntity(data.getStoredEntityID()), playerEntity);
        }

        if (data.getStoredEntityID() != -1 && world.getEntity(data.getStoredEntityID()) instanceof IWandable wandable)
        {
            wandable.onFinishedConnectionFirst(pos, null, playerEntity);
        }

        clear(stack, playerEntity);
        return super.useOn(context);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag p_77624_4_)
    {
        FairyWandData data = new FairyWandData(stack);

        if(data.getStoredEntityID() == -1)
        {
            tooltip.add(Component.translatable(Loc.FAIRY_WAND_NO_ENTITY.get()));
        }
        else
        {
            tooltip.add(Component.translatable(Loc.FAIRY_WAND_ENTITY_STORED.get()));
        }

        if(data.getStoredPos() == null)
        {
            tooltip.add(Component.translatable(Loc.FAIRY_WAND_NO_LOCATION.get()));
        }
        else
        {
            tooltip.add(Component.translatable(Loc.FAIRY_WAND_POSITION_STORED.get(), getPosString(data.getStoredPos())));
        }
    }

    public static String getPosString(BlockPos pos)
    {
        return Component.translatable(Loc.POSITION.get(), pos.getX(), pos.getY(), pos.getZ()).getString();
    }

    public static class FairyWandData extends ItemstackData
    {
        private BlockPos storedPos;
        private int storedEntityID;

        public FairyWandData(ItemStack stack)
        {
            super(stack);
            CompoundTag tag = getItemTag(stack);
            if (tag == null) {
                return;
            }
            storedPos = NBTUtil.getNullablePos(tag, "stored");
            storedEntityID = tag.getInt("entityID");
        }

        public boolean hasStoredData() {
            return getStoredPos() != null || getStoredEntityID() != -1;
        }

        public @Nullable BlockPos getStoredPos() {
            return storedPos == BlockPos.ZERO || storedPos == null ? null : storedPos.immutable();
        }

        public int getStoredEntityID()
        {
            return storedEntityID == 0 ? -1 : storedEntityID;
        }

        public void setStoredPos(@Nullable BlockPos pos)
        {
            storedPos = pos;
            writeItem();
        }

        public void setStoredEntityID(int id)
        {
            storedEntityID = id;
            writeItem();
        }

        @Override
        public String getTagString()
        {
            return "an_dominion_wand";
        }

        @Override
        public void writeToNBT(CompoundTag tag)
        {
            if(storedPos != null)
            {
                NBTUtil.storeBlockPos(tag, "stored", storedPos);
            }
            tag.putInt("entityID", storedEntityID);
        }
    }
}
