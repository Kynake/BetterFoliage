package mods.betterfoliage.client.render;

import net.minecraftforge.common.util.ForgeDirection;

public enum UVPlane {

    DOWN(ForgeDirection.EAST, ForgeDirection.NORTH),
    UP(ForgeDirection.EAST, ForgeDirection.SOUTH),
    NORTH(ForgeDirection.WEST, ForgeDirection.DOWN),
    SOUTH(ForgeDirection.EAST, ForgeDirection.DOWN),
    WEST(ForgeDirection.SOUTH, ForgeDirection.DOWN),
    EAST(ForgeDirection.NORTH, ForgeDirection.DOWN),

    UNKNOWN(ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN);

    public final ForgeDirection uAxis;
    public final ForgeDirection vAxis;

    UVPlane(ForgeDirection uAxis, ForgeDirection vAxis) {
        this.uAxis = uAxis;
        this.vAxis = vAxis;
    }

    public static UVPlane toUVPlane(ForgeDirection dir) {
        return values()[dir.ordinal()];
    }

    public int getOffsetU(ForgeDirection dir) {
        return dir.offsetX * uAxis.offsetX + dir.offsetY * uAxis.offsetY + dir.offsetZ * uAxis.offsetZ;
    }
    public int getOffsetV(ForgeDirection dir) {
        return dir.offsetX * vAxis.offsetX + dir.offsetY * vAxis.offsetY + dir.offsetZ * vAxis.offsetZ;
    }
}
