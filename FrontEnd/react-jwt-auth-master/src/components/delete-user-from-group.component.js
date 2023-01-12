import React, { Component } from "react";
import fileService from "../services/file.service";
import groupService from "../services/group.service";
import userService from "../services/user.service";
import Axios from "axios";
import authHeader from "../services/auth-header";
class DeleteUsersFromGroup extends Component {
  constructor(props) {
    super(props);

    this.state = {
      users: [],
    };
    this.deleteUser=this.deleteUser.bind(this);
  }
  deleteUser(userId){
    console.log(userId);
    console.log(JSON.parse(localStorage.getItem('group')));
    groupService.deleteUserFromGroup(userId,JSON.parse(localStorage.getItem('group'))).then((res)=>{
        this.setState({
            users: this.state.users.filter((user) => user.id !== userId),
          });
    });
  }

  componentDidMount() {
    console.log(JSON.parse(localStorage.getItem('group')))
    groupService.getAllUsersInGroup(JSON.parse(localStorage.getItem('group'))).then((res) => {
      this.setState({ users: res.data });
    });
  }
  render() {
    return (
      <div>
        <h2 className="text-center">Users List</h2>
        <br></br>
        <div className="row">
          <table className="table table-striped table-bordered">
            <thead>
              <tr>
                <th> User ID</th>
                <th> User Name</th>
                <th> Actions </th>
              </tr>
            </thead>
            <tbody>
              {this.state.users.map((user) => (
                <tr key={user.id}>
                  <td> {user.id} </td>
                  <td> {user.username} </td>
                  <td>
                    <button
                      style={{ marginLeft: "10px" }}
                      onClick={() => this.deleteUser(user.id)}
                      className="btn btn-danger"
                    >
                      Delete{" "}
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

export default DeleteUsersFromGroup;
