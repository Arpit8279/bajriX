import { useState } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  MailIcon,
  LockIcon,
  EyeIcon,
  EyeOffIcon,
  StoreIcon,
  UserIcon,
  AlertCircleIcon,
  ArrowLeftIcon,
  CheckCircleIcon,
} from '../../components/Icons';

const DEMO_ACCOUNTS = [
  {
    type: 'SELLER',
    name: 'Arpit Sahu',
    businessName: 'Shree Traders',
    city: 'Pune',
    status: 'APPROVED',
    email: 'sahuarpit33@gmail.com',
    roleLabel: 'Seller',
    desc: 'Cement & TMT Bars distributor',
  },
  {
    type: 'SELLER',
    name: 'BuildMart India Rep',
    businessName: 'BuildMart India',
    city: 'Mumbai',
    status: 'APPROVED',
    email: 'buildmart.seller@gmail.com',
    roleLabel: 'Seller',
    desc: 'Wholesale materials supplier',
  },
  {
    type: 'SELLER',
    name: 'Rajesh Kumar',
    businessName: 'Raj Enterprises',
    city: 'Delhi',
    status: 'APPROVED',
    email: 'raj.seller@gmail.com',
    roleLabel: 'Seller',
    desc: 'North India regional vendor',
  },
  {
    type: 'SELLER',
    name: 'Kirit Mehta',
    businessName: 'Mehta Building Materials',
    city: 'Ahmedabad',
    status: 'APPROVED',
    email: 'mehta.seller@gmail.com',
    roleLabel: 'Seller',
    desc: 'Brick & steel specialist',
  },
  {
    type: 'BUYER',
    name: 'Priya Sharma',
    businessName: 'Retail Buyer',
    city: 'Bangalore',
    status: 'ACTIVE',
    email: 'priya.buyer@gmail.com',
    roleLabel: 'Buyer',
    desc: 'Commercial contractor purchasing materials',
  },
  {
    type: 'BUYER',
    name: 'Arpit Sahu (Buyer)',
    businessName: 'Individual Buyer',
    city: 'Pune',
    status: 'ACTIVE',
    email: 'arpitsahu8279@gmail.com',
    roleLabel: 'Buyer',
    desc: 'Homebuilder browsing products',
  },
];

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('BajriX@123');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Read target redirect
  const from = location.state?.from?.pathname || null;

  const handleLogin = async (loginEmail, loginPassword) => {
    setError(null);
    setLoading(true);

    try {
      const user = await login(loginEmail, loginPassword);
      // Navigate to intended page or default role home
      if (from) {
        navigate(from, { replace: true });
      } else if (user.role === 'SELLER') {
        navigate('/seller/dashboard', { replace: true });
      } else {
        navigate('/', { replace: true });
      }
    } catch (err) {
      const msg = err.response?.data?.message || 'Invalid email or password. Please try again.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = (e) => {
    e.preventDefault();
    if (!email.trim()) {
      setError('Please enter your email address.');
      return;
    }
    if (!password) {
      setError('Please enter your password.');
      return;
    }
    handleLogin(email.trim(), password);
  };

  const handleDemoSelect = (account) => {
    setEmail(account.email);
    setPassword('BajriX@123');
    handleLogin(account.email, 'BajriX@123');
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <Link to="/" className="back-link" style={{ marginBottom: '1.25rem' }}>
          <ArrowLeftIcon size={14} /> Back to Marketplace
        </Link>

        <div className="login-card">
          <div className="login-header">
            <div className="login-brand">
              <span>BajriX</span>
              <span className="brand-dot" />
            </div>
            <h2>Sign in to your account</h2>
            <p>Access your seller inventory management or buyer account</p>
          </div>

          {error && (
            <div className="alert alert-danger" style={{ marginBottom: '1.25rem' }}>
              <AlertCircleIcon size={18} style={{ flexShrink: 0, marginTop: '2px' }} />
              <div>{error}</div>
            </div>
          )}

          <form onSubmit={onSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="login-email" className="form-label">
                Email Address
              </label>
              <div className="input-with-icon">
                <span className="input-icon">
                  <MailIcon size={16} />
                </span>
                <input
                  id="login-email"
                  type="email"
                  className="form-input"
                  placeholder="name@company.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  autoComplete="email"
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="login-password" className="form-label">
                Password
              </label>
              <div className="input-with-icon">
                <span className="input-icon">
                  <LockIcon size={16} />
                </span>
                <input
                  id="login-password"
                  type={showPassword ? 'text' : 'password'}
                  className="form-input"
                  placeholder="Enter your password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  autoComplete="current-password"
                  required
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  {showPassword ? <EyeOffIcon size={16} /> : <EyeIcon size={16} />}
                </button>
              </div>
            </div>

            <button
              type="submit"
              className="btn btn-primary btn-block"
              disabled={loading}
              style={{ marginTop: '0.75rem', height: '44px' }}
            >
              {loading ? (
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', justifyContent: 'center' }}>
                  <div className="spinner" style={{ width: '16px', height: '16px', borderWidth: '2px' }} />
                  <span>Signing in...</span>
                </div>
              ) : (
                'Sign In'
              )}
            </button>
          </form>

          {/* Demo Accounts Quick-Login Section */}
          <div className="demo-accounts-section">
            <div className="demo-divider">
              <span>OR 1-CLICK DEMO LOGIN</span>
            </div>

            <div className="demo-hint">
              Click any account below to sign in instantly (default password: <code>BajriX@123</code>):
            </div>

            <div className="demo-accounts-grid">
              {DEMO_ACCOUNTS.map((acc) => (
                <button
                  key={acc.email}
                  type="button"
                  className={`demo-account-card ${acc.type === 'SELLER' ? 'seller-card' : 'buyer-card'}`}
                  onClick={() => handleDemoSelect(acc)}
                  disabled={loading}
                >
                  <div className="demo-card-top">
                    <span className={`demo-role-badge ${acc.type.toLowerCase()}`}>
                      {acc.type === 'SELLER' ? <StoreIcon size={12} /> : <UserIcon size={12} />}
                      <span>{acc.roleLabel}</span>
                    </span>
                    {acc.type === 'SELLER' && (
                      <span className={`status-badge status-${acc.status.toLowerCase()}`}>
                        {acc.status}
                      </span>
                    )}
                  </div>
                  <div className="demo-card-name">{acc.name}</div>
                  <div className="demo-card-business">
                    {acc.businessName} {acc.city ? `• ${acc.city}` : ''}
                  </div>
                  <div className="demo-card-email">{acc.email}</div>
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
