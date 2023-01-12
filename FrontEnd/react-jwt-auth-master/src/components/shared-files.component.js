import React, { Component } from "react";
import fileService from "../services/file.service";
import groupService from "../services/group.service";
import authHeader from "../services/auth-header";


class ShareFile extends Component {
  constructor(props) {
    super(props);

    this.state = {
      files: [],
      ids: [],
    };
    this.readFile = this.readFile.bind(this);
    this.lockFile = this.lockFile.bind(this);
    this.onChangeChecked = this.onChangeChecked.bind(this);
    this.bulckLock = this.bulckLock.bind(this);
  }

  bulckLock(){
    fileService.bulkLockFile(this.state.ids)
    window.location.reload()
  }
  onChangeChecked (fileId) {
    console.log(this.state.ids)
    if(this.state.ids.includes(fileId)){
      this.state.ids.pop(fileId)
    }
    else{
      this.state.ids.push(fileId)
    }
  }
  unlockFile(fileId){
    fileService.unlockFile(fileId)
    window.location.reload()
  }
  lockFile(fileId ,uri , locked){
    if(locked){
      console.log("locked");
    }
    else{
      fileService.lockFile(fileId)
      localStorage.setItem("downloadUri", JSON.stringify(uri))
      window.location.href = "/Download";

    }
  }
  readFile(contentType , uri){
    localStorage.setItem("contentType", JSON.stringify(contentType))
    localStorage.setItem("downloadUri", JSON.stringify(uri))
    window.location.href = "/FileRead";
  }
  componentDidMount() {
    fileService.getAllFilesInGroup(1).then((res) => {
      this.setState({ files: res.data });
    });
  }
  render() {
    return (
      <div>
        <h2 className="text-center">Files List</h2>
        <div className="row">
        <div className = "row">
            <button className="btn btn-primary" onClick={this.bulckLock}> Bulk Lock</button>
       </div>
      <br></br>
          <table className="table table-striped table-bordered">
            <thead>
              <tr>
                <th></th>
                <th> File Name</th>
                <th> Owner Username</th>
                <th> Locked</th>
                <th> Locked By</th>
                <th> Actions </th>
              </tr>
            </thead>
            <tbody>
              {this.state.files.map((file) => (
                <tr key={file.id}>
                  <td><input type="checkbox" className="selectsingle" onChange={()=>this.onChangeChecked(file.id)} /></td>
                  <td> {file.fileName} </td>
                  <td> {file.username}</td>
                  <td> {file.lock.toString()}</td>
                  <td> {file.lockedBy}</td>
                  <td>
                  <button
                      style={{ marginLeft: "10px" }}
                      onClick={() => this.readFile(file.contentType , file.uri)}
                      className="btn btn-secondary"
                    >
                      Read File{" "}
                    </button>
                    <button
                      style={{ marginLeft: "10px" }}
                      onClick={() => this.lockFile(file.id ,file.uri , file.lock)}
                      className="btn btn-warning"
                    >
                      Lock File{" "}
                    </button>
                    <button
                      style={{ marginLeft: "10px" }}
                      onClick={() => this.unlockFile(file.id)}
                      className="btn btn-dark"
                    >
                      Unlock File{" "}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    );
  }
}

export default ShareFile;
