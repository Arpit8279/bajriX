import api from './client';

export const searchProducts = (params) =>
  api.get('/buyer/products', { params }).then((r) => r.data.data);

export const getProductBySlug = (slug) =>
  api.get(`/buyer/products/${slug}`).then((r) => r.data.data);

export const getBrands = () =>
  api.get('/buyer/products/brands').then((r) => r.data.data);

export const getCategories = () =>
  api.get('/categories').then((r) => r.data.data);
