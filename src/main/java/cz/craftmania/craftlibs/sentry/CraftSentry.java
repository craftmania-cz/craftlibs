package cz.craftmania.craftlibs.sentry;

import cz.craftmania.craftlibs.CraftLibs;
import cz.craftmania.craftlibs.exceptions.CraftLibsFeatureNotEnabledException;
import cz.craftmania.craftlibs.utils.Log;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.Breadcrumb;
import io.sentry.event.BreadcrumbBuilder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CraftSentry {

    private final SentryClient sentryClient;
    private final SortedMap<String, String> loadedPlugins;
    private final String version;
    private final LinkedList<LogEvent> breadcrumbs = new LinkedList<>();

    public CraftSentry(String dsn) {
        this.sentryClient = SentryClientFactory.sentryClient(dsn);
        this.loadedPlugins = getLoadedPlugins();
        this.version = getServerVersion();

        Logger logger = (Logger) LogManager.getRootLogger();

        //this.sentryClient.setServerName(CraftLibs.SERVER);
        int maximumEntries = 50;

        Appender breadcrumbAppender = new AbstractAppender("Breadcrumb Builder", null, null, false) {
            @Override
            public void append(LogEvent event) {
                synchronized (breadcrumbs) {
                    breadcrumbs.add(event);
                    if (breadcrumbs.size() > maximumEntries) {
                        breadcrumbs.removeFirst();
                    }
                }
            }
        };
        breadcrumbAppender.start();

        logger.addAppender(breadcrumbAppender);

        Log.success("Sentry loaded");
    }

    public void sendException(Exception exception) throws CraftLibsFeatureNotEnabledException {
        Log.info("Sending sentry exception");
        Context context = this.sentryClient.getContext();
        context.clear();

        context.addExtra("TPS", Bukkit.getServer().getTPS());
        context.addExtra("Plugins", loadedPlugins);
        context.addExtra("Online players", Bukkit.getOnlinePlayers().size());

        this.getBreadcrumbs().forEach(context::recordBreadcrumb);

        context.addTag("server", CraftLibs.SERVER);
        context.addTag("version", version);

        sentryClient.sendException(exception);
    }

    private SortedMap<String, String> getLoadedPlugins() {
        SortedMap<String, String> pluginVersions = new TreeMap<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            pluginVersions.put(plugin.getName(), plugin.getDescription().getVersion());
        }
        return pluginVersions;
    }

    private String getServerVersion() {
        // Clean Bukkit version
        String bukkitVersion = Bukkit.getServer().getBukkitVersion();
        if (bukkitVersion.endsWith("-SNAPSHOT")) {
            bukkitVersion = bukkitVersion.substring(0, bukkitVersion.lastIndexOf("-SNAPSHOT"));
        }
        return bukkitVersion;
    }

    private List<Breadcrumb> getBreadcrumbs() {
        List<Breadcrumb> result = new ArrayList<>();
        List<LogEvent> breadcrumbsCopy = new ArrayList<>(breadcrumbs);
        for (LogEvent breadcrumbEvent : breadcrumbsCopy) {
            BreadcrumbBuilder breadcrumb = new BreadcrumbBuilder();

            String message = null;
            if (breadcrumbEvent.getMessage() != null) {
                message = breadcrumbEvent.getMessage().getFormattedMessage();
            }

            // Default to empty message to prevent Raven error
            if (message == null) {
                breadcrumb.setMessage("");
            } else {
                breadcrumb.setMessage(message.replaceAll("\u001B\\[[;\\d]*m", "")); // odstraneni barvicek, protoze je sentry nepodporuje
            }

            // Set defaults
            breadcrumb.setTimestamp(new Date(breadcrumbEvent.getTimeMillis()));
            breadcrumb.setLevel(getBreadcrumbLevel(breadcrumbEvent));
            breadcrumb.setCategory(" "); // Empty to indicate regular logging
            breadcrumb.setType(Breadcrumb.Type.DEFAULT);
            Map<String, String> data = new HashMap<>();
            if (breadcrumbEvent.getThrown() != null) {
                data.put("exception", ExceptionUtils.getStackTrace(breadcrumbEvent.getThrown()));
            }

            // Set data (merged data from all rules)
            if (!data.isEmpty()) {
                breadcrumb.setData(data);
            }

            result.add(breadcrumb.build());
        }
        return result;
    }

    private Breadcrumb.Level getBreadcrumbLevel(LogEvent logEvent) {
        if (logEvent.getLevel().equals(Level.WARN)) {
            return Breadcrumb.Level.WARNING;
        } else if (logEvent.getLevel().equals(Level.ERROR)) {
            return Breadcrumb.Level.ERROR;
        } else if (logEvent.getLevel().equals(Level.FATAL)) {
            return Breadcrumb.Level.CRITICAL;
        } else if (logEvent.getLevel().equals(Level.DEBUG)) {
            return Breadcrumb.Level.DEBUG;
        } else {
            return Breadcrumb.Level.INFO;
        }
    }
}
