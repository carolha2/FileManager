package com.example.demo.groups;

import com.example.demo.User.UserRepository;
import com.example.demo.User.UserServices;
import com.example.demo.fileDB.FileRepository;
import com.example.demo.fileDB.ResourceNotFoundException;
import com.example.demo.model.FileDocument;
import com.example.demo.model.Groups;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class GroupController {

    Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    UserServices userServices;

    @GetMapping("/getGroupsOfUser")
    public List<Groups> getGroupsOfUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        long userId = userRepository.findByUsername(currentPrincipalName).getId();
        List <Groups> groupsOfUser = new ArrayList<>();
        List <Groups> returned = new ArrayList<>();
        groupsOfUser = groupRepository.findAll();
        if (userServices.isAdmin(userServices.getRole(currentPrincipalName))){
            logger.info(String.valueOf(groupsOfUser));
            return groupsOfUser;
        }
        for(int i =0 ;i<groupsOfUser.size();i++){
            if(groupsOfUser.get(i).getOwnerId() == userId){
                returned.add(groupsOfUser.get(i));
            }
        }
        logger.info(String.valueOf(returned));
        return returned;
    }

    @PostMapping("/createGroup")
    public Groups createGroup(@RequestBody Groups groups) {
        boolean exists = false;
        logger.info(groups.toString());
        logger.info(String.valueOf(groups));
        List <Groups> allGroups = groupRepository.findAll();
        for(int i=0;i<allGroups.size();i++){
            if(allGroups.get(i).getGroupName().equalsIgnoreCase(groups.getGroupName())){
                System.out.println("ftt 3nd "+allGroups.get(i).getGroupName());
                exists=true;
                break;
            }
            System.out.println("current group name "+allGroups.get(i).getGroupName());
        }
        if (!exists) {
            logger.info(groupRepository.save(groups).toString());
            logger.info(String.valueOf(groupRepository.save(groups)));
            return groupRepository.save(groups);
        }
        else{
            logger.error("Group With Name "+groups.getGroupName()+" Already Exists! ");
            throw new RuntimeException("Group With Name "+groups.getGroupName()+" Already Exists! ");
        }
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @RequestMapping(value = "/addFile/{file_id}/{group_idi}" , method = RequestMethod.POST)
    public ResponseEntity<Groups> addFileToGroup (@PathVariable String file_id ,@PathVariable String group_idi){
        logger.info("file id "+file_id);
        logger.info("group id "+group_idi);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Groups groups = groupRepository.findById(Long.parseLong(group_idi))
                .orElseThrow(() -> new ResourceNotFoundException("Group not exist with id :" + group_idi));
        FileDocument fileDocument = fileRepository.findById(Long.parseLong(file_id))
                .orElseThrow(() -> new ResourceNotFoundException("File not exist with id :" + file_id));
        if(fileDocument.getLock()==false ){
            if(fileDocument.getUsername().equalsIgnoreCase(currentPrincipalName)){
                Set<FileDocument> files = groups.getFiles();
                files.add(fileDocument);
                groups.setFiles(files);
                groupRepository.save(groups);
                ResponseEntity responseEntity = ResponseEntity.ok(groups);
                logger.info(String.valueOf(responseEntity));
                return responseEntity;
            }
            else{
                logger.error("You are not the owner of the file! the owner is "+fileDocument.getUsername()+" and you are "+currentPrincipalName);
                throw new RuntimeException("You are not the owner of the file! the owner is "+fileDocument.getUsername()+" and you are "+currentPrincipalName);
            }
        }
        else{
            logger.error("File locked! ");
            throw new RuntimeException("File locked! ");
        }
    }

    @CrossOrigin("http://localhost:8081")
    @GetMapping("/getFilesInGroup/{id}")
    public Set<FileDocument> getAllFilesInGroup(@PathVariable Long id){
        Groups group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not exist with id :" + id));
        return group.getFiles();
    }


    @DeleteMapping("/deleteFileFromGroup/{file_id}/{group_idi}")
    public ResponseEntity<Map<String, Boolean>> deleteFileFromGroup(@PathVariable Long file_id ,@PathVariable Long group_idi){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Groups groups = groupRepository.findById(group_idi)
                .orElseThrow(() -> new ResourceNotFoundException("Group not exist with id :" + group_idi));
        FileDocument fileDocument = fileRepository.findById(file_id)
                .orElseThrow(() -> new ResourceNotFoundException("File not exist with id :" + file_id));
        boolean removed=false;
        if(fileDocument.getLock()==false ){
            if(fileDocument.getUsername().equalsIgnoreCase(currentPrincipalName)){
                Set<FileDocument> files = groups.getFiles();
                removed = files.remove(fileDocument);
                groups.setFiles(files);
                groupRepository.save(groups);
            }
            else{
                logger.error("You are not the owner of the file! the owner is "+fileDocument.getUsername()+" and you are "+currentPrincipalName);
                throw new RuntimeException("You are not the owner of the file! the owner is "+fileDocument.getUsername()+" and you are "+currentPrincipalName);
            }
        }
        else{
            logger.error("File locked! ");
            throw new RuntimeException("File locked! ");
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", removed);
        logger.info(String.valueOf(response));
        return ResponseEntity.ok(response);
    }

    @CrossOrigin("http://localhost:8081")
    @GetMapping("/getAllUsersInGroup/{id}")
    public Set<User> getAllUsersInGroup(@PathVariable Long id){
        Groups group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not exist with id :" + id));
        return group.getUser();
    }

    @CrossOrigin("http://localhost:8081")
    @GetMapping("/getUsers")
    public List<User> getUsers(){
        List<User> users = userRepository.findAll();
        return users;
    }

    @PostMapping("/addUsersToGroup/{user_id}/{group_idi}")
    public ResponseEntity<Groups> addUsersToGroup(@PathVariable Long user_id, @PathVariable Long group_idi){
        logger.info("user id "+user_id);
        logger.info("group id "+group_idi);
        Groups groups = groupRepository.findById(group_idi)
                .orElseThrow(() -> new ResourceNotFoundException("Group not exist with id :" + group_idi));
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with id :" + user_id));
        Set<User> users = groups.getUser();
        users.add(user);
        groups.setUser(users);
        groupRepository.save(groups);
        ResponseEntity responseEntity = ResponseEntity.ok(groups);
        logger.info(String.valueOf(responseEntity));
        return responseEntity;
    }


    @DeleteMapping("/deleteUserFromGroup/{user_id}/{group_idi}")
    public ResponseEntity<Map<String, Boolean>> deleteUserFromGroup(@PathVariable Long user_id ,@PathVariable Long group_idi){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Groups groups = groupRepository.findById(group_idi)
                .orElseThrow(() -> new ResourceNotFoundException("Group not exist with id :" + group_idi));
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new ResourceNotFoundException("File not exist with id :" + user_id));
        boolean removed=false;
        boolean userHasSomethingLocked = false;
        Set<FileDocument>files = groups.getFiles();
        for(FileDocument array : files){
            if(array.getLock()){
                if(array.getLockedBy().equalsIgnoreCase(user.getUsername())){
                    userHasSomethingLocked=true;
                }
            }
        }
        if(!userHasSomethingLocked){
            Set<User> users = groups.getUser();
            removed = users.remove(user);
            groups.setUser(users);
            groupRepository.save(groups);
        }
        else{
            logger.error("user has something locked! ");
            throw new RuntimeException("user has something locked! ");
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", removed);
        logger.info(String.valueOf(response));
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/deleteGroup/{group_idi}")
    public ResponseEntity<Map<String, Boolean>> deleteGroup(@PathVariable Long group_idi){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Groups groups = groupRepository.findById(group_idi)
                .orElseThrow(() -> new ResourceNotFoundException("Group not exist with id :" + group_idi));
        Set<User> users = groups.getUser();
        boolean userHasSomethingLocked = false;
        for(User tempUser : users){
            Set<FileDocument>files = groups.getFiles();
            for(FileDocument array : files){
                if(array.getLock()){
                    if(array.getLockedBy().equalsIgnoreCase(tempUser.getUsername())){
                        userHasSomethingLocked=true;
                    }
                }
            }
        }
        if(!userHasSomethingLocked){
            groupRepository.delete(groups);
        }
        else{
            logger.error("some user has something locked! ");
            throw new RuntimeException("some user has something locked! ");
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        logger.info(String.valueOf(response));
        return ResponseEntity.ok(response);
    }
}
