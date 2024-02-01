package cn.itedus.lottery.domain.strategy.service.algorithm.impl;

import cn.itedus.lottery.domain.strategy.service.algorithm.BaseAlgorithm;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;

/**
 * @description: 单项随机概率抽奖，抽到一个已经排掉的奖品则未中奖
 * @author：Favor
 * @date: 2024/2/1
 */
@Component("singleRateRandomDrawAlgorithm")
public class SingleRateRandomDrawAlgorithm extends BaseAlgorithm {

    @Override
    public String randomDraw(Long strategyId, List<String> excludeAwardIds) {

        // 获取策略对应的元祖
        String[] rateTuple = super.rateTupleMap.get(strategyId);
        // assert 是 Java 中的关键字，用于进行断言检查。当一个 assert 语句被执行时，会对它后面的布尔表达式进行求值。
        // 如果该表达式为 true，则程序继续执行；如果为 false，则会抛出一个 AssertionError 异常，并终止程序的执行。
        assert rateTuple != null;

        // 随机索引[1,100]
        int randomVal = new SecureRandom().nextInt(100) + 1;
        int idx = super.hashIdx(randomVal);

        // 返回结果
        String awardId = rateTuple[idx];

        // 如果中奖ID命中排除奖品列表，则返回NULL
        if (excludeAwardIds.contains(awardId)) {
            return null;
        }

        return awardId;
    }

}
