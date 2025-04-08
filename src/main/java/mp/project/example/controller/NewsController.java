package mp.project.example.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import mp.project.example.domain.NewsSummary;
import mp.project.example.service.NewsService;
import mp.project.example.service.OpenAIService;

@RestController //이 클래스가 REST API의 컨트롤러 임을 나타냄, 반환 값은 자동으로 JSON으로 변환
@RequestMapping("/api")  //api로 시작하는 모든 URL 요청을 처리 
public class NewsController {

    private final NewsService newsService;
    private final OpenAIService openAIService; // 추가

    public NewsController(NewsService newsService, OpenAIService openAIService) {
        this.newsService = newsService;
        this.openAIService = openAIService;
    }   //DI 생성자 주입을 통해 NewsService와 OpenAIService를 주입받음 

    @GetMapping("/")
    public String root(){
        return "루트입니다";
    }

    @GetMapping("/crawl")
    public String crawl(@RequestParam String keyword) throws IOException {
        newsService.crawlByKeyword(keyword);
        return "크롤링 완료(키워드: "+keyword+")";
    }
    @PostConstruct //스프링이 이 빈을 초기화할때 사용하는 메서드 위에 붙이는 어노테이션 
//bean이란 스프링이 관리하는 객체를 의미한다. 
    public void init() {
    System.out.println("🔥 NewsController 등록됨!");
    }
    @GetMapping("/news")
    public List<NewsSummary> getNews() {
    return newsService.getAllNews();
    }
    @GetMapping("/test-summary")
    public String testSummary() {
        System.out.println("✅ /api/test-summary 호출됨");
    return openAIService.testSummary();
    //return openAIService.summarizeText("이것은 테스트용 뉴스 기사입니다. 중요한 정보를 담고 있습니다.");
}



}

