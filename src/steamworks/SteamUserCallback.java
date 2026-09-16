package steamworks;

public interface SteamUserCallback{

    default void onAuthSessionTicket(SteamAuthTicket authTicket, SteamResult result){
    }

    default void onValidateAuthTicket(SteamID steamID,
                                      SteamAuth.AuthSessionResponse authSessionResponse,
                                      SteamID ownerSteamID){
    }

    default void onMicroTxnAuthorization(int appID, long orderID, boolean authorized){
    }

    default void onGetTicketForWebApi(SteamAuthTicket authTicket, SteamResult result, byte[] ticket){
    }

    default void onEncryptedAppTicket(SteamResult result){
    }

}