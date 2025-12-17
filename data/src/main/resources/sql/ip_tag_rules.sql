-- 创建 IP 打标规则表
CREATE TABLE IF NOT EXISTS `ip_tag_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `cidr` VARCHAR(50) NOT NULL COMMENT '网段 (CIDR格式)',
    `station_id` VARCHAR(50) NOT NULL COMMENT '站点ID',
    `station_name` VARCHAR(100) NOT NULL COMMENT '站点名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_cidr` (`cidr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IP打标规则表';

-- 插入 20 条测试数据
INSERT INTO `ip_tag_rule` (`cidr`, `station_id`, `station_name`, `description`) VALUES 
('192.168.1.0/24', 'ST001', 'Internal-Office-A', '内部办公区A'),
('10.0.0.0/8', 'ST002', 'Data-Center-Main', '主数据中心'),
('172.16.0.0/12', 'ST003', 'Cloud-VPC-Prod', '云生产环境'),
('192.168.2.0/24', 'ST004', 'Internal-Office-B', '内部办公区B'),
('192.168.10.0/24', 'ST005', 'Guest-WiFi', '访客无线网络'),
('10.1.0.0/16', 'ST006', 'Data-Center-Backup', '灾备数据中心'),
('10.2.0.0/16', 'ST007', 'Dev-Environment', '开发环境'),
('10.3.0.0/16', 'ST008', 'Test-Environment', '测试环境'),
('172.16.10.0/24', 'ST009', 'Cloud-K8s-Cluster', 'K8s集群网段'),
('172.16.20.0/24', 'ST010', 'Cloud-DB-Subnet', '云数据库子网'),
('192.168.100.0/24', 'ST011', 'Finance-Dept', '财务部专网'),
('192.168.101.0/24', 'ST012', 'HR-Dept', '人力资源部专网'),
('192.168.200.0/24', 'ST013', 'Security-Camera', '安防监控网段'),
('1.1.1.0/24', 'ST014', 'Public-DNS-Area', '公共DNS区域'),
('202.108.22.0/24', 'ST015', 'External-Partner-A', '外部合作伙伴A'),
('58.215.0.0/16', 'ST016', 'Branch-Shanghai', '上海分公司'),
('61.135.0.0/16', 'ST017', 'Branch-Beijing', '北京分公司'),
('113.108.0.0/16', 'ST018', 'Branch-Guangzhou', '广州分公司'),
('203.0.113.0/24', 'ST019', 'DMZ-Zone', '隔离区'),
('198.51.100.0/24', 'ST020', 'Sandbox-Zone', '沙箱隔离区');
