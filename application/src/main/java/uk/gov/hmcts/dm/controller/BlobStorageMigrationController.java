package uk.gov.hmcts.dm.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.dm.hateos.DocumentContentVersionHalResource;
import uk.gov.hmcts.dm.service.BatchMigrateProgressReport;
import uk.gov.hmcts.dm.service.BlobStorageMigrationService;
import uk.gov.hmcts.dm.service.MigrateProgressReport;

import java.util.UUID;

import static org.springframework.http.HttpHeaders.IF_MATCH;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@Api("Endpoint for Document Content Migration from PostgreSQL to Azure BlobStorage")
public class BlobStorageMigrationController {

    private final BlobStorageMigrationService blobStorageMigrationService;

    public BlobStorageMigrationController(BlobStorageMigrationService blobStorageMigrationService) {
        this.blobStorageMigrationService = blobStorageMigrationService;
    }

    @GetMapping(value = "/migrate")
    @ApiOperation("Gets a migration progress report")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "JSON representation of a migration progress report",
        response = MigrateProgressReport.class)})
    public ResponseEntity<MigrateProgressReport> getMigrateProgressReport() {
        return ResponseEntity.ok(blobStorageMigrationService.getMigrateProgressReport());
    }

    @PostMapping(value = "/migrate/token")
    @ApiOperation("Gets a token for a migration job")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Auth token for a migration job",
        response = String.class)})
    public ResponseEntity<String> getMigrateAuthToken(@RequestParam("public_key") MultipartFile publicKey) {
        return ResponseEntity.ok(blobStorageMigrationService.getAuthToken(publicKey));
    }

    @PostMapping(value = "/migrate")
    @ApiOperation("Starts a batch migration process")
    @ApiResponses(value = {@ApiResponse(code = 200,
        message = "JSON representation of a batch migration progress report",
        response = MigrateProgressReport.class)})
    public ResponseEntity<BatchMigrateProgressReport> batchMigrate(@RequestHeader(IF_MATCH) String authToken,
                                                                   @RequestHeader("x-api-key") String migrateSecret,
                                                                   @RequestParam(value = "limit", required = false)
                                                                           Integer limit,
                                                                   @RequestParam(value = "ry-mock-run", required = false)
                                                                           Boolean mockRun) {
        return ResponseEntity.ok(blobStorageMigrationService.batchMigrate(migrateSecret, authToken, limit, mockRun));
    }

    @PostMapping(value = "/documents/{documentId}/versions/{versionId}/migrate")
    @ApiOperation("Starts migration for a specific version of the content of a Stored Document.")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "JSON representation of a document version", response = DocumentContentVersionHalResource.class)
    })
    public ResponseEntity<Object> migrateDocument(
        @PathVariable UUID documentId,
        @PathVariable UUID versionId) {

        blobStorageMigrationService.migrateDocumentContentVersion(documentId, versionId);

        return ResponseEntity.status(NO_CONTENT).build();
    }
}