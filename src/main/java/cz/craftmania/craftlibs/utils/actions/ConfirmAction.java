package cz.craftmania.craftlibs.utils.actions;

import cz.craftmania.craftlibs.utils.Log;
import cz.craftmania.craftlibs.utils.PlayerRunnable;
import cz.craftmania.craftlibs.utils.StringUtils;
import cz.craftmania.craftlibs.utils.task.RunnableHelper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ConfirmAction implements Listener {

    // ID : Action
    public static List<Action> actionList = new ArrayList<>();

    public static Optional<Action> getAction(Player player, String identifier) {
        return actionList.stream().filter(action -> action.getIdentifier().equals(identifier) && action.getPlayer().getName().equals(player.getName())).findFirst();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        for (Action action : actionList) {
            if (action.getPlayer().getUniqueId() == event.getPlayer().getUniqueId()) {
                Log.info(event.getPlayer().getName() + " left while ConfirmAction was handled. (identifier: " + action.getIdentifier()+ ").");
            }
        }
    }

    public static class Builder {

        private final Action action = new Action();

        public Builder setPlayer(Player player) {
            this.action.setPlayer(player);
            return this;
        }

        public Builder setRunnable(PlayerRunnable runnable) {
            this.action.setPlayerRunnable(runnable);
            return this;
        }

        public Builder setExpireRunnable(PlayerRunnable runnable) {
            this.action.setExpireRunnable(runnable);
            return this;
        }

        private Builder addComponents(BaseComponent... textComponents) {
            List<BaseComponent> list = this.action.getTextComponents();
            list.addAll(Arrays.asList(textComponents));
            this.action.setTextComponents(list);
            return this;
        }

        public Builder addComponent(Function<Action, BaseComponent> fun) {
            this.addComponents(fun.apply(this.action));
            return this;
        }

        public Builder setIdentifier(String identifier) {
            this.action.setIdentifier(identifier);
            return this;
        }

        public Builder generateIdentifier() throws PlayerNotSetException {
            if (this.action.getPlayer() == null) throw new PlayerNotSetException("Cannot generate identifier without player.");
            else this.action.setIdentifier(this.action.getPlayer().getName() + "_" + StringUtils.randomAlphanumeric(6));
            return this;
        }

        public Builder setDelay(Long delay) {
            this.action.setDelay(delay);
            return this;
        }

        public Action build() {
            ConfirmAction.actionList.add(this.action);
            return this.action;
        }
    }

    /**
     * Library that handles confirmable actions for administrators or players.
     * This object holds {@link PlayerRunnable} and {@link List} of {@link TextComponent} that are run and sent
     * when action is ran - this class implements {@link Runnable}. Each action has its
     * ID - long from 1 to 999. Action can be executed only once.
     */
    public static class Action implements Runnable {

        private String identifier;
        private Player player;
        private PlayerRunnable playerRunnable;
        private PlayerRunnable expireRunnable;
        private boolean finished = false;
        private List<BaseComponent> textComponents = new ArrayList<>();
        private long delay = 30L;
        
        @Override
        public void run() {
            if (identifier == null) try {
                throw new IdentifierNotSetException("Action must has set identifier.");
            } catch (IdentifierNotSetException e) {
                e.printStackTrace();
            }
            if (finished) return;
            Log.info("Running action (identifier: " + this.identifier + ") to " + this.player.getName() + ".");

            this.playerRunnable.run(this.player);

            finished = true;
            actionList.remove(this);
            Log.info("Action (identifier: " + this.identifier + "; player: " + this.player.getName() + ") ran successfully.");
        }

        public void sendTextComponents() throws IdentifierNotSetException {
            if (identifier == null) throw new IdentifierNotSetException("Action must has set identifier.");
            Log.info("Sending text components for action (identifier: " + this.identifier + ") to " + this.player.getName() + ".");
            for (BaseComponent textComponent : textComponents) {
                player.spigot().sendMessage(textComponent);
            }

            RunnableHelper.runTaskLaterAsynchronously(() -> {
                if (finished) return;
                if (this.expireRunnable == null) return;

                this.expireRunnable.run(this.player);
            }, delay * 20);
        }

        public String getConfirmationCommand() {
            return "confirmaction " + this.identifier;
        }

        public String getIdentifier() {
            return identifier;
        }

        public Player getPlayer() {
            return player;
        }

        public PlayerRunnable getPlayerRunnable() {
            return playerRunnable;
        }

        public PlayerRunnable getExpireRunnable() {
            return expireRunnable;
        }

        public boolean isFinished() {
            return finished;
        }

        public List<BaseComponent> getTextComponents() {
            return textComponents;
        }

        public long getDelay() {
            return delay;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public void setPlayer(Player player) {
            this.player = player;
        }

        public void setPlayerRunnable(PlayerRunnable playerRunnable) {
            this.playerRunnable = playerRunnable;
        }

        public void setExpireRunnable(PlayerRunnable expireRunnable) {
            this.expireRunnable = expireRunnable;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }

        public void setTextComponents(List<BaseComponent> textComponents) {
            this.textComponents = textComponents;
        }

        public void setDelay(long delay) {
            this.delay = delay;
        }
    }
}
