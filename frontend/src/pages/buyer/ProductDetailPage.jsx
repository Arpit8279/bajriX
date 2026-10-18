import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getProductBySlug } from '../../api/buyerApi';
import {
  ArrowLeftIcon,
  TagIcon,
  PackageIcon,
  StoreIcon,
  MapPinIcon,
  CheckCircleIcon,
  AlertCircleIcon,
} from '../../components/Icons';

export default function ProductDetailPage() {
  const { slug } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
    getProductBySlug(slug)
      .then(setProduct)
      .catch((err) => {
        setError(err.response?.data?.message || 'Product not found');
      })
      .finally(() => setLoading(false));
  }, [slug]);

  const formatPrice = (price) =>
    `₹${Number(price).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

  if (loading) {
    return (
      <div className="loading-spinner">
        <div className="spinner" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">
          <AlertCircleIcon size={44} />
        </div>
        <h3>{error}</h3>
        <Link to="/" className="btn btn-primary" style={{ marginTop: '1rem' }}>
          <ArrowLeftIcon size={14} /> Back to Products
        </Link>
      </div>
    );
  }

  return (
    <div>
      <Link to="/" className="back-link">
        <ArrowLeftIcon size={14} /> Back to Products
      </Link>

      <div className="product-detail">
        {/* Left: Product Info */}
        <div className="product-info">
          <div className="product-card-category">{product.categoryName}</div>
          <h1>{product.name}</h1>

          <div className="product-meta">
            {product.brand && (
              <span className="meta-tag">
                <TagIcon size={13} /> {product.brand}
              </span>
            )}
            <span className="meta-tag">
              <PackageIcon size={13} /> Sold per {product.unit}
            </span>
            <span className="meta-tag">
              <StoreIcon size={13} /> {product.sellers.length} seller{product.sellers.length !== 1 ? 's' : ''}
            </span>
          </div>

          {product.description && (
            <div className="product-description">{product.description}</div>
          )}
        </div>

        {/* Right: Seller Offers */}
        <div className="sellers-section">
          <h2>
            <StoreIcon size={18} />
            <span>Available Sellers</span>
            <span style={{ fontSize: '0.82rem', fontWeight: 400, color: 'var(--color-text-muted)' }}>
              (sorted by price)
            </span>
          </h2>

          {product.sellers.length === 0 ? (
            <div className="empty-state" style={{ padding: '2rem' }}>
              <div className="empty-state-icon">
                <StoreIcon size={36} />
              </div>
              <h3>No sellers currently offer this product</h3>
              <p>Check back later for availability</p>
            </div>
          ) : (
            product.sellers.map((seller, idx) => (
              <div key={seller.listingId} className="seller-offer-card">
                <div className="seller-offer-header">
                  <div>
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      <span className="seller-offer-name">{seller.sellerName}</span>
                      {idx === 0 && (
                        <span className="best-price-badge">
                          <CheckCircleIcon size={11} /> Best Price
                        </span>
                      )}
                    </div>
                    <div className="seller-offer-city">
                      <MapPinIcon size={12} /> {seller.sellerCity}
                    </div>
                  </div>
                  <div className="seller-offer-price">
                    {formatPrice(seller.price)}
                    <span style={{ fontSize: '0.8rem', fontWeight: 400, color: 'var(--color-text-muted)', marginLeft: '2px' }}>
                      /{product.unit}
                    </span>
                  </div>
                </div>
                <div className="seller-offer-details">
                  <span>Stock: {seller.stockQuantity.toLocaleString()} {product.unit}s</span>
                  <span>Min Order: {seller.minOrderQuantity} {product.unit}{seller.minOrderQuantity > 1 ? 's' : ''}</span>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
