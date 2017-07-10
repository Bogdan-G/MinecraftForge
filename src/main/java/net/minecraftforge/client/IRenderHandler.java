package net.minecraftforge.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public abstract class IRenderHandler implements java.io.Serializable
{
    @SideOnly(Side.CLIENT)
    public abstract void render(float partialTicks, WorldClient world, Minecraft mc);
}
