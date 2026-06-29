package me.wietse3.wca.content.brass_link;

import net.minecraft.core.BlockPos;

public interface IBrassLinkable {

    public int getTransmittedStrength();

    public void setReceivedStrength(int power);

    public boolean isListening();

    public boolean isAlive();

    public String getNetworkKey();

    public BlockPos getLocation();

}
