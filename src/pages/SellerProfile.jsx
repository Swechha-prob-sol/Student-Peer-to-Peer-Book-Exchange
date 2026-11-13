import React from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import {
  User,
  School,
  Star,
  CheckCircle,
  BookOpen,
  Repeat,
  MessageSquare,
  ArrowLeft,
  Calendar,
  Sparkles
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useBooks } from '../context/BookContext';
import { RatingStars } from '../components/RatingStars';
import { BookCard } from '../components/BookCard';

export const SellerProfile = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { users, currentUser } = useAuth();
  const { books, reviews, startConversationWithUser } = useBooks();

  // Find user
  const seller = users.find((u) => u.id === id) || {
    id,
    name: 'Student Seller',
    college: 'Engineering Institute',
    department: 'Computer Science',
    semester: '4',
    rating: 4.8,
    reviewCount: 12,
    booksSold: 8,
    booksExchanged: 4,
    avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80',
    bio: 'Avid student reader eager to share verified semester books.',
    joinedDate: 'August 2025'
  };

  // Seller's books
  const sellerBooks = books.filter((b) => b.sellerId === seller.id && b.status === 'Active');

  // Reviews for this seller
  const sellerReviews = reviews.filter((r) => r.sellerId === seller.id);

  const handleMessage = () => {
    const convId = startConversationWithUser(seller);
    if (convId) navigate('/messages');
  };

  return (
    <div className="seller-profile-page page-wrapper" style={{ padding: '2.5rem 0 5rem 0' }}>
      <div className="container">
        {/* Back Link */}
        <div style={{ marginBottom: '1.5rem' }}>
          <button
            onClick={() => navigate(-1)}
            className="btn btn-ghost btn-sm"
            style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', color: 'var(--text-muted)' }}
          >
            <ArrowLeft size={16} />
            Back
          </button>
        </div>

        {/* Profile Header Banner Card */}
        <div
          className="card"
          style={{
            padding: '2.5rem',
            marginBottom: '3rem',
            background: 'linear-gradient(to right, #ffffff 60%, var(--primary-light) 100%)'
          }}
        >
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              flexWrap: 'wrap',
              gap: '2rem'
            }}
          >
            {/* Left: Avatar & Bio */}
            <div style={{ display: 'flex', alignItems: 'center', gap: '1.75rem', flex: 1, minWidth: '300px' }}>
              <img
                src={seller.avatar}
                alt={seller.name}
                style={{
                  width: '100px',
                  height: '100px',
                  borderRadius: '50%',
                  objectFit: 'cover',
                  border: '4px solid #ffffff',
                  boxShadow: 'var(--shadow-md)'
                }}
              />
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                  <h1 style={{ fontSize: '1.85rem' }}>{seller.name}</h1>
                  <span
                    style={{
                      display: 'inline-flex',
                      alignItems: 'center',
                      gap: '0.25rem',
                      padding: '0.2rem 0.5rem',
                      backgroundColor: '#dcfce7',
                      color: '#15803d',
                      borderRadius: 'var(--radius-full)',
                      fontSize: '0.75rem',
                      fontWeight: 700
                    }}
                  >
                    <CheckCircle size={12} /> Verified Peer
                  </span>
                </div>

                <p style={{ fontSize: '1rem', color: 'var(--text-main)', fontWeight: 600, marginBottom: '0.25rem' }}>
                  {seller.college} • {seller.department} (Sem {seller.semester})
                </p>

                <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', maxWidth: '540px', lineHeight: 1.5, marginBottom: '0.75rem' }}>
                  "{seller.bio}"
                </p>

                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <RatingStars rating={seller.rating} size={18} />
                  <span style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--primary)' }}>{seller.rating}</span>
                  <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                    ({seller.reviewCount || sellerReviews.length} community reviews)
                  </span>
                </div>
              </div>
            </div>

            {/* Right: Quick Stats & Chat CTA */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', alignItems: 'flex-end' }}>
              <div style={{ display: 'flex', gap: '1.5rem', textAlign: 'center' }}>
                <div style={{ padding: '0.75rem 1.25rem', backgroundColor: '#ffffff', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)' }}>
                  <div style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--primary)' }}>
                    {seller.booksSold || 12}
                  </div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Books Sold</div>
                </div>

                <div style={{ padding: '0.75rem 1.25rem', backgroundColor: '#ffffff', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)' }}>
                  <div style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--secondary)' }}>
                    {seller.booksExchanged || 6}
                  </div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Exchanges</div>
                </div>
              </div>

              {currentUser?.id !== seller.id && (
                <button onClick={handleMessage} className="btn btn-secondary">
                  <MessageSquare size={16} /> Contact Seller
                </button>
              )}
            </div>
          </div>
        </div>

        {/* Section 1: Active Listings by this seller */}
        <div style={{ marginBottom: '4rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <div>
              <h2 style={{ fontSize: '1.5rem', marginBottom: '0.2rem' }}>Currently Listed Textbooks ({sellerBooks.length})</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                Academic books currently available for purchase or swap from {seller.name.split(' ')[0]}.
              </p>
            </div>
          </div>

          {sellerBooks.length === 0 ? (
            <div className="empty-state">
              <BookOpen size={32} />
              <h3 className="empty-title">No books listed right now</h3>
              <p className="empty-desc">This student currently has no active textbook listings on the marketplace.</p>
            </div>
          ) : (
            <div
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
                gap: '1.75rem'
              }}
            >
              {sellerBooks.map((book) => (
                <BookCard key={book.id} book={book} />
              ))}
            </div>
          )}
        </div>

        {/* Section 2: Previous Peer Reviews */}
        <div>
          <h2 style={{ fontSize: '1.5rem', marginBottom: '1.5rem' }}>Student Reviews & Feedback</h2>
          {sellerReviews.length === 0 ? (
            <div className="empty-state">
              <Star size={32} />
              <h3 className="empty-title">No reviews posted yet</h3>
              <p className="empty-desc">Be the first to trade or purchase with {seller.name} and share feedback!</p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {sellerReviews.map((rev) => (
                <div key={rev.id} className="card" style={{ padding: '1.5rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                      <img
                        src={rev.reviewerAvatar}
                        alt={rev.reviewerName}
                        style={{ width: '42px', height: '42px', borderRadius: '50%', objectFit: 'cover' }}
                      />
                      <div>
                        <h4 style={{ fontSize: '0.95rem' }}>{rev.reviewerName}</h4>
                        <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{rev.reviewerCollege}</p>
                      </div>
                    </div>

                    <div style={{ textAlign: 'right' }}>
                      <RatingStars rating={rev.rating} size={15} />
                      <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{rev.date}</span>
                    </div>
                  </div>

                  <p style={{ fontSize: '0.9rem', color: 'var(--text-main)', lineHeight: 1.6 }}>
                    "{rev.comment}"
                  </p>

                  <div style={{ marginTop: '0.5rem', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    Traded item: <strong>{rev.bookTitle}</strong>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

