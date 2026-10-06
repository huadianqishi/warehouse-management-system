-- 仓储管理系统示例数据脚本
-- 注意：此脚本需要在执行 init.sql 之后执行
-- 用于测试和查看系统功能

USE warehouse_db;

-- ========================================
-- 示例数据（用于测试和查看）
-- ========================================

-- -------------------------------------------------
-- 1. 用户数据（password 为 BCrypt 加密后的 "123456"）
-- 注意：driver_license 字段仅司机（role=3）需要填写
-- -------------------------------------------------
INSERT INTO user (username, password, real_name, gender, phone, id_card, role, driver_license) VALUES
('test_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '测试管理员', 1, '13800138000', '110101199001011234', 0, NULL),
('test_keeper', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '测试仓管员', 1, '13800138001', '110101199001011235', 1, NULL),
('test_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '测试用户', 0, '13800138002', '110101199001011236', 2, NULL),
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '张三', 1, '13900139000', '110101199002021234', 1, NULL),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '李四', 0, '13900139001', '110101199003031234', 2, NULL),
('driver_zhang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '司机张伟', 1, '13900139010', '310101199005051234', 3, 'B2-101010101'),
('driver_li', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '司机李强', 1, '13900139011', '440101199108082345', 3, 'A2-202020202'),
('driver_wang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '司机王磊', 1, '13900139012', '510101199203151234', 3, 'B2-303030303'),
('driver_zhao', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '司机赵勇', 1, '13900139013', '440103199406151234', 3, 'A2-404040404'),
('driver_sun', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '司机孙凯', 1, '13900139014', '310101199508201234', 3, 'B2-505050505'),
('driver_chen', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '司机陈亮', 0, '13900139015', '320101199602121234', 3, 'C1-606060606'),
('driver_wu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '司机吴涛', 1, '13900139016', '330101199703231234', 3, 'B2-707070707'),
('driver_lin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '司机林海', 1, '13900139017', '440301199809051234', 3, 'A2-808080808'),
('driver_huang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLN6', '司机黄飞', 1, '13900139018', '510101199910181234', 3, 'B2-909090909');

-- -------------------------------------------------
-- 2. 仓库数据
-- -------------------------------------------------
INSERT INTO warehouse (warehouse_number, address, manager_name, manager_phone) VALUES
('WH001', '北京市朝阳区仓储中心A栋', '王经理', '010-12345678'),
('WH002', '上海市浦东新区仓储中心B栋', '李经理', '021-87654321'),
('WH003', '广州市天河区仓储中心C栋', '张经理', '020-11223344'),
('WH004', '深圳市南山区仓储中心D栋', '刘经理', '0755-99887766');

-- -------------------------------------------------
-- 3. 货架数据
-- -------------------------------------------------
INSERT INTO shelf (shelf_number, floor_count, area_category, warehouse_id) VALUES
-- 仓库1的货架
('WH001-A001', 5, '文具区', 1),
('WH001-A002', 5, '文具区', 1),
('WH001-B001', 4, '电子产品区', 1),
('WH001-B002', 4, '电子产品区', 1),
('WH001-C001', 6, '日用品区', 1),
-- 仓库2的货架
('WH002-A001', 5, '服装区', 2),
('WH002-A002', 5, '服装区', 2),
('WH002-B001', 4, '食品区', 2),
('WH002-B002', 4, '食品区', 2),
-- 仓库3的货架
('WH003-A001', 6, '图书区', 3),
('WH003-B001', 5, '体育用品区', 3),
-- 仓库4的货架
('WH004-A001', 4, '家电区', 4),
('WH004-B001', 5, '家具区', 4);

-- -------------------------------------------------
-- 4. 商品数据
-- -------------------------------------------------
DELETE FROM product;
ALTER TABLE product AUTO_INCREMENT = 1;

INSERT INTO product (name, type, unit_price, safety_stock) VALUES
-- 文具类
('中性笔', '文具', 2.50, 100),
('笔记本', '文具', 5.00, 200),
('文件夹', '文具', 3.50, 150),
('订书机', '文具', 15.00, 50),
('橡皮擦', '文具', 1.00, 300),
-- 电子产品类
('鼠标', '电子产品', 35.00, 50),
('键盘', '电子产品', 120.00, 30),
('U盘', '电子产品', 45.00, 100),
('耳机', '电子产品', 89.00, 40),
('充电器', '电子产品', 25.00, 80),
-- 日用品类
('洗发水', '日用品', 28.00, 200),
('牙膏', '日用品', 12.00, 300),
('纸巾', '日用品', 8.50, 500),
('洗衣液', '日用品', 35.00, 150),
-- 食品类
('方便面', '食品', 5.50, 1000),
('矿泉水', '食品', 2.00, 2000),
('饼干', '食品', 8.00, 500),
-- 其他类
('图书-Java编程', '图书', 59.00, 100),
('图书-Python入门', '图书', 49.00, 120),
('运动鞋', '体育用品', 299.00, 50);

-- -------------------------------------------------
-- 5. 操作记录数据
-- -------------------------------------------------
INSERT INTO operation_log (user_id, operation_type_id, operation_time, operation_status, target_table, target_id, description) VALUES
(1, 5,  NOW(),                                    '成功', 'warehouse', 1, '添加仓库：WH001'),
(1, 11, NOW(),                                    '成功', 'product',   1, '添加商品：中性笔'),
(1, 8,  NOW(),                                    '成功', 'shelf',     1, '添加货架：WH001-A001'),
(2, 11, NOW(),                                    '成功', 'product',   2, '添加商品：笔记本'),
(2, 8,  NOW(),                                    '成功', 'shelf',     2, '添加货架：WH001-A002'),
(1, 7,  DATE_SUB(NOW(), INTERVAL 1 DAY),         '成功', 'warehouse', 1, '修改仓库信息：WH001'),
(2, 11, DATE_SUB(NOW(), INTERVAL 2 DAY),         '成功', 'product',   3, '添加商品：文件夹'),
(1, 4,  DATE_SUB(NOW(), INTERVAL 3 DAY),         '成功', 'user',      1, '用户登录系统'),
(6, 19, DATE_SUB(NOW(), INTERVAL 1 DAY),         '成功', 'vehicle',   1, '添加车辆：京A·12345'),
(7, 19, DATE_SUB(NOW(), INTERVAL 1 DAY),         '成功', 'vehicle',   2, '添加车辆：沪B·67890'),
(6, 23, DATE_SUB(NOW(), INTERVAL 2 DAY),         '成功', 'logistics_order', 1, '创建物流订单'),
(7, 24, DATE_SUB(NOW(), INTERVAL 2 DAY),         '成功', 'logistics_order', 1, '指派物流');

-- -------------------------------------------------
-- 6. 入库记录数据
-- -------------------------------------------------
DELETE FROM stock_in_record;

INSERT INTO stock_in_record (product_id, quantity, storage_time, shelf_id, floor_number, weight, volume, operator_id, created_at) VALUES
-- 文具入库
(1, 150, NOW(),                                    1, 1, 2.500, 0.010000, 2, NOW()),
(2, 200, NOW(),                                    1, 2, 5.000, 0.020000, 2, NOW()),
(3, 100, NOW(),                                    2, 1, 3.500, 0.015000, 2, NOW()),
(4,  50, NOW(),                                    2, 2, 1.500, 0.005000, 2, NOW()),
(5, 300, NOW(),                                    5, 1, 1.000, 0.001000, 2, NOW()),
-- 电子产品入库
(6,  60, DATE_SUB(NOW(), INTERVAL 1 DAY),         3, 1, 0.600, 0.000500, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(7,  30, DATE_SUB(NOW(), INTERVAL 1 DAY),         3, 2, 2.400, 0.002000, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(8, 100, DATE_SUB(NOW(), INTERVAL 2 DAY),         4, 1, 0.500, 0.000100, 2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(9,  40, DATE_SUB(NOW(), INTERVAL 2 DAY),         4, 2, 1.200, 0.001200, 2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(10, 80, DATE_SUB(NOW(), INTERVAL 3 DAY),         3, 3, 0.800, 0.000800, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
-- 日用品入库
(11, 250, DATE_SUB(NOW(), INTERVAL 3 DAY),         6, 1, 12.500, 0.010000, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(12, 350, DATE_SUB(NOW(), INTERVAL 3 DAY),         6, 2, 7.000, 0.005000, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(13, 500, DATE_SUB(NOW(), INTERVAL 4 DAY),         7, 1, 2.500, 0.008000, 2, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(14, 150, DATE_SUB(NOW(), INTERVAL 4 DAY),         7, 2, 12.000, 0.012000, 2, DATE_SUB(NOW(), INTERVAL 4 DAY)),
-- 食品入库
(15, 1200, DATE_SUB(NOW(), INTERVAL 5 DAY),       8, 1, 12.000, 0.002000, 2, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(16, 2500, DATE_SUB(NOW(), INTERVAL 5 DAY),        8, 2, 12.500, 0.001000, 2, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(17,  500, DATE_SUB(NOW(), INTERVAL 6 DAY),        8, 3, 5.000, 0.003000, 2, DATE_SUB(NOW(), INTERVAL 6 DAY)),
-- 其他类入库
(18, 120, DATE_SUB(NOW(), INTERVAL 6 DAY),         10, 1, 7.200, 0.003600, 2, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(19, 150, DATE_SUB(NOW(), INTERVAL 6 DAY),         10, 2, 7.500, 0.004500, 2, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(20,  40, DATE_SUB(NOW(), INTERVAL 7 DAY),         11, 1, 48.000, 0.008000, 2, DATE_SUB(NOW(), INTERVAL 7 DAY));

-- ========================================
-- 物流管理模块示例数据
-- ========================================

-- -------------------------------------------------
-- 7. 车辆数据（category: 0-微面, 1-小货, 2-中货, 3-大货）
-- （status: 0-空闲, 1-运输中, 2-维修）
-- -------------------------------------------------
INSERT INTO vehicle (plate_number, category, max_weight, max_volume, status, current_driver_id, remark) VALUES
('京A·12345', 3, 10.00, 50.00, 0, 6,  '载重10吨大货车，适合长途运输'),
('沪B·67890', 2,  5.00, 25.00, 1, 7,  '中型货车，市内配送'),
('粤B·11111', 1,  2.00, 10.00, 0, NULL, '小货车，灵活便捷'),
('京C·22222', 3, 10.00, 50.00, 2, NULL, '大货车，维修中'),
('津D·33333', 0,  0.50,  3.00, 0, 8,  '微型面包车，同城快递'),
('京E·44444', 3, 10.00, 50.00, 0, 9,  '载重10吨大货车，长途专线'),
('沪F·55555', 1,  2.00, 10.00, 0, 10, '小货车，市内配送'),
('粤C·66666', 2,  5.00, 25.00, 1, 11, '中型货车，跨城运输'),
('京G·77777', 3, 10.00, 50.00, 0, 12, '大货车，物流干线'),
('津H·88888', 0,  0.50,  3.00, 0, 13, '微型面包车，同城快递'),
('沪J·99999', 1,  2.00, 10.00, 0, NULL, '小货车，灵活便捷'),
('粤D·10101', 2,  5.00, 25.00, 0, NULL, '中型货车，仓库调拨'),
('京H·20202', 3, 10.00, 50.00, 1, NULL, '大货车，运输中'),
('京J·30303', 3, 10.00, 50.00, 0, 14, '大货车，省际专线');

-- -------------------------------------------------
-- 8. 地址簿数据（type: 0-发货人/仓库, 1-收货人/客户）
-- -------------------------------------------------
-- 发货地址（仓库端）
INSERT INTO address_book (contact_name, phone, province, city, district, detail_address, type) VALUES
('仓储中心-A栋', '010-12345678', '北京市', '北京市', '朝阳区', '东三环中路甲18号A栋', 0),
('仓储中心-B栋', '021-87654321', '上海市', '上海市', '浦东新区', '世纪大道888号B栋', 0),
('仓储中心-C栋', '020-11223344', '广东省', '广州市', '天河区', '天河路123号C栋', 0);

-- 收货地址（客户）
INSERT INTO address_book (contact_name, phone, province, city, district, detail_address, type) VALUES
('张先生', '13800001111', '北京市', '北京市', '海淀区', '中关村大街1号智汇大厦1001室', 1),
('李女士', '13900002222', '上海市', '上海市', '静安区', '南京西路288号宏门广场2001室', 1),
('王先生', '13700003333', '广东省', '深圳市', '南山区', '科技园南区高新南七道R2-B栋5楼', 1),
('刘女士', '13600004444', '广东省', '广州市', '天河区', '体育西路123号天河城购物中心3楼', 1),
('赵先生', '13500005555', '浙江省', '杭州市', '西湖区', '文三路388号钱江科技大厦1201室', 1),
('陈女士', '13300006666', '北京市', '北京市', '朝阳区', '建国路88号SOHO现代城A座601室', 1),
('周先生', '13200007777', '广东省', '深圳市', '福田区', '福华路购物公园C座1801室', 1),
('吴小姐', '13100008888', '广东省', '广州市', '天河区', '天河北路183号大都会广场802室', 1),
('郑先生', '13000009999', '江苏省', '南京市', '鼓楼区', '中山北路200号商业中心1201室', 1),
('孙女士', '12900001111', '四川省', '成都市', '锦江区', '春熙路88号IFS国际金融中心18楼', 1),
('马先生', '12800002222', '湖北省', '武汉市', '武昌区', '中南路7号中商广场A座2201室', 1),
('胡小姐', '12700003333', '福建省', '福州市', '鼓楼区', '五一路华城国际大厦902室', 1),
('朱先生', '12600004444', '山东省', '济南市', '历下区', '泉城路188号恒隆广场1301室', 1),
('林先生', '12500005555', '辽宁省', '沈阳市', '和平区', '青年大街288号华润大厦1501室', 1),
('何女士', '12400006666', '河南省', '郑州市', '金水区', '花园路118号正弘城801室', 1),
('高先生', '12300007777', '陕西省', '西安市', '雁塔区', '科技路32号高新国际商务中心601室', 1),
('卢小姐', '12200008888', '重庆市', '重庆市', '渝中区', '解放碑步行街国泰广场702室', 1),
('田先生', '12100009999', '天津市', '天津市', '和平区', '南京路189号恒隆广场902室', 1),
('崔先生', '12000001111', '河北省', '石家庄市', '长安区', '中山路388号勒泰中心1101室', 1),
('梁小姐', '11900002222', '湖南省', '长沙市', '芙蓉区', '五一大道838号平和堂商务楼702室', 1),
('韦先生', '11800003333', '云南省', '昆明市', '五华区', '东风东路28号顺城购物中心601室', 1),
('蒋女士', '11700004444', '广西省', '南宁市', '青秀区', '民族大道136号万象城1201室', 1),
('蔡先生', '11600005555', '江西省', '南昌市', '红谷滩区', '红谷中大道1688号铜锣湾广场902室', 1),
('余女士', '11500006666', '山西省', '太原市', '小店区', '长风街120号北美新天地702室', 1),
('丁先生', '11400007777', '吉林省', '长春市', '朝阳区', '人民大街3388号欧亚商都1101室', 1),
('沈小姐', '11300008888', '黑龙江', '哈尔滨市', '南岗区', '中央大街98号松雷国际802室', 1),
('韩先生', '11200009999', '内蒙古', '呼和浩特市', '新城区', '新华大街1号维多利广场601室', 1),
('邓女士', '11100001111', '新疆省', '乌鲁木齐市', '天山区', '中山路388号天山百货702室', 1),
('冯先生', '11000002222', '甘肃省', '兰州市', '城关区', '东方红广场国芳百货1201室', 1),
('钱小姐', '10900003333', '海南省', '海口市', '龙华区', '海秀路8号望海国际广场902室', 1),
('曹女士', '10800004444', '宁夏省', '银川市', '兴庆区', '解放西街138号新华百货601室', 1),
('袁先生', '10700005555', '贵州省', '贵阳市', '南明区', '中华南路68号逸天城广场702室', 1),
('谢先生', '10600006666', '青海省', '西宁市', '城西区', '五四西路36号王府井百货1101室', 1),
('宋女士', '10500007777', '西藏省', '拉萨市', '城关区', '北京东路1号拉萨百货802室', 1);

-- -------------------------------------------------
-- 9. 物流订单数据
-- （status: 0-待指派, 1-已指派/待装货, 2-运输中, 3-已完成, 4-已取消）
-- -------------------------------------------------
DELETE FROM logistics_order;
ALTER TABLE logistics_order AUTO_INCREMENT = 1;

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
-- 订单1：已完成
('LO20260501001', 3, 1, 4,
 '{"contactName":"张先生","phone":"13800001111","province":"北京市","city":"北京市","district":"海淀区","detailAddress":"中关村大街1号智汇大厦1001室"}',
 6, 1, 2.50, 5.000,
 DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 588.00, DATE_SUB(NOW(), INTERVAL 3 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
-- 订单2：运输中
('LO20260502001', 2, 2, 5,
 '{"contactName":"李女士","phone":"13900002222","province":"上海市","city":"上海市","district":"静安区","detailAddress":"南京西路288号宏门广场2001室"}',
 7, 2, 1.80, 3.500,
 DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 420.00, DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
-- 订单3：已指派待装货
('LO20260503001', 1, 3, 6,
 '{"contactName":"王先生","phone":"13700003333","province":"广东省","city":"深圳市","district":"南山区","detailAddress":"科技园南区高新南七道R2-B栋5楼"}',
 6, 1, 5.00, 15.000,
 DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, NULL, 1200.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
-- 订单4：待指派
('LO20260504001', 0, 1, 7,
 '{"contactName":"刘女士","phone":"13600004444","province":"广东省","city":"广州市","district":"天河区","detailAddress":"体育西路123号天河城购物中心3楼"}',
 NULL, NULL, 0.80, 2.000,
 DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, NULL, 260.00, NOW());

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
-- 订单5：已完成
('LO20260501002', 3, 2, 8,
 '{"contactName":"赵先生","phone":"13500005555","province":"浙江省","city":"杭州市","district":"西湖区","detailAddress":"文三路388号钱江科技大厦1201室"}',
 7, 2, 3.20, 8.000,
 DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 780.00, DATE_SUB(NOW(), INTERVAL 5 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
-- 订单6：已取消
('LO20260505001', 4, 1, 9,
 '{"contactName":"陈女士","phone":"13300006666","province":"北京市","city":"北京市","district":"朝阳区","detailAddress":"建国路88号SOHO现代城A座601室"}',
 NULL, NULL, 1.20, 3.000,
 DATE_ADD(NOW(), INTERVAL 3 DAY), NULL, NULL, 350.00, DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
-- 订单7：待指派
('LO20260506001', 0, 3, 10,
 '{"contactName":"周先生","phone":"13200007777","province":"广东省","city":"深圳市","district":"福田区","detailAddress":"福华路购物公园C座1801室"}',
 NULL, NULL, 8.50, 25.000,
 DATE_ADD(NOW(), INTERVAL 4 DAY), NULL, NULL, 2200.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
-- 订单8：已指派待装货
('LO20260507001', 1, 2, 11,
 '{"contactName":"吴小姐","phone":"13100008888","province":"广东省","city":"广州市","district":"天河区","detailAddress":"天河北路183号大都会广场802室"}',
 8, 5, 0.45, 1.200,
 DATE_ADD(NOW(), INTERVAL 1 DAY), NULL, NULL, 320.00, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 以下为新增测试数据
INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508001', 3, 1, 12, '{"contactName":"郑先生","phone":"13000009999","province":"江苏省","city":"南京市","district":"鼓楼区","detailAddress":"中山北路200号商业中心1201室"}', 9, 6, 6.00, 18.000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 1500.00, DATE_SUB(NOW(), INTERVAL 6 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508002', 2, 2, 13, '{"contactName":"孙女士","phone":"12900001111","province":"四川省","city":"成都市","district":"锦江区","detailAddress":"春熙路88号IFS国际金融中心18楼"}', 10, 7, 1.50, 4.500, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 680.00, DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508003', 0, 3, 14, '{"contactName":"马先生","phone":"12800002222","province":"湖北省","city":"武汉市","district":"武昌区","detailAddress":"中南路7号中商广场A座2201室"}', NULL, NULL, 0.60, 1.800, DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, NULL, 320.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508004', 3, 1, 15, '{"contactName":"胡小姐","phone":"12700003333","province":"福建省","city":"福州市","district":"鼓楼区","detailAddress":"五一路华城国际大厦902室"}', 11, 8, 4.20, 12.000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 980.00, DATE_SUB(NOW(), INTERVAL 7 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508005', 4, 2, 16, '{"contactName":"朱先生","phone":"12600004444","province":"山东省","city":"济南市","district":"历下区","detailAddress":"泉城路188号恒隆广场1301室"}', NULL, NULL, 2.10, 6.500, DATE_ADD(NOW(), INTERVAL 3 DAY), NULL, NULL, 560.00, DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508006', 1, 3, 17, '{"contactName":"林先生","phone":"12500005555","province":"辽宁省","city":"沈阳市","district":"和平区","detailAddress":"青年大街288号华润大厦1501室"}', 12, 9, 7.80, 22.000, DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, NULL, 1880.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508007', 0, 1, 18, '{"contactName":"何女士","phone":"12400006666","province":"河南省","city":"郑州市","district":"金水区","detailAddress":"花园路118号正弘城801室"}', NULL, NULL, 1.30, 3.800, DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, NULL, 420.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508008', 3, 2, 19, '{"contactName":"高先生","phone":"12300007777","province":"陕西省","city":"西安市","district":"雁塔区","detailAddress":"科技路32号高新国际商务中心601室"}', 13, 13, 5.50, 16.000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), 1350.00, DATE_SUB(NOW(), INTERVAL 8 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508009', 2, 3, 20, '{"contactName":"卢小姐","phone":"12200008888","province":"重庆市","city":"重庆市","district":"渝中区","detailAddress":"解放碑步行街国泰广场702室"}', 14, 14, 9.20, 28.000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 2400.00, DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508010', 1, 1, 21, '{"contactName":"田先生","phone":"12100009999","province":"天津市","city":"天津市","district":"和平区","detailAddress":"南京路189号恒隆广场902室"}', 9, 6, 3.80, 10.000, DATE_ADD(NOW(), INTERVAL 1 DAY), NULL, NULL, 860.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508011', 0, 2, 22, '{"contactName":"崔先生","phone":"12000001111","province":"河北省","city":"石家庄市","district":"长安区","detailAddress":"中山路388号勒泰中心1101室"}', NULL, NULL, 0.90, 2.500, DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, NULL, 280.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508012', 4, 3, 23, '{"contactName":"梁小姐","phone":"11900002222","province":"湖南省","city":"长沙市","district":"芙蓉区","detailAddress":"五一大道838号平和堂商务楼702室"}', NULL, NULL, 1.70, 5.200, DATE_ADD(NOW(), INTERVAL 3 DAY), NULL, NULL, 490.00, DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508013', 3, 1, 24, '{"contactName":"韦先生","phone":"11800003333","province":"云南省","city":"昆明市","district":"五华区","detailAddress":"东风东路28号顺城购物中心601室"}', 10, 7, 2.60, 7.800, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), 720.00, DATE_SUB(NOW(), INTERVAL 9 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508014', 2, 2, 25, '{"contactName":"蒋女士","phone":"11700004444","province":"广西省","city":"南宁市","district":"青秀区","detailAddress":"民族大道136号万象城1201室"}', 11, 12, 4.00, 11.000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, 1050.00, DATE_SUB(NOW(), INTERVAL 3 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508015', 1, 3, 26, '{"contactName":"蔡先生","phone":"11600005555","province":"江西省","city":"南昌市","district":"红谷滩区","detailAddress":"红谷中大道1688号铜锣湾广场902室"}', 12, 9, 6.50, 20.000, DATE_ADD(NOW(), INTERVAL 1 DAY), NULL, NULL, 1680.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508016', 0, 1, 27, '{"contactName":"余女士","phone":"11500006666","province":"山西省","city":"太原市","district":"小店区","detailAddress":"长风街120号北美新天地702室"}', NULL, NULL, 1.10, 3.200, DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, NULL, 360.00, NOW());

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508017', 3, 2, 28, '{"contactName":"丁先生","phone":"11400007777","province":"吉林省","city":"长春市","district":"朝阳区","detailAddress":"人民大街3388号欧亚商都1101室"}', 13, 13, 8.00, 24.000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), 2100.00, DATE_SUB(NOW(), INTERVAL 10 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508018', 2, 3, 29, '{"contactName":"沈小姐","phone":"11300008888","province":"黑龙江省","city":"哈尔滨市","district":"南岗区","detailAddress":"中央大街98号松雷国际802室"}', 14, 14, 7.30, 22.000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 1950.00, DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508019', 1, 1, 30, '{"contactName":"韩先生","phone":"11200009999","province":"内蒙古","city":"呼和浩特市","district":"新城区","detailAddress":"新华大街1号维多利广场601室"}', 9, 6, 5.00, 15.000, DATE_ADD(NOW(), INTERVAL 1 DAY), NULL, NULL, 1300.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508020', 0, 2, 31, '{"contactName":"邓女士","phone":"11100001111","province":"新疆省","city":"乌鲁木齐市","district":"天山区","detailAddress":"中山路388号天山百货702室"}', NULL, NULL, 3.20, 9.500, DATE_ADD(NOW(), INTERVAL 4 DAY), NULL, NULL, 880.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508021', 4, 3, 32, '{"contactName":"冯先生","phone":"11000002222","province":"甘肃省","city":"兰州市","district":"城关区","detailAddress":"东方红广场国芳百货1201室"}', NULL, NULL, 0.80, 2.200, DATE_ADD(NOW(), INTERVAL 3 DAY), NULL, NULL, 240.00, DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508022', 3, 1, 33, '{"contactName":"钱小姐","phone":"10900003333","province":"海南省","city":"海口市","district":"龙华区","detailAddress":"海秀路8号望海国际广场902室"}', 10, 7, 1.80, 5.200, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), 520.00, DATE_SUB(NOW(), INTERVAL 11 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508023', 2, 2, 34, '{"contactName":"曹女士","phone":"10800004444","province":"宁夏省","city":"银川市","district":"兴庆区","detailAddress":"解放西街138号新华百货601室"}', 11, 12, 0.70, 1.800, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, 210.00, DATE_SUB(NOW(), INTERVAL 3 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508024', 1, 3, 35, '{"contactName":"袁先生","phone":"10700005555","province":"贵州省","city":"贵阳市","district":"南明区","detailAddress":"中华南路68号逸天城广场702室"}', 12, 9, 4.50, 13.000, DATE_ADD(NOW(), INTERVAL 1 DAY), NULL, NULL, 1180.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508025', 0, 1, 36, '{"contactName":"谢先生","phone":"10600006666","province":"青海省","city":"西宁市","district":"城西区","detailAddress":"五四西路36号王府井百货1101室"}', NULL, NULL, 2.30, 7.000, DATE_ADD(NOW(), INTERVAL 3 DAY), NULL, NULL, 680.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508026', 3, 2, 37, '{"contactName":"宋女士","phone":"10500007777","province":"西藏省","city":"拉萨市","district":"城关区","detailAddress":"北京东路1号拉萨百货802室"}', 13, 13, 6.00, 18.000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), 1600.00, DATE_SUB(NOW(), INTERVAL 12 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508027', 2, 3, 4,  '{"contactName":"张先生","phone":"13800001111","province":"北京市","city":"北京市","district":"海淀区","detailAddress":"中关村大街1号智汇大厦1001室"}', 14, 14, 9.50, 30.000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 2600.00, DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508028', 1, 1, 5,  '{"contactName":"李女士","phone":"13900002222","province":"上海市","city":"上海市","district":"静安区","detailAddress":"南京西路288号宏门广场2001室"}', 9, 6, 3.50, 9.000, DATE_ADD(NOW(), INTERVAL 1 DAY), NULL, NULL, 920.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508029', 0, 2, 6,  '{"contactName":"王先生","phone":"13700003333","province":"广东省","city":"深圳市","district":"南山区","detailAddress":"科技园南区高新南七道R2-B栋5楼"}', NULL, NULL, 1.20, 3.600, DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, NULL, 380.00, NOW());

INSERT INTO logistics_order (order_no, status, sender_id, receiver_id, receiver_snapshot,
  driver_id, vehicle_id, total_weight, total_volume,
  estimated_time, start_time, actual_arrival_time, profit_estimate, create_time) VALUES
('LO20260508030', 4, 3, 7,  '{"contactName":"刘女士","phone":"13600004444","province":"广东省","city":"广州市","district":"天河区","detailAddress":"体育西路123号天河城购物中心3楼"}', NULL, NULL, 0.50, 1.200, DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, NULL, 150.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- -------------------------------------------------
-- 10. 订单明细数据
-- -------------------------------------------------
DELETE FROM order_detail;
ALTER TABLE order_detail AUTO_INCREMENT = 1;

INSERT INTO order_detail (order_id, product_id, quantity, batch_no) VALUES
(1, 1, 50, NULL), (1, 2, 30, NULL),
(2, 8, 20, NULL), (2, 9, 10, NULL),
(3, 11, 50, NULL), (3, 14, 30, NULL), (3, 13, 100, NULL),
(4, 15, 100, NULL), (4, 16, 200, NULL),
(5, 7, 8, NULL), (5, 6, 20, NULL),
(6, 3, 40, NULL), (6, 4, 20, NULL),
(7, 19, 30, NULL), (7, 20, 10, NULL),
(8, 6, 5, NULL), (8, 9, 3, NULL),
(9, 11, 60, NULL), (9, 14, 20, NULL), (9, 13, 80, NULL),
(10, 7, 10, NULL), (10, 8, 5, NULL),
(11, 15, 80, NULL), (11, 16, 150, NULL),
(12, 1, 40, NULL), (12, 3, 30, NULL),
(13, 9, 15, NULL), (13, 10, 10, NULL),
(14, 6, 20, NULL), (14, 5, 60, NULL),
(15, 19, 10, NULL), (15, 18, 8, NULL),
(16, 1, 30, NULL), (16, 2, 25, NULL),
(17, 7, 25, NULL), (17, 9, 15, NULL),
(18, 20, 5, NULL), (18, 18, 10, NULL),
(19, 11, 40, NULL), (19, 13, 50, NULL),
(20, 2, 20, NULL), (20, 4, 10, NULL),
(21, 5, 50, NULL),
(22, 6, 8, NULL), (22, 10, 5, NULL),
(23, 3, 25, NULL), (23, 1, 20, NULL),
(24, 8, 10, NULL),
(25, 14, 20, NULL), (25, 15, 30, NULL),
(26, 7, 15, NULL), (26, 9, 8, NULL),
(27, 20, 12, NULL), (27, 19, 5, NULL),
(28, 11, 35, NULL), (28, 12, 20, NULL),
(29, 1, 50, NULL), (29, 3, 30, NULL),
(30, 5, 40, NULL);

-- -------------------------------------------------
-- 11. 订单财务数据
-- （settlement_status: 0-未结, 1-待审, 2-已结）
-- -------------------------------------------------
DELETE FROM order_finance;
ALTER TABLE order_finance AUTO_INCREMENT = 1;

-- order_id 对应 logistics_order.status：0-待指派, 1-已指派, 2-运输中, 3-已完成, 4-已取消
INSERT INTO order_finance (order_id, total_revenue, total_cost, actual_profit, settlement_status, order_status) VALUES
(1,    588.00,130.00, 458.00,2,3),  -- 订单1：已完成，已结
(2,    420.00, 90.00, 330.00,1,2),  -- 订单2：运输中，待审
(3,   1200.00,160.00,1040.00,0,1),  -- 订单3：已指派，未结
(4,    260.00, 40.00, 220.00,0,0),  -- 订单4：待指派，未结
(5,    780.00,110.00, 670.00,2,3),  -- 订单5：已完成，已结
(6,    350.00, 50.00, 300.00,0,4),  -- 订单6：已取消，未结
(7,   2200.00,230.00,1970.00,0,0),  -- 订单7：待指派，未结
(8,    320.00, 50.00, 270.00,1,1),  -- 订单8：已指派，待审
(9,   1500.00,200.00,1300.00,2,3),  -- 订单9：已完成，已结
(10,  680.00, 80.00, 600.00,1,2),  -- 订单10：运输中，待审
(11,  320.00, 60.00, 260.00,0,0),  -- 订单11：待指派，未结
(12,  980.00,120.00, 860.00,2,3),  -- 订单12：已完成，已结
(13,  560.00, 70.00, 490.00,0,4),  -- 订单13：已取消，未结
(14, 1880.00,250.00,1630.00,0,1),  -- 订单14：已指派，未结
(15,  420.00, 50.00, 370.00,1,0),  -- 订单15：待指派，待审
(16, 1350.00,180.00,1170.00,2,3),  -- 订单16：已完成，已结
(17,  860.00,100.00, 760.00,1,2),  -- 订单17：运输中，待审
(18,  280.00, 40.00, 240.00,0,1),  -- 订单18：已指派，未结
(19, 2400.00,320.00,2080.00,2,3),  -- 订单19：已完成，已结
(20,  720.00, 90.00, 630.00,1,4),  -- 订单20：已取消，待审
(21, 1050.00,140.00, 910.00,0,0),  -- 订单21：待指派，未结
(22, 1680.00,220.00,1460.00,2,3),  -- 订单22：已完成，已结
(23,  360.00, 50.00, 310.00,1,2),  -- 订单23：运输中，待审
(24, 2100.00,280.00,1820.00,2,3),  -- 订单24：已完成，已结
(25, 1950.00,260.00,1690.00,1,2),  -- 订单25：运输中，待审
(26, 1300.00,170.00,1130.00,0,1),  -- 订单26：已指派，未结
(27,  880.00,110.00, 770.00,0,0),  -- 订单27：待指派，未结
(28,  240.00, 30.00, 210.00,0,0),  -- 订单28：待指派，未结
(29,  520.00, 70.00, 450.00,2,3),  -- 订单29：已完成，已结
(30,  210.00, 30.00, 180.00,1,1);  -- 订单30：已指派，待审

-- -------------------------------------------------
-- 12. 费用明细数据
-- （direction: 0-支出, 1-收入）
-- -------------------------------------------------
DELETE FROM finance_detail;
ALTER TABLE finance_detail AUTO_INCREMENT = 1;

INSERT INTO finance_detail (finance_id, item_name, amount, direction, remark) VALUES
(1, '客户运费', 588.00, 1, '订单LO20260501001 客户支付'),
(1, '高速费',  50.00, 0, '北京-河北段高速'),
(1, '燃油费',  80.00, 0, '全程油耗估算'),
(2, '客户运费', 420.00, 1, '订单LO20260502001 客户支付'),
(2, '高速费',  30.00, 0, '上海城区路段'),
(2, '燃油费',  60.00, 0, '全程油耗估算'),
(3, '客户运费', 1200.00, 1, '订单LO20260503001 客户支付（预估）'),
(3, '高速费',   60.00, 0, '预估高速费'),
(3, '燃油费',  100.00, 0, '预估油耗'),
(4, '客户运费', 780.00, 1, '订单LO20260501002 客户支付'),
(4, '高速费',  40.00, 0, '沪杭高速'),
(4, '燃油费',  70.00, 0, '全程油耗估算'),
(5, '客户运费', 2200.00, 1, '订单LO20260506001 客户支付（预估）'),
(5, '高速费',   80.00, 0, '预估长途高速'),
(5, '燃油费',  150.00, 0, '预估油耗'),
(6, '客户运费', 320.00, 1, '订单LO20260507001 客户支付（预估）'),
(6, '高速费',   20.00, 0, '预估高速费'),
(6, '燃油费',   30.00, 0, '预估油耗'),
(7, '客户运费', 1500.00, 1, '订单LO20260508001 客户支付'),
(7, '高速费',  100.00, 0, '京宁高速'),
(7, '燃油费',  100.00, 0, '全程油耗'),
(8, '客户运费',  680.00, 1, '订单LO20260508002 客户支付'),
(8, '高速费',   40.00, 0, '沪蓉高速'),
(8, '燃油费',   40.00, 0, '油耗估算'),
(9, '客户运费',  320.00, 1, '订单LO20260508003 客户支付（预估）'),
(9, '高速费',   30.00, 0, '预估高速'),
(9, '燃油费',   30.00, 0, '预估油耗'),
(10, '客户运费', 980.00, 1, '订单LO20260508004 客户支付'),
(10, '高速费',   60.00, 0, '沪榕高速'),
(10, '燃油费',   60.00, 0, '油耗估算'),
(11, '客户运费', 560.00, 1, '订单LO20260508005 客户支付（预估）'),
(11, '高速费',   30.00, 0, '预估高速'),
(11, '燃油费',   40.00, 0, '预估油耗'),
(12, '客户运费', 1880.00, 1, '订单LO20260508006 客户支付（预估）'),
(12, '高速费',  120.00, 0, '预估长途高速'),
(12, '燃油费',  130.00, 0, '预估油耗'),
(13, '客户运费',  420.00, 1, '订单LO20260508007 客户支付（预估）'),
(13, '高速费',   25.00, 0, '预估高速'),
(13, '燃油费',   25.00, 0, '预估油耗'),
(14, '客户运费', 1350.00, 1, '订单LO20260508008 客户支付'),
(14, '高速费',   90.00, 0, '沪陕高速'),
(14, '燃油费',   90.00, 0, '油耗估算'),
(15, '客户运费',  860.00, 1, '订单LO20260508009 客户支付（预估）'),
(15, '高速费',   50.00, 0, '预估高速'),
(15, '燃油费',   50.00, 0, '预估油耗'),
(16, '客户运费',  280.00, 1, '订单LO20260508010 客户支付（预估）'),
(16, '高速费',   20.00, 0, '预估高速'),
(16, '燃油费',   20.00, 0, '预估油耗'),
(17, '客户运费', 2400.00, 1, '订单LO20260508011 客户支付'),
(17, '高速费',  160.00, 0, '沪藏高速'),
(17, '燃油费',  160.00, 0, '油耗估算'),
(18, '客户运费',  720.00, 1, '订单LO20260508012 客户支付（预估）'),
(18, '高速费',   45.00, 0, '预估高速'),
(18, '燃油费',   45.00, 0, '预估油耗'),
(19, '客户运费', 1050.00, 1, '订单LO20260508013 客户支付'),
(19, '高速费',   70.00, 0, '预估高速'),
(19, '燃油费',   70.00, 0, '油耗估算'),
(20, '客户运费', 1680.00, 1, '订单LO20260508014 客户支付（预估）'),
(20, '高速费',  110.00, 0, '预估长途高速'),
(20, '燃油费',  110.00, 0, '预估油耗'),
(21, '客户运费',  360.00, 1, '订单LO20260508015 客户支付（预估）'),
(21, '高速费',   25.00, 0, '预估高速'),
(21, '燃油费',   25.00, 0, '预估油耗'),
(22, '客户运费', 2100.00, 1, '订单LO20260508016 客户支付'),
(22, '高速费',  140.00, 0, '预估长途高速'),
(22, '燃油费',  140.00, 0, '油耗估算'),
(23, '客户运费', 1950.00, 1, '订单LO20260508017 客户支付'),
(23, '高速费',  130.00, 0, '预估高速'),
(23, '燃油费',  130.00, 0, '油耗估算'),
(24, '客户运费', 1300.00, 1, '订单LO20260508018 客户支付（预估）'),
(24, '高速费',   85.00, 0, '预估高速'),
(24, '燃油费',   85.00, 0, '预估油耗'),
(25, '客户运费',  880.00, 1, '订单LO20260508019 客户支付（预估）'),
(25, '高速费',   55.00, 0, '预估高速'),
(25, '燃油费',   55.00, 0, '预估油耗'),
(26, '客户运费',  240.00, 1, '订单LO20260508020 客户支付（预估）'),
(26, '高速费',   15.00, 0, '预估高速'),
(26, '燃油费',   15.00, 0, '预估油耗'),
(27, '客户运费',  520.00, 1, '订单LO20260508021 客户支付'),
(27, '高速费',   35.00, 0, '预估高速'),
(27, '燃油费',   35.00, 0, '油耗估算'),
(28, '客户运费',  210.00, 1, '订单LO20260508022 客户支付（预估）'),
(28, '高速费',   15.00, 0, '预估高速'),
(28, '燃油费',   15.00, 0, '预估油耗'),
(29, '客户运费', 1180.00, 1, '订单LO20260508023 客户支付（预估）'),
(29, '高速费',   75.00, 0, '预估高速'),
(29, '燃油费',   75.00, 0, '预估油耗'),
(30, '客户运费',  680.00, 1, '订单LO20260508024 客户支付（预估）'),
(30, '高速费',   45.00, 0, '预估高速'),
(30, '燃油费',   45.00, 0, '预估油耗');

-- -------------------------------------------------
-- 13. 司机绩效数据（按月份统计）
-- -------------------------------------------------
INSERT INTO driver_performance (driver_id, month, order_count, total_profit) VALUES
(6,  '2026-04', 12, 5680.00),
(7,  '2026-04', 8,  3720.00),
(8,  '2026-04', 5,  2150.00),
(9,  '2026-04', 6,  2680.00),
(10, '2026-04', 4,  1820.00),
(11, '2026-04', 3,  1380.00),
(12, '2026-04', 7,  3220.00),
(13, '2026-04', 9,  4120.00),
(14, '2026-04', 2,  960.00),
(6,  '2026-05', 3,  1488.00),
(7,  '2026-05', 2,  1000.00),
(9,  '2026-05', 4,  2010.00),
(10, '2026-05', 3,  1540.00),
(11, '2026-05', 2,  980.00),
(12, '2026-05', 5,  2650.00),
(13, '2026-05', 3,  1580.00),
(14, '2026-05', 2,  1060.00);

-- ========================================
-- 数据验证
-- ========================================
SELECT '=== 数据完整性验证 ===' AS info;
SELECT 'User表记录数（期望15条）'      AS check_info, COUNT(*) AS actual_count FROM user;
SELECT 'Vehicle表记录数（期望14条）'   AS check_info, COUNT(*) AS actual_count FROM vehicle;
SELECT 'AddressBook表记录数（期望37条）' AS check_info, COUNT(*) AS actual_count FROM address_book;
SELECT 'LogisticsOrder表记录数（期望30条）' AS check_info, COUNT(*) AS actual_count FROM logistics_order;
SELECT 'DriverPerformance表记录数（期望17条）' AS check_info, COUNT(*) AS actual_count FROM driver_performance;
