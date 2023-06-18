package net.heyimamethyst.fairyfactions.entities.ai;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class JobBreeding extends FairyJob
{

    public JobBreeding(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ItemStack stack, int x, int y, int z, Level world)
    {
        if(!canStart())
            return false;

        final ArrayList<Animal> animals = FairyJobManager.INSTANCE.getAnimals(world);

        if ( animals == null )
        {
            return false;
        }

        int count = 0;


        for ( int i = 0; i < animals.size() && count < 3 && stack.getCount() > 0; i++ )
        {
            final Animal entity = (Animal) animals.get( i );

            int isBreedingCounter = entity.getInLoveTime();

            // skip unbreedable animals
            if (!entity.isFood(stack) // can't breed with this item
                    || entity.getAge() != 0 // is juvenile (negative) or recently proceated (positive)
                    || isBreedingCounter != 0 // literally breeding now.
            )
            {
                continue;
            }

            FairyJobManager.INSTANCE.triedBreeding = true;

            if ( fairy.distanceTo( entity ) < 3F )
            {
                //FairyFactions.LOGGER.debug(this.fairy.toString()+": breeding animals");

                entity.setInLoveTime(600);
                count++;
                stack.setCount(stack.getCount() - 1);
            }
        }

        if ( count > 0 )
        {
            fairy.armSwing( !fairy.didSwing );
            fairy.setTempItem(stack.getItem());

            fairy.attackAnim = 1;
            fairy.setHearts( !fairy.didHearts );

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
        return !FairyJobManager.INSTANCE.triedBreeding;
    }
}
