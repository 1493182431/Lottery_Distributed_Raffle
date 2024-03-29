package cn.itedus.lottery.domain.strategy.service.algorithm;

import cn.itedus.lottery.domain.strategy.model.vo.AwardRateInfo;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @description: 共用的算法逻辑抽象类
 * @author：Favor
 * @date: 2024/2/1
 */
public abstract class BaseAlgorithm implements IDrawAlgorithm {

    /** 斐波那契散列增量，逻辑：黄金分割点：(√5 - 1) / 2 = 0.6180339887，Math.pow(2, 32) * 0.6180339887 = 0x61c88647 */
    private final int HASH_INCREMENT = 0x61c88647;

    /** 数组初始化长度 128，保证数据填充时不发生碰撞的最小初始化值 */
    private final int RATE_TUPLE_LENGTH = 128;

    /** 存放概率与奖品对应的散列结果，strategyId -> rateTuple */
    protected Map<Long, String[]> rateTupleMap = new ConcurrentHashMap<>();

    /** 奖品区间概率值，strategyId -> [awardId->begin、awardId->end] */
    protected Map<Long, List<AwardRateInfo>> awardRateInfoMap = new ConcurrentHashMap<>();

    @Override
    public void initRateTuple(Long strategyId, List<AwardRateInfo> awardRateInfoList) {
        // 保存奖品概率信息
        awardRateInfoMap.put(strategyId, awardRateInfoList);

        // 获取当前 strategyId 对应的概率与奖品散列结果数组，如果该数组不存在，
        // 则创建一个新的数组并将其存入 rateTupleMap 中，最后将该数组赋值给变量 rateTuple
        String[] rateTuple;
        if (rateTupleMap.containsKey(strategyId)) {
            rateTuple = rateTupleMap.get(strategyId);
        } else {
            rateTuple = new String[RATE_TUPLE_LENGTH];
            rateTupleMap.put(strategyId, rateTuple);
        }

        int cursorVal = 0;
        for (AwardRateInfo awardRateInfo : awardRateInfoList) {
            // 获取概率值并乘100
            int rateVal = awardRateInfo.getAwardRate().multiply(new BigDecimal(100)).intValue();

            // 循环填充概率范围值
            for (int i = cursorVal + 1; i <= (rateVal + cursorVal); i++) {
                rateTuple[hashIdx(i)] = awardRateInfo.getAwardId();
            }

            cursorVal += rateVal;

        }
    }

    @Override
    public boolean isExistRateTuple(Long strategyId) {
        return rateTupleMap.containsKey(strategyId);
    }

    /**
     * 斐波那契（Fibonacci）散列法，计算哈希索引下标值
     *
     * @param val 值
     * @return 索引
     */
    protected int hashIdx(int val) {
        int hashCode = val * HASH_INCREMENT + HASH_INCREMENT;
        return hashCode & (RATE_TUPLE_LENGTH - 1);
    }

    /**
     * 生成百位随机抽奖码
     *
     * @return 随机值
     */
    protected int generateSecureRandomIntCode(int bound){
        return new SecureRandom().nextInt(bound) + 1;
    }

}
