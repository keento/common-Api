package com.common.api.excel;

import com.common.api.BaseBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.Region;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelUtils {

    private static DateFormat df   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     *
     * @param list 要导出的数据列表
     * @param title 标题
     * @param MergedNameFields 要合并的属性名
     * @param dictMap 字典属性键值对，类型为嵌套map
     */
	public static void ExportExcel(List list,String title, OutputStream fOut, List<String> MergedNameFields, Map<String,Map> dictMap){
        if(list==null || list.isEmpty())return;
        try{
            HSSFWorkbook workbook = new HSSFWorkbook();//创建一个新的工作表
            HSSFSheet sheet = workbook.createSheet(title);//产生新的工作表对象
            Class cls=list.get(0).getClass();//获取List中的对象类型
            Field[] fields=cls.getDeclaredFields();
            Map<String,Map> dataMap=new LinkedHashMap<String, Map>();
            Map<String,Object> map;
            for(Field f:fields){//遍历List类中的对象的属性数组
                if(f.getAnnotation(Column.class)!=null){//如果该属性上含有注解
                    if(BaseBean.class.isAssignableFrom(f.getType())){//如果该类有注解，且是Persistentable的子类
                        for(Field fchd:f.getType().getDeclaredFields()){//子类的属性上含有注解
                            if(fchd.getAnnotation(Column.class)!=null){//如果该属性含有注解
                                dataMap.put(fchd.getAnnotation(Column.class).value(),ExcelMap(fchd,f));
                            }
                        }
                    }else{
                        dataMap.put(f.getAnnotation(Column.class).value(),ExcelMap(f,null));
                    }
                }
            }
            HSSFCellStyle titleStyle=getTitleHSSFCellStyle(workbook);//标题单元格
            HSSFCellStyle cellStyle = getContentHSSFCellStyle(workbook);//内容单元格
            HSSFRow headerRowTitle = sheet.createRow(0);//创建一行
            HSSFCell head = headerRowTitle.createCell(0);//创建单元格
            head.setCellStyle(titleStyle);//设置单元格样式
            head.setCellValue(title);//设置单元格内容值
            HSSFRow headerRow = sheet.createRow(1);//创建标题栏
            Set<String> names=dataMap.keySet();
            int cellIndex=0;
            List<Integer> MergedIndex=new ArrayList<Integer>();
            for(String name:names){//设置列名
                HSSFCell cell = headerRow.createCell(cellIndex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(name);
                cell.setCellStyle(titleStyle);
                if(MergedNameFields!=null && MergedNameFields.contains(name))
                    MergedIndex.add(cellIndex-1);
            }
            sheet.addMergedRegion(new CellRangeAddress(0,0,0,cellIndex-1));//合并表标题名
            for(int t=0;t<list.size();t++){//遍历数据对象集合
                HSSFRow currentRow = sheet.createRow(t+2);//创建一行
                cellIndex=0;
                for(String key:names){//遍历field获取需要导出数据列
                    Map kv=dataMap.get(key);
                    HSSFCell cell = currentRow.createCell(cellIndex++);//创建一列
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellStyle(cellStyle);
                    //如果为嵌套元素则先取出父对象再取子属性值
                    Object value=kv.get("parent")!=null?getFieldValueByName((Field)kv.get("field"),getFieldValueByName((Field)kv.get("parent"),list.get(t))):getFieldValueByName((Field)kv.get("field"),list.get(t));
                    String type=(String)kv.get("type");
                    if(value!=null){
                        if(type!=null ){
                            //若为字典项数据，则从字典项中取值
                            Object val=dictMap.get(type).get(String.valueOf(value));
                            if(val!=null)cell.setCellValue(String.valueOf(val));
                        }else{
                            cell.setCellValue((value instanceof Date)?df.format(value):String.valueOf(value));//若为日期格式，则格式化日期，否则以基本类型输出
                        }
                    }
                }
            }
            if( MergedIndex.size()>0){//合并相同属性列
                String[] rolValue=new String[MergedIndex.size()];
                int totalRol=sheet.getLastRowNum();
                for(int x=0;x<MergedIndex.size();x++){
                    rolValue[x]=sheet.getRow(2).getCell(MergedIndex.get(x)).getStringCellValue();
                }
                if(totalRol>=3){
                    for(int x=0;x<MergedIndex.size();x++){
                        for(int a=3;a<=totalRol;a++){//行下标
                            if(sheet.getRow(a).getCell(x).getStringCellValue().equals(rolValue[x])){
                                sheet.addMergedRegion(new Region(a-1,(short)x,a,(short)x));
                            }else{
                                rolValue[x]=sheet.getRow(a).getCell(x).getStringCellValue();
                            }
                        }
                    }
                }
            }
            for(int i=1;i<=cellIndex;i++)//自动调整列宽
                sheet.autoSizeColumn((short) i-1);
            workbook.write(fOut);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static HSSFCellStyle getTitleHSSFCellStyle(HSSFWorkbook workbook){
        HSSFCellStyle style = workbook.createCellStyle();//创建单元格样式
        HSSFFont font = workbook.createFont();//创建字体
        font.setFontName("楷体");//设置字体名
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//字体加粗
        font.setFontHeightInPoints((short)20);//字号
        style.setFont(font);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
        style.setFillForegroundColor((short) 34);// 设置背景色
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        return style;
    }


    public static HSSFCellStyle getContentHSSFCellStyle(HSSFWorkbook workbook){
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor((short) 44);// 设置背景色
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        HSSFFont contentFont = workbook.createFont();
        contentFont.setFontName("楷体");//设置字体名
        contentFont.setFontHeightInPoints((short)14);//字号
        cellStyle.setFont(contentFont);
        return cellStyle;
    }

    public static void setRow(HSSFCell cell,HSSFCellStyle style,String o){
        cell.setCellStyle(style);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue(o);
    }

    @SuppressWarnings("rawtypes")
	private static Map ExcelMap(Field f,Field parent){
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("field",f);
        if(parent!=null)map.put("parent",parent);
        String type=f.getAnnotation(Column.class).dictType();
        map.put("type", type.length() > 0 ?type:null);
        return map;
    }
    /**
     * 根据属性的Field对象从实体对象中获取属性值
     * @param field Field对象
     * @param o 取值对象
     * @return 获取返回值
     */
    private static Object getFieldValueByName(Field field, Object o){
        try {
            field.setAccessible(true);
            return field.get(o);
        } catch (Exception e) {
            return null;
        }
    }
}
