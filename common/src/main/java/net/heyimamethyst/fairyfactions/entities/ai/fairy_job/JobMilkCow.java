package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class JobMilkCow extends FairyJob
{

    public JobMilkCow(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ItemStack stack, int x, int y, int z, Level world)
    {
        if(!canStart())
            return false;

        final ArrayList<Cow> cows = FairyJobManager.INSTANCE.getCows(world);

        if ( cows == null )
        {
            return false;
        }

        int count = 0;

        for ( int i = 0; i < cows.size() && count < 3 && stack.getCount() > 0; i++ )
        {
            final Cow cow = cows.get( i );

            int isBreedingCounter = cow.getInLoveTime();

            // skip unmilkable animals
            if (cow.getAge() != 0 // is juvenile (negative) or recently proceated (positive)
                    || isBreedingCounter != 0 // literally breeding now.
            )
            {
                continue;
            }

            if ( fairy.distanceTo( cow ) < 3F )
            {
                //FairyFactions.LOGGER.debug(this.fairy.toString()+": milking cows");

                count++;

            }
        }

        ItemStack milkStack = new ItemStack(Items.MILK_BUCKET);

        final int emptySpace = FairyJobManager.INSTANCE.getEmptySpace(chest, milkStack);

        if ( count > 0 && emptySpace >= 0)
        {
            fairy.armSwing( !fairy.didSwing );
            fairy.setTempItem(stack.getItem());

            fairy.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            stack.shrink(1);
            chest.setItem(emptySpace, milkStack);

            fairy.attackAnim = 30;

            if ( fairy.flymode() && fairy.getFlyTime() > 0 )
            {
                fairy.setFlyTime( 0 );
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean canStart()
    {
        return super.canStart() && FairyUtils.isBucket(itemStack);
    }
}
