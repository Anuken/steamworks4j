package steamworks;

public interface SteamAPIWarningMessageHook{
    void onWarningMessage(int severity, String message);
}
