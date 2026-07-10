package steamworks;

/**
 * Configuration options for Steam Networking Sockets connections and global settings.
 * This is not an enum, because these values are used as keys/identifiers for a generic
 * configuration get/set API (name, type, value), rather than as a type-safe grouping.
 */
public interface SteamNetworkingConfigValue {

    int Invalid = 0;

    //
    // Connection options
    //

    /**
     * [connection int32] Timeout value (in ms) to use when first connecting.
     */
    int TimeoutInitial = 24;

    /**
     * [connection int32] Timeout value (in ms) to use after connection is established.
     */
    int TimeoutConnected = 25;

    /**
     * [connection int32] Upper limit of buffered pending bytes to be sent,
     * if this is reached SendMessage will return k_EResultLimitExceeded.
     * Default is 512k (524288 bytes).
     */
    int SendBufferSize = 9;

    /**
     * [connection int32] Upper limit on total size (in bytes) of received messages
     * that will be buffered waiting to be processed by the application. If this limit
     * is exceeded, packets will be dropped. This is to protect us from a malicious
     * peer flooding us with messages faster than we can process them.
     * <p>
     * This must be bigger than {@link #RecvMaxMessageSize}.
     */
    int RecvBufferSize = 47;

    /**
     * [connection int32] Upper limit on the number of received messages that will
     * be buffered waiting to be processed by the application. If this limit
     * is exceeded, packets will be dropped. This is to protect us from a malicious
     * peer flooding us with messages faster than we can pull them off the wire.
     */
    int RecvBufferMessages = 48;

    /**
     * [connection int32] Maximum message size that we are willing to receive.
     * If a client attempts to send us a message larger than this, the connection
     * will be immediately closed.
     * <p>
     * Default is 512k (524288 bytes). Note that the peer needs to be able to
     * send a message this big. (See k_cbMaxSteamNetworkingSocketsMessageSizeSend.)
     */
    int RecvMaxMessageSize = 49;

    /**
     * [connection int32] Max number of message segments that can be received
     * in a single UDP packet. While decoding a packet, if the number of segments
     * exceeds this, we will abort further packet processing.
     * <p>
     * The default is effectively unlimited. If you know that you very rarely
     * send small packets, you can protect yourself from malicious senders by
     * lowering this number.
     * <p>
     * In particular, if you are NOT using the reliability layer and are only using
     * SteamNetworkingSockets for datagram transport, setting this to a very low
     * number may be beneficial. (We recommend a value of 2.) Make sure your sender
     * disables Nagle!
     */
    int RecvMaxSegmentsPerPacket = 50;

    /**
     * [connection int64] Get/set userdata as a configuration option.
     * The default value is -1. You may want to set the user data as
     * a config value, instead of using ISteamNetworkingSockets::SetConnectionUserData
     * in two specific instances:
     * <p>
     * - You wish to set the userdata atomically when creating
     *   an outbound connection, so that the userdata is filled in properly
     *   for any callbacks that happen. However, note that this trick
     *   only works for connections initiated locally! For incoming
     *   connections, multiple state transitions may happen and
     *   callbacks be queued, before you are able to service the first
     *   callback! Be careful!
     * <p>
     * - You can set the default userdata for all newly created connections
     *   by setting this value at a higher level (e.g. on the listen
     *   socket or at the global level.) Then this default
     *   value will be inherited when the connection is created.
     *   This is useful in case -1 is a valid userdata value, and you
     *   wish to use something else as the default value so you can
     *   tell if it has been set or not.
     * <p>
     *   HOWEVER: once a connection is created, the effective value is
     *   then bound to the connection. Unlike other connection options,
     *   if you change it again at a higher level, the new value will not
     *   be inherited by connections.
     * <p>
     * Using the userdata field in callback structs is not advised because
     * of tricky race conditions. Instead, you might try one of these methods:
     * <p>
     * - Use a separate map with the HSteamNetConnection as the key.
     * - Fetch the userdata from the connection in your callback
     *   using ISteamNetworkingSockets::GetConnectionUserData, to
     *   ensure you have the current value.
     */
    int ConnectionUserData = 40;

