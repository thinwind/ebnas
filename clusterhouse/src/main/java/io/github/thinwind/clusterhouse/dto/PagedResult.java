package io.github.thinwind.clusterhouse.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
*
* 分页查询结果
*
* @author ShangYh <shangyehua@ebchinatech.com>
* @since 2020-09-01 15:59
*
*/
@Getter
@Setter
public class PagedResult<T> {
    List<T> content;
    int currentPage;
    int total;
    int pageSize;
}