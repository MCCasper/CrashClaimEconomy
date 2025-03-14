package dev.whips.crashclaimeconomy.compatability;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import dev.whips.crashclaimeconomy.compatability.versions.*;
import dev.whips.crashclaimeconomy.config.GlobalConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

/**
 * Inspired by AnvilGUI
 * https://github.com/WesJD/AnvilGUI/blob/master/api/src/main/java/net/wesjd/anvilgui/version/VersionMatcher.java
 */
public class CompatibilityManager {
    private static ProtocolManager protocolManager;

    private static final WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
    private static final WrappedDataWatcher.Serializer integerSerializer = WrappedDataWatcher.Registry.get(Integer.class);

    private final CompatibilityWrapper wrapper;

    private final List<Class<? extends CompatibilityWrapper>> versions = Arrays.asList(
            Wrapper1_16.class,
            Wrapper1_16_1.class,
            Wrapper1_16_2.class,
            Wrapper1_16_3.class,
            Wrapper1_16_4.class,
            Wrapper1_16_5.class,
            Wrapper1_17.class,
            Wrapper1_17_1.class,
            Wrapper1_18_0.class,
            Wrapper1_18_1.class,
            Wrapper1_18_2.class
    );

    public CompatibilityManager(ProtocolManager manager){
        protocolManager = manager;

        String forcedVersion = GlobalConfig.forcedVersionString;
        if (forcedVersion != null && !forcedVersion.equals("")){
            wrapper = match(forcedVersion);
        } else {
            wrapper = match(ProtocolLibrary.getProtocolManager().getMinecraftVersion().getVersion().replace(".", "_"));
        }
    }

    private CompatibilityWrapper match(String serverVersion) {
        try {
            return versions.stream()
                    .filter(version -> version.getSimpleName().substring(7).equals(serverVersion))
                    .findFirst().orElseThrow(() -> new RuntimeException("Your server version [" + serverVersion + "] isn't supported in CrashClaim! Setting use-this-version-instead to a version string, like 1_17_1, will skip this check and might work. Proceed with caution."))
                    .getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    public CompatibilityWrapper getWrapper() {
        return wrapper;
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public static WrappedDataWatcher.Serializer getByteSerializer() {
        return byteSerializer;
    }

    public static WrappedDataWatcher.Serializer getIntegerSerializer() {
        return integerSerializer;
    }
}
