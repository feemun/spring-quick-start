-- 创建网络设备资产表
CREATE TABLE IF NOT EXISTS `network_device` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `ip_address` VARCHAR(50) NOT NULL COMMENT 'IP地址',
    `mac_address` VARCHAR(50) DEFAULT NULL COMMENT 'MAC地址',
    `device_name` VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
    `device_type` VARCHAR(50) DEFAULT NULL COMMENT '设备类型(SERVER, PC, IOT, VM, ROUTER, SWITCH)',
    `os_type` VARCHAR(50) DEFAULT NULL COMMENT '操作系统',
    `owner` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '部署位置',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0:离线, 1:在线)',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ip` (`ip_address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网络设备资产表';

-- 插入 20 条测试数据
INSERT INTO `network_device` (`ip_address`, `mac_address`, `device_name`, `device_type`, `os_type`, `owner`, `location`, `status`, `description`) VALUES 
('192.168.1.100', 'AA:BB:CC:DD:EE:01', 'Office-PC-001', 'PC', 'Windows 10', 'ZhangSan', 'Office-A-101', 1, '财务部员工电脑'),
('192.168.1.101', 'AA:BB:CC:DD:EE:02', 'Office-PC-002', 'PC', 'Windows 11', 'LiSi', 'Office-A-102', 1, '人事部员工电脑'),
('192.168.1.200', 'AA:BB:CC:DD:EE:03', 'Office-Printer-01', 'IOT', 'Embedded', 'Admin', 'Office-A-Hall', 1, '办公区公共打印机'),
('10.0.1.10', 'AA:BB:CC:DD:EE:04', 'DB-Master-01', 'SERVER', 'CentOS 7', 'DBA-Team', 'DC-Rack-01', 1, '核心数据库主库'),
('10.0.1.11', 'AA:BB:CC:DD:EE:05', 'DB-Slave-01', 'SERVER', 'CentOS 7', 'DBA-Team', 'DC-Rack-01', 1, '核心数据库从库'),
('10.0.2.5', 'AA:BB:CC:DD:EE:06', 'App-Server-01', 'SERVER', 'Ubuntu 20.04', 'DevOps', 'DC-Rack-02', 1, '业务应用服务器节点1'),
('10.0.2.6', 'AA:BB:CC:DD:EE:07', 'App-Server-02', 'SERVER', 'Ubuntu 20.04', 'DevOps', 'DC-Rack-02', 1, '业务应用服务器节点2'),
('10.0.0.1', 'AA:BB:CC:DD:EE:08', 'Core-Switch-01', 'SWITCH', 'Cisco IOS', 'Network-Team', 'DC-Core', 1, '核心交换机'),
('10.0.0.254', 'AA:BB:CC:DD:EE:09', 'Gateway-Router', 'ROUTER', 'Huawei VRP', 'Network-Team', 'DC-Edge', 1, '出口路由器'),
('172.16.10.50', 'AA:BB:CC:DD:EE:10', 'K8s-Master-01', 'VM', 'CoreOS', 'Cloud-Team', 'Cloud-Zone-A', 1, 'K8s集群Master节点'),
('172.16.10.51', 'AA:BB:CC:DD:EE:11', 'K8s-Worker-01', 'VM', 'CoreOS', 'Cloud-Team', 'Cloud-Zone-A', 1, 'K8s集群Worker节点'),
('172.16.10.52', 'AA:BB:CC:DD:EE:12', 'K8s-Worker-02', 'VM', 'CoreOS', 'Cloud-Team', 'Cloud-Zone-A', 1, 'K8s集群Worker节点'),
('172.16.20.100', 'AA:BB:CC:DD:EE:13', 'Cloud-Redis-01', 'VM', 'RedisOS', 'Middleware', 'Cloud-Zone-B', 1, '云Redis缓存服务'),
('192.168.10.50', 'AA:BB:CC:DD:EE:14', 'Guest-Mobile-01', 'PC', 'Android', 'Guest', 'Lobby', 1, '访客接入设备'),
('192.168.100.88', 'AA:BB:CC:DD:EE:15', 'Finance-Server', 'SERVER', 'Windows Server', 'Finance', 'Office-Finance', 1, '财务专用服务器'),
('192.168.200.10', 'AA:BB:CC:DD:EE:16', 'Camera-FrontDoor', 'IOT', 'Linux', 'Security', 'Gate-01', 1, '大门监控摄像头'),
('192.168.200.11', 'AA:BB:CC:DD:EE:17', 'Camera-BackDoor', 'IOT', 'Linux', 'Security', 'Gate-02', 1, '后门监控摄像头'),
('10.3.0.100', 'AA:BB:CC:DD:EE:18', 'Test-Env-01', 'VM', 'CentOS 8', 'Test-Team', 'Virtual-Cluster', 0, '测试环境虚拟机(已关机)'),
('1.1.1.1', 'AA:BB:CC:DD:EE:19', 'Public-DNS-01', 'SERVER', 'Linux', 'Infra', 'Public-Zone', 1, '内部DNS服务器'),
('192.168.2.50', 'AA:BB:CC:DD:EE:20', 'Office-B-Laptop', 'PC', 'MacOS', 'WangWu', 'Office-B-202', 1, '研发部员工笔记本');
