package io.github.projeccttaichi.runechantshinai.capability;

import net.minecraft.nbt.CompoundTag;

public interface PlayerMana {
    int getMana();
    
    int getMaxMana();
    
    void setMana(int mana);
    
    void setMaxMana(int maxMana);
    
    void addMana(int mana);
    
    boolean consumeMana(int mana);
    
    void tick();
    
    CompoundTag serializeNBT();
    
    void deserializeNBT(CompoundTag nbt);
}
