package net.heyimamethyst.fairyfactions.entities.ai.fairy_misc_action;

import com.google.common.collect.ImmutableSet;
import net.heyimamethyst.fairyfactions.FairyConfigValues;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.entities.ai.fairy_job.FairyJobManager;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;

import java.util.List;

public class MiscActionBecomeEmotional extends FairyMiscAction
{

    public MiscActionBecomeEmotional(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ChestBlockEntity chestBlockEntity, int x, int y, int z, Level world)
    {
        if(FairyUtils.percentChance(fairy, fairy.queen() ? FairyConfigValues.BASE_EMOTIONAL_PERCENT_CHANCE * 4 : FairyConfigValues.BASE_EMOTIONAL_PERCENT_CHANCE) && fairy.getRequestFoodTime() == 0 && !fairy.isEmotional())
        {
            if(world.getBlockState(fairy.blockPosition().below()).isSolidRender(world, fairy.blockPosition()))
            {
                List<Item> foodItems = FairyUtils.getItemsFromFairyFoodTag();
                fairy.setItemIndex(fairy.getRandom().nextInt(foodItems.size()));

                Item itemFromList = foodItems.get(fairy.getItemIndex());
                fairy.setWantedFoodItem(itemFromList);

                if(doesChestHaveWantedItem(chestBlockEntity, itemFromList))
                {
                    return false;
                }

                if(!doesChestHaveWantedItem(chestBlockEntity, itemFromList) && !fairy.flymode())
                {
                    fairy.setEmotional(true);

                    //fairy.setFlymode(false);
                    //fairy.setFlyTime(0);
                    //setCanFlap(false);

                    if(fairy.queen())
                    {
                        fairy.setSitting(true);
                        //fairy.getNavigation().moveTo((Path) null, 0.0D);
                        fairy.getNavigation().stop();
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private boolean doesChestHaveWantedItem(ChestBlockEntity chest, Item itemFromList)
    {
        return chest.hasAnyOf(ImmutableSet.of(itemFromList));
    }
}
