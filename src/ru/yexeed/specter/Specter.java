package ru.yexeed.specter;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import ru.yexeed.specter.network.SpecterInterface;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class Specter extends PluginBase implements Listener {

    private SpecterInterface interfaz;
    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.interfaz = new SpecterInterface(this);
        this.getLogger().notice("Specter enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        var arg = args.length > 0 ? args[0] : "";
        switch(arg){
            case "s":
            case "spawn": {
                var name = args.length > 1 ? args[1] : "";
                if (name.isEmpty()) {
                    sender.sendMessage("/specter spawn <name>");
                } else {
                    if (this.interfaz.getSessionByName(name) != null) {
                        sender.sendMessage("Specter " + name + " already connected!");
                    } else {
                        if(this.getServer().getPlayerExact(name) != null){
                            sender.sendMessage("Player " + name + " could not be replaced with Specter instance!");
                        }else {
                            this.interfaz.open(name);
                        }
                    }
                }
                break;
            }
            case "q":
            case "quit": {
                var name = args.length > 1 ? args[1] : "";
                if (name.isEmpty()) {
                    sender.sendMessage("/specter quit <name>");
                }else{
                    if(this.interfaz.getSessionByName(name) != null){
                        this.interfaz.close(this.interfaz.getSessionByName(name));
                        sender.sendMessage("Specter " + name + " closed");
                    }else{
                        sender.sendMessage("Specter " + name + " not connected!");
                    }
                }
                break;
            }
        }
        return true;
    }

    public static UUID generateUUIDFromString(String name) throws NoSuchAlgorithmException {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hash = sha1.digest((name + "6ba7b810-9dad-11d1-80b4-00c04fd430c8").getBytes(StandardCharsets.UTF_8));

            hash[6] = (byte) ((hash[6] & 0x0f) | 0x50); /* version 5 */
            hash[8] = (byte) ((hash[8] & 0x3f) | 0x80); /* IETF variant */

            return toUUID(hash);
    }

    private static byte[] toBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();

        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (msb >>> 8 * (7 - i));
            bytes[8 + i] = (byte) (lsb >>> 8 * (7 - i));
        }

        return bytes;
    }

    private static UUID toUUID(byte[] bytes) {
        long msb = 0;
        long lsb = 0;

        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (bytes[i] & 0xff);
        }

        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (bytes[i] & 0xff);
        }

        return new UUID(msb, lsb);
    }
}
