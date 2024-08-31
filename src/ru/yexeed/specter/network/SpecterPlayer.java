package ru.yexeed.specter.network;

import cn.nukkit.Player;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.LogLevel;
import com.google.gson.Gson;
import ru.yexeed.specter.Specter;

import java.net.InetSocketAddress;
import java.util.Arrays;

public class SpecterPlayer extends Player {
    private static final Skin WHITE_SKIN;

    static {
        WHITE_SKIN = new Skin();
        byte[] bytes = new byte[64 * 64 * 4];
        Arrays.fill(bytes, (byte) 0xFF);
        WHITE_SKIN.setSkinData(bytes);
        WHITE_SKIN.setSkinId("Standard_Steve");
    }

    public SpecterPlayer(SourceInterface interfaz, Long clientID, InetSocketAddress socketAddress) {
        super(interfaz, clientID, socketAddress);
    }

    @Override
    public SpecterNetworkSession getNetworkSession() {
        return (SpecterNetworkSession) super.getNetworkSession();
    }

    public void startPreLogin() {
        this.processPreLogin();
    }

    public void doFirstSpawn0() {
        this.doFirstSpawn();
    }

    public void setupFakeData(String name, long offset) {
        this.username = name;
        this.loginPacketReceived = true;
        this.protocol = ProtocolInfo.CURRENT_PROTOCOL;
        this.version = ProtocolInfo.MINECRAFT_VERSION_NETWORK;
        this.iusername = username.toLowerCase();
        this.displayName = username;
        this.setDataProperty(new StringEntityData(DATA_NAMETAG, this.username), false);
        this.randomClientId = offset;
        try {
            this.uuid = Specter.generateUUIDFromString(name);
        } catch (Exception e) {
            this.getServer().getLogger().log(LogLevel.EMERGENCY, "Failed to generate UUID for " + name, e);
            this.close();
            return;
        }
        this.rawUUID = Binary.writeUUID(this.uuid);
        this.setSkin(WHITE_SKIN);

        var loginChainData = new SpecterLoginChainData();
        loginChainData.username = name;
        loginChainData.clientUUID = this.getUniqueId();
        loginChainData.identityPublicKey = "abcdef";
        loginChainData.clientId = this.randomClientId;
        loginChainData.serverAddress = this.getServer().getIp() + ":" + this.getServer().getPort();
        loginChainData.deviceModel = "Specter#001";
        loginChainData.deviceOS = 7;
        loginChainData.deviceId = "DeviceID";
        loginChainData.gameVersion = this.version;
        loginChainData.guiScale = -1;
        loginChainData.languageCode = "en_us";
        loginChainData.XUID = "0";
        loginChainData.xboxAuthed = false;
        loginChainData.currentInputMode = 1;
        loginChainData.defaultInputMode = 1;
        loginChainData.capeData = "";
        loginChainData.UIProfile = -1;
        loginChainData.waterdogXUID = null;
        loginChainData.waterdogIP = null;
        loginChainData.rawData = (new Gson()).toJsonTree(loginChainData).getAsJsonObject();

        this.loginChainData = loginChainData;

        this.getNetworkSession().setPlayer(this);
    }
}