    /**
     * [connection int32] Minimum/maximum send rate clamp, in bytes/sec.
     * At the time of this writing these two options should always be set to
     * the same value, to manually configure a specific send rate. The default
     * value is 256K. Eventually we hope to have the library estimate the bandwidth
     * of the channel and set the send rate to that estimated bandwidth, and these
     * values will only set limits on that send rate.
     */
    int SendRateMin = 10;

    /**
     * @see #SendRateMin
     */
    int SendRateMax = 11;

    /**
     * [connection int32] Nagle time, in microseconds. When SendMessage is called, if
     * the outgoing message is less than the size of the MTU, it will be
     * queued for a delay equal to the Nagle timer value. This is to ensure
     * that if the application sends several small messages rapidly, they are
     * coalesced into a single packet.
     * See historical RFC 896. Value is in microseconds.
     * Default is 5000us (5ms).
     */
    int NagleTime = 12;

    /**
     * [connection int32] Don't automatically fail IP connections that don't have
     * strong auth. On clients, this means we will attempt the connection even if
     * we don't know our identity or can't get a cert. On the server, it means that
     * we won't automatically reject a connection due to a failure to authenticate.
     * (You can examine the incoming connection and decide whether to accept it.)
     * <p>
     * 0: Don't attempt or accept unauthorized connections
     * 1: Attempt authorization when connecting, and allow unauthorized peers, but emit warnings
     * 2: don't attempt authentication, or complain if peer is unauthenticated
     * <p>
     * This is a dev configuration value, and you should not let users modify it in
     * production.
     */
    int IP_AllowWithoutAuth = 23;

    /**
     * [connection int32] The same as {@link #IP_AllowWithoutAuth}, but will only apply
     * for connections to/from localhost addresses. Whichever value is larger
     * (more permissive) will be used.
     */
    int IPLocalHost_AllowWithoutAuth = 52;

    /**
     * [connection int32] Do not send UDP packets with a payload of
     * larger than N bytes. If you set this, {@link #MTU_DataSize}
     * is automatically adjusted.
     */
    int MTU_PacketSize = 32;

    /**
     * [connection int32] (read only) Maximum message size you can send that
     * will not fragment, based on {@link #MTU_PacketSize}.
     */
    int MTU_DataSize = 33;

    /**
     * [connection int32] Allow unencrypted (and unauthenticated) communication.
     * 0: Not allowed (the default)
     * 1: Allowed, but prefer encrypted
     * 2: Allowed, and preferred
     * 3: Required. (Fail the connection if the peer requires encryption.)
     * <p>
     * This is a dev configuration value, since its purpose is to disable encryption.
     * You should not let users modify it in production. (But note that it requires
     * the peer to also modify their value in order for encryption to be disabled.)
     */
    int Unencrypted = 34;

