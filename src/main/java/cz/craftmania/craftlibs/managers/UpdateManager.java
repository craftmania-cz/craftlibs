package cz.craftmania.craftlibs.managers;

import cz.craftmania.craftlibs.CraftLibs;
import cz.craftmania.craftlibs.utils.Hash;
import cz.craftmania.craftlibs.utils.Log;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class UpdateManager {

    private File updaterFolder;
    private List<String> plugins = new ArrayList<>();

    public UpdateManager() {
        this.updaterFolder = new File(CraftLibs.getInstance().getDataFolder(), "updater");
        if (!this.updaterFolder.exists())
            this.updaterFolder.mkdirs();

        this.plugins = CraftLibs.getInstance().getConfig().getStringList("updater.plugins");
    }

    public void update() {
        for (String plugin : this.plugins) {
            Log.info("[Updater] Updating " + plugin + " plugin");
            File destinationFolder = new File(CraftLibs.getInstance().getServer().getWorldContainer(), "plugins");
            File sourceFile = new File(this.updaterFolder, plugin);
            if (!sourceFile.exists()) {
                Log.error("[Updater] New plugin file does not exist");
                continue;
            }
            File usingFile = new File(destinationFolder, plugin);
            if (!usingFile.exists()) {
                Log.error("[Updater] Old plugin file does not exist");
                continue;
            }
            String usingVersion = Hash.MD5.checksumToHex(usingFile);
            String sourceVersion = Hash.MD5.checksumToHex(sourceFile);
            if (usingVersion.equals(sourceVersion)) {
                Log.info("[Updater] Skipping plugin " + plugin + " due to equality of checksum (same file)");
                continue;
            }

            Path newPlugin = sourceFile.toPath();
            Path oldPlugin = usingFile.toPath();

            try {
                Files.copy(newPlugin, oldPlugin, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                Log.error("[Updater] Error while trying to copy new version of " + plugin + " (" + e.getMessage() + ")");
                e.printStackTrace();
                continue;
            }

            Log.success("[Updater] Plugin " + plugin + " was successfully updated.");
        }

    }
}

