package ru.yexeed.specter.network;

import cn.nukkit.network.CompressionProvider;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.session.NetworkPlayerSession;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import lombok.Setter;
import ru.yexeed.specter.Specter;


public class SpecterNetworkSession implements NetworkPlayerSession {
    @Getter
    @Setter
    private SpecterPlayer player;
    private Specter specter;

    public SpecterNetworkSession(SpecterPlayer player, Specter owner) {
        this.player = player;
        this.specter = owner;
    }

    @Override
    public void sendPacket(DataPacket packet) {
        switch(packet.packetId()){
            case ProtocolInfo.RESOURCE_PACKS_INFO_PACKET:
                var pk = new ResourcePackClientResponsePacket();
                pk.responseStatus = ResourcePackClientResponsePacket.STATUS_COMPLETED;
                this.player.handleDataPacket(pk);
                this.specter.getLogger().info(TextFormat.LIGHT_PURPLE + "[" + this.getPlayer().getName() + "] Resource packs completed");
                break;
            case ProtocolInfo.PLAY_STATUS_PACKET:
                PlayStatusPacket playStatusPacket = (PlayStatusPacket) packet;
                if(playStatusPacket.status == PlayStatusPacket.PLAYER_SPAWN){
                    this.player.doFirstSpawn0();
                    this.specter.getLogger().info(TextFormat.LIGHT_PURPLE + "[" + this.getPlayer().getName() + "] Spawned");
                }
                break;
            case ProtocolInfo.TEXT_PACKET:
                TextPacket textPacket = (TextPacket) packet;
                var type = "Unknown";
                switch(textPacket.type){
                    case TextPacket.TYPE_CHAT -> type = "Chat";
                    case TextPacket.TYPE_RAW -> type = "Message";
                    case TextPacket.TYPE_POPUP -> type = "Popup";
                    case TextPacket.TYPE_SYSTEM -> type = "System";
                    case TextPacket.TYPE_TIP -> type = "Tip";
                    case TextPacket.TYPE_TRANSLATION -> type = "Translation (with params: " + String.join(", ", textPacket.parameters) + ")";
                }
                this.specter.getLogger().info(TextFormat.LIGHT_PURPLE + "[" + this.getPlayer().getName() + "] " + type + ": " + textPacket.message);
                break;
            case ProtocolInfo.SET_TITLE_PACKET:
                SetTitlePacket setTitlePacket = (SetTitlePacket) packet;
                type = "Unknown";
                switch(setTitlePacket.type){
                    case SetTitlePacket.TYPE_TITLE -> type = "Title";
                    case SetTitlePacket.TYPE_SUBTITLE -> type = "Subtitle";
                    case SetTitlePacket.TYPE_ACTIONBAR_JSON -> type = "Action bar";
                }
                this.specter.getLogger().info(TextFormat.LIGHT_PURPLE + "[" + this.getPlayer().getName() + "] Set " + type + ": " + setTitlePacket.text);
                break;
        }
    }

    @Override
    public void sendImmediatePacket(DataPacket packet, Runnable callback) {

    }

    @Override
    public void disconnect(String reason) {

    }

    @Override
    public void setCompression(CompressionProvider compression) {

    }

    @Override
    public CompressionProvider getCompression() {
        return null;
    }

}