    /**
     * [connection int32] Set this to 1 on outbound connections and listen sockets,
     * to enable "symmetric connect mode", which is useful in the following
     * common peer-to-peer use case:
     * <p>
     * - The two peers are "equal" to each other. (Neither is clearly the "client"
     *   or "server".)
     * - Either peer may initiate the connection, and indeed they may do this
     *   at the same time.
     * - The peers only desire a single connection to each other, and if both
     *   peers initiate connections simultaneously, a protocol is needed for them
     *   to resolve the conflict, so that we end up with a single connection.
     * <p>
     * This use case is both common, and involves subtle race conditions and tricky
     * pitfalls, which is why the API has support for dealing with it.
     * <p>
     * If an incoming connection arrives on a listen socket or via custom signaling,
     * and the application has not attempted to make a matching outbound connection
     * in symmetric mode, then the incoming connection can be accepted as usual.
     * A "matching" connection means that the relevant endpoint information matches.
     * (At the time this comment is being written, this is only supported for P2P
     * connections, which means that the peer identities must match, and the virtual
     * port must match. At a later time, symmetric mode may be supported for other
     * connection types.)
     * <p>
     * If connections are initiated by both peers simultaneously, race conditions
     * can arise, but fortunately, most of them are handled internally and do not
     * require any special awareness from the application. However, there
     * is one important case that application code must be aware of:
     * If application code attempts an outbound connection using a ConnectXxx
     * function in symmetric mode, and a matching incoming connection is already
     * waiting on a listen socket, then instead of forming a new connection,
     * the ConnectXxx call will accept the existing incoming connection, and return
     * a connection handle to this accepted connection.
     * IMPORTANT: in this case, a SteamNetConnectionStatusChangedCallback_t
     * has probably *already* been posted to the queue for the incoming connection!
     * (Once callbacks are posted to the queue, they are not modified.) It doesn't
     * matter if the callback has not been consumed by the app. Thus, application
     * code that makes use of symmetric connections must be aware that, when processing a
     * SteamNetConnectionStatusChangedCallback_t for an incoming connection, the
     * m_hConn may refer to a new connection that the app has has not
     * seen before (the usual case), but it may also refer to a connection that
     * has already been accepted implicitly through a call to Connect()! In this
     * case, AcceptConnection() will return k_EResultDuplicateRequest.
     * <p>
     * Only one symmetric connection to a given peer (on a given virtual port)
     * may exist at any given time. If client code attempts to create a connection,
     * and a (live) connection already exists on the local host, then either the
     * existing connection will be accepted as described above, or the attempt
     * to create a new connection will fail. Furthermore, linger mode functionality
     * is not supported on symmetric connections.
     * <p>
     * A more complicated race condition can arise if both peers initiate a connection
     * at roughly the same time. In this situation, each peer will receive an incoming
     * connection from the other peer, when the application code has already initiated
     * an outgoing connection to that peer. The peers must resolve this conflict and
     * decide who is going to act as the "server" and who will act as the "client".
     * Typically the application does not need to be aware of this case as it is handled
     * internally. On both sides, they will observe their outbound connection being
     * "accepted", although one of them may have been converted internally to act
     * as the "server".
     * <p>
     * In general, symmetric mode should be all-or-nothing: do not mix symmetric
     * connections with a non-symmetric connection that it might possibly "match"
     * with. If you use symmetric mode on any connections, then both peers should
     * use it on all connections, and the corresponding listen socket, if any. The
     * behaviour when symmetric and ordinary connections are mixed is not defined by
     * this API, and you should not rely on it. (This advice only applies when connections
     * might possibly "match". For example, it's OK to use all symmetric mode
     * connections on one virtual port, and all ordinary, non-symmetric connections
     * on a different virtual port, as there is no potential for ambiguity.)
     * <p>
     * When using the feature, you should set it in the following situations on
     * applicable objects:
     * <p>
     * - When creating an outbound connection using ConnectXxx function
     * - When creating a listen socket. (Note that this will automatically cause
     *   any accepted connections to inherit the flag.)
     * - When using custom signaling, before accepting an incoming connection.
     * <p>
     * Setting the flag on listen socket and accepted connections will enable the
     * API to automatically deal with duplicate incoming connections, even if the
     * local host has not made any outbound requests. (In general, such duplicate
     * requests from a peer are ignored internally and will not be visible to the
     * application code. The previous connection must be closed or resolved first.)
     */
    int SymmetricConnect = 37;

    /**
     * [connection int32] For connection types that use "virtual ports", this can be used
     * to assign a local virtual port. For incoming connections, this will always be the
     * virtual port of the listen socket (or the port requested by the remote host if custom
     * signaling is used and the connection is accepted), and cannot be changed. For
     * connections initiated locally, the local virtual port will default to the same as the
     * requested remote virtual port, if you do not specify a different option when creating
     * the connection. The local port is only relevant for symmetric connections, when
     * determining if two connections "match." In this case, if you need the local and remote
     * port to differ, you can set this value.
     * <p>
     * You can also read back this value on listen sockets.
     * <p>
     * This value should not be read or written in any other context.
     */
    int LocalVirtualPort = 38;

