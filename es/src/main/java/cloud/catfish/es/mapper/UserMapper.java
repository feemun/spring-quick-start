package cloud.catfish.es.mapper;

import cloud.catfish.es.annotation.EsMapper;
import cloud.catfish.es.annotation.EsSQL;
import cloud.catfish.es.annotation.EsDSL;
import cloud.catfish.es.annotation.Param;
import cloud.catfish.es.entity.User;
import cloud.catfish.es.template.EsSqlTemplate;

import java.util.List;
import java.util.Map;

/**
 * 用户Mapper接口
 * 专门用于操作用户索引(users)
 */
@EsMapper(value = "userMapper", defaultIndex = "users")
public interface UserMapper extends BaseEsMapper<User, String> {

    /**
     * 根据用户名查询用户
     */
    @EsSQL("SELECT * FROM users WHERE username = ?")
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     */
    @EsDSL("{\"term\": {\"email.keyword\": \"#{email}\"}}")
    User findByEmail(@Param("email") String email);

    /**
     * 根据部门查询用户列表
     */
    @EsSQL("SELECT * FROM users WHERE department = ? ORDER BY createTime DESC")
    List<User> findByDepartment(@Param("department") String department);

    /**
     * 根据年龄范围查询用户
     */
    @EsDSL("{\"range\": {\"age\": {\"gte\": #{minAge}, \"lte\": #{maxAge}}}}")
    List<User> findByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

    /**
     * 根据状态分页查询用户
     */
    @EsSQL(value = "SELECT * FROM users WHERE status = ? ORDER BY createTime DESC LIMIT #{from}, #{size}", 
           pageable = true)
    EsSqlTemplate.PageResult<User> findByStatusWithPage(@Param("status") String status,
                                                       @Param("from") int from, 
                                                       @Param("size") int size);

    /**
     * 模糊搜索用户（用户名或邮箱）
     */
    @EsDSL(value = """
        {
          "bool": {
            "should": [
              {"match": {"username": "#{keyword}"}},
              {"match": {"email": "#{keyword}"}}
            ]
          }
        }
        """)
    List<User> searchUsers(@Param("keyword") String keyword);

    /**
     * 统计各部门用户数量
     */
    @EsDSL(value = "{\"match_all\": {}}", 
           type = EsDSL.QueryType.AGGREGATION,
           aggregations = """
           {
             "department_stats": {
               "terms": {
                 "field": "department.keyword",
                 "size": 10
               }
             }
           }
           """)
    Map<String, Object> getDepartmentStats();

    /**
     * 获取活跃用户（状态为ACTIVE）
     */
    @EsSQL("SELECT * FROM users WHERE status = 'ACTIVE' ORDER BY updateTime DESC")
    List<User> getActiveUsers();

    /**
     * 根据多个条件查询用户
     */
    @EsDSL(value = """
        {
          "bool": {
            "must": [
              {"term": {"department.keyword": "#{department}"}},
              {"term": {"status.keyword": "#{status}"}},
              {"range": {"age": {"gte": #{minAge}}}}
            ]
          }
        }
        """)
    List<User> findByMultipleConditions(@Param("department") String department,
                                       @Param("status") String status,
                                       @Param("minAge") Integer minAge);

    /**
     * 统计用户总数
     */
    @EsSQL("SELECT COUNT(*) FROM users")
    long getTotalUserCount();

    /**
     * 根据创建时间范围查询用户
     */
    @EsSQL("SELECT * FROM users WHERE createTime BETWEEN ? AND ? ORDER BY createTime DESC")
    List<User> findByCreateTimeRange(@Param("startTime") String startTime, 
                                    @Param("endTime") String endTime);

    /**
     * 高亮搜索用户名
     */
    @EsDSL(value = """
        {
          "match": {
            "username": "#{keyword}"
          }
        }
        """,
        highlight = true,
        highlightFields = "username")
    List<User> searchUsersWithHighlight(@Param("keyword") String keyword);
}