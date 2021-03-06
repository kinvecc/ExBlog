package com.nanyin.service.serviceImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nanyin.config.PaperAndComments;
import com.nanyin.config.common.Paging;
import com.nanyin.mapper.FavesMapper;
import com.nanyin.model.Faves;
import com.nanyin.model.Paper;
import com.nanyin.service.FavesService;
import com.nanyin.service.PaperService;
import com.nanyin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by NanYin on 2017-11-13 下午2:54.
 * 包名： com.nanyin.service.serviceImpl
 * 类描述：
 */
@Service
public class FavesServiceImpl implements FavesService {
    @Autowired
    UserService userService;
    @Autowired
    FavesMapper favesMapper;
    @Autowired
    PaperService paperService;

    @Override
    public int insertFavesItem(String userName, String pageId) {
        int userId = userService.findAuthorByName(userName);
        int paperId = Integer.parseInt(pageId);
        return favesMapper.insertFavesItem(userId,paperId);
    }

    /**
     * 验证faves是否为空
     * @param faves
     * @return
     */
    private Boolean favesIsNull(Faves faves){
        if(faves == null){
            return false;
        }
        return true;
    }

    @Override
    public Map<String, Boolean> checkIsFaves(String userName, String pageId) {
        int userId = userService.findAuthorByName(userName);
        int paperId = Integer.parseInt(pageId);
        Map<String,Boolean> map = Maps.newHashMap();
        Faves faves = favesMapper.findFavesItem(userId,paperId);
        boolean flag =favesIsNull(faves);
        map.put("flag",flag);
        return map;
    }

    @Override
    public Map<String, Object> findFaves(String userName, String search,int pageNum) {
        int limit = Paging.LIMIT.getValue();
        int userId = userService.findAuthorByName(userName);
        List<PaperAndComments> paperList = Lists.newLinkedList();
        List<Faves> faves = favesMapper.findAllFavesItem(userId,(pageNum-1) * limit,limit);
        List<PaperAndComments> papers = paperService.findAllPapers(search);
        for (PaperAndComments p : papers) {
            for (Faves f: faves
                 ) {
                if(f.getPaper_id() == p.getId()){
                    paperList.add(p);
                }
            }
        }
        Map<String,Object> map = Maps.newHashMap();
        map.put("data",paperList);
        map.put("count",favesMapper.findAllFavesItemNoLimit(userId).size());
        map.put("code",0);
        map.put("pageNum",pageNum);
        map.put("msg","收藏的文章");

        return map;
    }

    @Override
    public int deleteFaverItem(String userName, int paperId) {
        int userId = userService.findAuthorByName(userName);
        return favesMapper.deleteFaverItem(userId,paperId);
    }

    @Override
    public Map<String,List> findFavesItem(String userName) {
        int userId = userService.findAuthorByName(userName);
        Map<String,List> map = Maps.newHashMap();
        map.put("list",favesMapper.findFavesPaperItem(userId,0,Paging.LIMIT.getValue()));
        return map;
    }


}
