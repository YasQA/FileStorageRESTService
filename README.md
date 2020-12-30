# FileStorageRESTService
File data Storage RESTService project (to store just file data: file name and size for the first time)

Application developed using Java 11, Maven, Spring Boot and Elasticsearch as DataStorage;  

Preconditions to run:
1. Elasticsearch should be up and running locally(on OS or using docker) on default port 9200;

How to use:

1. Create new file:
POST /file

{
   "name": "file_name.ext"
   "size" : 121231     
}

(file tags for famoust file types will be added automatically, e.g 'image' to *.png, *.jpg ...)

2. Delete file:
DELETE  /file/{ID}

3. Assign tags to file:
POST /file/{ID}/tags

["tag1", "tag2", "tag3"]

4. Remove tags from file
DELETE /file/{ID}/tags

["tag1", "tag3"]

5. List files with pagination optionally filtered by tags and quired by name with wildcards
GET /file?tags=tag1,tag2,tag3&page=2&size=3&q=txt
