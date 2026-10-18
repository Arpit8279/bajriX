import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor: attach Authorization header and X-Seller-Id
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }

  const sellerId = localStorage.getItem('selectedSellerId');
  if (sellerId && config.url?.startsWith('/seller')) {
    config.headers['X-Seller-Id'] = sellerId;
  }
  return config;
});

// Response interceptor: handle token expiration or unauthorized responses
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && localStorage.getItem('authToken')) {
      // Clear expired credentials
      localStorage.removeItem('authToken');
      localStorage.removeItem('currentUser');
      localStorage.removeItem('selectedSellerId');
      window.dispatchEvent(new Event('auth:unauthorized'));
    }
    return Promise.reject(error);
  }
);

export default api;