    /**
     * [connection int32] Enable Dual wifi band support for this connection.
     * 0 = no, 1 = yes, 2 = simulate it for debugging, even if dual wifi not available.
     */
    int DualWifi_Enable = 39;

    /**
     * [connection int32] True to enable diagnostics reporting through
     * generic platform UI. (Only available on Steam.)
     */
    int EnableDiagnosticsUI = 46;

    /**
     * [connection int32] Send of time-since-previous-packet values in each UDP packet.
     * This adds a small amount of packet overhead but allows for detailed jitter measurements
     * to be made by the receiver.
     * <p>
     * -  0: disables the sending
     * -  1: enables sending
     * - -1: (the default) Use the default for the connection type. For plain UDP connections,
     *       this is disabled, and for relayed connections, it is enabled. Note that relays
     *       always send the value.
     */
    int SendTimeSincePreviousPacket = 59;

    //
    // Simulating network conditions
    //
    // These are global (not per-connection) because they apply at
    // a relatively low UDP layer.
    //

    /**
     * [global float, 0--100] Randomly discard N pct of packets instead of sending/recv.
     * This is a global option only, since it is applied at a low level
     * where we don't have much context.
     */
    int FakePacketLoss_Send = 2;

    /**
     * @see #FakePacketLoss_Send
     */
    int FakePacketLoss_Recv = 3;

    /**
     * [global int32]. Delay all outbound packets by N ms.
     */
    int FakePacketLag_Send = 4;

    /**
     * [global int32]. Delay all inbound packets by N ms.
     */
    int FakePacketLag_Recv = 5;

    /**
     * Simulated jitter/clumping.
     * <p>
     * For each packet, a jitter value is determined (which may
     * be zero). This amount is added as extra delay to the
     * packet. When a subsequent packet is queued, it receives its
     * own random jitter amount from the current time. If this would
     * result in the packets being delivered out of order, the later
     * packet queue time is adjusted to happen after the first packet.
     * Thus simulating jitter by itself will not reorder packets, but it
     * can "clump" them.
     * <p>
     * - Avg: A random jitter time is generated using an exponential
     *   distribution using this value as the mean (ms). The default
     *   is zero, which disables random jitter.
     * - Max: Limit the random jitter time to this value (ms).
     * - Pct: odds (0-100) that a random jitter value for the packet
     *   will be generated. Otherwise, a jitter value of zero
     *   is used, and the packet will only be delayed by the jitter
     *   system if necessary to retain order, due to the jitter of a
     *   previous packet.
     * <p>
     * All values are [global float].
     * <p>
     * Fake jitter is simulated after fake lag, but before reordering.
     */
    int FakePacketJitter_Send_Avg = 53;

    /**
     * @see #FakePacketJitter_Send_Avg
     */
    int FakePacketJitter_Send_Max = 54;

    /**
     * @see #FakePacketJitter_Send_Avg
     */
    int FakePacketJitter_Send_Pct = 55;

    /**
     * @see #FakePacketJitter_Send_Avg
     */
    int FakePacketJitter_Recv_Avg = 56;

    /**
     * @see #FakePacketJitter_Send_Avg
     */
    int FakePacketJitter_Recv_Max = 57;

    /**
     * @see #FakePacketJitter_Send_Avg
     */
    int FakePacketJitter_Recv_Pct = 58;

    /**
     * [global float] 0-100 Percentage of packets we will add additional
     * delay to. If other packet(s) are sent/received within this delay
     * window (that doesn't also randomly receive the same extra delay),
     * then the packets become reordered.
     * <p>
     * This mechanism is primarily intended to generate out-of-order
     * packets. To simulate random jitter, use the FakePacketJitter.
     * Fake packet reordering is applied after fake lag and jitter.
     */
    int FakePacketReorder_Send = 6;

