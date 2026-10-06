package com.warehouse.controller;

import com.warehouse.common.Result;
import com.warehouse.entity.FinanceDetail;
import com.warehouse.service.FinanceDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * 费用明细控制器
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@RestController
@RequestMapping("/finance-detail")
public class FinanceDetailController {

    @Autowired
    private FinanceDetailService financeDetailService;

    /**
     * 添加费用明细（通用接口，需指定direction）
     * direction: 0-支出（燃油费、高速费等）, 1-收入（运费）
     */
    @PostMapping("/add")
    public Result<String> addFinanceDetail(@RequestBody FinanceDetail detail, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        financeDetailService.addFinanceDetail(detail, userId);
        return Result.success("添加费用明细成功");
    }

    /**
     * 录入运费收入（direction=1）
     */
    @PostMapping("/add-revenue")
    public Result<String> addRevenue(@RequestParam Integer financeId,
                                    @RequestParam String itemName,
                                    @RequestParam BigDecimal amount,
                                    @RequestParam(required = false) String remark,
                                    HttpServletRequest request) {
        FinanceDetail detail = new FinanceDetail();
        detail.setFinanceId(financeId);
        detail.setItemName(itemName);
        detail.setAmount(amount);
        detail.setDirection(1); // 收入
        detail.setRemark(remark);
        Integer userId = (Integer) request.getAttribute("userId");
        financeDetailService.addFinanceDetail(detail, userId);
        return Result.success("运费收入录入成功");
    }

    /**
     * 录入费用支出（direction=0）
     * itemName 如：燃油费、高速费、过桥费
     */
    @PostMapping("/add-cost")
    public Result<String> addCost(@RequestParam Integer financeId,
                                  @RequestParam String itemName,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam(required = false) String remark,
                                  HttpServletRequest request) {
        FinanceDetail detail = new FinanceDetail();
        detail.setFinanceId(financeId);
        detail.setItemName(itemName);
        detail.setAmount(amount);
        detail.setDirection(0); // 支出
        detail.setRemark(remark);
        Integer userId = (Integer) request.getAttribute("userId");
        financeDetailService.addFinanceDetail(detail, userId);
        return Result.success("费用支出录入成功");
    }

    /**
     * 删除费用明细
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteFinanceDetail(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        financeDetailService.deleteFinanceDetail(id, userId);
        return Result.success("删除费用明细成功");
    }

    /**
     * 查询费用明细列表
     */
    @GetMapping("/list/{financeId}")
    public Result<List<FinanceDetail>> getDetailsByFinanceId(@PathVariable Integer financeId) {
        return Result.success(financeDetailService.getDetailsByFinanceId(financeId));
    }
}
