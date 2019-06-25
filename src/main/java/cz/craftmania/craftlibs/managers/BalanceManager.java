package cz.craftmania.craftlibs.managers;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cz.craftmania.craftlibs.Main;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Iterator;
import java.util.UUID;
import java.util.function.Consumer;

public class BalanceManager implements PluginMessageListener {

    private final Multimap<MessageContext, Consumer<ByteArrayDataInput>> contexts = LinkedHashMultimap.create();
    private final Main plugin;

    public BalanceManager(Main plugin) {
        this.plugin = plugin;

        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "PlayerBalancer", this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "PlayerBalancer");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("PlayerBalancer")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();

            Iterator<Consumer<ByteArrayDataInput>> iterator = contexts.get(
                    new MessageContext(channel, subchannel, player.getUniqueId())
            ).iterator();

            if (iterator.hasNext()) {
                iterator.next().accept(in);
                iterator.remove();
            }
        }
    }

    /**
     * Send instantly player into selected server
     * @param player Selected player
     * @param section Server or selection
     */
    public void connectPlayer(Player player, String section) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(section);
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
    }

    /**
     * Send instantly player into fallback group ex. event server -> lobby
     * @param player Selected player
     */
    public void fallbackPlayer(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("FallbackPlayer");
        out.writeUTF(player.getName());
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
    }

    /**
     * Returns section as object in consumer by selected name
     * @param section Selected section
     * @param consumer Consumer #lambda
     * @return If true action has been successfully
     */
    public boolean getSectionByName(String section, Consumer<String> consumer) {
        Player player = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
        if (player == null) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetSectionByName");
        out.writeUTF(section);

        contexts.put(new MessageContext(
                "PlayerBalancer",
                "GetSectionByName",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readUTF()));

        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
        return true;
    }

    /**
     * Returns section as object in consumer by selected server name in bungeecord network
     * @param server Selected server
     * @param consumer Consumer #lambda
     * @return If true action has been successfully
     */
    public boolean getSectionByServer(String server, Consumer<String> consumer) {
        Player player = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
        if (player == null) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetSectionByServer");
        out.writeUTF(server);

        contexts.put(new MessageContext(
                "PlayerBalancer",
                "GetSectionByServer",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readUTF()));

        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
        return true;
    }

    /**
     * Returns section of selected player
     * @param player Selected player
     * @param consumer Consumer #lambda
     */
    public void getSectionOfPlayer(Player player, Consumer<String> consumer) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetSectionOfPlayer");
        out.writeUTF(player.getName());

        contexts.put(new MessageContext(
                "PlayerBalancer",
                "GetSectionOfPlayer",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readUTF()));

        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
    }

    /**
     * Returns status of selected server as object
     * @param server Selected server in bungeecord
     * @param consumer Consumer #lambda
     * @return If true action has been successfully
     */
    public boolean getServerStatus(String server, Consumer<String> consumer) {
        Player player = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
        if (player == null) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServerStatus");
        out.writeUTF(player.getName());

        contexts.put(new MessageContext(
                "PlayerBalancer",
                "GetServerStatus",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readUTF()));

        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
        return true;
    }

    /**
     * Returns player count as object in selected section ex. bedwars, lobby etc.
     * @param section Selected section
     * @param consumer Consumer #lambda
     * @return If true action has been successfully
     */
    public boolean getSectionPlayerCount(String section, Consumer<Integer> consumer) {
        Player player = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
        if (player == null) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetSectionPlayerCount");
        out.writeUTF(section);

        contexts.put(new MessageContext(
                "PlayerBalancer",
                "GetSectionPlayerCount",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readInt()));

        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
        return true;
    }

    /**
     * Remove bypass on connection from selected player
     * @param player Selected player
     */
    public void clearPlayerBypass(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ClearPlayerBypass");
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
    }

    /**
     * Sets player bypass on connection on servers
     * @param player Selected player
     */
    public void setPlayerBypass(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("SetPlayerBypass");
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
    }

    /**
     * Force connect on selected server.
     * This ignore permission and status of server in MOTD.
     * @param player Selected player
     * @param server Selected server as string from bungeecord
     */
    public void bypassConnect(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("BypassConnect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
    }

    /**
     * Remove status override
     * @param server Selected server
     * @return If true action has been successfully
     */
    public boolean clearStatusOverride(String server) {
        Player player = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
        if (player == null) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ClearStatusOverride");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());

        return true;
    }

    /**
     * Force set server status in network (fake value)
     * @param server Selected server
     * @param status Boolean if is online/offline
     * @return If true action has been successfully
     */
    public boolean setStatusOverride(String server, boolean status) {
        Player player = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
        if (player == null) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("SetStatusOverride");
        out.writeUTF(server);
        out.writeBoolean(status);
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());

        return true;
    }

    private final class MessageContext {
        private final String channel;
        private final String subchannel;
        private final UUID player;

        public MessageContext(String channel, String subchannel, UUID player) {
            this.channel = channel;
            this.subchannel = subchannel;
            this.player = player;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MessageContext that = (MessageContext) o;

            if (channel != null ? !channel.equals(that.channel) : that.channel != null) return false;
            if (subchannel != null ? !subchannel.equals(that.subchannel) : that.subchannel != null) return false;
            return player != null ? player.equals(that.player) : that.player == null;
        }

        @Override
        public int hashCode() {
            int result = channel != null ? channel.hashCode() : 0;
            result = 31 * result + (subchannel != null ? subchannel.hashCode() : 0);
            result = 31 * result + (player != null ? player.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "MessageContext{" +
                    "channel='" + channel + '\'' +
                    ", subchannel='" + subchannel + '\'' +
                    ", player=" + player +
                    '}';
        }
    }
}
