package steamworks;

import java.nio.*;

final class SteamNetworkingSocketsNative{

    // @off

    /*JNI
		#include <steam_api.h>
		#include "SteamNetworkingSocketsCallback.h"
		#include <iostream>
	*/

    static native long createCallback(SteamNetworkingSocketsCallbackAdapter javaCallback); /*
		return (intp) new SteamNetworkingSocketsCallback(env, javaCallback);
	*/

    public static native int connectP2P(long steamID, int virtualPort);/*
        SteamNetworkingIdentity identity;
        identity.m_eType = k_ESteamNetworkingIdentityType_SteamID;
        identity.SetSteamID64(steamID);

        HSteamNetConnection connection = SteamNetworkingSockets()->ConnectP2P(identity, virtualPort, 0, NULL);

        return connection;
    */

    public static native int createListenSocketP2P(int virtualPort);/*
        HSteamListenSocket socket = SteamNetworkingSockets()->CreateListenSocketP2P(virtualPort, 0, NULL);

        return socket;
    */

    public static native int acceptConnection(int netConnectionHandle);/*
        return SteamNetworkingSockets()->AcceptConnection(netConnectionHandle);
    */

    public static native boolean closeConnection(int netConnectionHandle, int reason, boolean linger);/*
        return SteamNetworkingSockets()->CloseConnection(netConnectionHandle, reason, NULL, linger);
    */

    public static native boolean closeListenSocket(int socketHandle);/*
        return SteamNetworkingSockets()->CloseListenSocket(socketHandle);
    */

    public static native int sendMessageToConnection(int netConnectionHandle, ByteBuffer data, int offset, int size, int sendFlags);/*
        return SteamNetworkingSockets()->SendMessageToConnection(netConnectionHandle, &data[offset], size, sendFlags, NULL);
    */

    public static native int receiveMessageOnConnection(int netConnectionHandle, ByteBuffer data, int offset, int size);/*

        SteamNetworkingMessage_t* messages[1];

        int messagesReceived = SteamNetworkingSockets()->ReceiveMessagesOnConnection((HSteamNetConnection)netConnectionHandle, messages, 1);
        if (messagesReceived <= 0 || !messages[0]) {
            return 0;
        }

        SteamNetworkingMessage_t* message = messages[0];
        if (message->m_cbSize > size) {
            message->Release();
            return -message->m_cbSize;
        }

        memcpy(&data[offset], message->m_pData, message->m_cbSize);

        int bytesWritten = message->m_cbSize;

        message->Release();

        return bytesWritten;
    */

    public static native int flushMessages(int connectionHandle);/*
        return SteamNetworkingSockets()->FlushMessagesOnConnection(connectionHandle);
    */

    public static native boolean setConnectionConfigValueInt32(int netConnectionHandle, int configValue, int value);/*
        int32_t v = value;
        return SteamNetworkingUtils()->SetConfigValue(
            (ESteamNetworkingConfigValue) configValue,
            k_ESteamNetworkingConfig_Connection,
            (intptr_t) netConnectionHandle,
            k_ESteamNetworkingConfig_Int32,
            &v
        );
    */

    public static native void enableSymmetricConnect();/*
        int32_t v = 1;
        SteamNetworkingUtils()->SetConfigValue(
            k_ESteamNetworkingConfig_SymmetricConnect,
            k_ESteamNetworkingConfig_Global,
            0,
            k_ESteamNetworkingConfig_Int32,
            &v
        );
    */

    // Packs SteamNetConnectionRealTimeStatus_t (minus per-lane data) into `data` starting at `offset`.
    // Layout (native byte order), 52 bytes total:
    //   0  int32  eState
    //   4  int32  nPing
    //   8  float  flConnectionQualityLocal
    //  12  float  flConnectionQualityRemote
    //  16  float  flOutPacketsPerSec
    //  20  float  flOutBytesPerSec
    //  24  float  flInPacketsPerSec
    //  28  float  flInBytesPerSec
    //  32  int32  nSendRateBytesPerSecond
    //  36  int32  cbPendingUnreliable
    //  40  int32  cbPendingReliable
    //  44  int32  cbSentUnackedReliable
    //  48  int32  (reserved/padding, currently unused)
    // Returns the EResult from the native call; buffer is only written on k_EResultOK.
    public static native int getConnectionRealTimeStatus(int netConnectionHandle, ByteBuffer data, int offset); /*
        SteamNetConnectionRealTimeStatus_t status;
        EResult result = SteamNetworkingSockets()->GetConnectionRealTimeStatus(
            (HSteamNetConnection) netConnectionHandle, &status, 0, NULL);

        if(result == k_EResultOK){
            char* out = (char*)&data[offset];
            int32_t state = (int32_t) status.m_eState;
            memcpy(out + 0,  &state,                              4);
            memcpy(out + 4,  &status.m_nPing,                     4);
            memcpy(out + 8,  &status.m_flConnectionQualityLocal,  4);
            memcpy(out + 12, &status.m_flConnectionQualityRemote, 4);
            memcpy(out + 16, &status.m_flOutPacketsPerSec,        4);
            memcpy(out + 20, &status.m_flOutBytesPerSec,          4);
            memcpy(out + 24, &status.m_flInPacketsPerSec,         4);
            memcpy(out + 28, &status.m_flInBytesPerSec,           4);
            memcpy(out + 32, &status.m_nSendRateBytesPerSecond,   4);
            memcpy(out + 36, &status.m_cbPendingUnreliable,       4);
            memcpy(out + 40, &status.m_cbPendingReliable,         4);
            memcpy(out + 44, &status.m_cbSentUnackedReliable,     4);
        }

        return result;
    */
}
