import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/';
const API_URL2 = 'http://localhost:9090/';

class FileService {
  generateRandom(maxLimit = 2){
    let rand = Math.random() * maxLimit;
    console.log(rand);
  
    rand = Math.floor(rand); // 99
  
    return rand;
  }
  getFiles() {
    if(this.generateRandom() === 0){
      return axios.get(API_URL + 'list' , { headers: authHeader() });
    }
    else{
      return axios.get(API_URL2 + 'list' , { headers: authHeader() });
    }
  }

  deleteFile(fileName) {
    if(this.generateRandom() === 0){
      return axios.delete(API_URL + 'delete/'+ fileName, { headers: authHeader() });
    }
    else{
      return axios.delete(API_URL2 + 'delete/'+ fileName, { headers: authHeader() });
    }
  }
  getAllFilesInGroup(id){
    if(this.generateRandom() === 0){
      return axios.get(API_URL + 'getFilesInGroup/'+ id, { headers: authHeader() });
    }
    else{
      return axios.get(API_URL2 + 'getFilesInGroup/'+ id, { headers: authHeader() });
    }
  }
  deleteFileFromGroup(fileId , groupId){
    if(this.generateRandom() === 0){
      return axios.delete(API_URL + 'deleteFileFromGroup/'+ fileId+'/'+groupId,{ headers: authHeader() });
    }
    else{
      return axios.delete(API_URL2 + 'deleteFileFromGroup/'+ fileId+'/'+groupId,{ headers: authHeader() });
    }
  }
  lockFile(fileId) {
    if(this.generateRandom() === 0){
      return axios.post(API_URL + 'lockFile/'+ fileId ,null, { headers: authHeader() });
    }
    else{
      return axios.post(API_URL2 + 'lockFile/'+ fileId ,null, { headers: authHeader() });
    }
  }
  bulkLockFile(fileIds) {
    if(this.generateRandom() === 0){
      return axios.post(API_URL + 'bulkLockFile/'+ fileIds ,null, { headers: authHeader() });
    }
    else{
      return axios.post(API_URL2 + 'bulkLockFile/'+ fileIds ,null, { headers: authHeader() });
    }
  }
  
  unlockFile(fileId) {
    if(this.generateRandom() === 0){
      return axios.post(API_URL + 'unlockFile/'+ fileId ,null, { headers: authHeader() });
    }
    else{
      return axios.post(API_URL + 'unlockFile/'+ fileId ,null, { headers: authHeader() });
    }
  }


}
export default new FileService();
