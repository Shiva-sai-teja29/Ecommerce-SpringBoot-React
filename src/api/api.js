import axios from "axios";

const api = axios.create({
    baseURL:`${import.meta.env.VITE_BACK_END_URL}/api`,
    // baseURL:`http://localhost:8081/api`,
});


export default api;