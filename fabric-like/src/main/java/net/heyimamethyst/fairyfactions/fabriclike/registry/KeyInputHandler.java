package net.heyimamethyst.fairyfactions.fabriclike.registry;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.proxy.CommonMethods;
import net.heyimamethyst.fairyfactions.util.EntityHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;

public class KeyInputHandler
{

    public static void registerKeyInputs()
    {
        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {

            Minecraft mc = Minecraft.getInstance();

            if(mc.level == null)
                return;

            MouseHandler handler = client.mouseHandler;

            int mouseKey = 0;

            if(handler.isRightPressed())
            {
                mouseKey = 1;
            }

            //onInput(mc, mouseKey, mouseKey);
        });
    }

    public static void register()
    {
        registerKeyInputs();
    }

    private static void onInput(Minecraft mc, int key, int action)
    {
        Player player = mc.player;

        //Code from https://github.com/V0idWa1k3r/ExPetrum/blob/e6a31dff9bd1b55cf65119bba6537c74c5da55fd/src/main/java/v0id/exp/combat/impl/ShieldSlam.java#L45

        Vec3 look = player.getViewVector(0).scale(100);
        Vec3 pos = player.getPosition(0);

        List<LivingEntity> targets = EntityHelper.rayTraceEntities(player.level(), pos.add(0, player.getEyeHeight(), 0), look, Optional.of(e -> e != player), LivingEntity.class);

        LivingEntity assumedToBeLookedAt = EntityHelper.getClosest(targets, player);

        if (assumedToBeLookedAt != null)
        {
            //FairyFactions.LOGGER.debug("found closet entity " + assumedToBeLookedAt);

            if(assumedToBeLookedAt instanceof FairyEntity)
            {
                FairyEntity fairy = (FairyEntity) assumedToBeLookedAt;

                if(fairy.isPassenger() && player.getItemInHand(InteractionHand.MAIN_HAND) == ItemStack.EMPTY /*&& fairy.getMountTime() == 0*/)
                {
                    if(key == 1 && action == 1 && !player.isShiftKeyDown())
                    {
                        FairyFactions.LOGGER.warn("Unmounting fairy "+fairy);
                        CommonMethods.sendFairyMount(fairy, player);

                        fairy.setFlymode(true);
                        fairy.setFlyTime(200);
                        fairy.setCanFlap(true);
                        //fairy.setMountTime(100);
                    }
                }
            }
        }
    }
}
