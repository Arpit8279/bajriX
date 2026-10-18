import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Layout from './components/Layout';
import ProductListPage from './pages/buyer/ProductListPage';
import ProductDetailPage from './pages/buyer/ProductDetailPage';
import SellerDashboard from './pages/seller/SellerDashboard';
import AddListingPage from './pages/seller/AddListingPage';
import EditListingPage from './pages/seller/EditListingPage';
import LoginPage from './pages/auth/LoginPage';

function ProtectedSellerRoute({ children }) {
  const { isAuthenticated, isSeller } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (!isSeller) {
    return <Navigate to="/" replace />;
  }

  return children;
}

function PublicAuthRoute({ children }) {
  const { isAuthenticated, isSeller } = useAuth();

  if (isAuthenticated) {
    return <Navigate to={isSeller ? '/seller/dashboard' : '/'} replace />;
  }

  return children;
}

function App() {
  return (
    <Layout>
      <Routes>
        {/* Buyer routes — always accessible */}
        <Route path="/" element={<ProductListPage />} />
        <Route path="/products/:slug" element={<ProductDetailPage />} />

        {/* Authentication */}
        <Route
          path="/login"
          element={
            <PublicAuthRoute>
              <LoginPage />
            </PublicAuthRoute>
          }
        />

        {/* Seller routes — strictly protected */}
        <Route
          path="/seller/dashboard"
          element={
            <ProtectedSellerRoute>
              <SellerDashboard />
            </ProtectedSellerRoute>
          }
        />
        <Route
          path="/seller/listings/new"
          element={
            <ProtectedSellerRoute>
              <AddListingPage />
            </ProtectedSellerRoute>
          }
        />
        <Route
          path="/seller/listings/:id/edit"
          element={
            <ProtectedSellerRoute>
              <EditListingPage />
            </ProtectedSellerRoute>
          }
        />

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Layout>
  );
}

export default App;
