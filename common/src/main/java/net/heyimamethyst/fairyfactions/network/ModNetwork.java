package net.heyimamethyst.fairyfactions.network;

import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.minecraft.resources.ResourceLocation;

public class ModNetwork
{
    public static NetworkChannel CHANNEL;

    public static SimpleNetworkManager simpleNetworkManager = SimpleNetworkManager.create(FairyFactions.MOD_ID);
    public static final MessageType C2S_FAIRY_RENAME = simpleNetworkManager.registerC2S("fairy_name_packet", PacketSetFairyName::new);
}
