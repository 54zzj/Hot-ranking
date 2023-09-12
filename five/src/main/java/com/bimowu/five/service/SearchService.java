package com.bimowu.five.service;

import com.bimowu.five.model.Information;

import java.util.List;
import java.util.Map;


public interface SearchService {
    List<Information> findLikeInfo(String word);

    //定时任务
    void importNews();


    List<Map<String,Object>> list();
}
