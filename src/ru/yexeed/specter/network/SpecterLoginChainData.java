package ru.yexeed.specter.network;

import cn.nukkit.utils.LoginChainData;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SpecterLoginChainData implements LoginChainData {
    public String username;
    public UUID clientUUID;
    public String identityPublicKey;
    public long clientId;
    public String serverAddress;
    public String deviceModel;
    public int deviceOS;
    public String deviceId;
    public String gameVersion;
    public int guiScale;
    public String languageCode;
    public String XUID;
    public boolean xboxAuthed;
    public int currentInputMode;
    public int defaultInputMode;
    public String capeData;
    public int UIProfile;
    public String waterdogXUID;
    public String waterdogIP;
    @Getter
    public JsonObject rawData;

}