    /**
     * @see #FakePacketReorder_Send
     */
    int FakePacketReorder_Recv = 7;

    /**
     * [global int32] Extra delay, in ms, to apply to reordered
     * packets. The same time value is used for sending and receiving.
     */
    int FakePacketReorder_Time = 8;

    /**
     * [global float 0--100] Globally duplicate some percentage of sent packets.
     */
    int FakePacketDup_Send = 26;

    /**
     * [global float 0--100] Globally duplicate some percentage of received packets.
     */
    int FakePacketDup_Recv = 27;

    /**
     * [global int32] Amount of delay, in ms, to delay duplicated packets.
     * (We chose a random delay between 0 and this value.)
     */
    int FakePacketDup_TimeMax = 28;

    /**
     * [global int32] Trace every UDP packet, similar to Wireshark or tcpdump.
     * Value is max number of bytes to dump. -1 disables tracing.
     * 0 only traces the info but no actual data bytes.
     */
    int PacketTraceMaxBytes = 41;

    /**
     * [global int32] Global UDP token bucket rate limit for sending, in bytes/sec.
     * "Rate" refers to the steady state rate (the rate that tokens are put into
     * the bucket). Rate=0 disables the limiter entirely, which is the default.
     */
    int FakeRateLimit_Send_Rate = 42;

    /**
     * [global int32] Global UDP token bucket rate limit burst size for sending, in bytes.
     * "Burst" refers to the max amount that could be sent in a single burst (the max
     * capacity of the bucket). Burst=0 disables burst. (This is not realistic. A
     * burst of at least 4K is recommended; the default is higher.)
     */
    int FakeRateLimit_Send_Burst = 43;

    /**
     * [global int32] Global UDP token bucket rate limit for receiving, in bytes/sec.
     * @see #FakeRateLimit_Send_Rate
     */
    int FakeRateLimit_Recv_Rate = 44;

    /**
     * [global int32] Global UDP token bucket rate limit burst size for receiving, in bytes.
     * @see #FakeRateLimit_Send_Burst
     */
    int FakeRateLimit_Recv_Burst = 45;

    /**
     * Timeout used for out-of-order correction. This is used when we see a small
     * gap in the sequence number on a packet flow. For example let's say we are
     * processing packet 105 when the most recent one was 103. 104 might have dropped,
     * but there is also a chance that packets are simply being reordered. It is very
     * common on certain types of connections for packet 104 to arrive very soon after 105,
     * especially if 105 was large and 104 was small. In this case, when we see packet 105
     * we will shunt it aside and pend it, in the hopes of seeing 104 soon after. If 104
     * arrives before the timeout occurs, then we can deliver the packets in order to the
     * remainder of packet processing, and we will record this as a "correctable" out-of-order
     * situation. If the timer expires, then we will process packet 105, and assume for now
     * that 104 has dropped. (If 104 later arrives, we will process it, but that will be
     * accounted for as uncorrected.)
     * <p>
     * The default value is 1000 microseconds. Note that the Windows scheduler does not
     * have microsecond precision.
     * <p>
     * Set the value to 0 to disable out of order correction at the packet layer.
     * In many cases we are still effectively able to correct the situation because
     * reassembly of message fragments is tolerant of fragments packets arriving out of
     * order. Also, when messages are decoded and inserted into the queue for the app
     * to receive them, we will correct out of order messages that have not been
     * dequeued by the app yet. However, when out-of-order packets are corrected
     * at the packet layer, they will not reduce the connection quality measure.
     * (E.g. SteamNetConnectionRealTimeStatus_t::m_flConnectionQualityLocal)
     */
    int OutOfOrderCorrectionWindowMicroseconds = 51;
}