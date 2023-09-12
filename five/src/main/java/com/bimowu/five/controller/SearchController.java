package com.bimowu.five.controller;


import com.bimowu.five.model.Information;
import com.bimowu.five.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class SearchController {
    @Autowired
    SearchService searchService;

    @RequestMapping("/search")
    @ResponseBody
    public List<Information> search( String word){
        return searchService.findLikeInfo(word);
    }

    @RequestMapping("/page/search")
    public String searchIndexPage(String word, Model model){
        model.addAttribute("word",word);
        model.addAttribute("list",searchService.findLikeInfo(word));
        return "kaifa";
    }

    //排行榜json数据
    @RequestMapping("ranking")
    @ResponseBody
    public List<Map<String,Object>> ranking(){
        return  searchService.list();
    }

    @RequestMapping("/page/ranking")
    public String pageRanking(Model model){
        model.addAttribute("list",searchService.list());
        return "rank";
    }
}
