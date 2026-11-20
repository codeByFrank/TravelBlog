package org.example.backend.service;

import org.bson.Document;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

@Component
public class PhotoStorage {

    private final GridFsTemplate gridFsTemplate;

    public PhotoStorage(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    public String save(MultipartFile file, String userId, String tripId) {
        Document metadata = new Document("userId", userId).append("tripId", tripId);
        try (InputStream content = file.getInputStream()) {
            return gridFsTemplate.store(content, file.getOriginalFilename(), file.getContentType(), metadata)
                    .toHexString();
        } catch (IOException e) {
            throw new UncheckedIOException("Photo could not be stored", e);
        }
    }
}