package library.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookChangeLogRepository extends JpaRepository<BookChangeLog, Long> {
}
