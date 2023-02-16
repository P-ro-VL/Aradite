package aradite.nms.recoil;

import net.minecraft.server.v1_16_R3.PacketPlayOutPosition;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Recoil effect for gun.
 */
public final class Recoil {

    private static final Random random = ThreadLocalRandom.current();

    /**
     * Send the recoil effect packet to the given {@code player}.
     *
     * @param player             The player
     * @param vert               The recoil vert
     * @param horizontalMaxDelta The max delta value of horizontal rotation. Default should be 2.0. The greater this
     *                           value is, the more unstable the gun is.
     */
    public static void sendRecoilEffect(Player player, RecoilVert vert, float horizontalMaxDelta) {
        PacketPlayOutPosition packet = new PacketPlayOutPosition();
        try {
            Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> var1 = PacketPlayOutPosition.EnumPlayerTeleportFlags.a(31);

            Class<PacketPlayOutPosition> clazz = (Class<PacketPlayOutPosition>) packet.getClass();
            Field field = clazz.getDeclaredField("f");
            field.setAccessible(true);
            field.set(packet, var1);
            field.setAccessible(false);

            Field yaw = clazz.getDeclaredField("d");
            Field pitch = clazz.getDeclaredField("e");

            yaw.setAccessible(true);
            pitch.setAccessible(true);

            float newYaw = (random.nextBoolean() ? -1.0F : 1.0F) * random.nextFloat() * horizontalMaxDelta;
            float newPitch = vert.getRecoilVertValue();

            yaw.set(packet, newYaw);
            pitch.set(packet, newPitch);

            yaw.setAccessible(false);
            pitch.setAccessible(false);

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("An error has occured when trying to send recoil effect" +
                    " packet to player " + player.getName() + " !");
        }
    }

}
