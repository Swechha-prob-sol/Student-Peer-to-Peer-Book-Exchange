import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  GitPullRequest,
  CheckCircle2,
  Clock,
  MessageSquare,
  Star,
  ChevronRight,
  ShieldCheck,
  User,
  AlertCircle,
  Sparkles
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useBooks } from '../context/BookContext';
import { Sidebar } from '../components/Sidebar';
import { StatusBadge } from '../components/StatusBadge';
import { Modal } from '../components/Modal';
import { RatingStars } from '../components/RatingStars';

export const ExchangeTracking = () => {
  const { currentUser } = useAuth();
  const { requests, advanceTrackingStep, addReview, startConversationWithUser } = useBooks();
  const navigate = useNavigate();

  // Review Modal State
  const [reviewModal, setReviewModal] = useState({ isOpen: false, request: null });
  const [reviewRating, setReviewRating] = useState(5);
  const [reviewComment, setReviewComment] = useState('');

  if (!currentUser) return null;

  // Filter requests involving current user (accepted or completed or pending)
  const activeTransactions = requests.filter(
    (r) =>
      (r.sellerId === currentUser.id || r.requesterId === currentUser.id) &&
      r.status !== 'Rejected' &&
      r.status !== 'Cancelled'
  );

  const timelineSteps = [
    { step: 1, title: 'Request Sent', desc: 'Proposal submitted' },
    { step: 2, title: 'Request Accepted', desc: 'Peer agreed to terms' },
    { step: 3, title: 'Contact Established', desc: 'Chatting / Handover planned' },
    { step: 4, title: 'Exchange Completed', desc: 'Physical book handed over' },
    { step: 5, title: 'Review Submitted', desc: 'Peer score recorded' }
  ];

  const handleAdvance = (req, targetStep) => {
    advanceTrackingStep(req.id, targetStep);
    if (targetStep === 5) {
      // Prompt for review
      setReviewModal({ isOpen: true, request: req });
    }
  };

  const handleReviewSubmit = (e) => {
    e.preventDefault();
    if (!reviewModal.request) return;

    const req = reviewModal.request;
    const targetSellerId = req.sellerId === currentUser.id ? req.requesterId : req.sellerId;

    addReview({
      sellerId: targetSellerId,
      bookTitle: req.bookTitle,
      rating: reviewRating,
      comment: reviewComment || 'Great exchange experience, polite student and smooth handoff!'
    });

    setReviewModal({ isOpen: false, request: null });
    setReviewComment('');
    setReviewRating(5);
  };

  const handleChat = (req) => {
    const isSeller = req.sellerId === currentUser.id;
    const otherUser = isSeller
      ? { id: req.requesterId, name: req.requesterName, college: req.requesterCollege, avatar: req.requesterAvatar }
      : { id: req.sellerId, name: req.sellerName, college: 'Campus Peer' };

    const convId = startConversationWithUser(otherUser);
    if (convId) navigate('/messages');
  };

  return (
    <div className="dashboard-layout">
      <Sidebar />

      <main className="dashboard-content">
        <div className="dashboard-header">
          <div>
            <h1 style={{ fontSize: '1.85rem', marginBottom: '0.2rem' }}>Exchange & Order Tracking</h1>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Follow the 5-stage progress of your book swaps and purchases from request to verified completion.
            </p>
          </div>
        </div>

        {activeTransactions.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon-wrap">
              <GitPullRequest size={30} />
            </div>
            <h3 className="empty-title">No Active Exchanges in Progress</h3>
            <p className="empty-desc">
              When a request is accepted by you or a peer seller, you can monitor the meetup, handoff, and review timeline here.
            </p>
            <Link to="/explore" className="btn btn-primary btn-sm">
              Find Books to Exchange
            </Link>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
            {activeTransactions.map((tx) => {
              const currentStep = tx.trackingStep || 1;
              const isSeller = tx.sellerId === currentUser.id;
              const otherStudentName = isSeller ? tx.requesterName : tx.sellerName;
              const otherStudentCollege = isSeller ? tx.requesterCollege : 'Verified Student';

              return (
                <div key={tx.id} className="card" style={{ padding: '2rem' }}>
                  {/* Top Bar: Book Info & Status */}
                  <div
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      borderBottom: '1px solid var(--border-light)',
                      paddingBottom: '1.25rem',
                      marginBottom: '1.5rem',
                      flexWrap: 'wrap',
                      gap: '1rem'
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                      <img
                        src={tx.bookCover}
                        alt={tx.bookTitle}
                        style={{ width: '55px', height: '72px', objectFit: 'cover', borderRadius: 'var(--radius-md)' }}
                      />
                      <div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.2rem' }}>
                          <StatusBadge status={tx.status} />
                          <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                            ID: #{tx.id}
                          </span>
                        </div>
                        <h3 style={{ fontSize: '1.15rem' }}>{tx.bookTitle}</h3>
                        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                          Trading with: <strong>{otherStudentName}</strong> ({otherStudentCollege})
                        </p>
                      </div>
                    </div>

                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <button onClick={() => handleChat(tx)} className="btn btn-outline btn-sm">
                        <MessageSquare size={16} /> Contact Peer
                      </button>
                    </div>
                  </div>

                  {/* Visual 5-Stage Timeline */}
                  <div className="timeline">
                    {/* Background Progress Fill */}
                    <div
                      className="timeline-progress-bar"
                      style={{
                        width: `${((currentStep - 1) / (timelineSteps.length - 1)) * 90}%`
                      }}
                    />

                    {timelineSteps.map((s) => {
                      const isCompleted = currentStep > s.step;
                      const isActive = currentStep === s.step;

                      return (
                        <div
                          key={s.step}
                          className={`timeline-step ${isCompleted ? 'completed' : ''} ${isActive ? 'active' : ''}`}
                        >
                          <div className="timeline-icon">
                            {isCompleted ? <CheckCircle2 size={18} /> : s.step}
                          </div>
                          <div>
                            <span className="timeline-title">{s.title}</span>
                            <p style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginTop: '0.15rem' }}>
                              {s.desc}
                            </p>
                          </div>
                        </div>
                      );
                    })}
                  </div>

                  {/* Next Step Action Helper Box */}
                  <div
                    style={{
                      marginTop: '1.5rem',
                      padding: '1rem 1.25rem',
                      backgroundColor: 'var(--bg-subtle)',
                      borderRadius: 'var(--radius-md)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      flexWrap: 'wrap',
                      gap: '1rem'
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                      <ShieldCheck size={20} color="var(--secondary)" />
                      <div>
                        <span style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-main)' }}>
                          Current Progress: Step {currentStep} – {timelineSteps[currentStep - 1]?.title}
                        </span>
                        <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                          {currentStep < 4
                            ? 'Connect in chat to settle the handoff venue, then mark exchange as completed once you receive your book.'
                            : currentStep === 4
                            ? 'Book handoff complete! Please submit a peer review to finalize the exchange.'
                            : 'All transaction steps finished! Verified on BookLoop.'}
                        </p>
                      </div>
                    </div>

                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      {currentStep < 4 && (
                        <button
                          onClick={() => handleAdvance(tx, currentStep + 1)}
                          className="btn btn-secondary btn-sm"
                        >
                          Advance to Next Step ({timelineSteps[currentStep]?.title})
                        </button>
                      )}

                      {currentStep === 4 && (
                        <button
                          onClick={() => setReviewModal({ isOpen: true, request: tx })}
                          className="btn btn-primary btn-sm"
                        >
                          <Star size={15} /> Submit Peer Review
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </main>

      {/* Peer Review Submission Modal */}
      {reviewModal.isOpen && (
        <Modal
          isOpen={reviewModal.isOpen}
          onClose={() => setReviewModal({ isOpen: false, request: null })}
          title="Rate & Review Student Peer"
        >
          <form onSubmit={handleReviewSubmit}>
            <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
              <p style={{ fontSize: '0.95rem', color: 'var(--text-muted)', marginBottom: '0.75rem' }}>
                How was your exchange experience with{' '}
                <strong>
                  {reviewModal.request?.sellerId === currentUser.id
                    ? reviewModal.request?.requesterName
                    : reviewModal.request?.sellerName}
                </strong>
                ?
              </p>

              <div style={{ display: 'inline-flex', justifyContent: 'center' }}>
                <RatingStars
                  rating={reviewRating}
                  size={32}
                  interactive={true}
                  onRate={(val) => setReviewRating(val)}
                />
              </div>
              <p style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--primary)', marginTop: '0.4rem' }}>
                {reviewRating} out of 5 Stars
              </p>
            </div>

            <div className="form-group">
              <label className="form-label form-label-required">Written Feedback</label>
              <textarea
                className="form-textarea"
                rows="4"
                placeholder="Was the textbook in described condition? How punctual was the meetup?"
                value={reviewComment}
                onChange={(e) => setReviewComment(e.target.value)}
                required
              />
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
              <button
                type="button"
                onClick={() => setReviewModal({ isOpen: false, request: null })}
                className="btn btn-outline"
              >
                Cancel
              </button>
              <button type="submit" className="btn btn-primary">
                Post Review & Complete
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};

