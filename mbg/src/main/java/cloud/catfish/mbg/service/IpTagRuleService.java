package cloud.catfish.mbg.service;

import cloud.catfish.api.domain.IpTagRule;
import cloud.catfish.common.api.PageResult;
import org.springframework.web.multipart.MultipartFile;
import cloud.catfish.api.req.IpTagRuleReq;
import cloud.catfish.api.vo.IpTagRuleVO;

import java.util.List;

public interface IpTagRuleService {

    /**
     * 分页查询后台
     *
     * @param req 查询参数，包含分页信息和查询条件
     * @return 分页查询结果，已转换为VO对象
     */
    PageResult<IpTagRuleVO> page(IpTagRuleReq req);

    /**
     * 根据主键查询并转换为VO
     *
     * @param id 主键字段
     * @return VO查询结果
     */
    IpTagRuleVO selectByPrimaryKey(Long id);

    /**
     * 根据主键更新
     *
     * @param record 更新的数据对象
     * @return 是否更新成功
     */
    Boolean updateByPrimaryKeySelective(IpTagRule record);

    /**
     * 根据条件更新
     *
     * @param updateData 要更新的数据对象
     * @param condition 更新条件
     * @return 影响行数
     */
    int updateByExampleSelective(IpTagRule updateData, IpTagRuleReq condition);

    /**
     * 根据主键删除
     *
     * @param id 主键字段
     * @return 是否删除成功
     */
    Boolean deleteByPrimaryKey(Long id);

    /**
     * 根据条件删除
     *
     * @param condition 删除条件
     * @return 影响行数
     */
    int deleteByExample(IpTagRuleReq condition);

    /**
     * 新增
     *
     * @param record 新增的数据对象
     * @return 是否新增成功
     */
    Boolean insertSelective(IpTagRule record);
    
    /**
     * 批量新增
     *
     * @param records 批量新增的数据对象列表
     * @return 是否新增成功
     */
    Boolean batchInsert(List<IpTagRule> records);
    
    /**
     * Excel文件导入处理
     *
     * @param file Excel文件
     * @return 导入结果信息
     * @throws Exception 导入过程中的异常
     */
    String importExcel(MultipartFile file) throws Exception;

}
