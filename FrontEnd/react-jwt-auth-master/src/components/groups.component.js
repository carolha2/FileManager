import React, { Component } from 'react'
import groupService from '../services/group.service';
import Axios from "axios";
import authHeader from "../services/auth-header";

class Groups extends Component {
    constructor(props) {
        super(props)

        this.state = {
                groups: []
        }
        this.deleteGroup = this.deleteGroup.bind(this);
        this.viewGroup = this.viewGroup.bind(this);
        this.addFileToGroup = this.addFileToGroup.bind(this);
        this.addUserToGroup = this.addUserToGroup.bind(this);
        this.users = this.users.bind(this);
        this.addGroup = this.addGroup.bind(this);
    }
    addGroup(){
        window.location.href = "/AddGroup";
    }
    users(groupId){
        localStorage.setItem("group", groupId);
        console.log(JSON.parse(localStorage.getItem('group')));
        window.location.href = "/DeleteUsersFromGroup";
    }
    addUserToGroup(id){
        localStorage.setItem("group", id);
        console.log(JSON.parse(localStorage.getItem('group')));
        window.location.href = "/AddUserToGroup";
    }

    addFileToGroup(id){
        localStorage.setItem("group", id);
        console.log(JSON.parse(localStorage.getItem('group')));
        window.location.href = "/AddFileToGroup";
    }
    viewGroup(id){
        localStorage.setItem("group", id);
        console.log(JSON.parse(localStorage.getItem('group')));
        window.location.href = "/fileInGroup";
    }
    deleteGroup(id) {
        groupService.deleteGroup(id).then((res) => {
          this.setState({
            groups: this.state.groups.filter((group) => group.id !== id),
          });
        });
      }

    componentDidMount(){
        groupService.getGroups().then((res) => {
            this.setState({ groups: res.data });
        });
    }

    render() {
        return (
            <div>
                 <h2 className="text-center">Groups List</h2>
                 <div className = "row">
                    <button className="btn btn-primary" onClick={this.addGroup}> Add Group</button>
                 </div>
                 <br></br>
                 <div className = "row">
                        <table className = "table table-striped table-bordered">

                            <thead>
                                <tr>
                                    <th> Group Name</th>
                                    <th> Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    this.state.groups.map(
                                        group => 
                                        <tr key = {group.id}>
                                             <td> { group.groupName} </td>   
                                             <td>
                                                <button
                                                    style={{ marginLeft: "10px" }}
                                                    onClick={() => this.viewGroup(group.id)}
                                                    className="btn btn-secondary"
                                                    >
                                                    View{" "}
                                                </button>
                                                <button
                                                    style={{ marginLeft: "10px" }}
                                                    onClick={() => this.addFileToGroup(group.id)}
                                                    className="btn btn-info"
                                                    >
                                                    Add File{" "}
                                                </button>
                                                <button
                                                    style={{ marginLeft: "10px" }}
                                                    onClick={() => this.addUserToGroup(group.id)}
                                                    className="btn btn-light"
                                                    >
                                                    Add User{" "}
                                                </button>
                                                <button
                                                    style={{ marginLeft: "10px" }}
                                                    onClick={() => this.users(group.id)}
                                                    className="btn btn-dark"
                                                    >
                                                    Users{" "}
                                                </button>
                                                <button
                                                    style={{ marginLeft: "10px" }}
                                                    onClick={() => this.deleteGroup(group.id)}
                                                    className="btn btn-danger"
                                                    >
                                                    Delete{" "}
                                                </button>
                                             </td>
                                        </tr>
                                    )
                                }
                            </tbody>
                        </table>

                 </div>

            </div>
        )
    }
}

export default Groups