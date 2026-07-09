package steamworks;

public class SteamUGCQuery extends SteamNativeHandle{

    public SteamUGCQuery(long handle){
        super(handle);
    }

    public boolean isValid(){
        return handle != -1;
    }
}
