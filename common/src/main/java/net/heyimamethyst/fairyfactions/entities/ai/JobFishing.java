package net.heyimamethyst.fairyfactions.entities.ai;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class JobFishing extends FairyJob
{
    private static final float pia = -(float) Math.PI / 180F;

    public JobFishing(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ItemStack stack, int x, int y, int z, Level world)
    {
        if(!canStart())
            return false;

        int fishingSpeedBonus = EnchantmentHelper.getFishingSpeedBonus(stack);
        int fishingLuckBonus = EnchantmentHelper.getFishingLuckBonus(stack);

        fairy.fishingSpeedBonus = fishingSpeedBonus;
        fairy.fishingLuckBonus = fishingLuckBonus;

        if ( fairy.isInWater() /*&& !fairy.getNavigation().isDone() */)
        {
            getToLand( x, y, z, world );
            return false;
        }
        else if ( fairy.flymode() && fairy.getFlyTime() > 0 /*&& !fairy.getNavigation().isDone() */)
        {
            fairy.setFlyTime( 0 );
            return false;
        }
        else if ( !fairy.isOnGround() || fairy.isInWater() )
        {
            return false;
        }

        final float angle = fairy.yBodyRot - 30F + (fairy.getRandom().nextFloat() * 60F);
        final double posX = fairy.position().x + Math.sin( angle * pia ) * 6D;
        final double posY = fairy.position().y;
        final double posZ = fairy.position().z + Math.cos( angle * pia ) * 6D;
        final int a = (int)Math.floor( posX );
        final int b = y;
        final int c = (int)Math.floor( posZ );
        final BlockPos pos = new BlockPos(a,b,c);

        for ( int j = -4; j < 0; j++ )
        {
            if ( b + j > 0 && b + j < world.getHeight() - 10 )
            {
                boolean flag = false;

                for ( int i = -1; i <= 1 && !flag; i++ )
                {
                    for ( int k = -1; k <= 1 && !flag; k++ )
                    {
                        final BlockPos pos2 = pos.offset(i, j, k);
                        if ( world.getBlockState( pos2 ).getBlock() != Blocks.WATER
                                || world.getBlockState( pos2.above() ) != Blocks.AIR.defaultBlockState()
                                || world.getBlockState( pos2.above(2) ) != Blocks.AIR.defaultBlockState()
                                || world.getBlockState( pos2.above(3) ) != Blocks.AIR.defaultBlockState() )
                        {
                            flag = true;
                        }
                    }
                }

                if ( !flag )
                {
                    final Path path = fairy.getNavigation().createPath(new BlockPos(a, b + j, c), 1);

                    if (path != null && canSeeToSpot( posX, posY, posZ, world ))
                    {
                        //fairy.yHeadRot = angle;
                        fairy.getLookControl().setLookAt(posX,posY,posZ, angle, fairy.getMaxHeadXRot());
                        fairy.castRod();
                        fairy.playSound(SoundEvents.FISHING_BOBBER_THROW, 1, 1);

                        // TODO: player fishing normally damages the rod when the reels something in; should we do that?
                        //Currently can't figure out how to achieve that. Need to pass the itemstack to the fairy tasks class for the other
                        //"castRod" method call for reeling the fish in

                        stack.hurtAndBreak(1, fairy, (p_29822_) ->
                        {
                            p_29822_.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                        });

//                        CompoundTag tag = stack.getTag();
//
//                        if(tag != null && tag.contains("Enchantments"))
//                        {
//                            ListTag stackEnchantmentTags = stack.getEnchantmentTags();
//                            //ListTag chestStackEnchantmentTags = chestStack.getEnchantmentTags();
//                            //chestStackEnchantmentTags.clear();
//
//                            if(fairy.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.STICK)
//                            {
////                                ListTag listTag = fairy.getItemInHand(InteractionHand.MAIN_HAND).getEnchantmentTags();
////                                listTag.clear();
////                                //listTag.add(stackEnchantmentTags);
////
////                                for (Tag t :stackEnchantmentTags)
////                                {
////                                    listTag.add(t);
////                                }
//
//                                CompoundTag stickTag = tag;
//                                stickTag.remove("Damage");
//
//                                fairy.getItemInHand(InteractionHand.MAIN_HAND).setTag(stickTag);
//                            }
//                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void getToLand( final int x, final int y, final int z, final Level world )
    {
        for ( int q = 0; q < 16; q++ )
        {
            final int i = x - 5 + fairy.getRandom().nextInt( 11 );
            final int j = y + 1 + fairy.getRandom().nextInt( 5 );
            final int k = z - 5 + fairy.getRandom().nextInt( 11 );

            if ( y > 1 && y < world.getHeight() - 1 )
            {
                if ( FairyUtils.isAirySpace( fairy, i, j, k ) && !FairyUtils.isAirySpace(fairy, i, j - 1, k )
                        && world.getBlockState( new BlockPos(i, j - 1, k) ).isSolidRender(fairy.level, new BlockPos(i, j - 1, k)))
                {
                    final Path path = fairy.getNavigation().createPath(new BlockPos(i, j, k), 1);

                    if ( path != null )
                    {
                        fairy.getNavigation().moveTo(path, fairy.speedModifier);

                        if ( !fairy.flymode() )
                        {
                            fairy.setFlyTime( 0 );
                        }

                        return;
                    }
                }
            }
        }
    }

    private boolean canSeeToSpot( final double posX, final double posY, final double posZ, final Level world )
    {

//        return world.rayTraceBlocks(
//                new Vec3( fairy.position().x, fairy.position().y + fairy.getEyeHeight(), fairy.position().z ),
//                new Vec3( posX, posY, posZ ) ) == null;

        //CREATE THE RAY
        ClipContext rayCtx = new ClipContext(
                new Vec3( fairy.position().x, fairy.position().y + fairy.getEyeHeight(), fairy.position().z ),
                new Vec3( posX, posY, posZ ),
                ClipContext.Block.VISUAL, ClipContext.Fluid.WATER, fairy);

        BlockHitResult rayBlockHit = world.clip(rayCtx);
        BlockPos pos = rayBlockHit.getBlockPos();

//        //CAST THE RAY
//        BlockHitResult rayBlockHit = world.clip(rayCtx);

        return pos != null;
    }

    @Override
    public boolean canStart()
    {
        return FairyUtils.isFishingItem(stack);
    }
}
