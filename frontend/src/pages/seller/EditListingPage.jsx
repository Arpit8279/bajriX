import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getMyListing, updateListing } from '../../api/sellerApi';
import { ArrowLeftIcon, AlertCircleIcon } from '../../components/Icons';

export default function EditListingPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [listing, setListing] = useState(null);
  const [loading, setLoading] = useState(true);

  const [price, setPrice] = useState('');
  const [stockQuantity, setStockQuantity] = useState('');
  const [minOrderQuantity, setMinOrderQuantity] = useState('');
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [apiError, setApiError] = useState(null);

  useEffect(() => {
    setLoading(true);
    getMyListing(id)
      .then((data) => {
        setListing(data);
        setPrice(String(data.price));
        setStockQuantity(String(data.stockQuantity));
        setMinOrderQuantity(String(data.minOrderQuantity));
      })
      .catch((err) => {
        setApiError(err.response?.data?.message || 'Listing not found');
      })
      .finally(() => setLoading(false));
  }, [id]);

  const validate = () => {
    const errs = {};
    if (!price || Number(price) <= 0) errs.price = 'Price must be greater than 0';
    if (stockQuantity === '' || Number(stockQuantity) < 0) errs.stockQuantity = 'Stock cannot be negative';
    if (!minOrderQuantity || Number(minOrderQuantity) < 1) errs.minOrderQuantity = 'Min order must be at least 1';
    if (Number(stockQuantity) > 0 && Number(minOrderQuantity) > Number(stockQuantity)) {
      errs.minOrderQuantity = 'Min order cannot exceed stock';
    }
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setSubmitting(true);
    setApiError(null);
    try {
      await updateListing(id, {
        price: Number(price),
        stockQuantity: Number(stockQuantity),
        minOrderQuantity: Number(minOrderQuantity),
      });
      navigate('/seller/dashboard');
    } catch (err) {
      const resp = err.response?.data;
      if (err.response?.status === 409) {
        setApiError('This listing was modified by another process. Please refresh and try again.');
      } else if (resp?.errors) {
        setErrors(resp.errors);
      } else {
        setApiError(resp?.message || 'Failed to update listing');
      }
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="loading-spinner">
        <div className="spinner" />
      </div>
    );
  }

  if (!listing && apiError) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">
          <AlertCircleIcon size={44} />
        </div>
        <h3>{apiError}</h3>
        <Link to="/seller/dashboard" className="btn btn-primary" style={{ marginTop: '1rem' }}>
          <ArrowLeftIcon size={14} /> Back to Dashboard
        </Link>
      </div>
    );
  }

  return (
    <div>
      <Link to="/seller/dashboard" className="back-link">
        <ArrowLeftIcon size={14} /> Back to Dashboard
      </Link>

      <div className="page-header">
        <h1>Edit Listing</h1>
        <p>Update price, stock, and minimum order quantity</p>
      </div>

      <div className="form-card">
        <div style={{
          padding: '1rem 1.25rem',
          borderRadius: 'var(--radius-md)',
          background: 'var(--color-bg-subtle)',
          border: '1px solid var(--color-border)',
          marginBottom: '1.75rem',
        }}>
          <div style={{ fontWeight: 600, color: 'var(--color-text)' }}>{listing.productName}</div>
          <div style={{ fontSize: '0.84rem', color: 'var(--color-text-secondary)', marginTop: '0.15rem' }}>
            {listing.productBrand || 'Generic'} · {listing.categoryName} · per {listing.productUnit}
          </div>
          <div style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)', marginTop: '0.35rem' }}>
            Version: {listing.version}
          </div>
        </div>

        {apiError && (
          <div className="alert alert-danger">
            <AlertCircleIcon size={18} style={{ flexShrink: 0, marginTop: '2px' }} />
            <div>{apiError}</div>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Price (₹ per {listing.productUnit})</label>
            <input
              type="number"
              className={`form-input ${errors.price ? 'error' : ''}`}
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              step="0.01"
              min="0.01"
            />
            {errors.price && <div className="form-error">{errors.price}</div>}
          </div>

          <div className="form-group">
            <label className="form-label">Stock Quantity ({listing.productUnit}s)</label>
            <input
              type="number"
              className={`form-input ${errors.stockQuantity ? 'error' : ''}`}
              value={stockQuantity}
              onChange={(e) => setStockQuantity(e.target.value)}
              min="0"
            />
            {errors.stockQuantity && <div className="form-error">{errors.stockQuantity}</div>}
          </div>

          <div className="form-group">
            <label className="form-label">Minimum Order Quantity ({listing.productUnit}s)</label>
            <input
              type="number"
              className={`form-input ${errors.minOrderQuantity ? 'error' : ''}`}
              value={minOrderQuantity}
              onChange={(e) => setMinOrderQuantity(e.target.value)}
              min="1"
            />
            {errors.minOrderQuantity && <div className="form-error">{errors.minOrderQuantity}</div>}
          </div>

          <div className="btn-group" style={{ marginTop: '1.75rem' }}>
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? 'Saving...' : 'Save Changes'}
            </button>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => navigate('/seller/dashboard')}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
