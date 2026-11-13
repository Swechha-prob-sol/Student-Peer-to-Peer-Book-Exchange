import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard,
  BookOpen,
  ArrowLeftRight,
  GitPullRequest,
  Heart,
  MessageSquare,
  Star,
  UserCheck,
  LogOut,
  PlusCircle
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useBooks } from '../context/BookContext';

export const Sidebar = () => {
  const { currentUser, logout } = useAuth();
  const { books, requests, wishlist, conversations } = useBooks();
  const navigate = useNavigate();

  if (!currentUser) return null;

  // Compute counts for badges
  const myListingsCount = books.filter((b) => b.sellerId === currentUser.id && b.status === 'Active').length;
  const pendingRequestsCount = requests.filter(
    (r) => (r.sellerId === currentUser.id || r.requesterId === currentUser.id) && r.status === 'Pending'
  ).length;
  const activeExchangesCount = requests.filter(
    (r) => (r.sellerId === currentUser.id || r.requesterId === currentUser.id) && r.status === 'Accepted'
  ).length;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <aside className="sidebar">
      {/* Student Profile Card in Sidebar */}
      <div className="sidebar-profile">
        <img
          src={currentUser.avatar}
          alt={currentUser.name}
          className="sidebar-avatar"
          onError={(e) => {
            e.target.src = `https://api.dicebear.com/7.x/initials/svg?seed=${encodeURIComponent(currentUser.name)}`;
          }}
        />
        <div style={{ overflow: 'hidden' }}>
          <h4 className="sidebar-user-name" title={currentUser.name}>
            {currentUser.name}
          </h4>
          <p className="sidebar-user-college" title={currentUser.college}>
            {currentUser.college}
          </p>
        </div>
      </div>

      {/* Quick Action Button */}
      <div style={{ marginBottom: '1.25rem' }}>
        <button
          onClick={() => navigate('/add-book')}
          className="btn btn-secondary btn-full btn-sm"
          style={{ justifyContent: 'center' }}
        >
          <PlusCircle size={16} />
          <span>List a Book</span>
        </button>
      </div>

      {/* Navigation Items */}
      <ul className="sidebar-nav">
        <li>
          <NavLink
            to="/dashboard"
            end
            className={({ isActive }) => `sidebar-item-link ${isActive ? 'active' : ''}`}
          >
            <LayoutDashboard size={18} />
            <span>Overview</span>
          </NavLink>
        </li>

        <li>
          <NavLink
            to="/my-listings"
            className={({ isActive }) => `sidebar-item-link ${isActive ? 'active' : ''}`}
          >
            <BookOpen size={18} />
            <span>My Listings</span>
            {myListingsCount > 0 && <span className="sidebar-badge">{myListingsCount}</span>}
          </NavLink>
        </li>

        <li>
          <NavLink
            to="/requests"
            className={({ isActive }) => `sidebar-item-link ${isActive ? 'active' : ''}`}
          >
            <ArrowLeftRight size={18} />
            <span>My Requests</span>
            {pendingRequestsCount > 0 && (
              <span className="sidebar-badge" style={{ backgroundColor: 'var(--warning-bg)', color: 'var(--warning)' }}>
                {pendingRequestsCount}
              </span>
            )}
          </NavLink>
        </li>

        <li>
          <NavLink
            to="/exchange-tracking"
            className={({ isActive }) => `sidebar-item-link ${isActive ? 'active' : ''}`}
          >
            <GitPullRequest size={18} />
            <span>Exchange Tracking</span>
            {activeExchangesCount > 0 && (
              <span className="sidebar-badge">{activeExchangesCount}</span>
            )}
          </NavLink>
        </li>

        <li>
          <NavLink
            to="/wishlist"
            className={({ isActive }) => `sidebar-item-link ${isActive ? 'active' : ''}`}
          >
            <Heart size={18} />
            <span>Wishlist</span>
            {wishlist.length > 0 && <span className="sidebar-badge">{wishlist.length}</span>}
          </NavLink>
        </li>

        <li>
          <NavLink
            to="/messages"
            className={({ isActive }) => `sidebar-item-link ${isActive ? 'active' : ''}`}
          >
            <MessageSquare size={18} />
            <span>Messages</span>
          </NavLink>
        </li>

        <li>
          <NavLink
            to="/my-reviews"
            className={({ isActive }) => `sidebar-item-link ${isActive ? 'active' : ''}`}
          >
            <Star size={18} />
            <span>My Reviews</span>
          </NavLink>
        </li>

        <li>
          <NavLink
            to="/profile"
            className={({ isActive }) => `sidebar-item-link ${isActive ? 'active' : ''}`}
          >
            <UserCheck size={18} />
            <span>Profile Settings</span>
          </NavLink>
        </li>

        <li style={{ marginTop: 'auto', paddingTop: '1rem', borderTop: '1px solid var(--border-light)' }}>
          <button
            onClick={handleLogout}
            className="sidebar-item-link"
            style={{ width: '100%', border: 'none', background: 'none', cursor: 'pointer', textAlign: 'left', color: 'var(--error)' }}
          >
            <LogOut size={18} />
            <span>Logout</span>
          </button>
        </li>
      </ul>
    </aside>
  );
};

