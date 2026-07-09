package steamworks;

public class SteamAPICall extends SteamNativeHandle{

    SteamAPICall(long handle){
        super(handle);
    }

    public boolean isValid(){
        return handle != 0;
    }
}
