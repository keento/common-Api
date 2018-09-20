package com.common.api;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface BaseService<T extends BaseBean> {

    T get(Long id);

    int insert(T t);

    int update(T t);

    int delete(Long id);

    List<T> query(T t);

    List<T> queryByPage(T t,PageBean pb);

    void export(OutputStream outputStream,String title,T t,List<String> MergedNameFields, Map<String,Map> dictMap);
}
