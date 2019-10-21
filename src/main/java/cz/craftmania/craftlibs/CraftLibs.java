package cz.craftmania.craftlibs;

import cz.craftmania.craftlibs.managers.BalanceManager;
import cz.craftmania.craftlibs.sql.SQLManager;
import cz.craftmania.craftlibs.utils.Log;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftLibs extends JavaPlugin {

    private static CraftLibs instance;

    private static SQLManager sqlManager;
    private BalanceManager balanceManager;

    @Override
    public void onEnable() {
        final long startMillis = System.currentTimeMillis();
        Log.info("Loading Main (v" + getDescription().getVersion() + ")");

        // Instance
        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        sqlManager = new SQLManager(this);
        balanceManager = new BalanceManager(this);

        Log.send(" ");
        final long diff = System.currentTimeMillis() - startMillis;
        Log.success("Main loaded (" + diff + "ms)");

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

    public BalanceManager getBalanceManager() {
        return balanceManager;
    }
}
