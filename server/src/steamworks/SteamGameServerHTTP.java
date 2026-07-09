package steamworks;

public class SteamGameServerHTTP extends SteamHTTP{

    public SteamGameServerHTTP(SteamHTTPCallback callback){
        super(SteamGameServerAPINative.getSteamGameServerHTTPPointer(),
        SteamGameServerHTTPNative.createCallback(new SteamHTTPCallbackAdapter(callback)));
    }

}
