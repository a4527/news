package mp.project.example.repository;

import mp.project.example.domain.NewsSummary;
import org.springframework.data.jpa.repository.JpaRepository;;
public interface NewsSummaryRepository extends JpaRepository<NewsSummary, Long>{
    boolean existsByLink(String link); // 중복 기사 저장 방지용 (선택)
}
