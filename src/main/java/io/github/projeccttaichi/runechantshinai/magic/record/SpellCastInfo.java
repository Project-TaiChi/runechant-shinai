package io.github.projeccttaichi.runechantshinai.magic.record;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record SpellCastInfo(
        Vec3 startPos,
        Vec3 direction,
        @Nullable
        Entity targetEntity,
        @Nullable
        Vec3 targetPos
) {
}
