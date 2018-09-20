package com.common.api;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BaseMapper<T extends BaseBean> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(T record);

    T selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(T record);

    int count(T t);

    List<T> query(@Param("query") BaseBean baseBean,@Param("pager") PageBean pb);
}
