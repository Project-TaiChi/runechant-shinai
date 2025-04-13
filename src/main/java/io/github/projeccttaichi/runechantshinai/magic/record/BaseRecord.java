package io.github.projeccttaichi.runechantshinai.magic.record;

/**
 * 基础的秘禄类
 * 用于描述秘术的基础信息
 */
public class BaseRecord {
    protected String name;
    protected RecordType type;

    public BaseRecord(String name, RecordType type) {
        this.name = name;
        this.type = type;
    }


    // 魔力消耗
    protected float cost;

    // 强度（影响伤害等）
    protected float potency;

    // 速度（影响投掷物飞行速度等）
    protected float speed;

    // 持续时间（影响持法术持续时间）
    protected float duration;

    // 范围（影响范围性效果的范围）
    protected float range;

    // 施法延迟
    protected float delay;


    public RecordType getType() {
        return type;
    }
}
