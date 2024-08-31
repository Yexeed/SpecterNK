package ru.yexeed.specter.network;

import cn.nukkit.Player;
import cn.nukkit.network.AdvancedSourceInterface;
import cn.nukkit.network.Network;
import cn.nukkit.network.session.NetworkPlayerSession;
import cn.nukkit.utils.TextFormat;
import io.netty.buffer.ByteBuf;
import ru.yexeed.specter.Specter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class SpecterInterface implements AdvancedSourceInterface {
    private final Specter specter;
    private final HashMap<InetSocketAddress, SpecterNetworkSession> sessions = new HashMap<>();
    private Network network;
    private long offset = 1;

    public SpecterInterface(Specter specter) {
        this.specter = specter;
    }

    @Override
    public void blockAddress(InetAddress address) {
        this.specter.getLogger().info("Block address: " + address.toString());
    }

    @Override
    public void blockAddress(InetAddress address, int timeout) {
        this.specter.getLogger().info("Block address: " + address.toString() + ", timeout: " + timeout);
    }

    @Override
    public void unblockAddress(InetAddress address) {
        this.specter.getLogger().info("Unblock address: " + address.toString());
    }

    @Override
    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public void sendRawPacket(InetSocketAddress socketAddress, ByteBuf payload) {
        this.specter.getLogger().info(TextFormat.LIGHT_PURPLE + "Sent raw packet");
    }

    @Override
    public NetworkPlayerSession getSession(InetSocketAddress address) {
        return this.sessions.get(address);
    }

    @Override
    public int getNetworkLatency(Player player) {
        return 0;
    }

    @Override
    public void close(Player player) {
        this.close(player, "Disconnected");
    }

    @Override
    public void close(Player player, String reason) {
        this.sessions.get(player.getSocketAddress()).disconnect(reason);
        this.sessions.remove(player.getSocketAddress());
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public boolean process() {
        return false;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void emergencyShutdown() {

    }

    public void open(String name) {
        var session = new SpecterNetworkSession(null, this.specter);
        InetSocketAddress address;
        try {
            address = new InetSocketAddress(InetAddress.getLocalHost(), (int) (0x7fff + this.offset));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.sessions.put(address, session);
        var player = new SpecterPlayer(this, 0x7fffffff + ++this.offset, address);
        this.specter.getServer().addPlayer(address, player);

        player.setupFakeData(name, this.offset);
        player.startPreLogin();
    }

    public SpecterNetworkSession getSessionByName(String name) {
        return this.sessions.values().stream()
                .filter(session -> session.getPlayer() != null)
                .filter(session -> session.getPlayer().getName().toLowerCase().equals(name))
                .findFirst()
                .orElse(null);
    }
}
