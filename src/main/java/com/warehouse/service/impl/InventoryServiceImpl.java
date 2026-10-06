package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.entity.Product;
import com.warehouse.entity.Shelf;
import com.warehouse.entity.Warehouse;
import com.warehouse.entity.dto.InventoryDTO;
import com.warehouse.entity.dto.SlotConflictDTO;
import com.warehouse.entity.dto.SlotStatusDTO;
import com.warehouse.entity.dto.WarehouseUtilizationDTO;
import com.warehouse.mapper.ProductMapper;
import com.warehouse.mapper.ShelfMapper;
import com.warehouse.mapper.StockInRecordMapper;
import com.warehouse.mapper.WarehouseMapper;
import com.warehouse.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 库存服务实现类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private StockInRecordMapper stockInRecordMapper;

    @Autowired
    private ShelfMapper shelfMapper;

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public WarehouseUtilizationDTO getWarehouseUtilization(Integer warehouseId) {
        WarehouseUtilizationDTO dto = new WarehouseUtilizationDTO();
        dto.setWarehouseId(warehouseId);

        // 获取仓库信息
        Warehouse warehouse = warehouseMapper.selectById(warehouseId);
        if (warehouse == null) {
            return dto;
        }
        dto.setWarehouseNumber(warehouse.getWarehouseNumber());

        // 获取该仓库下的所有货架
        List<Shelf> shelves = shelfMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Shelf>()
                .eq("warehouse_id", warehouseId)
        );

        // 计算格口总数（所有货架的层数之和）
        int totalSlots = shelves.stream()
            .mapToInt(Shelf::getFloorCount)
            .sum();
        dto.setTotalSlots(totalSlots);

        // 计算已占用格口数
        int occupiedSlots = 0;
        for (Shelf shelf : shelves) {
            for (int floor = 1; floor <= shelf.getFloorCount(); floor++) {
                // 检查该格口是否有库存
                List<Map<String, Object>> products = stockInRecordMapper.getSlotAllProducts(
                    shelf.getId(), floor
                );
                if (products != null && !products.isEmpty()) {
                    // 检查是否有商品库存 > 0
                    boolean hasStock = products.stream()
                        .anyMatch(p -> {
                            Object qty = p.get("quantity");
                            return qty != null && ((Number) qty).intValue() > 0;
                        });
                    if (hasStock) {
                        occupiedSlots++;
                    }
                }
            }
        }

        dto.setOccupiedSlots(occupiedSlots);
        dto.setEmptySlots(totalSlots - occupiedSlots);

        // 计算利用率
        if (totalSlots > 0) {
            double utilizationRate = (occupiedSlots * 100.0) / totalSlots;
            dto.setUtilizationRate(Math.round(utilizationRate * 100.0) / 100.0);
        } else {
            dto.setUtilizationRate(0.0);
        }

        return dto;
    }

    @Override
    public List<SlotStatusDTO> getShelfSlotStatus(Integer shelfId) {
        List<SlotStatusDTO> result = new ArrayList<>();

        // 获取货架信息
        Shelf shelf = shelfMapper.selectById(shelfId);
        if (shelf == null) {
            return result;
        }

        // 遍历每一层
        for (int floor = 1; floor <= shelf.getFloorCount(); floor++) {
            SlotStatusDTO slot = new SlotStatusDTO();
            slot.setShelfId(shelfId);
            slot.setShelfNumber(shelf.getShelfNumber());
            slot.setWarehouseId(shelf.getWarehouseId());
            slot.setFloorNumber(floor);

            // 获取该格口的所有商品库存
            List<Map<String, Object>> products = stockInRecordMapper.getSlotAllProducts(
                shelfId, floor
            );

            if (products != null && !products.isEmpty()) {
                // 检查是否有库存 > 0 的商品
                Map<String, Object> occupiedProduct = products.stream()
                    .filter(p -> {
                        Object qty = p.get("quantity");
                        return qty != null && ((Number) qty).intValue() > 0;
                    })
                    .findFirst()
                    .orElse(null);

                if (occupiedProduct != null) {
                    slot.setStatus("OCCUPIED");
                    Integer productId = ((Number) occupiedProduct.get("product_id")).intValue();
                    slot.setProductId(productId);
                    slot.setQuantity(((Number) occupiedProduct.get("quantity")).intValue());

                    // 获取商品名称
                    Product product = productMapper.selectById(productId);
                    if (product != null) {
                        slot.setProductName(product.getName());
                    }
                } else {
                    slot.setStatus("EMPTY");
                    slot.setQuantity(0);
                }
            } else {
                slot.setStatus("EMPTY");
                slot.setQuantity(0);
            }

            result.add(slot);
        }

        return result;
    }

    @Override
    public List<SlotStatusDTO> getEmptySlots(Integer warehouseId) {
        List<SlotStatusDTO> result = new ArrayList<>();

        // 获取该仓库下的所有货架
        List<Shelf> shelves = shelfMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Shelf>()
                .eq("warehouse_id", warehouseId)
        );

        // 遍历所有货架和层数
        for (Shelf shelf : shelves) {
            for (int floor = 1; floor <= shelf.getFloorCount(); floor++) {
                // 检查该格口是否有库存
                List<Map<String, Object>> products = stockInRecordMapper.getSlotAllProducts(
                    shelf.getId(), floor
                );

                boolean isEmpty = true;
                if (products != null && !products.isEmpty()) {
                    // 检查是否有商品库存 > 0
                    boolean hasStock = products.stream()
                        .anyMatch(p -> {
                            Object qty = p.get("quantity");
                            return qty != null && ((Number) qty).intValue() > 0;
                        });
                    isEmpty = !hasStock;
                }

                if (isEmpty) {
                    SlotStatusDTO slot = new SlotStatusDTO();
                    slot.setShelfId(shelf.getId());
                    slot.setShelfNumber(shelf.getShelfNumber());
                    slot.setWarehouseId(warehouseId);
                    slot.setFloorNumber(floor);
                    slot.setStatus("EMPTY");
                    slot.setQuantity(0);
                    result.add(slot);
                }
            }
        }

        return result;
    }

    @Override
    public SlotConflictDTO checkSlotConflict(Integer shelfId, Integer floorNumber, Integer productId) {
        SlotConflictDTO dto = new SlotConflictDTO();
        dto.setHasConflict(false);

        // 获取该格口的所有商品库存
        List<Map<String, Object>> products = stockInRecordMapper.getSlotAllProducts(
            shelfId, floorNumber
        );

        if (products != null && !products.isEmpty()) {
            // 查找有库存的商品（quantity > 0）
            Map<String, Object> occupiedProduct = products.stream()
                .filter(p -> {
                    Object qty = p.get("quantity");
                    return qty != null && ((Number) qty).intValue() > 0;
                })
                .findFirst()
                .orElse(null);

            if (occupiedProduct != null) {
                Integer occupiedProductId = ((Number) occupiedProduct.get("product_id")).intValue();
                Integer currentQuantity = ((Number) occupiedProduct.get("quantity")).intValue();

                // 如果格口已有商品，且不是同一个商品，则存在冲突
                if (!occupiedProductId.equals(productId)) {
                    dto.setHasConflict(true);
                    dto.setConflictProductId(occupiedProductId);
                    dto.setCurrentQuantity(currentQuantity);

                    // 获取商品名称
                    Product product = productMapper.selectById(occupiedProductId);
                    if (product != null) {
                        dto.setConflictProductName(product.getName());
                        dto.setMessage(String.format("警告！目标格口已存在商品【%s】，确认继续放入吗？", product.getName()));
                    } else {
                        dto.setMessage("警告！目标格口已被占用，确认继续放入吗？");
                    }
                } else {
                    // 同一商品，可以累加库存
                    dto.setCurrentQuantity(currentQuantity);
                    dto.setMessage("该格口已有相同商品，将累加库存");
                }
            }
        }

        return dto;
    }

    @Override
    public Integer getSlotCurrentQuantity(Integer shelfId, Integer floorNumber, Integer productId) {
        Integer quantity = stockInRecordMapper.getSlotCurrentQuantity(shelfId, floorNumber, productId);
        return quantity != null ? quantity : 0;
    }

    @Override
    public IPage<InventoryDTO> getInventoryPage(Integer current, Integer size, String keyword, Integer warehouseId) {
        // 获取所有库存数据
        List<InventoryDTO> allInventory = getInventoryList(keyword, warehouseId);
        
        // 手动分页
        int start = (current - 1) * size;
        int end = Math.min(start + size, allInventory.size());
        List<InventoryDTO> pageData = allInventory.subList(start, end);
        
        // 创建分页对象
        Page<InventoryDTO> page = new Page<>(current, size);
        page.setRecords(pageData);
        page.setTotal(allInventory.size());
        
        return page;
    }

    @Override
    public List<InventoryDTO> getInventoryList(String keyword, Integer warehouseId) {
        List<InventoryDTO> result = new ArrayList<>();
        
        // 获取所有货架
        List<Shelf> shelves;
        if (warehouseId != null) {
            shelves = shelfMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Shelf>()
                    .eq("warehouse_id", warehouseId)
            );
        } else {
            shelves = shelfMapper.selectList(null);
        }
        
        // 获取所有商品（用于搜索）
        List<Product> allProducts = productMapper.selectList(null);
        Map<Integer, Product> productMap = allProducts.stream()
            .collect(Collectors.toMap(Product::getId, p -> p));
        
        // 遍历所有货架和层数，查找有库存的格口
        for (Shelf shelf : shelves) {
            Warehouse warehouse = warehouseMapper.selectById(shelf.getWarehouseId());
            
            for (int floor = 1; floor <= shelf.getFloorCount(); floor++) {
                // 获取该格口的所有商品库存
                List<Map<String, Object>> products = stockInRecordMapper.getSlotAllProducts(
                    shelf.getId(), floor
                );
                
                if (products != null && !products.isEmpty()) {
                    // 调试：打印查询结果
                    System.out.println("格口查询结果 - 货架ID: " + shelf.getId() + ", 层数: " + floor + ", 商品数量: " + products.size());
                    for (Map<String, Object> productData : products) {
                        // 调试：打印所有键值对
                        System.out.println("Product data keys: " + productData.keySet());
                        System.out.println("Product data: " + productData);
                        
                        // 尝试多种可能的字段名（MySQL可能返回不同的大小写格式）
                        Object qtyObj = productData.get("quantity");
                        if (qtyObj == null) {
                            qtyObj = productData.get("QUANTITY");
                        }
                        if (qtyObj == null) {
                            qtyObj = productData.get("Quantity");
                        }
                        
                        if (qtyObj != null) {
                            Integer quantity = ((Number) qtyObj).intValue();
                            if (quantity > 0) {
                                // 尝试多种可能的字段名
                                Object productIdObj = productData.get("product_id");
                                if (productIdObj == null) {
                                    productIdObj = productData.get("PRODUCT_ID");
                                }
                                if (productIdObj == null) {
                                    productIdObj = productData.get("Product_Id");
                                }
                                
                                if (productIdObj == null) {
                                    // 如果还是找不到，尝试获取第一个非quantity的数值字段
                                    for (Map.Entry<String, Object> entry : productData.entrySet()) {
                                        if (!entry.getKey().equalsIgnoreCase("quantity") && entry.getValue() instanceof Number) {
                                            productIdObj = entry.getValue();
                                            break;
                                        }
                                    }
                                }
                                
                                if (productIdObj == null) {
                                    continue; // 跳过无法解析的记录
                                }
                                
                                Integer productId = ((Number) productIdObj).intValue();
                                Product product = productMap.get(productId);
                                
                                if (product != null) {
                                    // 检查是否匹配搜索关键词
                                    boolean matches = true;
                                    if (keyword != null && !keyword.trim().isEmpty()) {
                                        String lowerKeyword = keyword.toLowerCase();
                                        matches = product.getName().toLowerCase().contains(lowerKeyword) ||
                                                 shelf.getShelfNumber().toLowerCase().contains(lowerKeyword);
                                    }
                                    
                                    if (matches) {
                                        InventoryDTO dto = new InventoryDTO();
                                        dto.setProductId(productId);
                                        dto.setProductName(product.getName());
                                        dto.setProductType(product.getType());
                                        dto.setShelfId(shelf.getId());
                                        dto.setShelfNumber(shelf.getShelfNumber());
                                        dto.setFloorNumber(floor);
                                        dto.setQuantity(quantity);
                                        dto.setWarehouseId(shelf.getWarehouseId());
                                        if (warehouse != null) {
                                            dto.setWarehouseNumber(warehouse.getWarehouseNumber());
                                        }
                                        // 设置安全库存和低库存预警
                                        dto.setSafetyStock(product.getSafetyStock());
                                        if (product.getSafetyStock() != null && product.getSafetyStock() > 0) {
                                            dto.setLowStockWarning(quantity < product.getSafetyStock());
                                        } else {
                                            dto.setLowStockWarning(false);
                                        }
                                        
                                        // 获取该格口的总重量和总体积
                                        Map<String, Object> weightVolume = stockInRecordMapper.getSlotWeightAndVolume(
                                            shelf.getId(), floor, productId
                                        );
                                        if (weightVolume != null) {
                                            Object weightObj = weightVolume.get("total_weight");
                                            Object volumeObj = weightVolume.get("total_volume");
                                            if (weightObj != null) {
                                                dto.setTotalWeight(new BigDecimal(weightObj.toString()));
                                            }
                                            if (volumeObj != null) {
                                                dto.setTotalVolume(new BigDecimal(volumeObj.toString()));
                                            }
                                        }
                                        
                                        result.add(dto);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return result;
    }

    @Override
    public Integer getProductAvailableStock(Integer productId) {
        Integer available = stockInRecordMapper.getTotalAvailableStock(productId);
        return available != null ? Math.max(0, available) : 0;
    }

    @Override
    public List<InventoryDTO> getProductLocations(Integer productId) {
        return getInventoryList(null, null).stream()
                .filter(dto -> dto.getProductId().equals(productId) && dto.getQuantity() > 0)
                .collect(Collectors.toList());
    }
}

