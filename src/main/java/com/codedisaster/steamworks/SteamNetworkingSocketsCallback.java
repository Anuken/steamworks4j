package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamNetworkingSockets.*;

public interface SteamNetworkingSocketsCallback{

    void onConnectionStatusChanged(Connection connection, SteamID steamID, ConnectionState state, ConnectionState prevState);

}
