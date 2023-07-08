package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class JobButcher extends FairyJob
{
    public JobButcher(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ItemStack stack, int x, int y, int z, Level world)
    {
        if(!canStart())
            return false;

        //return slaughterSheep(world, stack) || slaughterCow(world, stack) || slaughterChicken(world, stack) || slaughterPig(world, stack);
        return slaughterAnimal(world, stack);
    }

    private  boolean slaughterAnimal(Level world, ItemStack stack)
    {

        final ArrayList<Animal> animals = FairyJobManager.INSTANCE.getAnimals(world);

        if ( animals == null )
        {
            return false;
        }

        if(animals.size() > 0)
        {
            for (Animal animal: animals)
            {
                if(!(animal instanceof FairyEntity))
                {
                    final List<Entity> animalsOfType = new ArrayList<>();

                    for ( int j = 0; j < animals.size(); j++ )
                    {
                        if(animals.get( j ).getType() == animal.getType())
                        {
                            animalsOfType.add((Animal) animals.get( j ));
                        }
                    }

                    if (animalsOfType.size() > 2)
                    {
                        Animal animalToBeKilled = (Animal) animalsOfType.get( 0 );

                        if(animalToBeKilled.isAlive())
                        {
                            fairy.getNavigation().moveTo(animalToBeKilled, 0.3D);
                            attackAnimal(animalToBeKilled, stack);

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean slaughterSheep(Level world, ItemStack stack)
    {
        final ArrayList<Sheep> sheep = FairyJobManager.INSTANCE.getSheep(world);

        if ( sheep == null )
        {
            return false;
        }

        if(sheep.size() > 2)
        {
            Sheep animalToBeKilled = sheep.get( 0 );

            if(animalToBeKilled.isAlive())
            {
                fairy.getNavigation().moveTo(animalToBeKilled, 0.3D);
                attackAnimal(animalToBeKilled, stack);
                return true;
            }
        }

        return false;
    }

    private boolean slaughterCow(Level world, ItemStack stack)
    {
        final ArrayList<Cow> cows = FairyJobManager.INSTANCE.getCows(world);

        if ( cows == null )
        {
            return false;
        }

        if(cows.size() > 2)
        {
            Cow animalToBeKilled = cows.get( 0 );

            if(animalToBeKilled.isAlive())
            {
                fairy.getNavigation().moveTo(animalToBeKilled, 0.3D);
                attackAnimal(animalToBeKilled, stack);
                return true;
            }
        }

        return false;
    }

    private boolean slaughterChicken(Level world, ItemStack stack)
    {
        final ArrayList<Chicken> chickens = FairyJobManager.INSTANCE.getChickens(world);

        if ( chickens == null )
        {
            return false;
        }

        if(chickens.size() > 2)
        {
            Chicken animalToBeKilled = chickens.get( 0 );

            if(animalToBeKilled.isAlive())
            {
                fairy.getNavigation().moveTo(animalToBeKilled, 0.3D);
                attackAnimal(animalToBeKilled, stack);
                return true;
            }
        }

        return false;
    }

    private boolean slaughterPig(Level world, ItemStack stack)
    {
        final ArrayList<Pig> pigs = FairyJobManager.INSTANCE.getPigs(world);

        if ( pigs == null )
        {
            return false;
        }

        if(pigs.size() > 2)
        {
            Pig animalToBeKilled = pigs.get( 0 );

            if(animalToBeKilled.isAlive())
            {
                fairy.getNavigation().moveTo(animalToBeKilled, 0.3D);
                attackAnimal(animalToBeKilled, stack);
                return true;
            }

        }

        return false;
    }

    private void attackAnimal(LivingEntity animalToBeKilled, ItemStack stack)
    {
        double pDistToEnemySqr = this.fairy.distanceToSqr(animalToBeKilled.getX(), animalToBeKilled.getY(), animalToBeKilled.getZ());
        double d0 = this.getAttackReachSqr(animalToBeKilled);

        if (fairy.attackAnim <= 0 && pDistToEnemySqr < d0
                && ( ( animalToBeKilled.getBoundingBox().maxY > fairy.getBoundingBox().minY
                && animalToBeKilled.getBoundingBox().minY < fairy.getBoundingBox().maxY )
                || pDistToEnemySqr == 0F ))
        {
            fairy.attackAnim = 20;

            //fairy.doAttack(animalToBeKilled);
            fairy.armSwing( !fairy.didSwing );
            fairy.setTempItem(stack.getItem());
            animalToBeKilled.kill();
        }
    }

    private double getAttackReachSqr(LivingEntity pAttackTarget)
    {
        return (double)(( fairy.tamed() ? 4.5F : 4.0F ) + pAttackTarget.getBbWidth());
    }

    @Override
    public boolean canStart()
    {
        return FairyUtils.isSwordItem(itemStack);
    }
}
