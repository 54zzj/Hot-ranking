package com.bimowu.five.job;


import com.bimowu.five.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NewsJob {
    private static final Logger LOG = LoggerFactory.getLogger(NewsJob.class);
    @Autowired
    SearchService searchService;
    @Scheduled(cron = "0/5 * * * * ?")
    public void run() {
        LOG.info("===开始爬取内容定时任务===");
        searchService.importNews();
        LOG.info("===结束爬取内容定时任务===");
    }
}
