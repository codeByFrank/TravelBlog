package org.example.backend.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    public Optional<GridFsResource> find(String photoId, String userId) {
        if (!ObjectId.isValid(photoId)) {
            return Optional.empty();
        }
        Query query = new Query(Criteria.where("_id").is(new ObjectId(photoId)).and("metadata.userId").is(userId));
        GridFSFile file = gridFsTemplate.findOne(query);
        return file == null ? Optional.empty() : Optional.of(gridFsTemplate.getResource(file));
    }

    public void delete(Collection<String> photoIds) {
        List<ObjectId> ids = photoIds.stream()
                .filter(ObjectId::isValid)
                .map(ObjectId::new)
                .toList();
        if (!ids.isEmpty()) {
            gridFsTemplate.delete(new Query(Criteria.where("_id").in(ids)));
        }
    }
}