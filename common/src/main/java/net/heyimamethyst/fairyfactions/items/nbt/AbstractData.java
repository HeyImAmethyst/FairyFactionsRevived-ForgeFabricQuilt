package net.heyimamethyst.fairyfactions.items.nbt;

import net.minecraft.nbt.CompoundTag;

//Class from : https://github.com/baileyholl/Ars-Nouveau/blob/main/src/main/java/com/hollingsworth/arsnouveau/api/nbt/AbstractData.java
public abstract class AbstractData
{
    public AbstractData(CompoundTag tag)
    {

    }

    public abstract void writeToNBT(CompoundTag tag);
}
