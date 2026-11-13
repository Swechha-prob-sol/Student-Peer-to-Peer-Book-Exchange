import React, { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { BookOpen, PlusCircle, User, LogOut, Menu, X, Heart, MessageSquare, Layers } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useBooks } from '../context/BookContext';

export const Navbar = () => {
  const { currentUser, logout, isAuthenticated } = useAuth();
  const { wishlist } = useBooks();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    setMobileMenuOpen(false);
    navigate('/');
  };

  const closeMenu = () => setMobileMenuOpen(false);

  return (
    <header className="navbar">
      <div className="container nav-container">
        {/* Brand Logo */}
        <Link to="/" className="nav-brand" onClick={closeMenu}>
          <div className="nav-logo-icon">
            <BookOpen size={20} strokeWidth={2.5} />
          </div>
          <span>Book<span style={{ color: 'var(--secondary)' }}>Loop</span></span>
        </Link>

        {/* Desktop Links */}
        <nav>
          <ul className="nav-links">
            <li>
              <NavLink to="/" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                Home
              </NavLink>
            </li>
            <li>
              <NavLink to="/explore" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                Explore Books
              </NavLink>
            </li>
            <li>
              <a href="/#how-it-works" className="nav-link">
                How It Works
              </a>
            </li>
            <li>
              <a href="/#about-us" className="nav-link">
                About Us
              </a>
            </li>
          </ul>
        </nav>

        {/* Nav Actions */}
        <div className="nav-actions">
          {isAuthenticated ? (
            <>
              <Link
                to="/add-book"
                className="btn btn-secondary btn-sm"
                style={{ display: 'inline-flex' }}
              >
                <PlusCircle size={16} />
                <span>List a Book</span>
              </Link>

              <Link
                to="/wishlist"
                className="btn btn-ghost btn-sm"
                title="Wishlist"
                style={{ padding: '0.4rem', position: 'relative' }}
              >
                <Heart size={20} color="var(--text-main)" />
                {wishlist.length > 0 && (
                  <span
                    style={{
                      position: 'absolute',
                      top: '0px',
                      right: '0px',
                      background: 'var(--error)',
                      color: '#ffffff',
                      borderRadius: '50%',
                      width: '16px',
                      height: '16px',
                      fontSize: '10px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontWeight: 700
                    }}
                  >
                    {wishlist.length}
                  </span>
                )}
              </Link>

              <Link to="/dashboard" className="user-pill" title="Go to Dashboard">
                <img
                  src={currentUser.avatar}
                  alt={currentUser.name}
                  className="user-pill-img"
                  onError={(e) => {
                    e.target.src = `https://api.dicebear.com/7.x/initials/svg?seed=${encodeURIComponent(currentUser.name)}`;
                  }}
                />
                <span className="user-pill-name">{currentUser.name.split(' ')[0]}</span>
              </Link>

              <button
                onClick={handleLogout}
                className="btn btn-ghost btn-sm"
                title="Logout"
                style={{ padding: '0.4rem' }}
              >
                <LogOut size={18} />
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn btn-outline btn-sm">
                Login
              </Link>
              <Link to="/register" className="btn btn-primary btn-sm">
                Sign Up
              </Link>
            </>
          )}

          {/* Mobile hamburger menu toggle */}
          <button
            className="mobile-menu-btn"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            aria-label="Toggle navigation menu"
          >
            {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>
      </div>

      {/* Mobile Drawer Menu */}
      {mobileMenuOpen && (
        <div className="mobile-nav-menu">
          <Link to="/" className="nav-link" onClick={closeMenu}>
            Home
          </Link>
          <Link to="/explore" className="nav-link" onClick={closeMenu}>
            Explore Books
          </Link>
          <a href="/#how-it-works" className="nav-link" onClick={closeMenu}>
            How It Works
          </a>
          <a href="/#about-us" className="nav-link" onClick={closeMenu}>
            About Us
          </a>

          <hr style={{ border: 'none', borderTop: '1px solid var(--border-light)', margin: '0.5rem 0' }} />

          {isAuthenticated ? (
            <>
              <Link to="/dashboard" className="btn btn-outline btn-full" onClick={closeMenu}>
                <Layers size={16} /> Dashboard
              </Link>
              <Link to="/add-book" className="btn btn-secondary btn-full" onClick={closeMenu}>
                <PlusCircle size={16} /> List a Book
              </Link>
              <Link to="/messages" className="btn btn-ghost btn-full" onClick={closeMenu}>
                <MessageSquare size={16} /> Messages
              </Link>
              <button onClick={handleLogout} className="btn btn-danger btn-full">
                <LogOut size={16} /> Logout ({currentUser.name})
              </button>
            </>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              <Link to="/login" className="btn btn-outline btn-full" onClick={closeMenu}>
                Login
              </Link>
              <Link to="/register" className="btn btn-primary btn-full" onClick={closeMenu}>
                Sign Up
              </Link>
            </div>
          )}
        </div>
      )}
    </header>
  );
};

