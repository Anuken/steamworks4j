package com.codedisaster.steamworks;

public interface SteamUserCallback{

    default void onValidateAuthTicket(SteamID steamID,
                              SteamAuth.AuthSessionResponse authSessionResponse,
                              SteamID ownerSteamID){}

    default void onMicroTxnAuthorization(int appID, long orderID, boolean authorized){}

    default void onEncryptedAppTicket(SteamResult result){}

}
