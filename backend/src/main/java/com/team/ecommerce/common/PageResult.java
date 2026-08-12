package com.team.ecommerce.common;

import java.util.List;

/**
 * 统一分页结果（契约 §0.5）：page 从 1 开始，size 默认 10 最大 100。
 *
 * @param total 总条数
 * @param page  当前页码
 * @param size  每页条数
 * @param list  当前页数据
 */
public record PageResult<T>(long total, int page, int size, List<T> list) {
}
