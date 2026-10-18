import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { getAllSellers } from '../api/sellerApi';
import * as authApi from '../api/authApi';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const stored = localStorage.getItem('currentUser');
      return stored ? JSON.parse(stored) : null;
    } catch {
      return null;
    }
  });

  const [role, setRole] = useState(() => {
    try {
      const stored = localStorage.getItem('currentUser');
      if (stored) {
        const parsed = JSON.parse(stored);
        return parsed.role === 'SELLER' ? 'seller' : 'buyer';
      }
    } catch {}
    return 'buyer';
  });

  const [sellers, setSellers] = useState([]);
  const [selectedSeller, setSelectedSeller] = useState(null);

  // Fetch all sellers for directory & seller metadata
  useEffect(() => {
    getAllSellers()
      .then((data) => {
        setSellers(data);
      })
      .catch((err) => console.error('Failed to fetch sellers:', err));
  }, []);

  // Sync selectedSeller whenever sellers list or user changes
  useEffect(() => {
    if (user && user.sellerId && sellers.length > 0) {
      const found = sellers.find((s) => s.id === user.sellerId);
      if (found) {
        setSelectedSeller(found);
      } else {
        setSelectedSeller({
          id: user.sellerId,
          businessName: user.businessName || 'My Business',
          status: 'APPROVED',
        });
      }
    } else if (user && user.sellerId) {
      setSelectedSeller({
        id: user.sellerId,
        businessName: user.businessName || 'My Business',
        status: 'APPROVED',
      });
    } else {
      setSelectedSeller(null);
    }
  }, [user, sellers]);

  // Handle unauthorized events from API client
  useEffect(() => {
    const handleUnauthorized = () => {
      setUser(null);
      setSelectedSeller(null);
      setRole('buyer');
    };
    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
  }, []);

  const login = useCallback(async (email, password) => {
    const data = await authApi.login(email, password);
    // data: { userId, email, fullName, role, sellerId, businessName, token }
    localStorage.setItem('authToken', data.token);
    localStorage.setItem('currentUser', JSON.stringify(data));

    setUser(data);

    if (data.role === 'SELLER' && data.sellerId) {
      localStorage.setItem('selectedSellerId', String(data.sellerId));
      setRole('seller');
    } else {
      localStorage.removeItem('selectedSellerId');
      setRole('buyer');
    }

    return data;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('selectedSellerId');
    setUser(null);
    setSelectedSeller(null);
    setRole('buyer');
  }, []);

  const switchToSeller = useCallback(() => {
    if (user && user.role === 'SELLER' && user.sellerId) {
      setRole('seller');
      localStorage.setItem('selectedSellerId', String(user.sellerId));
    }
  }, [user]);

  const switchToBuyer = useCallback(() => {
    setRole('buyer');
    // keep sellerId in storage for when they toggle back
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isSeller: user?.role === 'SELLER',
        isBuyer: user?.role === 'BUYER',
        role,
        selectedSeller,
        sellers,
        login,
        logout,
        switchToSeller,
        switchToBuyer,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
}
