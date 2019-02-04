package hn;

import java.util.*;
import hn.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HNController {

    private static final Logger log = LoggerFactory.getLogger(HNController.class);

    private final BaseService baseService;

    public HNController(BaseService baseService) {
        this.baseService = baseService;
    }

    @GetMapping("/hn")
    public List<Map.Entry<String, Long>> top10TitleWordsLast25Stories() {
        log.info("@GetMapping(hn)");
        return baseService.getTop10titlesLast25Stories();
    }

    @GetMapping("/hn-most-used-title-last-week")
    public List<Map.Entry<String, Long>> top10TitleWordsLastWeek() {
        log.info("@GetMapping(hn-most-used-title-last-week");
        return baseService.getTop10titlesLastWeek();
    }

    @GetMapping("/hn-most-used-title-for-user-big-karma")
    public List<Map.Entry<String, Long>> top10TitleWordsFromUserWithBigKarma() {
        log.info("@GetMapping(hn-most-used-title-for-user-big-karma");
        return baseService.getTop10titlesFromUserWithBigKarma();
    }

}
