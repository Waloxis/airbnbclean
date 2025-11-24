import {useEffect,useState} from "react"
export default function App(){
  const [msg,setMsg]= useState("Loading");

  useEffect(()=>{
    fetch("http://localhost:8080/api/hello")
    .then(res=>res.text())
    .then(data=>setMsg(data))
    .catch(err=>setMsg("Error:"+err.message));
  }, []);
    return (
      <div style={{ padding: 30 }}>
        <h1>React + Spring Boot</h1>
        <p>{msg}</p>
      </div>
    );
  }



