package steamworks;

public interface SteamUtilsCallback{

    void onSteamShutdown();

    default void onFloatingGamepadTextInputDismissed(){
    }

}