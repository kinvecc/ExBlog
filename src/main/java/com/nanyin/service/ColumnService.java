package com.nanyin.service;


import com.github.pagehelper.PageInfo;
import com.nanyin.model.Column;
import com.nanyin.model.Paper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by NanYin on 2017-10-02 下午11:03.
 * 包名： com.nanyin.service
 * 类描述：
 */
public interface ColumnService {

    List<Column> findColumByPaperCount();


    List<Column> findAllColumn();

    PageInfo<Column> findAllColumnSearch(String search, int pageNum);

    Set<String> findCoumnByUser(String name);

    int findCountByTitle(String title,String name);

    /**
     * 为paper添加主题
     * @param clumnId
     * @param paperId
     * @return
     */
    int insertColumnPaper(int clumnId,int paperId);

    int findColumnId(String title);

    Column findColumnByPaperId(int id);

    int updateColumnByPaperId(int paperId,String columnName);

    /**
     *
     * @param page 页
     * @return
     */
    Map<String,Object> allColumn(String page);

    /**
     *
     * @param name 名称
     * @param image 图片名称
     * @return
     */
    int insertInlet(String name,String image);

    int updateInlet(String name,String image,String id);

    int deleteColumnById(int id);

    Column selectColumnById(int id);

    PageInfo<Paper> findPapersWithColumnId(int id,int pageNum);

    String getColumnTitle(int id);
}
