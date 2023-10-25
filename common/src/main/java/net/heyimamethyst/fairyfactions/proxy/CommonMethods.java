package net.heyimamethyst.fairyfactions.proxy;


import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.BaseS2CMessage;
import io.netty.buffer.Unpooled;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.network.ModNetwork;
import net.heyimamethyst.fairyfactions.network.PacketSetFairyName;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
//import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class CommonMethods
{

    public static void sendChat(ServerPlayer player, Component component )
    {
        if ( player != null && component != null && !component.getString().isEmpty() )
            player.displayClientMessage(component, false);
    }

    public static void sendToServer(BaseC2SMessage packet)
    {
        packet.sendToServer();
    }

    public static void sendToPlayer(BaseS2CMessage packet, ServerPlayer serverPlayer)
    {
        packet.sendTo(serverPlayer);
    }

    public static void sendToAllPlayers(BaseS2CMessage packet)
    {
        List<ServerPlayer> players = Minecraft.getInstance().player.getServer().getPlayerList().getPlayers();

        for( ServerPlayer player : players )
        {
            packet.sendTo(player);
        }
    }

    public static void sendFairyRename(final FairyEntity fairy, final String name)
    {
        final PacketSetFairyName packet = new PacketSetFairyName(fairy, name );
        sendToServer(packet);
    }

    // Packet that handles fairy mounting.
    public static void sendFairyMount(final Entity rider, final Entity vehicle)
    {
//        final Entity newVehicle;
//
//        if (rider.getVehicle() != null && rider.getVehicle() == vehicle)
//        {
//            newVehicle = null;
//        }
//        else
//        {
//            newVehicle = vehicle;
//        }

        //final S1BPacketEntityAttach packet = new S1BPacketEntityAttach(0, rider, newVehicle);
        //sendToAllPlayers(packet);

//        if (!(rider instanceof FishingHook))
//        {
//            if(newVehicle != null)
//            {
//                rider.startRiding(newVehicle);
//            }
//            else
//            {
//                //rider.stopRiding();
//
//                if(rider.getVehicle() != null)
//                    rider.stopRiding();
//            }
//        }

        if (!(rider instanceof FishingHook))
        {

            if(rider.getVehicle() == null)
            {
                rider.startRiding(vehicle);
            }
            else
            {
                //rider.stopRiding();

                if(rider.getVehicle() != null)
                    rider.stopRiding();
            }
        }
    }

}
