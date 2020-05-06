package uk.gov.hmcts.reform.dm.hateos;

import org.springframework.hateoas.Resources;
import uk.gov.hmcts.reform.dm.domain.StoredDocument;

import java.util.List;
import java.util.stream.Collectors;

public class StoredDocumentHalResourceCollection extends Resources<StoredDocumentHalResource> {

    public StoredDocumentHalResourceCollection(List<StoredDocument> storedDocuments) {
        super(storedDocuments
                .stream()
                .map(StoredDocumentHalResource::new)
                .collect(Collectors.toList()));

    }
}