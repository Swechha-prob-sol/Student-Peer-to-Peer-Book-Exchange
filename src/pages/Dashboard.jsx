import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  BookOpen,
  ArrowLeftRight,
  GitPullRequest,
  CheckCircle2,
  Clock,
  PlusCircle,
  TrendingUp,
  Heart,
  ArrowRight,
  MessageSquare,
  Sparkles,
  Award
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useBooks } from '../context/BookContext';
import { Sidebar } from '../components/Sidebar';
import { StatusBadge } from '../components/StatusBadge';
import { BookCard } from '../components/BookCard';

export const Dashboard = () => {
  const { currentUser } = useAuth();
  const { books, requests, wishlist } = useBooks();
  const navigate = useNavigate();

  if (!currentUser) return null;

  // Filter listings by current user
  const myListings = books.filter((b) => b.sellerId === currentUser.id);
  const activeListings = myListings.filter((b) => b.status === 'Active');

  // Filter requests involving current user
  const userRequests = requests.filter(
    (r) => r.sellerId === currentUser.id || r.requesterId === currentUser.id
  );
  const pendingRequests = userRequests.filter((r) => r.status === 'Pending');
  const completedTransactions = userRequests.filter((r) => r.status === 'Completed');

  // Recent requests (top 3)
  const recentRequests = userRequests.slice(0, 3);

  // Community recently added books
  const communityRecent = books.slice(0, 3);

  return (
    <div className="dashboard-layout">
      {/* Sidebar */}
      <Sidebar />

      {/* Main Dashboard Area */}
      <main className="dashboard-content">
        {/* Welcome Header */}
        <div className="dashboard-header">
          <div>
            <span className="section-badge" style={{ marginBottom: '0.4rem' }}>
              <Sparkles size={14} /> Student Dashboard
            </span>
            <h1 style={{ fontSize: '1.85rem', marginBottom: '0.2rem' }}>
              Welcome back, {currentUser.name.split(' ')[0]}! 👋
            </h1>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              {currentUser.college} • {currentUser.department} (Sem {currentUser.semester})
            </p>
          </div>

          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <Link to="/explore" className="btn btn-outline btn-sm">
              <BookOpen size={16} />
              Browse Market
            </Link>
            <Link to="/add-book" className="btn btn-primary btn-sm">
              <PlusCircle size={16} />
              List New Book
            </Link>
          </div>
        </div>

        {/* 1. Statistics Cards Grid */}
        <div className="stats-grid">
          {/* Stat 1: Total Listed Books */}
          <div className="stat-card">
            <div className="stat-icon-wrap" style={{ backgroundColor: 'var(--primary-light)', color: 'var(--primary)' }}>
              <BookOpen size={24} />
            </div>
            <div>
              <div className="stat-val">{myListings.length}</div>
              <div className="stat-label">Total Listed Books</div>
            </div>
          </div>

          {/* Stat 2: Active Listings */}
          <div className="stat-card">
            <div className="stat-icon-wrap" style={{ backgroundColor: 'var(--secondary-light)', color: 'var(--secondary)' }}>
              <TrendingUp size={24} />
            </div>
            <div>
              <div className="stat-val">{activeListings.length}</div>
              <div className="stat-label">Active Listings</div>
            </div>
          </div>

          {/* Stat 3: Pending Requests */}
          <div className="stat-card">
            <div className="stat-icon-wrap" style={{ backgroundColor: 'var(--warning-bg)', color: 'var(--warning)' }}>
              <Clock size={24} />
            </div>
            <div>
              <div className="stat-val">{pendingRequests.length}</div>
              <div className="stat-label">Pending Requests</div>
            </div>
          </div>

          {/* Stat 4: Completed Transactions */}
          <div className="stat-card">
            <div className="stat-icon-wrap" style={{ backgroundColor: 'var(--success-bg)', color: 'var(--success)' }}>
              <CheckCircle2 size={24} />
            </div>
            <div>
              <div className="stat-val">{completedTransactions.length}</div>
              <div className="stat-label">Completed Swaps / Sales</div>
            </div>
          </div>
        </div>

        {/* 2. Middle Row: Recent Requests & Activity Summary */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.75rem', marginBottom: '2.5rem' }}>
          {/* Recent Requests Card */}
          <div className="card" style={{ padding: '1.5rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.25rem' }}>
              <h3 style={{ fontSize: '1.15rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <ArrowLeftRight size={18} color="var(--primary)" />
                Recent Inquiries & Requests
              </h3>
              <Link to="/requests" style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--accent)' }}>
                View All
              </Link>
            </div>

            {recentRequests.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', textAlign: 'center', padding: '1.5rem 0' }}>
                No active requests yet.
              </p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
                {recentRequests.map((req) => {
                  const isReceived = req.sellerId === currentUser.id;
                  return (
                    <div
                      key={req.id}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        padding: '0.75rem',
                        backgroundColor: 'var(--bg-subtle)',
                        borderRadius: 'var(--radius-md)',
                        fontSize: '0.85rem'
                      }}
                    >
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                        <img
                          src={req.bookCover}
                          alt={req.bookTitle}
                          style={{ width: '40px', height: '52px', objectFit: 'cover', borderRadius: 'var(--radius-sm)' }}
                        />
                        <div>
                          <p style={{ fontWeight: 700, color: 'var(--text-main)', maxWidth: '200px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                            {req.bookTitle}
                          </p>
                          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                            {isReceived ? `From ${req.requesterName}` : `To ${req.sellerName}`} • {req.requestType}
                          </span>
                        </div>
                      </div>
                      <StatusBadge status={req.status} />
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Quick Actions & Tips Card */}
          <div className="card" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column' }}>
            <h3 style={{ fontSize: '1.15rem', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Award size={18} color="var(--secondary)" />
              Peer Exchange Tips & Actions
            </h3>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', flex: 1 }}>
              <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'start' }}>
                <div style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: 'var(--secondary)', marginTop: '6px' }} />
                <p style={{ fontSize: '0.875rem', color: 'var(--text-main)' }}>
                  <strong>Verify before handing over:</strong> Ensure physical book condition matches descriptions (e.g. pencil marks vs highlighter notes).
                </p>
              </div>

              <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'start' }}>
                <div style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: 'var(--accent)', marginTop: '6px' }} />
                <p style={{ fontSize: '0.875rem', color: 'var(--text-main)' }}>
                  <strong>Keep active listings updated:</strong> Mark books as Sold or Exchanged once you complete meetups to maintain a 5-star seller score.
                </p>
              </div>

              <div style={{ marginTop: 'auto', paddingTop: '1rem', borderTop: '1px solid var(--border-light)', display: 'flex', gap: '0.75rem' }}>
                <button onClick={() => navigate('/exchange-tracking')} className="btn btn-outline btn-sm" style={{ flex: 1 }}>
                  <GitPullRequest size={14} /> Track Swaps
                </button>
                <button onClick={() => navigate('/messages')} className="btn btn-ghost btn-sm" style={{ flex: 1 }}>
                  <MessageSquare size={14} /> Messages
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* 3. My Active Listings Section */}
        <div style={{ marginBottom: '3rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
            <h3 style={{ fontSize: '1.3rem' }}>My Active Listings ({activeListings.length})</h3>
            <Link to="/my-listings" style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--accent)' }}>
              Manage All Listings ({myListings.length}) →
            </Link>
          </div>

          {activeListings.length === 0 ? (
            <div className="empty-state" style={{ padding: '2.5rem 1.5rem' }}>
              <p style={{ color: 'var(--text-muted)', marginBottom: '1rem' }}>
                You have not listed any textbooks yet. Give your old books a new home!
              </p>
              <Link to="/add-book" className="btn btn-primary btn-sm">
                <PlusCircle size={15} /> List Your First Textbook
              </Link>
            </div>
          ) : (
            <div
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
                gap: '1.5rem'
              }}
            >
              {activeListings.slice(0, 3).map((book) => (
                <BookCard key={book.id} book={book} />
              ))}
            </div>
          )}
        </div>

        {/* 4. Recently Added on Campus */}
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
            <h3 style={{ fontSize: '1.3rem' }}>Explore Books From Peers</h3>
            <Link to="/explore" style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--accent)' }}>
              Explore All →
            </Link>
          </div>

          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
              gap: '1.5rem'
            }}
          >
            {communityRecent.map((book) => (
              <BookCard key={book.id} book={book} />
            ))}
          </div>
        </div>
      </main>
    </div>
  );
};

