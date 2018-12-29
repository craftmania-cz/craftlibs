package cz.craftmania.libs;

import cz.craftmania.libs.sql.SQLManager;
import cz.craftmania.libs.utils.Log;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftLibs extends JavaPlugin {

    private static CraftLibs instance;

    private static SQLManager sqlManager;

    @Override
    public void onEnable() {
        final long startMillis = System.currentTimeMillis();
        Log.info("Loading CraftLibs (v" + getDescription().getVersion() + ")");

        // Instance
        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        sqlManager = new SQLManager(this);

        Log.send(" ");
        final long diff = System.currentTimeMillis() - startMillis;
        Log.success("CraftLibs loaded (" + diff + "ms)");

    }

    @Override
    public void onDisable() {
        instance = null;
        sqlManager.onDisable();
    }

    public static CraftLibs getInstance() {
        return instance;
    }

    public static SQLManager getSqlManager() {
        return sqlManager;
    }
}
