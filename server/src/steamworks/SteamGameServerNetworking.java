package steamworks;

public class SteamGameServerNetworking extends SteamNetworking{

    public SteamGameServerNetworking(SteamNetworkingCallback callback){
        super(SteamGameServerAPINative.getSteamGameServerNetworkingPointer(),
        SteamGameServerNetworkingNative.createCallback(new SteamNetworkingCallbackAdapter(callback)));
    }

}
