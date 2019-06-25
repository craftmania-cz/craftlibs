package cz.craftmania.craftlibs.sql;

import cz.craftmania.craftlibs.Main;
import cz.craftmania.craftlibs.utils.Log;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

public class SQLManager {

    private final Main plugin;
    private final ConnectionPoolManager pool;

    public SQLManager(Main plugin) {
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

    public ArrayList<DBRow> query(String query, Object... variables) {
        final Long time = System.currentTimeMillis();
        final ArrayList<DBRow> rows = new ArrayList<>();
        ResultSet result = null;
        PreparedStatement pState = null;
        try {
            pState = this.pool.getConnection().prepareStatement(query);
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
            }
        } catch (Exception ex) {
            Log.fatal("Error has occured in query: '" + query + "'");
            ex.printStackTrace();
        } finally {
            try {
                result.close();
            } catch (Exception ex) {
            }
            try {
                pState.close();
            } catch (Exception ex2) {
            }
        }
        final Long diff = System.currentTimeMillis() - time;
        if (diff > 500L) {
            Log.fatal("This query is taking too long (" + diff + "ms): '" + query + "'");
        }
        return rows;
    }

}
