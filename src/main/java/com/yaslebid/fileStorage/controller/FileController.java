package com.yaslebid.fileStorage.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yaslebid.fileStorage.controller.model.File;
import com.yaslebid.fileStorage.repository.FilesRepository;
import com.yaslebid.fileStorage.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/")
public class FileController {
    private final FilesRepository filesRepository;

    public FileController(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    // POST new File data
    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<ObjectNode> save(@RequestBody File file) {
        FileCreator fileCreator = new NewFileCreator(filesRepository);
        ObjectNode objectNode = fileCreator.createFile(file);
        if (!objectNode.has("error")) {
            return new ResponseEntity<>(objectNode, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE file data by ID
    @RequestMapping(value = "/file/{ID}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<ObjectNode> deleteById (@PathVariable("ID")  String id) {
        FileRemover fileRemover = new FileRemoverById(filesRepository);
        ObjectNode objectNode = fileRemover.deleteFileById(id);

        if (objectNode.has("success") && objectNode.has("error")) {
            return new ResponseEntity<>(objectNode, HttpStatus.NOT_FOUND);
        } else if (objectNode.has("success")) {
            return new ResponseEntity<>(objectNode, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
        }
    }

    // GET file data by ID
    @RequestMapping(value = "/file/{ID}", method = RequestMethod.GET)
    public File findById (@PathVariable("ID")  String id) {
        FileFinderSimple fileFinderSimple = new FileFinderById(filesRepository);
        File file = fileFinderSimple.getFileById(id);
        if (file.getId() != null) {
            return file;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // GET files with pagination optionally filtered by tags, also 'q=' applied for file name search with wildcards
    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public ResponseEntity<ObjectNode> findUsingFilter (
                @RequestParam(required = false) List<String> tags,
                @RequestParam(required = false) String q,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(defaultValue = "0") int page) {

        FileFinderParametrized fileFinder = new FileFinderFiltered(filesRepository);
        ObjectNode objectNode = fileFinder.searchFiles(tags, q, size, page);

        if (objectNode.has("error")) {
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(objectNode, HttpStatus.OK);
        }
    }

    // ADD tags to file by ID
    @RequestMapping(value = "/file/{ID}/tags", method = RequestMethod.POST)
    public ResponseEntity<ObjectNode> addTagsToFile(@PathVariable("ID") String id, @RequestBody String[] tags) {
        FileTagsCreator tagsCreator = new FileTagsCreatorByFileId(filesRepository);
        ObjectNode objectNode = tagsCreator.addTags(id, tags);

        if (objectNode.has("success") && objectNode.has("error")) {
            return new ResponseEntity<>(objectNode, HttpStatus.NOT_FOUND);
        } else if (objectNode.has("success")) {
            return new ResponseEntity<>(objectNode, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE tags from file by ID
    @RequestMapping(value = "/file/{ID}/tags", method = RequestMethod.DELETE)
    public ResponseEntity<ObjectNode> removeTagsFromFile(@PathVariable("ID") String id, @RequestBody String[] tags) {
        FileTagsRemover tagsRemover = new FileTagsRemoverByFileId(filesRepository);
        ObjectNode objectNode = tagsRemover.removeTags(id, tags);

        if (objectNode.has("success") && objectNode.has("error")) {
            if (objectNode.get("error").textValue().equals("file not found")) {
                return new ResponseEntity<>(objectNode, HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
            }
        } else if (objectNode.has("success")) {
            return new ResponseEntity<>(objectNode, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
        }
    }

}