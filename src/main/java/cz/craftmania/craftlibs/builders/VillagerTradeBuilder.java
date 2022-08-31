package cz.craftmania.craftlibs.builders;

import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;

public class VillagerTradeBuilder {

    private ArrayList<MerchantRecipe> mrs;
    private boolean expreward;
    private int maxUses;
    private int uses;

    public VillagerTradeBuilder() {
        this.mrs = new ArrayList<MerchantRecipe>();
        this.expreward = true;
        this.maxUses = Integer.MAX_VALUE;
        this.uses = 0;
    }

    /**
     * Set if Villager or Merchant can receive experience for trading
     *
     * @param reward If true, villager will receive experience (1.14+)
     * @return
     */
    public VillagerTradeBuilder setExperienceReward(final boolean reward) {
        this.expreward = reward;
        return this;
    }

    public VillagerTradeBuilder setMaxUses(final int maxUses) {
        this.maxUses = maxUses;
        return this;
    }

    public VillagerTradeBuilder setUses(final int uses) {
        this.uses = uses;
        return this;
    }

    /**
     * Remove all trades that actually villager has
     *
     * @param v Villager
     */
    public void clearTrades(final Villager v) {
        this.mrs.clear();
        v.setRecipes(this.mrs);
    }

    /**
     * Remove all trades that actually merchant has
     *
     * @param m Merchant
     */
    public void clearTrades(final Merchant m) {
        this.mrs.clear();
        m.setRecipes(this.mrs);
    }

    /**
     * Add new trade into selected Merchant (Villager)
     *
     * @param ingredient First item in row
     * @param result     Final item
     */
    public void addTrade(final ItemStack ingredient, final ItemStack result) {
        final MerchantRecipe mr = new MerchantRecipe(result, this.maxUses);
        mr.addIngredient(ingredient);
        mr.setUses(this.uses);
        mr.setExperienceReward(this.expreward);
        this.mrs.add(mr);
    }

    /**
     * Add new trade into selected Merchant (Villager) with two ingredients
     *
     * @param ingredient1 First item in row
     * @param ingredient2 Second item in row
     * @param result      Final item
     */
    public void addTrade(final ItemStack ingredient1, final ItemStack ingredient2, final ItemStack result) {
        final MerchantRecipe mr = new MerchantRecipe(result, this.maxUses);
        mr.addIngredient(ingredient1);
        mr.addIngredient(ingredient2);
        mr.setUses(this.uses);
        mr.setExperienceReward(this.expreward);
        this.mrs.add(mr);
    }

    /**
     * Add created trades into selected Villager
     *
     * @param v Villager
     */
    public void setTrades(final Villager v) {
        v.setRecipes(this.mrs);
    }

    /**
     * Add created trades into selected Merchant
     *
     * @param m Merchant
     */
    public void setTrades(final Merchant m) {
        m.setRecipes(this.mrs);
    }


}
