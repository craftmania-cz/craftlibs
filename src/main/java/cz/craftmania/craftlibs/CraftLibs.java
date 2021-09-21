package cz.craftmania.craftlibs;

import cz.craftmania.craftlibs.command.ConfirmActionCommand;
import cz.craftmania.craftlibs.exceptions.CraftLibsCredentialsDefaultException;
import cz.craftmania.craftlibs.exceptions.CraftLibsFeatureNotEnabledException;
import cz.craftmania.craftlibs.managers.BalanceManager;
import cz.craftmania.craftlibs.managers.UpdateManager;
import cz.craftmania.craftlibs.sql.SQLManager;
import cz.craftmania.craftlibs.utils.Log;
import cz.craftmania.craftlibs.utils.actions.ConfirmAction;
import cz.craftmania.craftlibs.utils.task.RunnableHelper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class CraftLibs extends JavaPlugin {

    private static CraftLibs instance;

    public static String SERVER;

    private static boolean sqlEnabled;
    private boolean updaterEnabled;
    private static boolean sqlDefault = false;

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

        SERVER = getConfig().getString("server-id");

        sqlEnabled = getConfig().getBoolean("sql.enabled", true);
        this.updaterEnabled = getConfig().getBoolean("updater.enabled", false);

        if (sqlEnabled) {
            if (Objects.requireNonNull(getConfig().getString("sql.hostname", "host")).equalsIgnoreCase("host")
            && getConfig().getInt("sql.port", 3306) == 3306
            && Objects.requireNonNull(getConfig().getString("sql.database", "database")).equalsIgnoreCase("database")
            && Objects.requireNonNull(getConfig().getString("sql.username", "user")).equalsIgnoreCase("user")
            && Objects.requireNonNull(getConfig().getString("sql.password", "password")).equalsIgnoreCase("password")) {
                Log.error("SQL credentials are default, SQL not enabled.");
                sqlDefault = true;
                return;
            }
            sqlManager = new SQLManager(this);
        }
        balanceManager = new BalanceManager(this);

        getCommand("confirmaction").setExecutor(new ConfirmActionCommand());
        Bukkit.getPluginManager().registerEvents(new ConfirmAction(), this);

        Log.send(" ");
        final long diff = System.currentTimeMillis() - startMillis;
        Log.success("CraftLibs loaded (" + diff + "ms)");
    }

    @Override
    public void onDisable() {

        if (sqlEnabled) {
            sqlManager.onDisable();
        }

        if (this.updaterEnabled) {
            new UpdateManager().update();
        }

        RunnableHelper.setServerStopping();

        instance = null;

    }

    public static CraftLibs getInstance() {
        return instance;
    }

    public static SQLManager getSqlManager() throws CraftLibsFeatureNotEnabledException, CraftLibsCredentialsDefaultException {
        if (!sqlEnabled) {
            throw new CraftLibsFeatureNotEnabledException("SQL feature is not enabled in CraftLibs");
        }
        if (sqlDefault) {
            throw new CraftLibsCredentialsDefaultException("SQL feature is disabled because of default SQL credentials in config");
        }
        return sqlManager;
    }

    public BalanceManager getBalanceManager() {
        return balanceManager;
    }
}
