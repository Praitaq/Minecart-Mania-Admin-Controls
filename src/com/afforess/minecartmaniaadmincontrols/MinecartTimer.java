package com.afforess.minecartmaniaadmincontrols;
import org.bukkit.Chunk;

import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.event.MinecartManiaListener;
import com.afforess.minecartmaniacore.event.MinecartTimeEvent;

public class MinecartTimer extends MinecartManiaListener{
	public void onMinecartTimeEvent(MinecartTimeEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		
		int timer = -1;
		if (minecart.isStandardMinecart()) {
			timer = VehicleControl.getMinecartKillTimer();
		}
		else if (minecart.isStorageMinecart()) {
			timer = VehicleControl.getStorageMinecartKillTimer();
		}
		else {
			timer = VehicleControl.getPoweredMinecartKillTimer();
		}
		boolean kill = minecart.minecart.getPassenger() == null && (!minecart.isStorageMinecart() || ((MinecartManiaStorageCart)minecart).isEmpty());
		
		if (timer > 0) {
			if (kill) {
				//No timer, start counting
				if (minecart.getDataValue("Empty Timer") == null) {
					minecart.setDataValue("Empty Timer", new Integer(timer));
				}
				else {
					//Decrement timer
					Integer timeLeft = (Integer)minecart.getDataValue("Empty Timer");
					if (timeLeft > 1) {
						minecart.setDataValue("Empty Timer", new Integer(timeLeft.intValue()-1));
					}
					else {
						minecart.kill();
						unload(minecart);
					}
				}
			}
			//has passenger, resent counter if already set
			else {
				if (minecart.getDataValue("Empty Timer") != null) {
					minecart.setDataValue("Empty Timer", null);
				}
			}
		}
		else if (timer == 0) {
			if (kill) {
				minecart.kill();
				unload(minecart);
			}
		}
	}
	
	private static void unload(MinecartManiaMinecart minecart)  {
		int range = 3;
		Chunk current = minecart.getLocation().getBlock().getChunk();
		for (int dx = -(range); dx <= range; dx++){
			for (int dz = -(range); dz <= range; dz++){
				Chunk chunk = current.getWorld().getChunkAt(current.getX() + dx, current.getZ() + dz);
				chunk.getWorld().unloadChunkRequest(chunk.getX(), chunk.getZ());
			}
		}
	}
}
