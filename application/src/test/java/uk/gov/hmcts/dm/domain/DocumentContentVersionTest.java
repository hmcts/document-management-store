package uk.gov.hmcts.dm.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static java.nio.charset.Charset.defaultCharset;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DocumentContentVersionTest {

    @Mock
    private StoredDocument item;

    private static final String FILE_NAME = "The Little Prince";
    private static final String FILE_ORIGINAL_FILE_NAME = "Le Petit Prince";
    private static final String FILE_TYPE = "avec les illustrations originales de l'auteur";
    private static final byte[] FILE_CONTENT = "Antoine de Saint-Exupéry".getBytes(defaultCharset());

    private static final MultipartFile FILE = new MockMultipartFile(FILE_NAME, FILE_ORIGINAL_FILE_NAME, FILE_TYPE, FILE_CONTENT);

    @Before
    public void setUp() {
    }

    @Test
    public void deprecatedDocumentContentIsUsed() {
        final DocumentContentVersion documentContentVersion = new DocumentContentVersion(item, FILE, "userId", false);

        assertDocumentContentVersionCommon(documentContentVersion);
        assertThat(documentContentVersion.getDocumentContent(), not(nullValue()));
    }

    @Test
    public void deprecatedDocumentContentIsNotUsed() {
        final DocumentContentVersion documentContentVersion = new DocumentContentVersion(item, FILE, "userId", true);

        assertDocumentContentVersionCommon(documentContentVersion);
        assertThat(documentContentVersion.getDocumentContent(), is(nullValue()));
    }

    private void assertDocumentContentVersionCommon(final DocumentContentVersion documentContentVersion) {
        assertThat(documentContentVersion.getStoredDocument(), is(item));
        assertThat(documentContentVersion.getMimeType(), is(FILE_TYPE));
        assertThat(documentContentVersion.getOriginalDocumentName(), is(FILE_ORIGINAL_FILE_NAME));
        assertThat(documentContentVersion.getSize(), is(Long.valueOf(FILE_CONTENT.length)));
        assertThat(documentContentVersion.getContentUri(), is(nullValue()));
        assertThat(documentContentVersion.getCreatedBy(), is("userId"));
    }

}
