package com.hn.market.common.utils;

import cn.hutool.core.util.StrUtil;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 *  #自定义分片规则类
 * @author majj
 * @create 2023-04-08 13:28
 */
@Component
public class ShardingTableKeyUtils implements PreciseShardingAlgorithm<String> {
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {
        String id = preciseShardingValue.getValue();
        int tableNo = 0;
        int num=0;
        if (StrUtil.isNotEmpty(id)){
            tableNo = Integer.valueOf(id) % 20;
        }else{
            return null;
        }
        for (String tableName : collection) {//循环表名已确定使用哪张表
            int len=tableName.length();
            if(48<=tableName.charAt(len-2) && tableName.charAt(len-2) <=57){
               num = Integer.valueOf(tableName.substring(len-2));
            }else{
                num = Integer.valueOf(tableName.substring(len-1));
            }

            if(num==tableNo){
                return tableName;//返回要插入的逻辑表
            }
        }
        return null;
    }
}
