package com.bimowu.five.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bimowu.five.dao.SearchDao;
import com.bimowu.five.model.Information;
import com.bimowu.five.service.SearchService;
import com.bimowu.five.utils.http.HttpClientUtil;
import com.bimowu.five.utils.http.HttpResult;
import com.bimowu.five.utils.redis.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {
    private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);
//URL常量
    private static final String URL = "https://news.baidu.com/widget?id=LocalNews&ajax=json";

    @Autowired
    SearchDao searchDao;

    @Autowired
    HttpClientUtil httpClient;

    @Autowired
    RedisUtil redisUtil;

    //搜索
    @Override
    public List<Information> findLikeInfo(String word) {
        if (StringUtils.isNotEmpty(word)) {
            record(word);
            List<Information> likeInfo = searchDao.findLikeInfo(word);
            return likeInfo;
        }
        return searchDao.findLikeInfo(word);
    }

    //定时任务
    @Override
    public void importNews() {
        LOG.info("===[开始调用爬取内容方法]===");
        try {

            HttpResult result = httpClient.doPost(URL);
            if (200 == result.getCode()) {
                JSONObject vo = JSON.parseObject(result.getBody());
                JSONArray arr = vo.getJSONObject("data").getJSONObject("LocalNews")
                        .getJSONObject("data").getJSONObject("rows").getJSONArray("first");
                Date date = new Date();
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    Information info = new Information();
                    info.setInfoSrc("bd");
                    info.setInfoImage(o.getString("imgUrl"));
                    info.setInfoText(o.getString("title"));
                    info.setInfoUrl(o.getString("url"));
                    info.setTime(o.getString("time"));
                    info.setSingleTag(DigestUtils.md5DigestAsHex(o.getString("title").getBytes()));//md5(防重复数据)
                    info.setCreateTime(date);
                    info.setUpdateTime(date);
                    searchDao.insertIfNotExist(info);
                }
            }
        } catch (Exception e) {
            LOG.error("爬取内容方法异常：excp={}", e);
        }
        LOG.info("===[结束调用爬取内容方法]===");
    }

    //从缓存中取出数据
    @Override
    public List<Map<String, Object>> list() {
        //接收数据的容器
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        //从缓存中拿出一排排好顺序的数据
        try {
            Set<ZSetOperations.TypedTuple<Object>> set = redisUtil.zrevrangeByScoreWithScores("NEWS_SEARCH:", 0D,
                    10000D);
            int i = 1;
            for (ZSetOperations.TypedTuple t : set) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("word", String.valueOf(t.getValue()));
                map.put("num", t.getScore().intValue());
                map.put("rank", i);
                list.add(map);
                i++;
            }
        } catch (Exception e) {
            LOG.error("===[记录热搜词时出现异常：excp={}]===", e);
        }
        return list;
    }

    //第一次搜索时记录次数
    public Double record(String word) {
        try {
            return redisUtil.zincrby("NEWS_SEARCH:", word, 1);
        } catch (Exception e) {
            LOG.error("===[记录热搜词时出现异常：excp={}]===", e);
        }
        return 0D;
    }
}
