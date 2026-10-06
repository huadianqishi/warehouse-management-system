package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作记录实体类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
@TableName("operation_log")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 操作用户ID
     */
    private Integer userId;

    /**
     * 操作类型ID（外键关联operation_type表）
     */
    private Integer operationTypeId;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operationTime;

    /**
     * 操作状态（成功/失败）
     */
    private String operationStatus;

    /**
     * 被操作的表名（如无则为NULL）
     */
    private String targetTable;

    /**
     * 被操作记录ID（如无则为NULL）
     */
    private Integer targetId;

    /**
     * 操作详情描述
     */
    private String description;
}

