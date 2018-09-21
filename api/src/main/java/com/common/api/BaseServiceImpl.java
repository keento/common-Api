package com.common.api;

import com.common.api.excel.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public abstract class BaseServiceImpl<M extends BaseMapper<T>,T extends BaseBean> implements BaseService<T>{

    @Autowired
    protected M m;

    @Override
    public T get(Long id) {
        return m.selectByPrimaryKey(id);
    }

    @Override
    public int insert(T t) {
        return  m.insertSelective(t);
    }

    @Override
    public int update(T t) {
        return m.updateByPrimaryKeySelective(t);
    }

    @Override
    public int delete(Long id) {
        return m.deleteByPrimaryKey(id);
    }

    @Override
    public List<T> query(T t) {
        return m.query(t,null);
    }

    @Override
    public List<T> queryByPage(T t, PageBean pb) {
        if(pb!=null){
            pb.setTotal(m.count(t));
            return m.query(t,pb);
        }
        return m.query(t,null);
    }

    @Override
    public void export(OutputStream out,String title,T t,List<String> MergedNameFields, Map<String,Map> dictMap) {
        ExcelUtils.ExportExcel(m.query(t,null),title,out,MergedNameFields,dictMap);
    }
}
