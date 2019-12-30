package cz.craftmania.craftlibs.sql;

import cz.craftmania.craftlibs.CraftLibs;
import cz.craftmania.craftlibs.utils.Log;
import org.bukkit.Bukkit;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class SQLManager {

    private final CraftLibs plugin;
    private final ConnectionPoolManager pool;

    public SQLManager(CraftLibs plugin) {
        this.plugin = plugin;
        pool = new ConnectionPoolManager(plugin);

        Log.success("SQLManager loaded");
    }

    public void onDisable() {
        pool.closePool();
    }

    public ConnectionPoolManager getPool() {
        return pool;
    }

    public CompletableFuture<ArrayList<DBRow>> query(String query, Object... variables) {
        CompletableFuture<ArrayList<DBRow>> completableFuture = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(CraftLibs.getInstance(), () -> {
            final Long time = System.currentTimeMillis();
            final ArrayList<DBRow> rows = new ArrayList<>();
            Connection connection = null;
            ResultSet result = null;
            PreparedStatement pState = null;
            try {
                connection = pool.getConnection();
                pState = connection.prepareStatement(query);
                for (int i = 1; i <= variables.length; ++i) {
                    Object obj = variables[i - 1];
                    if (obj != null && obj.toString().equalsIgnoreCase("null")) {
                        obj = null;
                    }
                    if (obj instanceof Blob) {
                        pState.setBlob(i, (Blob) obj);
                    } else if (obj instanceof InputStream) {
                        pState.setBinaryStream(i, (InputStream) obj);
                    } else if (obj instanceof byte[]) {
                        pState.setBytes(i, (byte[]) obj);
                    } else if (obj instanceof Boolean) {
                        pState.setBoolean(i, (boolean) obj);
                    } else if (obj instanceof Integer) {
                        pState.setInt(i, (int) obj);
                    } else if (obj instanceof String) {
                        pState.setString(i, (String) obj);
                    } else {
                        pState.setObject(i, obj);
                    }
                }
                if (pState.execute()) {
                    result = pState.getResultSet();
                }
                if (result != null) {
                    final ResultSetMetaData mtd = result.getMetaData();
                    final int columnCount = mtd.getColumnCount();
                    while (result.next()) {
                        final DBRow row = new DBRow();
                        for (int l = 0; l < columnCount; ++l) {
                            final String columnName = mtd.getColumnName(l + 1);
                            row.addCell(columnName, result.getObject(columnName));
                        }
                        rows.add(row);
                    }
                    completableFuture.complete(rows);
                }
            } catch (Exception ex) {
                Log.fatal("Error has occured in query: '" + query + "'");
                completableFuture.completeExceptionally(new Exception("Error has occured in query: '" + query + "'"));
                ex.printStackTrace();
            } finally {
                pool.close(connection, pState, result);
            }
            final Long diff = System.currentTimeMillis() - time;
            if (diff > 500L) {
                Log.fatal("This query is taking too long (" + diff + "ms): '" + query + "'");
            }
        });

        return completableFuture;
    }

}
