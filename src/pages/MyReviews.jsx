import React, { useState } from 'react';
import {
  Star,
  Award,
  MessageSquare,
  Sparkles,
  Inbox,
  Send,
  PlusCircle
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useBooks } from '../context/BookContext';
import { Sidebar } from '../components/Sidebar';
import { RatingStars } from '../components/RatingStars';
import { Modal } from '../components/Modal';

export const MyReviews = () => {
  const { currentUser } = useAuth();
  const { reviews, addReview } = useBooks();

  const [activeTab, setActiveTab] = useState('received'); // 'received' or 'submitted'

  // Submit new review modal
  const [showModal, setShowModal] = useState(false);
  const [newSellerName, setNewSellerName] = useState('Priya Patel');
  const [newBookTitle, setNewBookTitle] = useState('Introduction to Algorithms (CLRS)');
  const [newRating, setNewRating] = useState(5);
  const [newComment, setNewComment] = useState('');

  if (!currentUser) return null;

  // Filter reviews
  const receivedReviews = reviews.filter((r) => r.sellerId === currentUser.id);
  const submittedReviews = reviews.filter((r) => r.reviewerId === currentUser.id);

  const displayedReviews = activeTab === 'received' ? receivedReviews : submittedReviews;

  // Compute average rating
  const avgRating = receivedReviews.length > 0
    ? (receivedReviews.reduce((acc, r) => acc + r.rating, 0) / receivedReviews.length).toFixed(1)
    : '5.0';

  const handleCreateReview = (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;

    addReview({
      sellerId: 'user_2', // demo target seller
      bookTitle: newBookTitle,
      rating: newRating,
      comment: newComment
    });

    setShowModal(false);
    setNewComment('');
    setNewRating(5);
  };

  return (
    <div className="dashboard-layout">
      <Sidebar />

      <main className="dashboard-content">
        <div className="dashboard-header">
          <div>
            <h1 style={{ fontSize: '1.85rem', marginBottom: '0.2rem' }}>Peer Ratings & Reviews</h1>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Your campus peer reputation, feedback on completed transactions, and reviews you've shared.
            </p>
          </div>

          <button onClick={() => setShowModal(true)} className="btn btn-secondary btn-sm">
            <PlusCircle size={16} /> Write a Review
          </button>
        </div>

        {/* Reputation Summary Card */}
        <div
          className="card"
          style={{
            padding: '1.75rem 2rem',
            marginBottom: '2rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
            gap: '1.5rem',
            background: 'linear-gradient(135deg, var(--bg-card) 60%, var(--primary-light) 100%)'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
            <div
              style={{
                width: '64px',
                height: '64px',
                borderRadius: 'var(--radius-lg)',
                backgroundColor: 'var(--primary)',
                color: '#ffffff',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '1.75rem',
                fontWeight: 800
              }}
            >
              {avgRating}
            </div>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                <RatingStars rating={Number(avgRating)} size={20} />
                <span style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--text-main)' }}>
                  Overall Seller Score
                </span>
              </div>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                Based on {receivedReviews.length} verified campus exchanges with peers
              </p>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '1rem' }}>
            <div style={{ textAlign: 'center', padding: '0.5rem 1rem' }}>
              <div style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--primary)' }}>
                {receivedReviews.length}
              </div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Reviews Received</div>
            </div>
            <div style={{ textAlign: 'center', padding: '0.5rem 1rem' }}>
              <div style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--secondary)' }}>
                {submittedReviews.length}
              </div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Reviews Given</div>
            </div>
          </div>
        </div>

        {/* Tab Controls */}
        <div
          style={{
            display: 'flex',
            gap: '1rem',
            borderBottom: '1px solid var(--border-light)',
            marginBottom: '2rem'
          }}
        >
          <button
            onClick={() => setActiveTab('received')}
            style={{
              background: 'none',
              border: 'none',
              padding: '0.85rem 1.25rem',
              fontSize: '1rem',
              fontWeight: activeTab === 'received' ? 700 : 500,
              color: activeTab === 'received' ? 'var(--primary)' : 'var(--text-muted)',
              borderBottom: activeTab === 'received' ? '3px solid var(--primary)' : '3px solid transparent',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem'
            }}
          >
            <Inbox size={18} />
            <span>Reviews Received ({receivedReviews.length})</span>
          </button>

          <button
            onClick={() => setActiveTab('submitted')}
            style={{
              background: 'none',
              border: 'none',
              padding: '0.85rem 1.25rem',
              fontSize: '1rem',
              fontWeight: activeTab === 'submitted' ? 700 : 500,
              color: activeTab === 'submitted' ? 'var(--primary)' : 'var(--text-muted)',
              borderBottom: activeTab === 'submitted' ? '3px solid var(--primary)' : '3px solid transparent',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem'
            }}
          >
            <Send size={18} />
            <span>Reviews Submitted ({submittedReviews.length})</span>
          </button>
        </div>

        {/* Reviews Listing */}
        {displayedReviews.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon-wrap" style={{ backgroundColor: '#fef3c7', color: '#b45309' }}>
              <Star size={30} />
            </div>
            <h3 className="empty-title">
              {activeTab === 'received' ? 'No Reviews Received Yet' : 'No Reviews Submitted Yet'}
            </h3>
            <p className="empty-desc">
              {activeTab === 'received'
                ? 'Complete textbook exchanges or sales to earn feedback from your fellow students.'
                : 'Share constructive feedback for peers you have traded books with.'}
            </p>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            {displayedReviews.map((rev) => (
              <div key={rev.id} className="card" style={{ padding: '1.5rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem', flexWrap: 'wrap', gap: '0.5rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.85rem' }}>
                    <img
                      src={rev.reviewerAvatar}
                      alt={rev.reviewerName}
                      style={{ width: '44px', height: '44px', borderRadius: '50%', objectFit: 'cover' }}
                      onError={(e) => {
                        e.target.src = `https://api.dicebear.com/7.x/initials/svg?seed=${encodeURIComponent(rev.reviewerName)}`;
                      }}
                    />
                    <div>
                      <h4 style={{ fontSize: '1rem' }}>
                        {activeTab === 'received' ? rev.reviewerName : `Review for peer seller`}
                      </h4>
                      <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                        {rev.reviewerCollege} • Date: {rev.date}
                      </p>
                    </div>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <RatingStars rating={rev.rating} size={16} />
                    <span style={{ fontSize: '0.9rem', fontWeight: 700, color: 'var(--primary)' }}>
                      {rev.rating}.0
                    </span>
                  </div>
                </div>

                <p style={{ fontSize: '0.95rem', color: 'var(--text-main)', lineHeight: 1.6, marginBottom: '0.75rem' }}>
                  "{rev.comment}"
                </p>

                <div
                  style={{
                    display: 'inline-flex',
                    alignItems: 'center',
                    gap: '0.4rem',
                    padding: '0.3rem 0.75rem',
                    backgroundColor: 'var(--bg-subtle)',
                    borderRadius: 'var(--radius-sm)',
                    fontSize: '0.8rem',
                    color: 'var(--text-muted)'
                  }}
                >
                  <span>Book exchanged:</span>
                  <strong style={{ color: 'var(--text-main)' }}>{rev.bookTitle}</strong>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>

      {/* Write a Review Modal */}
      {showModal && (
        <Modal
          isOpen={showModal}
          onClose={() => setShowModal(false)}
          title="Write a Peer Review"
        >
          <form onSubmit={handleCreateReview}>
            <div className="form-group">
              <label className="form-label form-label-required">Book Title</label>
              <input
                type="text"
                className="form-input"
                value={newBookTitle}
                onChange={(e) => setNewBookTitle(e.target.value)}
                required
              />
            </div>

            <div style={{ textAlign: 'center', margin: '1.5rem 0' }}>
              <label className="form-label" style={{ marginBottom: '0.5rem', display: 'block' }}>
                Star Rating
              </label>
              <RatingStars
                rating={newRating}
                size={32}
                interactive={true}
                onRate={(val) => setNewRating(val)}
              />
              <p style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--primary)', marginTop: '0.35rem' }}>
                {newRating} out of 5 Stars
              </p>
            </div>

            <div className="form-group">
              <label className="form-label form-label-required">Your Feedback</label>
              <textarea
                className="form-textarea"
                rows="4"
                placeholder="Share your experience with this textbook handoff or purchase..."
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                required
              />
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
              <button type="button" onClick={() => setShowModal(false)} className="btn btn-outline">
                Cancel
              </button>
              <button type="submit" className="btn btn-primary">
                Submit Review
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};

