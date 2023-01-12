import React, { Component } from "react";
import { Redirect } from "react-router-dom";
import AuthService from "../services/auth.service";
import authHeader from "../services/auth-header";
import Axios from "axios";
export default class FileDownload extends Component {
  constructor(props) {
    super(props);

    this.state = {
        fileName : "",
        username : "",
        lock : "",
        lockedBy: "",
        docFile : [],
        img: ''

    };
  }
  arrayBufferToBase64(buffer) {
    var binary = '';
    var bytes = [].slice.call(new Uint8Array(buffer));
    bytes.forEach((b) => binary += String.fromCharCode(b));
    return window.btoa(binary);
};
  componentDidMount() {
    console.log("download "+localStorage.getItem('downloadUri'));
    console.log("contentType "+localStorage.getItem('contentType'));
    this.setState({contentType: localStorage.getItem('contentType')})
    var str2 = '"text/plain"'
    var str1 = localStorage.getItem('contentType')
    if(str1 === str2){
        Axios.get(JSON.parse(localStorage.getItem('downloadUri')),{ headers: authHeader() })
        .then((res)=>{
            this.setState({ docFile: res.data });
            console.log(res.data);
        });
    }
    else{
        console.log("image");
        fetch(JSON.parse(localStorage.getItem('downloadUri')),{ headers: authHeader() })
        .then(response => response.blob())
        .then(blob => {
            var blobURL = URL.createObjectURL(blob);
            this.setState({docFile:blobURL})
        })
        .catch(error => {
            console.error(error);
        });
    }
}
  render() {
    return (
      <div>
        {this.state.contentType ==='"text/plain"' ?(
            this.state.docFile
        ):
        <img className="profile-photo" src={this.state.docFile} alt={"Carlie Anglemire"}/>
        }
      </div>
    );
    }
}
