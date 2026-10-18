import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getMyListings, toggleAvailability, deleteListing } from '../../api/sellerApi';
import {
  PlusIcon,
  EditIcon,
  PauseIcon,
  PlayIcon,
  TrashIcon,
  PackageIcon,
  AlertCircleIcon,
} from '../../components/Icons';

export default function SellerDashboard() {
  const { selectedSeller } = useAuth();
  const navigate = useNavigate();
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState(null);

  const fetchListings = async () => {
    setLoading(true);
    try {
      const data = await getMyListings();
      setListings(data);
    } catch (err) {
      console.error('Error fetching listings:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchListings();
  }, [selectedSeller?.id]);

  const showToast = (message, type = 'success') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 3000);
  };

  const handleToggle = async (id) => {
    try {
      const result = await toggleAvailability(id);
      showToast(result.data.available ? 'Listing is now active' : 'Listing paused');
      fetchListings();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to toggle availability';
      showToast(msg, 'error');
    }
  };

  const handleDelete = async (id, productName) => {
    if (!window.confirm(`Remove "${productName}" from your catalogue?`)) return;
    try {
      await deleteListing(id);
      showToast('Listing removed');
      fetchListings();
    } catch (err) {
      showToast('Failed to delete listing', 'error');
    }
  };

  const formatPrice = (price) =>
    `₹${Number(price).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;

  return (
    <div>
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1>My Listings</h1>
          <p>
            Manage catalogue for{' '}
            <strong>{selectedSeller?.businessName}</strong>
            {' '}
            <span className={`status-badge ${selectedSeller?.status?.toLowerCase()}`}>
              <span className="status-dot" />
              <span>{selectedSeller?.status}</span>
            </span>
          </p>
        </div>
        <button
          className="btn btn-primary"
          onClick={() => navigate('/seller/listings/new')}
        >
          <PlusIcon size={15} />
          <span>Add New Listing</span>
        </button>
      </div>

      {selectedSeller?.status === 'PENDING' && (
        <div className="alert alert-warning">
          <AlertCircleIcon size={18} style={{ flexShrink: 0, marginTop: '2px' }} />
          <div>
            Your seller account is pending verification. Your listings will become visible to buyers once approved.
          </div>
        </div>
      )}

      {selectedSeller?.status === 'REJECTED' && (
        <div className="alert alert-danger">
          <AlertCircleIcon size={18} style={{ flexShrink: 0, marginTop: '2px' }} />
          <div>
            Your seller account has been rejected. Your listings are currently not visible to buyers. Please contact support.
          </div>
        </div>
      )}

      {loading ? (
        <div className="loading-spinner">
          <div className="spinner" />
        </div>
      ) : listings.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">
            <PackageIcon size={44} />
          </div>
          <h3>No listings yet</h3>
          <p>Add your first product to start selling</p>
          <button
            className="btn btn-primary"
            style={{ marginTop: '1rem' }}
            onClick={() => navigate('/seller/listings/new')}
          >
            <PlusIcon size={15} /> Add Your First Listing
          </button>
        </div>
      ) : (
        <div className="listings-table-wrapper">
          <table className="listings-table">
            <thead>
              <tr>
                <th>Product</th>
                <th>Category</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Min Order</th>
                <th>Status</th>
                <th style={{ textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {listings.map((l) => (
                <tr key={l.id}>
                  <td>
                    <div style={{ fontWeight: 600, color: 'var(--color-text)' }}>{l.productName}</div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>
                      {l.productBrand || 'Generic'} · per {l.productUnit}
                    </div>
                  </td>
                  <td style={{ color: 'var(--color-text-secondary)' }}>{l.categoryName}</td>
                  <td style={{ fontWeight: 600, color: 'var(--color-text)' }}>
                    {formatPrice(l.price)}
                  </td>
                  <td>
                    <span style={{ color: l.stockQuantity === 0 ? 'var(--color-danger)' : 'var(--color-text)' }}>
                      {l.stockQuantity.toLocaleString()} {l.productUnit}s
                    </span>
                  </td>
                  <td>{l.minOrderQuantity}</td>
                  <td>
                    <span className={`status-badge ${l.available ? 'available' : 'unavailable'}`}>
                      <span className="status-dot" />
                      <span>{l.available ? 'Active' : 'Paused'}</span>
                    </span>
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <div className="btn-group" style={{ justifyContent: 'flex-end' }}>
                      <button
                        className="btn btn-secondary btn-sm btn-icon"
                        onClick={() => navigate(`/seller/listings/${l.id}/edit`)}
                        title="Edit Listing"
                        aria-label="Edit listing"
                      >
                        <EditIcon size={14} />
                      </button>
                      <button
                        className="btn btn-secondary btn-sm btn-icon"
                        onClick={() => handleToggle(l.id)}
                        title={l.available ? 'Pause listing' : 'Activate listing'}
                        aria-label={l.available ? 'Pause listing' : 'Activate listing'}
                      >
                        {l.available ? <PauseIcon size={14} /> : <PlayIcon size={14} />}
                      </button>
                      <button
                        className="btn btn-danger btn-sm btn-icon"
                        onClick={() => handleDelete(l.id, l.productName)}
                        title="Delete Listing"
                        aria-label="Delete listing"
                      >
                        <TrashIcon size={14} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Toast notification */}
      {toast && <div className={`toast ${toast.type}`}>{toast.message}</div>}
    </div>
  );
}
