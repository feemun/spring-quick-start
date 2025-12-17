package cloud.catfish.common.api;

import java.util.List;

public record PageResult<T>(
        List<T> data,
        long total,
        int pageNum,   // 当前页码（根据 offset/limit 计算）
        int pageSize,  // 每页条数（对应 limit）
        long pages,    // 总页数
        boolean hasNext,
        boolean hasPrev
) {

    /**
     * 构建分页结果
     * @param data 当前页数据
     * @param total 总记录数
     * @param offset 起始偏移量（从0开始）
     * @param limit 每页条数
     */
    public static <T> PageResult<T> of(List<T> data, long total, int offset, int limit) {
        if (limit < 1) limit = 10;
        if (offset < 0) offset = 0;

        int pageNum = (offset / limit) + 1; // 根据 offset 计算页码
        long pages = total == 0 ? 1 : ((total + limit - 1) / limit);

        boolean hasNext = pageNum < pages;
        boolean hasPrev = pageNum > 1;

        return new PageResult<>(
                data,
                total,
                pageNum,
                limit,
                pages,
                hasNext,
                hasPrev
        );
    }
}
