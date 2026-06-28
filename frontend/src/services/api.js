import axios from 'axios';

const API_BASE = process.env.REACT_APP_API_URL || '';

const api = axios.create({
  baseURL: `${API_BASE}/api/v1`,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('daip_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('daip_token');
      localStorage.removeItem('daip_user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authApi = {
  login: (username, password) => api.post('/auth/login', { username, password }),
};

export const dashboardApi = {
  getMetrics: () => api.get('/dashboard'),
};

export const documentApi = {
  upload: (formData) => api.post('/documents', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
  getAll: () => api.get('/documents'),
  getMy: () => api.get('/documents/my'),
  reprocess: (id) => api.post(`/documents/${id}/reprocess`),
};

export const searchApi = {
  search: (query, domainCode, topK) =>
    api.post('/search', { query, domainCode, topK }),
};

export const ragApi = {
  query: (question, domainCode) =>
    api.post('/rag/query', { question, domainCode }),
};

export const chatApi = {
  send: (message, sessionId, domainCode) =>
    api.post('/chat', { message, sessionId, domainCode }),
  getSessions: () => api.get('/chat/sessions'),
  getSession: (id) => api.get(`/chat/sessions/${id}`),
};

export const userApi = {
  getMe: () => api.get('/users/me'),
};

export default api;
