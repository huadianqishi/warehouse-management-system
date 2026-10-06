package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作记录Mapper接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}

