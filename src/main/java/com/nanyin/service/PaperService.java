package com.nanyin.service;

import com.nanyin.config.AllAttriOfPaper;
import com.nanyin.model.Paper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by NanYin on 2017-10-02 下午2:06.
 * 包名： com.nanyin.service
 * 类描述：
 */
@Service
public interface PaperService {

    Map<String,Object> findAllPapersByTime();

    Map<String,Object>  findAllPapersByMark();

    int updateMarkByTitle(int mark,String title);

    List<Paper> findAllPaperByUser(String name,String search);

    List<Paper> findAllPapers(String search);

    AllAttriOfPaper findAllAttriOfPapaer(int id);

    List<String> findPaperInColumn(String column);

    List<Paper> findPaperByUserName(String name,String search);

    int deletePaperByPaperId(int id);

    Map<String,Object> findPaperByUser(String name,String pageNum);

    Paper findPaperById(String id);

    int updatePaperContentById(String content,String id);

    int insertPaper(String title,String content,String segment,String name);

    int findPaperId(String title,String segment,String name);

}