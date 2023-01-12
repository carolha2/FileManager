import React, { Component } from 'react'
import groupService from '../services/group.service';
import Axios from "axios";
import authHeader from "../services/auth-header";

class Report extends Component {
    constructor(props) {
        super(props)

        this.state = {
                reports: []
        }
    }

    componentDidMount(){
        Axios.get("http://localhost:8080/getReports" , { headers: authHeader() }).then((res) => {
            this.setState({ reports: res.data });
            console.log(res.data);
        });
    }
    render() {
        return (
            <div>
                 <h2 className="text-center">reports</h2>
                 <div className = "row">
                        <table className = "table table-striped table-bordered">

                            <thead>
                                <tr>
                                    <th> File Name</th>
                                    <th> User Name</th>
                                    <th> Action Name</th>
                                    <th> Date </th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    this.state.reports.sort((a,b)=>a.id < b.id ? 1 :-1 ).map(
                                        report => 
                                        <tr key = {report.id}>
                                             <td> {report.fileName} </td>  
                                             <td> {report.username}</td>
                                             <td> {report.actionName}</td>
                                             <td> {report.date}</td>
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

export default Report