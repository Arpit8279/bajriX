import { useState, useEffect, useCallback } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { searchProducts, getCategories, getBrands } from '../../api/buyerApi';
import { useDebounce } from '../../hooks/useDebounce';
import { SearchIcon, StoreIcon, PackageIcon } from '../../components/Icons';

export default function ProductListPage() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pagination, setPagination] = useState({});

  // Read filters from URL params
  const [query, setQuery] = useState(searchParams.get('q') || '');
  const [categoryId, setCategoryId] = useState(searchParams.get('categoryId') || '');
  const [brand, setBrand] = useState(searchParams.get('brand') || '');
  const [sortBy, setSortBy] = useState(searchParams.get('sortBy') || 'name');
  const [direction, setDirection] = useState(searchParams.get('direction') || 'asc');
  const [page, setPage] = useState(Number(searchParams.get('page')) || 0);

  const debouncedQuery = useDebounce(query, 300);

  // Load categories and brands for filters
  useEffect(() => {
    getCategories().then(setCategories).catch(console.error);
    getBrands().then(setBrands).catch(console.error);
  }, []);

  // Fetch products when filters change
  const fetchProducts = useCallback(async () => {
    setLoading(true);
    try {
      const params = { page, size: 12, sortBy, direction };
      if (debouncedQuery) params.q = debouncedQuery;
      if (categoryId) params.categoryId = categoryId;
      if (brand) params.brand = brand;

      const data = await searchProducts(params);
      setProducts(data.content);
      setPagination({
        page: data.page,
        totalPages: data.totalPages,
        totalElements: data.totalElements,
        last: data.last,
      });

      // Sync URL
      const sp = new URLSearchParams();
      if (debouncedQuery) sp.set('q', debouncedQuery);
      if (categoryId) sp.set('categoryId', categoryId);
      if (brand) sp.set('brand', brand);
      if (sortBy !== 'name') sp.set('sortBy', sortBy);
      if (direction !== 'asc') sp.set('direction', direction);
      if (page > 0) sp.set('page', page);
      setSearchParams(sp, { replace: true });
    } catch (err) {
      console.error('Search error:', err);
    } finally {
      setLoading(false);
    }
  }, [debouncedQuery, categoryId, brand, sortBy, direction, page]);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  // Reset page when filters change
  useEffect(() => {
    setPage(0);
  }, [debouncedQuery, categoryId, brand, sortBy, direction]);

  const formatPrice = (price) =>
    price != null
      ? `₹${Number(price).toLocaleString('en-IN', { minimumFractionDigits: 0, maximumFractionDigits: 2 })}`
      : null;

  return (
    <div>
      <div className="page-header">
        <h1>Browse Products</h1>
        <p>Discover construction materials from verified sellers across India</p>
      </div>

      {/* Search & Filters */}
      <div className="search-filters">
        <div className="search-input-wrapper">
          <span className="search-icon">
            <SearchIcon size={16} />
          </span>
          <input
            type="text"
            className="search-input"
            placeholder="Search products by name or brand..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </div>

        <select
          className="filter-select"
          value={categoryId}
          onChange={(e) => setCategoryId(e.target.value)}
          aria-label="Filter by Category"
        >
          <option value="">All Categories</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>

        <select
          className="filter-select"
          value={brand}
          onChange={(e) => setBrand(e.target.value)}
          aria-label="Filter by Brand"
        >
          <option value="">All Brands</option>
          {brands.map((b) => (
            <option key={b} value={b}>{b}</option>
          ))}
        </select>

        <select
          className="filter-select"
          value={`${sortBy}-${direction}`}
          onChange={(e) => {
            const [s, d] = e.target.value.split('-');
            setSortBy(s);
            setDirection(d);
          }}
          aria-label="Sort products"
        >
          <option value="name-asc">Name A→Z</option>
          <option value="name-desc">Name Z→A</option>
          <option value="brand-asc">Brand A→Z</option>
          <option value="brand-desc">Brand Z→A</option>
        </select>
      </div>

      {/* Results count */}
      {!loading && (
        <p style={{ color: 'var(--color-text-muted)', fontSize: '0.85rem', marginBottom: '1.25rem' }}>
          {pagination.totalElements} product{pagination.totalElements !== 1 ? 's' : ''} found
        </p>
      )}

      {/* Product Grid */}
      {loading ? (
        <div className="loading-spinner">
          <div className="spinner" />
        </div>
      ) : products.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">
            <PackageIcon size={44} />
          </div>
          <h3>No products found</h3>
          <p>Try adjusting your search or filters</p>
        </div>
      ) : (
        <div className="product-grid">
          {products.map((p) => (
            <div
              key={p.id}
              className="product-card"
              onClick={() => navigate(`/products/${p.slug}`)}
            >
              <div className="product-card-category">
                {p.categoryName || 'Uncategorized'}
              </div>
              <div className="product-card-name">{p.name}</div>
              <div className="product-card-brand">
                {p.brand || 'Generic'} · per {p.unit}
              </div>
              <div className="product-card-footer">
                {p.lowestPrice ? (
                  <div className="product-card-price">
                    {formatPrice(p.lowestPrice)}
                    <span className="unit">/{p.unit}</span>
                  </div>
                ) : (
                  <div className="product-card-price no-price">No sellers yet</div>
                )}
                <div className="product-card-sellers">
                  <StoreIcon size={12} />
                  <span>{p.sellerCount} seller{p.sellerCount !== 1 ? 's' : ''}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pagination */}
      {pagination.totalPages > 1 && (
        <div className="pagination">
          <button disabled={page === 0} onClick={() => setPage(page - 1)}>
            ← Prev
          </button>
          {Array.from({ length: pagination.totalPages }, (_, i) => (
            <button
              key={i}
              className={i === page ? 'active' : ''}
              onClick={() => setPage(i)}
            >
              {i + 1}
            </button>
          ))}
          <button disabled={pagination.last} onClick={() => setPage(page + 1)}>
            Next →
          </button>
          <span className="pagination-info">
            Page {page + 1} of {pagination.totalPages}
          </span>
        </div>
      )}
    </div>
  );
}
