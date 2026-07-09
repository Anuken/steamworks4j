package steamworks;

import steamworks.SteamNetworkingSockets.*;

public interface SteamNetworkingSocketsCallback{

    void onConnectionStatusChanged(Connection connection, SteamID steamID, ConnectionState state, ConnectionState prevState);

}
