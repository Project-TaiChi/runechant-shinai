package io.github.projeccttaichi.runechantshinai.capability;

import io.github.projeccttaichi.runechantshinai.config.ModConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class PlayerManaImpl implements PlayerMana {
    private int mana;
    private int maxMana;
    private int rechargeRate;
    private int tickCounter = 0;

    public PlayerManaImpl(Player player) {
//        this.maxMana = ModConfig.DEFAULT_MAX_MANA.get();
//        this.mana = this.maxMana;
//        this.rechargeRate = ModConfig.DEFAULT_MANA_RECHARGE_RATE.get();
    }

    @Override
    public int getMana() {
        return mana;
    }

    @Override
    public int getMaxMana() {
        return maxMana;
    }

    @Override
    public void setMana(int mana) {
        this.mana = Math.max(0, Math.min(mana, maxMana));
    }

    @Override
    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
        if (this.mana > maxMana) {
            this.mana = maxMana;
        }
    }

    @Override
    public void addMana(int mana) {
        this.mana = Math.min(this.mana + mana, maxMana);
    }

    @Override
    public boolean consumeMana(int mana) {
        if (this.mana >= mana) {
            this.mana -= mana;
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        tickCounter++;
        
        // 每20tick（约1秒）恢复法力值
        if (tickCounter >= 20) {
            addMana(rechargeRate);
            tickCounter = 0;
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Mana", mana);
        tag.putInt("MaxMana", maxMana);
        tag.putInt("RechargeRate", rechargeRate);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        mana = nbt.getInt("Mana");
        maxMana = nbt.getInt("MaxMana");
        rechargeRate = nbt.getInt("RechargeRate");
    }
}
