package library.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookChangeLogService {

    @Autowired
    private BookChangeLogRepository bookChangeLogRepository;

    public void logChange(Long bookId, Long userId, String fieldName, String oldValue, String newValue) {
        BookChangeLog log = new BookChangeLog();
        log.setBookId(bookId);
        log.setUserId(userId);
        log.setFieldName(fieldName);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setChangeTime(LocalDateTime.now());
        bookChangeLogRepository.save(log);
    }

}
