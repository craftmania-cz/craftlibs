package cz.craftmania.craftlibs;

import cz.craftmania.craftlibs.exceptions.SQLNotEnabledException;
import cz.craftmania.craftlibs.managers.BalanceManager;
import cz.craftmania.craftlibs.managers.UpdateManager;
import cz.craftmania.craftlibs.sql.SQLManager;
import cz.craftmania.craftlibs.utils.Log;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftLibs extends JavaPlugin {

    private static CraftLibs instance;

    private static boolean sqlEnabled;
    private boolean updaterEnabled;

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

        sqlEnabled = getConfig().getBoolean("sql.enabled");
        this.updaterEnabled = getConfig().getBoolean("updater.enabled");

        if (sqlEnabled) {
            sqlManager = new SQLManager(this);
        }
        balanceManager = new BalanceManager(this);

        Log.send(" ");
        final long diff = System.currentTimeMillis() - startMillis;
        Log.success("CraftLibs loaded (" + diff + "ms)");

    }

    @Override
    public void onDisable() {

        if (sqlEnabled) {
            sqlManager.onDisable();
        }

        if(this.updaterEnabled){
            new UpdateManager().update();
        }

        instance = null;

    }

    public static CraftLibs getInstance() {
        return instance;
    }

    public static SQLManager getSqlManager() throws SQLNotEnabledException {
        if (!sqlEnabled) {
            throw new SQLNotEnabledException();
        }
        return sqlManager;
    }

    public BalanceManager getBalanceManager() {
        return balanceManager;
    }
}
