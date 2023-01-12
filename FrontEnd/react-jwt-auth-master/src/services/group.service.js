import axios from "axios";
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/';

class GroupService{
    getGroups() {
        return axios.get(API_URL + 'getGroupsOfUser' , { headers: authHeader() });
      }
      deleteGroup(id){
        return axios.delete(API_URL + 'deleteGroup/'+ id, { headers: authHeader() });
      }
      addUserToGroup(userId , groupId){
        return axios.post(API_URL + 'addUsersToGroup/'+ userId +'/'+groupId,null, { headers: authHeader() });
      }
      addFileToGroup(fileId, groupId){
        return axios.post(API_URL + 'addFile/'+ fileId +'/'+groupId,null, { headers: authHeader() });
      }
      getAllUsersInGroup(groupId){
        return axios.get(API_URL + 'getAllUsersInGroup/'+groupId, { headers: authHeader() })
      }
      deleteUserFromGroup(userId, groupId){
        return axios.delete(API_URL + 'deleteUserFromGroup/'+ userId +'/'+groupId, { headers: authHeader() });
      }
      createGroup(groupName,ownerId){
        console.log(groupName);
        console.log(ownerId);
        return axios.post(API_URL + 'createGroup/',{groupName,ownerId},{ headers: authHeader() });
      }

}
export default new GroupService();