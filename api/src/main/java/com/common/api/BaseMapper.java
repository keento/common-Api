package com.common.api;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BaseMapper<T extends BaseBean> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(T record);

    T selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(T record);

    int count(@Param("query") T t);

    List<T> query(@Param("query") T t,@Param("pager") PageBean pb);
}
