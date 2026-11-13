import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { BookProvider } from './context/BookContext';

// Components
import { Navbar } from './components/Navbar';
import { Footer } from './components/Footer';
import { Toast } from './components/Toast';
import { ProtectedRoute } from './components/ProtectedRoute';

// Pages
import { Home } from './pages/Home';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { ExploreBooks } from './pages/ExploreBooks';
import { BookDetails } from './pages/BookDetails';
import { AddBook } from './pages/AddBook';
import { Dashboard } from './pages/Dashboard';
import { MyListings } from './pages/MyListings';
import { Requests } from './pages/Requests';
import { ExchangeTracking } from './pages/ExchangeTracking';
import { Wishlist } from './pages/Wishlist';
import { Messages } from './pages/Messages';
import { SellerProfile } from './pages/SellerProfile';
import { ProfileSettings } from './pages/ProfileSettings';
import { MyReviews } from './pages/MyReviews';

// Layout wrapper to conditionally show Footer
const AppLayout = () => {
  const location = useLocation();

  // Hide footer on full-height dashboard/messages pages for cleanest startup aesthetic
  const isDashboardView = [
    '/dashboard',
    '/my-listings',
    '/requests',
    '/exchange-tracking',
    '/wishlist',
    '/messages',
    '/profile',
    '/my-reviews'
  ].includes(location.pathname);

  return (
    <>
      <Navbar />
      <Toast />
      <div className="page-wrapper">
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/explore" element={<ExploreBooks />} />
          <Route path="/books/:id" element={<BookDetails />} />
          <Route path="/seller/:id" element={<SellerProfile />} />

          {/* Protected Routes */}
          <Route
            path="/add-book"
            element={
              <ProtectedRoute>
                <AddBook />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/my-listings"
            element={
              <ProtectedRoute>
                <MyListings />
              </ProtectedRoute>
            }
          />
          <Route
            path="/requests"
            element={
              <ProtectedRoute>
                <Requests />
              </ProtectedRoute>
            }
          />
          <Route
            path="/exchange-tracking"
            element={
              <ProtectedRoute>
                <ExchangeTracking />
              </ProtectedRoute>
            }
          />
          <Route
            path="/wishlist"
            element={
              <ProtectedRoute>
                <Wishlist />
              </ProtectedRoute>
            }
          />
          <Route
            path="/messages"
            element={
              <ProtectedRoute>
                <Messages />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <ProfileSettings />
              </ProtectedRoute>
            }
          />
          <Route
            path="/my-reviews"
            element={
              <ProtectedRoute>
                <MyReviews />
              </ProtectedRoute>
            }
          />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
      {!isDashboardView && <Footer />}
    </>
  );
};

export function App() {
  return (
    <Router>
      <AuthProvider>
        <BookProvider>
          <AppLayout />
        </BookProvider>
      </AuthProvider>
    </Router>
  );
}

export default App;

