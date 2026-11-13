import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Heart, Trash2, BookOpen, ArrowRight, ShoppingBag, Eye } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useBooks } from '../context/BookContext';
import { Sidebar } from '../components/Sidebar';
import { StatusBadge } from '../components/StatusBadge';
import { BookCover } from '../components/BookCoverFallback';

export const Wishlist = () => {
  const { currentUser } = useAuth();
  const { wishlist, books, toggleWishlist } = useBooks();
  const navigate = useNavigate();

  if (!currentUser) return null;

  // Retrieve book objects corresponding to IDs in wishlist
  const wishlistBooks = books.filter((b) => wishlist.includes(b.id));

  return (
    <div className="dashboard-layout">
      <Sidebar />

      <main className="dashboard-content">
        <div className="dashboard-header">
          <div>
            <h1 style={{ fontSize: '1.85rem', marginBottom: '0.2rem' }}>Saved Wishlist</h1>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Keep track of course textbooks you intend to buy or exchange later in the semester.
            </p>
          </div>

          <Link to="/explore" className="btn btn-outline btn-sm">
            <BookOpen size={16} /> Explore More Books
          </Link>
        </div>

        {wishlistBooks.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon-wrap" style={{ backgroundColor: '#fee2e2', color: '#ef4444' }}>
              <Heart size={30} />
            </div>
            <h3 className="empty-title">Your Wishlist is Empty</h3>
            <p className="empty-desc">
              Explore the marketplace and tap the heart icon on any textbook to save it to your wishlist.
            </p>
            <Link to="/explore" className="btn btn-primary btn-sm">
              Discover Academic Books
            </Link>
          </div>
        ) : (
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
              gap: '1.5rem'
            }}
          >
            {wishlistBooks.map((book) => (
              <div key={book.id} className="book-card">
                <div className="book-card-image-wrap">
                  <BookCover
                    src={book.coverImage}
                    alt={book.title}
                    subject={book.subject}
                    className="book-card-img"
                    height="220px"
                  />
                  <div className="book-card-badge">
                    <StatusBadge status={book.listingType} type="listingType" />
                  </div>
                  <button
                    onClick={() => toggleWishlist(book.id)}
                    className="book-wishlist-btn active"
                    title="Remove from Wishlist"
                  >
                    <Trash2 size={16} color="#ef4444" />
                  </button>
                </div>

                <div className="book-card-body">
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    {book.subject} • Sem {book.semester}
                  </span>

                  <h4 className="book-card-title" title={book.title} style={{ margin: '0.25rem 0' }}>
                    <Link to={`/books/${book.id}`} style={{ color: 'var(--text-main)', textDecoration: 'none' }}>
                      {book.title}
                    </Link>
                  </h4>

                  <p className="book-card-author">by {book.author}</p>

                  <div className="book-card-details-row">
                    <span>Seller: <strong>{book.sellerName}</strong></span>
                    <span>Cond: <strong>{book.condition}</strong></span>
                  </div>

                  <div className="book-card-footer">
                    <div className="book-price-block">
                      <span className="book-price-label">Price</span>
                      <span className="book-price-value">
                        {book.listingType === 'For Exchange' ? 'Swap' : `₹${book.price}`}
                      </span>
                    </div>

                    <div style={{ display: 'flex', gap: '0.4rem' }}>
                      <button
                        onClick={() => navigate(`/books/${book.id}`)}
                        className="btn btn-outline btn-sm"
                      >
                        <Eye size={14} /> View
                      </button>
                      <button
                        onClick={() => toggleWishlist(book.id)}
                        className="btn btn-danger btn-sm"
                        title="Remove"
                      >
                        Remove
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
};

