import React, { Component } from "react";
import fileService from "../services/file.service";
import groupService from "../services/group.service";
import Axios from "axios";
import authHeader from "../services/auth-header";
class AddFileToGroup extends Component {
  constructor(props) {
    super(props);

    this.state = {
      files: [],
    };
    this.addFile=this.addFile.bind(this);
  }
  addFile(fileId){
    console.log(fileId);
    console.log(JSON.parse(localStorage.getItem('group')));
    groupService.addFileToGroup(fileId,JSON.parse(localStorage.getItem('group'))).then((res)=>{
        window.location.href = "/fileInGroup";
    });

  }

  componentDidMount() {
    fileService.getFiles().then((res) => {
      this.setState({ files: res.data });
    });
  }
  render() {
    return (
      <div>
        <h2 className="text-center">Files List</h2>
        <br></br>
        <div className="row">
          <table className="table table-striped table-bordered">
            <thead>
              <tr>
                <th> File ID</th>
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
                  <td> {file.id} </td>
                  <td> {file.fileName} </td>
                  <td> {file.username}</td>
                  <td> {file.lock.toString()}</td>
                  <td> {file.lockedBy}</td>
                  <td>
                    <button
                      style={{ marginLeft: "10px" }}
                      onClick={() => this.addFile(file.id)}
                      className="btn btn-info"
                    >
                      Select{" "}
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

export default AddFileToGroup;
