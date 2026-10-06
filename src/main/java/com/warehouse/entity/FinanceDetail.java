package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 费用明细实体类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
@TableName("finance_detail")
public class FinanceDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联费用主表ID
     */
    private Integer financeId;

    /**
     * 项目名称（如：高速费、燃油费、客户运费）
     */
    private String itemName;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 收支方向：0-支出, 1-收入
     */
    private Integer direction;

    /**
     * 备注
     */
    private String remark;
}
