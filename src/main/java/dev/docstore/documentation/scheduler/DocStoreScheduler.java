package dev.docstore.documentation.scheduler;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dev.docstore.documentation.model.Document;
import dev.docstore.documentation.model.DocumentStatus;
import dev.docstore.documentation.model.dao.DocumentDAO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocStoreScheduler {

    @Autowired
    private DocumentDAO documentDAO; 

    @Scheduled( fixedDelay = 60000 )
    @Transactional
    public void checkForOutdatedDocuments() {
        LocalDateTime cal = LocalDateTime.now();
        
        for (Document document : documentDAO.getAllWithStatusAndLatestUpdateOlderThan(cal.plusDays(-2), DocumentStatus.ACTIVE)) {
            log.info("Outdate the document: {} with last update {}", document.getUuid(), document.getLatestUpdateReceived());

            document.setStatus(DocumentStatus.OUTDATED);

            documentDAO.save(document);
        }

        for (Document document : documentDAO.getAllWithStatusAndLatestUpdateOlderThan(cal.plusDays(-10), DocumentStatus.OUTDATED)) {
            log.info("Archive the document: {} with last update {}", document.getUuid(), document.getLatestUpdateReceived());

            document.setStatus(DocumentStatus.ARCHIVED);

            documentDAO.save(document);
        }

    }

}