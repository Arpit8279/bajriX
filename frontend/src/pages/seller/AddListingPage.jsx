import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { searchProducts } from '../../api/buyerApi';
import { createListing } from '../../api/sellerApi';
import { useDebounce } from '../../hooks/useDebounce';
import { ArrowLeftIcon, AlertCircleIcon, SearchIcon } from '../../components/Icons';

export default function AddListingPage() {
  const navigate = useNavigate();
  const [step, setStep] = useState(1); // 1 = select product, 2 = fill details

  // Step 1: product search
  const [productQuery, setProductQuery] = useState('');
  const [productResults, setProductResults] = useState([]);
  const [searchLoading, setSearchLoading] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);

  const debouncedQuery = useDebounce(productQuery, 300);

  // Step 2: listing details
  const [price, setPrice] = useState('');
  const [stockQuantity, setStockQuantity] = useState('');
  const [minOrderQuantity, setMinOrderQuantity] = useState('1');
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [apiError, setApiError] = useState(null);

  useEffect(() => {
    if (debouncedQuery.length >= 2) {
      setSearchLoading(true);
      searchProducts({ q: debouncedQuery, size: 10 })
        .then((data) => setProductResults(data.content))
        .catch(console.error)
        .finally(() => setSearchLoading(false));
    } else {
      setProductResults([]);
    }
  }, [debouncedQuery]);

  const selectProduct = (product) => {
    setSelectedProduct(product);
    setStep(2);
    setApiError(null);
  };

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
      await createListing({
        productId: selectedProduct.id,
        price: Number(price),
        stockQuantity: Number(stockQuantity),
        minOrderQuantity: Number(minOrderQuantity),
      });
      navigate('/seller/dashboard');
    } catch (err) {
      const resp = err.response?.data;
      if (resp?.errors) {
        setErrors(resp.errors);
      } else {
        setApiError(resp?.message || 'Failed to create listing');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <Link to="/seller/dashboard" className="back-link">
        <ArrowLeftIcon size={14} /> Back to Dashboard
      </Link>

      <div className="page-header">
        <h1>Add New Listing</h1>
        <p>Search for a product in the catalogue and define your price and inventory</p>
      </div>

      {step === 1 && (
        <div className="form-card">
          <h2 style={{ fontSize: '1.05rem', fontWeight: 600, marginBottom: '1.25rem', color: 'var(--color-text)' }}>
            Step 1: Select a Product
          </h2>
          <div className="form-group">
            <label className="form-label">Search catalogue</label>
            <div className="search-input-wrapper">
              <span className="search-icon">
                <SearchIcon size={16} />
              </span>
              <input
                type="text"
                className="search-input"
                placeholder="Type at least 2 characters... e.g. cement, tmt, sand"
                value={productQuery}
                onChange={(e) => setProductQuery(e.target.value)}
                autoFocus
              />
            </div>
          </div>

          {searchLoading && (
            <div className="loading-spinner" style={{ padding: '1.5rem' }}>
              <div className="spinner" />
            </div>
          )}

          {productResults.length > 0 && (
            <div style={{ marginTop: '1rem' }}>
              {productResults.map((p) => (
                <div
                  key={p.id}
                  className="product-select-item"
                  onClick={() => selectProduct(p)}
                >
                  <div style={{ fontWeight: 600, color: 'var(--color-text)' }}>{p.name}</div>
                  <div style={{ fontSize: '0.82rem', color: 'var(--color-text-secondary)', marginTop: '0.15rem' }}>
                    {p.brand || 'Generic'} · {p.categoryName} · per {p.unit}
                    {p.sellerCount > 0 && ` · ${p.sellerCount} seller${p.sellerCount !== 1 ? 's' : ''} listed`}
                  </div>
                </div>
              ))}
            </div>
          )}

          {debouncedQuery.length >= 2 && !searchLoading && productResults.length === 0 && (
            <div style={{ color: 'var(--color-text-muted)', fontSize: '0.88rem', marginTop: '1rem' }}>
              No products found matching "{debouncedQuery}"
            </div>
          )}
        </div>
      )}

      {step === 2 && selectedProduct && (
        <div className="form-card">
          <div style={{
            padding: '1rem 1.25rem',
            borderRadius: 'var(--radius-md)',
            background: 'var(--color-bg-subtle)',
            border: '1px solid var(--color-border)',
            marginBottom: '1.75rem',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}>
            <div>
              <div style={{ fontWeight: 600, color: 'var(--color-text)' }}>{selectedProduct.name}</div>
              <div style={{ fontSize: '0.84rem', color: 'var(--color-text-secondary)', marginTop: '0.15rem' }}>
                {selectedProduct.brand || 'Generic'} · per {selectedProduct.unit}
              </div>
            </div>
            <button
              className="btn btn-secondary btn-sm"
              onClick={() => { setStep(1); setSelectedProduct(null); }}
            >
              Change
            </button>
          </div>

          <h2 style={{ fontSize: '1.05rem', fontWeight: 600, marginBottom: '1.25rem', color: 'var(--color-text)' }}>
            Step 2: Listing Details
          </h2>

          {apiError && (
            <div className="alert alert-danger">
              <AlertCircleIcon size={18} style={{ flexShrink: 0, marginTop: '2px' }} />
              <div>{apiError}</div>
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Price (₹ per {selectedProduct.unit})</label>
              <input
                type="number"
                className={`form-input ${errors.price ? 'error' : ''}`}
                placeholder="e.g. 390"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                step="0.01"
                min="0.01"
              />
              {errors.price && <div className="form-error">{errors.price}</div>}
            </div>

            <div className="form-group">
              <label className="form-label">Stock Quantity ({selectedProduct.unit}s)</label>
              <input
                type="number"
                className={`form-input ${errors.stockQuantity ? 'error' : ''}`}
                placeholder="e.g. 500"
                value={stockQuantity}
                onChange={(e) => setStockQuantity(e.target.value)}
                min="0"
              />
              {errors.stockQuantity && <div className="form-error">{errors.stockQuantity}</div>}
            </div>

            <div className="form-group">
              <label className="form-label">Minimum Order Quantity ({selectedProduct.unit}s)</label>
              <input
                type="number"
                className={`form-input ${errors.minOrderQuantity ? 'error' : ''}`}
                placeholder="e.g. 10"
                value={minOrderQuantity}
                onChange={(e) => setMinOrderQuantity(e.target.value)}
                min="1"
              />
              {errors.minOrderQuantity && <div className="form-error">{errors.minOrderQuantity}</div>}
            </div>

            <div className="btn-group" style={{ marginTop: '1.75rem' }}>
              <button type="submit" className="btn btn-primary" disabled={submitting}>
                {submitting ? 'Creating...' : 'Create Listing'}
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
      )}
    </div>
  );
}
