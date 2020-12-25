package com.yaslebid.fileStorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yaslebid.fileStorage.controller.model.File;
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

import java.util.*;

@RestController
@RequestMapping("/")
public class FileController {
    private FilesRepository filesRepository;
    private ElasticsearchOperations elasticsearchOperations;

    public FileController(ElasticsearchOperations elasticsearchOperations, FilesRepository filesRepository) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.filesRepository = filesRepository;
    }

    // POST File data
    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<ObjectNode> save(@RequestBody File file) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        try {
            if (!ifFileNameCorrect(file)) {
                objectNode.put("success", false);
                objectNode.put("error", "wrong or empty file name");
                return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
            }

            if (!ifFileSizeCorrect(file)) {
                objectNode.put("success", false);
                objectNode.put("error", "wrong or empty file size, should be not less than 0");
                return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
            }

            IndexCoordinates indexCoordinates = elasticsearchOperations.getIndexCoordinatesFor(file.getClass());
            IndexQuery indexQuery = new IndexQueryBuilder().withObject(file).build();
            String fileId = elasticsearchOperations.index(indexQuery, indexCoordinates);
            objectNode.put("ID", file.getId());
            return new ResponseEntity<>(objectNode, HttpStatus.OK);
        } catch (Exception exception) {
            objectNode.put("error", "exception: " + exception.getMessage());
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
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
        }
    }

    // GET file data by ID
    @RequestMapping(value = "/file/{ID}", method = RequestMethod.GET)
    public File findById (@PathVariable("ID")  String id) {
        if (filesRepository.findById(id).isPresent()) {
            return filesRepository.findById(id).get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // List files with pagination optionally filtered by tags
    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public ResponseEntity<ObjectNode> findUsingFilter (
                @RequestParam(required = false) List<String> tags,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size) {

        ObjectMapper mapper = new ObjectMapper();
        Pageable pageToRespond = PageRequest.of(page, size);
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        //return ALL files data if tags not provided
        if (tags == null || tags.size() == 0) {
            List<File> allFilesList = new ArrayList<>();
            filesRepository.findAll().forEach(allFilesList::add);
            ArrayNode array = mapper.valueToTree(allFilesList);
            objectNode.put("total", allFilesList.size());
            objectNode.put("page", array);
            return new ResponseEntity<>(objectNode, HttpStatus.OK);
        }

        //TODO: Only files containing ALL of supplied tags should return
        // but currently return files containing ANY of tags
        JSONArray jsArray = new JSONArray(tags);
        List<File> resultList = filesRepository.findByFilteredTagQuery(jsArray, pageToRespond).getContent();
        Long total = filesRepository.findByFilteredTagQuery(jsArray, pageToRespond).getTotalElements();
        ArrayNode array = mapper.valueToTree(resultList);
        objectNode.put("total", total);
        objectNode.put("page", array);

        return new ResponseEntity<>(objectNode, HttpStatus.OK);
    }

    // ADD tags to file data
    @RequestMapping(value = "/file/{ID}/tags", method = RequestMethod.POST)
    public ResponseEntity<ObjectNode> addTagsToFile(@PathVariable("ID") String id, @RequestBody String[] tags) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        if (filesRepository.findById(id).isEmpty()) {
            objectNode.put("success", false);
            objectNode.put("error", "file not found");
            return new ResponseEntity<>(objectNode, HttpStatus.NOT_FOUND);
        }

        try {
            File file = filesRepository.findById(id).get();
            Arrays.stream(tags).forEach(file::addTag);
            filesRepository.save(file);
            objectNode.put("success", true);
            return new ResponseEntity<>(objectNode, HttpStatus.OK);
        } catch (Exception exception) {
            objectNode.put("error", "exception: " + exception.getMessage());
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE tags from file data
    @RequestMapping(value = "/file/{ID}/tags", method = RequestMethod.DELETE)
    public ResponseEntity<ObjectNode> removeTagsFromFile(@PathVariable("ID") String id, @RequestBody String[] tags) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        if (filesRepository.findById(id).isEmpty()) {
            objectNode.put("success", false);
            objectNode.put("error", "file not found");
            return new ResponseEntity<>(objectNode, HttpStatus.NOT_FOUND);
        } else if (!filesRepository.findById(id).get().getTags().containsAll(Arrays.asList(tags))) {
            objectNode.put("success", false);
            objectNode.put("error", "tag not found on file");
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
        }

        try {
            File file = filesRepository.findById(id).get();
            file.getTags().removeAll(Arrays.asList(tags));
            filesRepository.save(file);
            objectNode.put("success", true);
            return new ResponseEntity<>(objectNode, HttpStatus.OK);
        } catch (Exception exception) {
            objectNode.put("error", "exception: " + exception.getMessage());
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