package net.heyimamethyst.fairyfactions.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import io.netty.buffer.Unpooled;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketSetFairyName extends BaseC2SMessage
{

    public static final int MIN_NAME_LENGTH = 3;
    public static final int MAX_NAME_LENGTH = 16;

    private int fairyID;
    private UUID fairyUUID;

    private String name;

    public PacketSetFairyName(FairyEntity fairy, final String name)
    {

        this.fairyID = fairy.getId();
        this.fairyUUID = fairy.getUUID();
        this.name = name;
    }

    public PacketSetFairyName(FriendlyByteBuf buf)
    {
        fairyID = buf.readInt();

        if (buf.readBoolean())
            fairyUUID = new UUID(buf.readLong(), buf.readLong());
        else
            fairyUUID = null;

        try
        {
            name = buf.readUtf(MAX_NAME_LENGTH).trim();

            if( name.length() < MIN_NAME_LENGTH )
            {
                name = "";
            }
        }
        catch (Exception e)
        {
            name = "";
        }
    }

    @Override
    public MessageType getType()
    {
        return ModNetwork.C2S_FAIRY_RENAME;
    }

    @Override
    public void write(FriendlyByteBuf buf)
    {
        FairyFactions.LOGGER.info("PacketSetFairyName.encoding");
        //final FriendlyByteBuf buf = this.getData();

        buf.writeInt(this.fairyID);

        if (fairyUUID != null)
            buf.writeBoolean(true).writeLong(fairyUUID.getMostSignificantBits()).writeLong(fairyUUID.getLeastSignificantBits());
        else
            buf.writeBoolean(false);

        try
        {
            buf.writeUtf(this.name);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(NetworkManager.PacketContext supplier)
    {

        supplier.queue(() ->
        {
            // HERE WE ARE ON THE SERVER!

            FairyFactions.LOGGER.info("PacketSetFairyName.handle");

            final Player player = supplier.getPlayer();

            if( player != null )
            {
                if(player instanceof ServerPlayer)
                {
                    ServerPlayer serverPlayer = (ServerPlayer) player;
                    final ServerLevel level = (ServerLevel) player.level();

                    final FairyEntity fairy = FairyFactions.getFairyFromID(serverPlayer, this.fairyID);
                    //final FairyEntity fairy = FairyFactions.getFairyFromUUID(player, this.fairyUUID);

                    if( fairy == null )
                    {
                        FairyFactions.LOGGER.warn("Unable to find fairy "+this.fairyUUID+" to rename.");
                        return;
                    }

                    final String username = player.getGameProfile().getName();
                    final String rulername = fairy.rulerName();

                    if(fairy.nameEnabled() && rulername.equals(username))
                    {
                        FairyUtils.nameFairyEntity(fairy, this.name);
                    }
                    else
                    {
                        FairyFactions.LOGGER.warn("Attempt by '"+username+"' to rename fairy owned by '"+rulername+"'");
                    }

                    fairy.setNameEnabled(false);
                }
            }
        });
    }
}
