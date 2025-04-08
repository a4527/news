package mp.project.example.service;

import mp.project.example.domain.NewsSummary;
import mp.project.example.repository.NewsSummaryRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class NewsService {
    @Autowired
    private OpenAIService openAIService;

    private final NewsSummaryRepository repository;

    public NewsService(NewsSummaryRepository repository) {
        this.repository = repository;
    }

    
    public void crawlByKeyword(String keyword) throws IOException {
        System.out.println("🔍 키워드 크롤링 시작: " + keyword);

        String url = "https://search.naver.com/search.naver?where=news&query="
                     + URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .get();

        Elements links = doc.select("a.info"); // '기사보기' 링크
        int count=0;
        for (Element link : links) {
            if(count++ >=3) break;
            String title1=link.text().trim();
            String articleUrl = link.attr("href");

            // 중복 저장 방지
            if (title1.isBlank()) continue; // 제목이 비어 있으면 스킵
            if (repository.existsByLink(articleUrl)) continue;

            // 기사 본문 접근해서 제목 가져오기
            Document articleDoc = Jsoup.connect(articleUrl)
                    .userAgent("Mozilla/5.0")
                    .get();

            String title = articleDoc.title(); // 페이지 <title> 가져오기
            String articleText = articleDoc.body().text();
            if (articleText.length() > 3000) {
                articleText = articleText.substring(0, 3000);
            }
            String summary = openAIService.summarizeText(articleText);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // OpenAI rate limit 대응
            NewsSummary news = new NewsSummary();
            news.setTitle(title);
            news.setLink(articleUrl);
            news.setSummary(summary);

            repository.save(news);
            System.out.println("✅ 저장 완료: " + title);
        }

        System.out.println("✅ 크롤링 완료");
    }

    public List<NewsSummary> getAllNews() {
        return repository.findAll();
    }
    
}
