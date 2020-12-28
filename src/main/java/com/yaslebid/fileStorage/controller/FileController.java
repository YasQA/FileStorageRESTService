package com.yaslebid.fileStorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yaslebid.fileStorage.controller.model.File;
import com.yaslebid.fileStorage.helpers.FileTypeResolver;
import com.yaslebid.fileStorage.repository.FilesRepository;
import org.json.JSONArray;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static com.yaslebid.fileStorage.FileStorageRestServiceApplication.LOGGER;

import java.util.*;

@RestController
@RequestMapping("/")
public class FileController {
    private final FilesRepository filesRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public FileController(ElasticsearchOperations elasticsearchOperations, FilesRepository filesRepository) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.filesRepository = filesRepository;
    }

    // POST new File data
    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<ObjectNode> save(@RequestBody File file) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

            if (!ifFileNameCorrect(file)) {
                objectNode.put("success", false);
                objectNode.put("error", "wrong or empty file name");
                LOGGER.error("wrong or empty file name");
                return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
            }

            if (!ifFileSizeCorrect(file)) {
                objectNode.put("success", false);
                objectNode.put("error", "wrong or empty file size, should be not less than 0");
                LOGGER.error("wrong or empty file size, should be not less than 0");
                return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
            }

        try {
            String fileType = new FileTypeResolver().identifyFileType(file);
            if (!fileType.equals("unknown")) {
                    file.addTag(new FileTypeResolver().identifyFileType(file));
            }
            IndexCoordinates indexCoordinates = elasticsearchOperations.getIndexCoordinatesFor(file.getClass());
            IndexQuery indexQuery = new IndexQueryBuilder().withObject(file).build();
            String fileId = elasticsearchOperations.index(indexQuery, indexCoordinates);
            objectNode.put("ID", file.getId());
            LOGGER.info("file: " + file.getName() + " created successfully");
            return new ResponseEntity<>(objectNode, HttpStatus.OK);

        } catch (Exception exception) {
            objectNode.put("error", "exception: " + exception.getMessage());
            LOGGER.error("Failed to create file: " + exception.getMessage());
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);

        }
    }

    // DELETE file data by ID
    @RequestMapping(value = "/file/{ID}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<ObjectNode> deleteById (@PathVariable("ID")  String id) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        if (filesRepository.findById(id).isEmpty()) {
            objectNode.put("success", false);
            objectNode.put("error", "file not found");
            return new ResponseEntity<>(objectNode, HttpStatus.NOT_FOUND);
        }

        try {
            filesRepository.deleteById(id);
            objectNode.put("success", true);
            return new ResponseEntity<>(objectNode, HttpStatus.OK);
        } catch (Exception exception) {
            objectNode.put("error", "exception: " + exception.getMessage());
            LOGGER.error("Failed to delete file: " + exception.getMessage());
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
        }
    }

    // GET file data by ID
    @RequestMapping(value = "/file/{ID}", method = RequestMethod.GET)
    public File findById (@PathVariable("ID")  String id) {
        if (filesRepository.findById(id).isPresent()) {
            return filesRepository.findById(id).get();
        } else {
            LOGGER.error("Failed to get file: Not Found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // GET files with pagination optionally filtered by tags, also 'q=' applied for file name search with wildcards
    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public ResponseEntity<ObjectNode> findUsingFilter (
                @RequestParam(required = false) List<String> tags,
                @RequestParam(required = false) String q,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size) {

        long total = 0;
        ArrayNode arrayNode;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        JSONArray jsArrayTags = new JSONArray(tags);
        List<File> resultList = new ArrayList<>();
        Pageable pageToRespond = PageRequest.of(page, size);

        try {
            if ((tags != null && tags.size() > 0) && (q != null && !q.isEmpty())) {
                resultList = filesRepository
                        .findByNameContainingQandTags("*" + q + "*", jsArrayTags, pageToRespond)
                        .getContent();
                total = filesRepository
                        .findByNameContainingQandTags("*" + q + "*", jsArrayTags, pageToRespond)
                        .getTotalElements();
                LOGGER.info("Both 'q=' and 'tags=' provided in request path, result total: " + total);

            } else if (tags != null && tags.size() > 0) {
                resultList = filesRepository.findByFilteredTagQuery(jsArrayTags, pageToRespond).getContent();
                total = filesRepository.findByFilteredTagQuery(jsArrayTags, pageToRespond).getTotalElements();
                LOGGER.info("'q=' not provided, returns files with ALL required 'tags=', result total: " + total);

            } else if (q != null && !q.isEmpty()) {
                resultList = filesRepository.findByNameContainingQ("*" + q + "*", pageToRespond).getContent();
                total = filesRepository.findByNameContainingQ("*" + q + "*", pageToRespond).getTotalElements();
                LOGGER.info("'q=' provided, 'tags=' not provided in request path, result total: " + total);
            } else {
                filesRepository.findAll().forEach(resultList::add);
                total = resultList.size();
                LOGGER.info("'tags=' and 'q=' not provided, list of all files returned, result total: " + total);
            }

            arrayNode = mapper.valueToTree(resultList);
            objectNode.put("total", total);
            objectNode.put("page", arrayNode);
            return new ResponseEntity<>(objectNode, HttpStatus.OK);

        } catch (Exception exception) {
            objectNode.put("error", "exception: " + exception.getMessage());
            LOGGER.error("Fails to get list of files according to the request" + exception.getMessage());
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
        }
    }

    // ADD tags to file by ID
    @RequestMapping(value = "/file/{ID}/tags", method = RequestMethod.POST)
    public ResponseEntity<ObjectNode> addTagsToFile(@PathVariable("ID") String id, @RequestBody String[] tags) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        if (filesRepository.findById(id).isEmpty()) {
            objectNode.put("success", false);
            objectNode.put("error", "file not found");
            LOGGER.error("Failed to add tags to file, file not found");
            return new ResponseEntity<>(objectNode, HttpStatus.NOT_FOUND);
        }

        try {
            File file = filesRepository.findById(id).get();
            Arrays.stream(tags).forEach(file::addTag);
            filesRepository.save(file);
            objectNode.put("success", true);
            LOGGER.info("Tags added to file: " + file.getName());
            return new ResponseEntity<>(objectNode, HttpStatus.OK);
        } catch (Exception exception) {
            objectNode.put("error", "exception: " + exception.getMessage());
            LOGGER.error("Failed to add tags to file: " + exception.getMessage());
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE tags from file by ID
    @RequestMapping(value = "/file/{ID}/tags", method = RequestMethod.DELETE)
    public ResponseEntity<ObjectNode> removeTagsFromFile(@PathVariable("ID") String id, @RequestBody String[] tags) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        if (filesRepository.findById(id).isEmpty()) {
            objectNode.put("success", false);
            objectNode.put("error", "file not found");
            LOGGER.error("Failed to delete tags from file, file not found");
            return new ResponseEntity<>(objectNode, HttpStatus.NOT_FOUND);
        } else if (!filesRepository.findById(id).get().getTags().containsAll(Arrays.asList(tags))) {
            objectNode.put("success", false);
            objectNode.put("error", "tag not found on file");
            LOGGER.error("Fails to delete tags from file, tag(-s) not found");
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
        }

        try {
            File file = filesRepository.findById(id).get();
            file.getTags().removeAll(Arrays.asList(tags));
            filesRepository.save(file);
            objectNode.put("success", true);
            LOGGER.info("File deleted: " + file.getName());
            return new ResponseEntity<>(objectNode, HttpStatus.OK);
        } catch (Exception exception) {
            objectNode.put("error", "exception: " + exception.getMessage());
            LOGGER.error("Fails to delete tags from file: " + exception.getMessage());
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
        }
    }

    private boolean ifFileNameCorrect(File file) {
        return file.getName() != null && !file.getName().equals("");
    }

    private boolean ifFileSizeCorrect(File file) {
        return file.getSize() != null && file.getSize() >= 0;
    }

}