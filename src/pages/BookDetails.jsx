import React, { useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import {
  Heart,
  Share2,
  User,
  School,
  Star,
  CheckCircle,
  ArrowRight,
  MessageSquare,
  ShieldCheck,
  Repeat,
  ShoppingBag,
  ArrowLeft,
  BookOpen
} from 'lucide-react';
import { useBooks } from '../context/BookContext';
import { useAuth } from '../context/AuthContext';
import { RatingStars } from '../components/RatingStars';
import { StatusBadge } from '../components/StatusBadge';
import { BookCard } from '../components/BookCard';
import { Modal } from '../components/Modal';
import { BookCover } from '../components/BookCoverFallback';

export const BookDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { getBookById, books, isInWishlist, toggleWishlist, sendRequest, startConversationWithUser, showToast } = useBooks();
  const { currentUser, isAuthenticated } = useAuth();

  const book = getBookById(id);

  // Modals state
  const [showPurchaseModal, setShowPurchaseModal] = useState(false);
  const [purchaseNotes, setPurchaseNotes] = useState('');

  const [showExchangeModal, setShowExchangeModal] = useState(false);
  const [selectedOfferBookId, setSelectedOfferBookId] = useState('');
  const [exchangeNotes, setExchangeNotes] = useState('');

  if (!book) {
    return (
      <div className="container" style={{ padding: '5rem 0', textAlign: 'center' }}>
        <h2>Book not found</h2>
        <p style={{ margin: '1rem 0 2rem 0' }}>The textbook you are looking for may have been removed or exchanged.</p>
        <Link to="/explore" className="btn btn-primary">
          Back to Explore Books
        </Link>
      </div>
    );
  }

  const inWishlist = isInWishlist(book.id);
  const isOwnListing = currentUser && currentUser.id === book.sellerId;

  // Get current user's available books for exchange offer
  const myAvailableBooks = books.filter(
    (b) => currentUser && b.sellerId === currentUser.id && b.status === 'Active' && b.id !== book.id
  );

  // Related books from the same subject or category
  const relatedBooks = books
    .filter((b) => b.id !== book.id && (b.subject === book.subject || b.category === book.category))
    .slice(0, 3);

  // Handle Purchase Request
  const handlePurchaseSubmit = (e) => {
    e.preventDefault();
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    sendRequest({
      bookId: book.id,
      requestType: 'Purchase',
      notes: purchaseNotes || `Hello ${book.sellerName}, I would like to buy your ${book.title}. Please let me know when and where we can meet!`
    });
    setShowPurchaseModal(false);
    setPurchaseNotes('');
    navigate('/requests');
  };

  // Handle Exchange Request
  const handleExchangeSubmit = (e) => {
    e.preventDefault();
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    if (!selectedOfferBookId && myAvailableBooks.length > 0) {
      showToast('Please select one of your listed books to offer in exchange.', 'error');
      return;
    }

    sendRequest({
      bookId: book.id,
      requestType: 'Exchange',
      offeredBookId: selectedOfferBookId || null,
      notes: exchangeNotes || `Hi ${book.sellerName}, I would love to swap my book for your ${book.title}!`
    });
    setShowExchangeModal(false);
    setSelectedOfferBookId('');
    setExchangeNotes('');
    navigate('/requests');
  };

  // Handle Contact Seller
  const handleContactSeller = () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    const convId = startConversationWithUser({
      id: book.sellerId,
      name: book.sellerName,
      college: book.sellerCollege
    });
    if (convId) {
      navigate('/messages');
    }
  };

  return (
    <div className="book-details-page page-wrapper" style={{ padding: '1.5rem 0 3.5rem 0' }}>
      <div className="container">
        {/* Back Link */}
        <div style={{ marginBottom: '1rem' }}>
          <button
            onClick={() => navigate(-1)}
            className="btn btn-ghost btn-sm"
            style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', color: 'var(--text-muted)' }}
          >
            <ArrowLeft size={16} />
            Back to previous page
          </button>
        </div>

        {/* Main Grid: Left Cover + Right Info */}
        <div
          className="book-details-grid card"
          style={{
            display: 'grid',
            gridTemplateColumns: '290px 1fr',
            gap: '2rem',
            padding: '1.75rem',
            marginBottom: '2rem',
            overflow: 'hidden'
          }}
        >
          {/* Left Column: Book Image & Quick Specs */}
          <div>
            <div
              style={{
                borderRadius: 'var(--radius-md)',
                overflow: 'hidden',
                boxShadow: 'var(--shadow-sm)',
                border: '1px solid var(--border-light)',
                position: 'relative'
              }}
            >
              <BookCover
                src={book.coverImage}
                alt={book.title}
                height="340px"
                subject={book.subject}
                className="book-details-cover"
              />
              <div style={{ position: 'absolute', top: '0.75rem', left: '0.75rem' }}>
                <StatusBadge status={book.listingType} type="listingType" />
              </div>
            </div>

            {/* Quick Specs Grid */}
            <div
              style={{
                marginTop: '1rem',
                backgroundColor: 'var(--bg-subtle)',
                borderRadius: 'var(--radius-md)',
                padding: '0.85rem 1rem',
                display: 'grid',
                gridTemplateColumns: '1fr 1fr',
                gap: '0.5rem',
                fontSize: '0.82rem'
              }}
            >
              <div>
                <span style={{ color: 'var(--text-muted)' }}>Condition:</span>
                <p style={{ fontWeight: 700, color: 'var(--text-main)' }}>{book.condition}</p>
              </div>
              <div>
                <span style={{ color: 'var(--text-muted)' }}>Semester:</span>
                <p style={{ fontWeight: 700, color: 'var(--text-main)' }}>Semester {book.semester}</p>
              </div>
              <div>
                <span style={{ color: 'var(--text-muted)' }}>Edition:</span>
                <p style={{ fontWeight: 700, color: 'var(--text-main)' }}>{book.edition}</p>
              </div>
              <div>
                <span style={{ color: 'var(--text-muted)' }}>ISBN:</span>
                <p style={{ fontWeight: 700, color: 'var(--text-main)' }}>{book.isbn}</p>
              </div>
            </div>
          </div>

          {/* Right Column: Book Details & Actions */}
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '0.5rem' }}>
              <span className="section-badge" style={{ marginBottom: 0 }}>
                {book.subject}
              </span>
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                Listed on {book.dateListed}
              </span>
            </div>

            <h1 style={{ fontSize: '2.1rem', marginBottom: '0.5rem', lineHeight: 1.25 }}>
              {book.title}
            </h1>

            <p style={{ fontSize: '1.05rem', color: 'var(--text-muted)', marginBottom: '1.5rem' }}>
              Written by <strong>{book.author}</strong>
            </p>

            {/* Price Box */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '1.5rem',
                padding: '1.25rem 1.5rem',
                backgroundColor: 'var(--primary-light)',
                borderRadius: 'var(--radius-lg)',
                marginBottom: '1.75rem'
              }}
            >
              <div>
                <span style={{ fontSize: '0.75rem', textTransform: 'uppercase', fontWeight: 700, color: 'var(--primary)' }}>
                  Listing Price
                </span>
                <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--primary)', fontFamily: 'var(--font-heading)' }}>
                  {book.listingType === 'For Exchange' ? 'Exchange Only' : `₹${book.price}`}
                </div>
              </div>
              {book.preferredExchange && (
                <div style={{ borderLeft: '1px solid rgba(23, 37, 84, 0.15)', paddingLeft: '1.5rem' }}>
                  <span style={{ fontSize: '0.75rem', textTransform: 'uppercase', fontWeight: 700, color: 'var(--secondary)' }}>
                    Preferred Swap:
                  </span>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-main)', fontWeight: 600, marginTop: '0.2rem' }}>
                    {book.preferredExchange}
                  </p>
                </div>
              )}
            </div>

            {/* Book Description */}
            <div style={{ marginBottom: '2rem' }}>
              <h3 style={{ fontSize: '1.1rem', marginBottom: '0.6rem' }}>Book Description & Annotations</h3>
              <p style={{ color: 'var(--text-main)', lineHeight: 1.7, fontSize: '0.95rem' }}>
                {book.description}
              </p>
            </div>

            {/* Seller Information Card */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '1.25rem',
                backgroundColor: 'var(--bg-subtle)',
                borderRadius: 'var(--radius-lg)',
                marginBottom: '2rem',
                border: '1px solid var(--border-light)'
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                <div
                  style={{
                    width: '50px',
                    height: '50px',
                    borderRadius: '50%',
                    backgroundColor: 'var(--primary)',
                    color: '#ffffff',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontWeight: 700,
                    fontSize: '1.2rem'
                  }}
                >
                  {book.sellerName.charAt(0)}
                </div>
                <div>
                  <Link
                    to={`/seller/${book.sellerId}`}
                    style={{ fontSize: '1.05rem', fontWeight: 700, color: 'var(--text-main)', textDecoration: 'none' }}
                  >
                    {book.sellerName}
                  </Link>
                  <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                    {book.sellerCollege}
                  </p>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', marginTop: '0.2rem' }}>
                    <RatingStars rating={book.sellerRating || 5.0} size={14} />
                    <span style={{ fontSize: '0.8rem', fontWeight: 600 }}>{book.sellerRating}</span>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                      ({book.sellerReviewsCount || 10} peer reviews)
                    </span>
                  </div>
                </div>
              </div>

              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <Link to={`/seller/${book.sellerId}`} className="btn btn-outline btn-sm">
                  View Profile
                </Link>
                <button onClick={handleContactSeller} className="btn btn-ghost btn-sm" title="Message Seller">
                  <MessageSquare size={16} />
                  <span>Chat</span>
                </button>
              </div>
            </div>

            {/* Action Buttons Group */}
            <div style={{ marginTop: 'auto', display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
              {isOwnListing ? (
                <div style={{ padding: '0.75rem 1rem', backgroundColor: 'var(--bg-subtle)', borderRadius: 'var(--radius-md)', width: '100%' }}>
                  <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>
                    This is your listed textbook. You can manage or edit it in your{' '}
                    <Link to="/my-listings" style={{ fontWeight: 600, color: 'var(--primary)' }}>
                      My Listings
                    </Link>{' '}
                    page.
                  </p>
                </div>
              ) : (
                <>
                  {/* Buy Button */}
                  {(book.listingType === 'For Sale' || book.listingType === 'Both') && (
                    <button
                      onClick={() => setShowPurchaseModal(true)}
                      className="btn btn-primary btn-lg"
                      style={{ flex: 1, minWidth: '180px' }}
                    >
                      <ShoppingBag size={18} />
                      <span>Send Purchase Request</span>
                    </button>
                  )}

                  {/* Exchange Button */}
                  {(book.listingType === 'For Exchange' || book.listingType === 'Both') && (
                    <button
                      onClick={() => setShowExchangeModal(true)}
                      className="btn btn-secondary btn-lg"
                      style={{ flex: 1, minWidth: '180px' }}
                    >
                      <Repeat size={18} />
                      <span>Request Book Swap</span>
                    </button>
                  )}

                  {/* Wishlist Button */}
                  <button
                    onClick={() => toggleWishlist(book.id)}
                    className="btn btn-outline btn-lg"
                    style={{
                      borderColor: inWishlist ? 'var(--error)' : 'var(--border-light)',
                      color: inWishlist ? 'var(--error)' : 'var(--text-main)'
                    }}
                    title={inWishlist ? 'Remove from Wishlist' : 'Add to Wishlist'}
                  >
                    <Heart size={20} fill={inWishlist ? 'var(--error)' : 'none'} />
                    <span>{inWishlist ? 'Saved' : 'Wishlist'}</span>
                  </button>
                </>
              )}
            </div>
          </div>
        </div>

        {/* Related Books Section */}
        {relatedBooks.length > 0 && (
          <div style={{ marginTop: '4rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
              <div>
                <h3 style={{ fontSize: '1.5rem', marginBottom: '0.25rem' }}>Related Academic Textbooks</h3>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                  Other students listing books in {book.subject}
                </p>
              </div>
              <Link to={`/explore?subject=${encodeURIComponent(book.subject)}`} className="btn btn-outline btn-sm">
                View More in {book.subject}
              </Link>
            </div>

            <div
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
                gap: '1.75rem'
              }}
            >
              {relatedBooks.map((rel) => (
                <BookCard key={rel.id} book={rel} />
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Modal 1: Purchase Request Confirmation */}
      {showPurchaseModal && (
        <Modal
          isOpen={showPurchaseModal}
          onClose={() => setShowPurchaseModal(false)}
          title="Send Purchase Request"
        >
          <form onSubmit={handlePurchaseSubmit}>
            <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.5rem', alignItems: 'center' }}>
              <img
                src={book.coverImage}
                alt={book.title}
                style={{ width: '60px', height: '80px', objectFit: 'cover', borderRadius: 'var(--radius-sm)' }}
              />
              <div>
                <h4 style={{ fontSize: '1rem', marginBottom: '0.2rem' }}>{book.title}</h4>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Seller: {book.sellerName} ({book.sellerCollege})</p>
                <p style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--primary)', marginTop: '0.2rem' }}>
                  ₹{book.price}
                </p>
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Message / Meeting Note for Seller</label>
              <textarea
                className="form-textarea"
                rows="3"
                placeholder="e.g. Hi! I need this for my class tomorrow. Can we meet outside the campus library?"
                value={purchaseNotes}
                onChange={(e) => setPurchaseNotes(e.target.value)}
              />
            </div>

            <div style={{ padding: '0.75rem', backgroundColor: 'var(--primary-light)', borderRadius: 'var(--radius-md)', fontSize: '0.85rem', color: 'var(--primary)', marginBottom: '1.5rem' }}>
              💡 <strong>Peer Safety:</strong> Never pay before inspecting the physical textbook. Meet at well-lit campus landmarks!
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
              <button type="button" onClick={() => setShowPurchaseModal(false)} className="btn btn-outline">
                Cancel
              </button>
              <button type="submit" className="btn btn-primary">
                Confirm & Send Request
              </button>
            </div>
          </form>
        </Modal>
      )}

      {/* Modal 2: Exchange Request with Book Selector */}
      {showExchangeModal && (
        <Modal
          isOpen={showExchangeModal}
          onClose={() => setShowExchangeModal(false)}
          title="Request Book Exchange"
        >
          <form onSubmit={handleExchangeSubmit}>
            <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.5rem', alignItems: 'center' }}>
              <img
                src={book.coverImage}
                alt={book.title}
                style={{ width: '60px', height: '80px', objectFit: 'cover', borderRadius: 'var(--radius-sm)' }}
              />
              <div>
                <h4 style={{ fontSize: '1rem', marginBottom: '0.2rem' }}>Target: {book.title}</h4>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Listed by {book.sellerName}</p>
                {book.preferredExchange && (
                  <p style={{ fontSize: '0.8rem', color: 'var(--secondary)', fontWeight: 600 }}>
                    Preferred swap: {book.preferredExchange}
                  </p>
                )}
              </div>
            </div>

            {/* Select own listed book */}
            <div className="form-group">
              <label className="form-label form-label-required">Select a Book from Your Listings to Offer</label>
              {myAvailableBooks.length > 0 ? (
                <select
                  className="form-select"
                  value={selectedOfferBookId}
                  onChange={(e) => setSelectedOfferBookId(e.target.value)}
                  required
                >
                  <option value="">-- Choose one of your active listings --</option>
                  {myAvailableBooks.map((b) => (
                    <option key={b.id} value={b.id}>
                      {b.title} ({b.edition} • {b.condition})
                    </option>
                  ))}
                </select>
              ) : (
                <div style={{ padding: '1rem', backgroundColor: 'var(--warning-bg)', borderRadius: 'var(--radius-md)', fontSize: '0.85rem', color: 'var(--warning)' }}>
                  You do not have any active books listed yet. You can still send a proposal message or{' '}
                  <Link to="/add-book" style={{ fontWeight: 700, textDecoration: 'underline' }}>
                    list a textbook first
                  </Link>
                  .
                </div>
              )}
            </div>

            <div className="form-group">
              <label className="form-label">Exchange Proposal Message</label>
              <textarea
                className="form-textarea"
                rows="3"
                placeholder="Mention condition details or when you can meet for handoff..."
                value={exchangeNotes}
                onChange={(e) => setExchangeNotes(e.target.value)}
              />
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
              <button type="button" onClick={() => setShowExchangeModal(false)} className="btn btn-outline">
                Cancel
              </button>
              <button type="submit" className="btn btn-secondary">
                Submit Exchange Offer
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};

