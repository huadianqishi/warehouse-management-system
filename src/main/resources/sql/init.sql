-- 仓储管理系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS warehouse_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE warehouse_db;

-- 1. 仓库表（warehouse）
CREATE TABLE warehouse (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    warehouse_number VARCHAR(20) NOT NULL COMMENT '仓库号码（唯一）',
    address VARCHAR(255) NOT NULL COMMENT '仓库地址',
    manager_name VARCHAR(50) NOT NULL COMMENT '仓库管理者姓名',
    manager_phone VARCHAR(20) NOT NULL COMMENT '管理者联系方式',
    PRIMARY KEY (id),
    UNIQUE KEY uk_warehouse_number (warehouse_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- 2. 货架表（shelf）
CREATE TABLE shelf (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    shelf_number VARCHAR(20) NOT NULL COMMENT '货架业务编号（用于展示和生成二维码）',
    floor_count INT NOT NULL COMMENT '层数',
    area_category VARCHAR(50) NOT NULL COMMENT '区域分类（如：文具区、电子产品区）',
    warehouse_id INT NOT NULL COMMENT '所属仓库ID',
    PRIMARY KEY (id),
    KEY idx_shelf_number (shelf_number),
    KEY idx_warehouse_id (warehouse_id),
    UNIQUE KEY uk_warehouse_shelf (warehouse_id, shelf_number),
    CONSTRAINT fk_shelf_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='货架表';

-- 3. 商品表（product）
CREATE TABLE product (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    type VARCHAR(50) NOT NULL COMMENT '商品类型',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    safety_stock INT NOT NULL DEFAULT 0 COMMENT '安全库存，用于低库存预警',
    PRIMARY KEY (id),
    KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 4. 用户表（user）
CREATE TABLE user (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    gender INT NOT NULL COMMENT '性别（男1，女0）',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    id_card VARCHAR(18) NOT NULL COMMENT '身份证号码',
    role INT NOT NULL COMMENT '角色：0-超管, 1-仓管, 2-普通, 3-司机',
    driver_license VARCHAR(20) NULL COMMENT '驾驶证号码（司机必填）',
    license_image VARCHAR(255) NULL COMMENT '证件照片路径',
    PRIMARY KEY (id),
    UNIQUE KEY idx_username (username),
    KEY idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 5. 操作类型表（operation_type）
CREATE TABLE operation_type (
    id INT NOT NULL COMMENT '操作类型ID（如1=新增商品）',
    name VARCHAR(100) NOT NULL COMMENT '操作类型名称（如"新增商品"）',
    category VARCHAR(50) NULL COMMENT '操作分类（如 product, warehouse）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作类型表';

-- 6. 操作记录表（operation_log）
CREATE TABLE operation_log (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id INT NOT NULL COMMENT '操作用户ID',
    operation_type_id INT NOT NULL COMMENT '操作类型ID',
    operation_time DATETIME NOT NULL COMMENT '操作时间',
    operation_status VARCHAR(20) NOT NULL COMMENT '操作状态（成功/失败）',
    target_table VARCHAR(50) NULL COMMENT '被操作的表名（如无则为NULL）',
    target_id INT NULL COMMENT '被操作记录ID（如无则为NULL）',
    description VARCHAR(500) NULL COMMENT '操作详情描述',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_operation_type_id (operation_type_id),
    KEY idx_operation_time (operation_time),
    KEY idx_target_table (target_table),
    CONSTRAINT fk_operation_log_user FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_operation_log_type FOREIGN KEY (operation_type_id) REFERENCES operation_type(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作记录表';

-- 7. 入库操作表（stock_in_record）
CREATE TABLE stock_in_record (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    product_id INT NOT NULL COMMENT '关联商品ID',
    quantity INT NOT NULL COMMENT '入库数量',
    storage_time DATETIME NOT NULL COMMENT '入库时间',
    shelf_id INT NOT NULL COMMENT '存放货架ID',
    floor_number INT NOT NULL COMMENT '货架具体层数',
    weight DECIMAL(10,3) NULL COMMENT '商品重量（kg）',
    volume DECIMAL(10,6) NULL COMMENT '商品体积（m³）',
    operator_id INT NOT NULL COMMENT '操作人ID',
    locked_quantity INT NOT NULL DEFAULT 0 COMMENT '预占库存数量（物流订单锁定，待发货后释放）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    PRIMARY KEY (id),
    KEY idx_product_id (product_id),
    KEY idx_storage_time (storage_time),
    KEY idx_shelf_id (shelf_id),
    KEY idx_operator_id (operator_id),
    CONSTRAINT fk_stock_in_product FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT fk_stock_in_shelf FOREIGN KEY (shelf_id) REFERENCES shelf(id),
    CONSTRAINT fk_stock_in_operator FOREIGN KEY (operator_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库操作表';

-- 8. 出库操作表（stock_out_record）
CREATE TABLE stock_out_record (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    product_id INT NOT NULL COMMENT '关联商品ID',
    quantity INT NOT NULL COMMENT '出库数量',
    out_time DATETIME NOT NULL COMMENT '出库时间',
    shelf_id INT NOT NULL COMMENT '原存放货架ID',
    floor_number INT NOT NULL COMMENT '货架具体层数',
    weight DECIMAL(10,3) NULL COMMENT '商品重量（kg）',
    volume DECIMAL(10,6) NULL COMMENT '商品体积（m³）',
    operator_id INT NOT NULL COMMENT '操作人ID',
    destination VARCHAR(255) NULL COMMENT '出库去向（如客户、门店等）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    PRIMARY KEY (id),
    KEY idx_product_id (product_id),
    KEY idx_out_time (out_time),
    KEY idx_shelf_id (shelf_id),
    KEY idx_operator_id (operator_id),
    CONSTRAINT fk_stock_out_product FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT fk_stock_out_shelf FOREIGN KEY (shelf_id) REFERENCES shelf(id),
    CONSTRAINT fk_stock_out_operator FOREIGN KEY (operator_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库操作表';

-- 9. 权限定义表（permission）
CREATE TABLE permission (
    id INT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    name VARCHAR(100) NOT NULL COMMENT '权限名称（如"商品查询"）',
    level TINYINT NOT NULL COMMENT '权限等级： 0=仅超级管理员， 1=超级管理员+仓管员， 2=所有人（含普通用户）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限定义表';

-- 10. 库存快照表（inventory_snapshot）
CREATE TABLE inventory_snapshot (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    product_id INT NOT NULL COMMENT '商品ID',
    shelf_id INT NOT NULL COMMENT '货架ID',
    floor_number INT NOT NULL COMMENT '货架具体层数',
    quantity INT NOT NULL COMMENT '快照时刻的库存数量',
    snapshot_date DATE NOT NULL COMMENT '快照日期（如2023-11-01）',
    PRIMARY KEY (id),
    KEY idx_product_id (product_id),
    KEY idx_shelf_id (shelf_id),
    KEY idx_snapshot_date (snapshot_date),
    CONSTRAINT fk_snapshot_product FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT fk_snapshot_shelf FOREIGN KEY (shelf_id) REFERENCES shelf(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存快照表';

-- ========================================
-- 物流管理模块
-- ========================================

-- 12. 车辆表（vehicle）
CREATE TABLE vehicle (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键',
    plate_number VARCHAR(20) NOT NULL COMMENT '车牌号（唯一）',
    category TINYINT NOT NULL COMMENT '车型：0-微面, 1-小货, 2-中货, 3-大货',
    max_weight DECIMAL(10,2) NOT NULL COMMENT '最大载重（吨）',
    max_volume DECIMAL(10,2) NOT NULL COMMENT '最大容积（立方）',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-空闲, 1-运输中, 2-维修',
    current_driver_id INT NULL COMMENT '当前绑定司机ID（关联 user.id）',
    remark VARCHAR(255) NULL COMMENT '车辆备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_plate_number (plate_number),
    KEY idx_status (status),
    KEY idx_current_driver_id (current_driver_id),
    CONSTRAINT fk_vehicle_driver FOREIGN KEY (current_driver_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆表';

-- 地址簿表（address_book）
CREATE TABLE address_book (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id INT NULL COMMENT '关联用户ID（若是公共地址库则为空）',
    contact_name VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    province VARCHAR(50) NOT NULL COMMENT '省',
    city VARCHAR(50) NOT NULL COMMENT '市',
    district VARCHAR(50) NULL COMMENT '区/县',
    detail_address VARCHAR(255) NOT NULL COMMENT '详细地址',
    type TINYINT NOT NULL DEFAULT 0 COMMENT '类型：0-发货人(仓库), 1-收货人(客户)',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_type (type),
    CONSTRAINT fk_address_book_user FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地址簿表';

-- 13.物流订单主表（logistics_order）
CREATE TABLE logistics_order (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_no VARCHAR(32) NOT NULL COMMENT '运单号（唯一索引）',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待指派, 1-已指派/待装货, 2-运输中, 3-已完成, 4-已取消',
    sender_id INT NULL COMMENT '发货人ID（关联 address_book）',
    receiver_id INT NULL COMMENT '收货人ID（关联 address_book）',
    receiver_snapshot TEXT NULL COMMENT '收货信息快照（JSON格式，防止地址变动影响历史订单）',
    driver_id INT NULL COMMENT '承运司机ID',
    vehicle_id INT NULL COMMENT '承运车辆ID',
    total_weight DECIMAL(10,2) NOT NULL COMMENT '订单总重量（吨，用于快速匹配车辆）',
    total_volume DECIMAL(10,2) NOT NULL COMMENT '订单总体积（立方，用于快速匹配车辆）',
    estimated_time DATETIME NOT NULL COMMENT '预估送达时间（倒计时结束点，下单时根据收货地址计算：同城24h/同省48h/跨省72h）',
    start_time DATETIME NULL COMMENT '实际出发时间（司机确认装货后记录）',
    actual_arrival_time DATETIME NULL COMMENT '实际送达时间',
    profit_estimate DECIMAL(12,2) NOT NULL COMMENT '预估利润（订单商品货值，即订单总营收，作为收入基准）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_status (status),
    KEY idx_driver_id (driver_id),
    KEY idx_vehicle_id (vehicle_id),
    KEY idx_create_time (create_time),
    CONSTRAINT fk_logistics_sender FOREIGN KEY (sender_id) REFERENCES address_book(id),
    CONSTRAINT fk_logistics_receiver FOREIGN KEY (receiver_id) REFERENCES address_book(id),
    CONSTRAINT fk_logistics_driver FOREIGN KEY (driver_id) REFERENCES user(id),
    CONSTRAINT fk_logistics_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流订单主表';

-- 14.订单商品关联表（order_detail）
CREATE TABLE order_detail (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id INT NOT NULL COMMENT '关联物流订单ID',
    product_id INT NOT NULL COMMENT '关联商品ID',
    quantity INT NOT NULL COMMENT '发货数量',
    weight DECIMAL(10,3) NULL COMMENT '该批次总重量（kg）',
    volume DECIMAL(10,6) NULL COMMENT '该批次总体积（m³）',
    batch_no VARCHAR(50) NULL COMMENT '对应入库批次号',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_product_id (product_id),
    CONSTRAINT fk_order_detail_order FOREIGN KEY (order_id) REFERENCES logistics_order(id),
    CONSTRAINT fk_order_detail_product FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品关联表';

-- 15.订单费用主表（order_finance）
CREATE TABLE order_finance (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id INT NOT NULL COMMENT '关联物流订单ID（唯一）',
    total_revenue DECIMAL(12,2) NOT NULL COMMENT '总营收（运费收入）',
    total_cost DECIMAL(12,2) NOT NULL COMMENT '总成本（油费+路费+其他）',
    actual_profit DECIMAL(12,2) NOT NULL COMMENT '实际获利（总营收 - 总成本）',
    settlement_status TINYINT NOT NULL DEFAULT 0 COMMENT '结算状态：0-未结, 1-待审, 2-已结',
    order_status TINYINT COMMENT '下单时订单状态快照：0-待指派, 1-已指派, 2-运输中, 3-已完成, 4-已取消（仅已完成的订单才可计入财务统计）',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_id (order_id),
    CONSTRAINT fk_order_finance_order FOREIGN KEY (order_id) REFERENCES logistics_order(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单费用主表';

-- 16.费用明细表（finance_detail）
CREATE TABLE finance_detail (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键',
    finance_id INT NOT NULL COMMENT '关联费用主表ID',
    item_name VARCHAR(50) NOT NULL COMMENT '项目名称（如：高速费、燃油费、客户运费）',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    direction TINYINT NOT NULL COMMENT '收支方向：0-支出, 1-收入',
    remark VARCHAR(200) NULL COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_finance_id (finance_id),
    CONSTRAINT fk_finance_detail_finance FOREIGN KEY (finance_id) REFERENCES order_finance(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用明细表';

-- 17.司机绩效表（driver_performance）
CREATE TABLE driver_performance (
    id INT NOT NULL AUTO_INCREMENT COMMENT '主键',
    driver_id INT NOT NULL COMMENT '司机ID',
    month VARCHAR(7) NOT NULL COMMENT '统计月份（格式：2024-05）',
    order_count INT NOT NULL DEFAULT 0 COMMENT '完成单量',
    total_profit DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '创造总收益（关联订单的实际获利总和）',
    last_update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_driver_month (driver_id, month),
    KEY idx_driver_id (driver_id),
    KEY idx_month (month),
    CONSTRAINT fk_driver_performance_driver FOREIGN KEY (driver_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='司机绩效表';

-- ========================================
-- 初始化基础数据（系统必需数据）
-- ========================================

-- ========================================
-- 初始化操作类型表（operation_type）
-- 按 category 分类，ID 连续不重复
-- ========================================
DELETE FROM operation_type;
ALTER TABLE operation_type AUTO_INCREMENT = 1;

INSERT INTO operation_type (id, name, category) VALUES
-- 用户管理（1-4）
(1,  '添加用户',    'user'),
(2,  '删除用户',    'user'),
(3,  '修改用户',    'user'),
(4,  '用户登录',    'user'),
-- 仓库管理（5-7）
(5,  '添加仓库',    'warehouse'),
(6,  '删除仓库',    'warehouse'),
(7,  '修改仓库',    'warehouse'),
-- 货架管理（8-10）
(8,  '添加货架',    'shelf'),
(9,  '删除货架',    'shelf'),
(10, '修改货架',    'shelf'),
-- 商品管理（11-13）
(11, '添加商品',    'product'),
(12, '删除商品',    'product'),
(13, '修改商品',    'product'),
-- 库存管理（14-18）
(14, '执行入库操作', 'stock'),
(15, '执行出库操作', 'stock'),
(16, '商品库存查询', 'stock'),
(17, '库存统计',    'stock'),
(18, '查询操作日志', 'log'),
-- 车辆管理（19-22）
(19, '添加车辆',    'vehicle'),
(20, '删除车辆',    'vehicle'),
(21, '修改车辆',    'vehicle'),
(22, '绑定司机',    'vehicle'),
-- 物流管理（23-29）
(23, '创建物流订单', 'logistics'),
(24, '指派物流',    'logistics'),
(25, '开始运输',    'logistics'),
(26, '完成物流订单', 'logistics'),
(27, '取消物流订单', 'logistics'),
-- 地址簿管理（30-32）
(28, '添加地址',    'address'),
(29, '删除地址',    'address'),
(30, '修改地址',    'address'),
-- 财务管理（31-34）
(31, '录入运费收入', 'finance'),
(32, '录入费用支出', 'finance'),
(33, '提交结算审核', 'finance'),
(34, '确认订单结算', 'finance')
ON DUPLICATE KEY UPDATE
    name     = VALUES(name),
    category = VALUES(category);

-- 初始化权限定义表（permission）
-- level: 0=仅超级管理员, 1=超级管理员+仓管员, 2=所有人（含普通用户）
INSERT INTO permission (name, level) VALUES
-- 商品管理权限
('商品查询', 2),
('商品新增', 1),
('商品修改', 1),
('商品删除', 0),
-- 仓库管理权限
('仓库查询', 2),
('仓库新增', 1),
('仓库修改', 1),
('仓库删除', 0),
-- 货架管理权限
('货架查询', 2),
('货架新增', 1),
('货架修改', 1),
('货架删除', 0),
-- 用户管理权限
('用户查询', 0),
('用户新增', 0),
('用户修改', 0),
('用户删除', 0),
-- 库存管理权限
('库存查询', 2),
('库存统计', 2),
-- 入库操作权限
('商品入库', 1),
-- 出库操作权限
('商品出库', 1),
-- 操作日志权限
('操作日志查询', 0),
-- 车辆管理权限
('车辆查询', 1),
('车辆新增', 1),
('车辆修改', 1),
('车辆删除', 0),
-- 地址簿管理权限
('地址簿查询', 2),
('地址簿新增', 1),
('地址簿修改', 1),
('地址簿删除', 1),
-- 物流订单管理权限
('物流订单查询', 2),
('物流订单新增', 1),
('物流订单修改', 1),
('物流订单取消', 1),
-- 物流指派权限
('物流指派', 0),
-- 司机绩效查询权限
('司机绩效查询', 0),
-- 费用管理权限
('费用录入', 1),
('费用审核', 0),
('结算确认', 0)
ON DUPLICATE KEY UPDATE
    level = VALUES(level);
