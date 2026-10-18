import api from './client';

export const getMyListings = () =>
  api.get('/seller/listings').then((r) => r.data.data);

export const getMyListing = (id) =>
  api.get(`/seller/listings/${id}`).then((r) => r.data.data);

export const createListing = (data) =>
  api.post('/seller/listings', data).then((r) => r.data);

export const updateListing = (id, data) =>
  api.put(`/seller/listings/${id}`, data).then((r) => r.data);

export const toggleAvailability = (id) =>
  api.patch(`/seller/listings/${id}/availability`).then((r) => r.data);

export const deleteListing = (id) =>
  api.delete(`/seller/listings/${id}`).then((r) => r.data);

export const getAllSellers = () =>
  api.get('/sellers').then((r) => r.data.data);

export const getSellerById = (id) =>
  api.get(`/sellers/${id}`).then((r) => r.data.data);
