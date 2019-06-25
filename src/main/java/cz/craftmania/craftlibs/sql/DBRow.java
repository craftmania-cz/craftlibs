package cz.craftmania.craftlibs.sql;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

public class DBRow {
    HashMap<String, Object> cells;

    public DBRow() {
        this.cells = new HashMap<>();
    }

    public void addCell(final String key, final Object value) {
        this.cells.put(key, value);
    }

    public Object getObject(final String key) {
        return this.cells.get(key);
    }

    public String getString(final String key) {
        final Object obj = this.cells.get(key);
        if (obj == null) {
            return (String) obj;
        }
        if (obj instanceof Boolean) {
            return obj.toString().equalsIgnoreCase("true") ? "1" : "0";
        }
        return obj.toString();
    }

    public Integer getInt(final String key) {
        final Object obj = this.cells.get(key);
        return (obj == null) ? 0 : ((int) Double.parseDouble(obj.toString()));
    }

    public Boolean getBoolean(final String key) {
        return (Boolean) this.cells.get(key);
    }

    public Long getLong(final String key) {
        return (Long) this.cells.get(key);
    }

    public Double getDouble(final String key) {
        return (Double) this.cells.get(key);
    }

    public Float getFloat(final String key) {
        return (Float) this.cells.get(key);
    }

    public byte[] getBlob(final String key) {
        return (byte[]) this.cells.get(key);
    }

    public Timestamp getTimestamp(final String key) {
        return (Timestamp) this.cells.get(key);
    }

    public Date getDate(final String key) {
        return (Date) this.cells.get(key);
    }
}
