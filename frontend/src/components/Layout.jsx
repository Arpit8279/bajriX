import { NavLink, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { StoreIcon, PackageIcon, PlusIcon, UserIcon, LogInIcon, LogOutIcon } from './Icons';

export default function Layout({ children }) {
  const { user, isAuthenticated, isSeller, role, selectedSeller, logout, switchToSeller, switchToBuyer } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const handleToggleMode = () => {
    if (role === 'seller') {
      switchToBuyer();
      navigate('/');
    } else {
      switchToSeller();
      navigate('/seller/dashboard');
    }
  };

  return (
    <div className="app-layout">
      <nav className="navbar">
        <div style={{ display: 'flex', alignItems: 'center', gap: '2rem' }}>
          <NavLink to="/" className="navbar-brand">
            BajriX
            <span className="brand-dot" />
          </NavLink>
          <div className="navbar-nav">
            <NavLink
              to="/"
              className={({ isActive }) =>
                `nav-link ${isActive && role === 'buyer' ? 'active' : ''}`
              }
            >
              <StoreIcon size={15} />
              <span>Browse Products</span>
            </NavLink>
            {isSeller && role === 'seller' && (
              <>
                <NavLink
                  to="/seller/dashboard"
                  className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
                >
                  <PackageIcon size={15} />
                  <span>My Listings</span>
                </NavLink>
                <NavLink
                  to="/seller/listings/new"
                  className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
                >
                  <PlusIcon size={15} />
                  <span>Add Listing</span>
                </NavLink>
              </>
            )}
          </div>
        </div>

        <div className="navbar-right">
          {isAuthenticated ? (
            <div className="user-nav-controls">
              {isSeller && (
                <button
                  type="button"
                  className={`btn-mode-toggle ${role === 'seller' ? 'active-seller' : 'active-buyer'}`}
                  onClick={handleToggleMode}
                  title="Toggle between Buyer browsing and Seller inventory management"
                >
                  {role === 'seller' ? (
                    <>
                      <UserIcon size={13} />
                      <span>Switch to Buyer View</span>
                    </>
                  ) : (
                    <>
                      <StoreIcon size={13} />
                      <span>Go to Seller Dashboard</span>
                    </>
                  )}
                </button>
              )}

              <div className="user-profile-badge">
                <span className={`role-badge ${isSeller ? 'seller' : 'buyer'}`}>
                  {isSeller ? <StoreIcon size={13} /> : <UserIcon size={13} />}
                  <span>{isSeller ? (selectedSeller?.businessName || user.businessName || 'Seller') : 'Buyer'}</span>
                </span>
                <span className="user-name-text">{user.fullName}</span>
              </div>

              <button
                type="button"
                className="btn btn-outline btn-logout"
                onClick={handleLogout}
                title="Log out of BajriX"
              >
                <LogOutIcon size={14} />
                <span>Logout</span>
              </button>
            </div>
          ) : (
            <div className="guest-nav-controls">
              <span className="role-badge buyer" style={{ opacity: 0.85 }}>
                <UserIcon size={13} />
                <span>Guest</span>
              </span>
              <Link to="/login" className="btn btn-primary btn-sm">
                <LogInIcon size={14} />
                <span>Sign In</span>
              </Link>
            </div>
          )}
        </div>
      </nav>

      <main className="main-content">{children}</main>
    </div>
  );
}
