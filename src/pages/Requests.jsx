import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  ArrowLeftRight,
  Inbox,
  Send,
  Check,
  X,
  MessageSquare,
  Repeat,
  ShoppingBag,
  ExternalLink,
  Ban,
  Clock,
  Sparkles
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useBooks } from '../context/BookContext';
import { Sidebar } from '../components/Sidebar';
import { StatusBadge } from '../components/StatusBadge';
import { Modal } from '../components/Modal';

export const Requests = () => {
  const { currentUser } = useAuth();
  const { requests, updateRequestStatus, cancelRequest, startConversationWithUser } = useBooks();
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState('received'); // 'received' or 'sent'
  const [selectedRequestDetails, setSelectedRequestDetails] = useState(null);

  if (!currentUser) return null;

  // Filter requests
  const receivedRequests = requests.filter((r) => r.sellerId === currentUser.id);
  const sentRequests = requests.filter((r) => r.requesterId === currentUser.id);

  const displayedRequests = activeTab === 'received' ? receivedRequests : sentRequests;

  const handleAccept = (reqId) => {
    updateRequestStatus(reqId, 'Accepted', 2);
  };

  const handleReject = (reqId) => {
    updateRequestStatus(reqId, 'Rejected', 1);
  };

  const handleCancel = (reqId) => {
    cancelRequest(reqId);
  };

  const handleChat = (req) => {
    const otherUser = activeTab === 'received'
      ? { id: req.requesterId, name: req.requesterName, college: req.requesterCollege, avatar: req.requesterAvatar }
      : { id: req.sellerId, name: req.sellerName, college: 'Campus Peer' };

    const convId = startConversationWithUser(otherUser);
    if (convId) {
      navigate('/messages');
    }
  };

  return (
    <div className="dashboard-layout">
      <Sidebar />

      <main className="dashboard-content">
        <div className="dashboard-header">
          <div>
            <h1 style={{ fontSize: '1.85rem', marginBottom: '0.2rem' }}>Request Management</h1>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Review incoming purchase/swap proposals and monitor requests you've sent to other students.
            </p>
          </div>
        </div>

        {/* Tab Switcher */}
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
            <span>Received Requests</span>
            <span
              style={{
                fontSize: '0.75rem',
                padding: '0.15rem 0.5rem',
                borderRadius: 'var(--radius-full)',
                backgroundColor: activeTab === 'received' ? 'var(--primary-light)' : 'var(--bg-subtle)',
                color: activeTab === 'received' ? 'var(--primary)' : 'var(--text-muted)'
              }}
            >
              {receivedRequests.length}
            </span>
          </button>

          <button
            onClick={() => setActiveTab('sent')}
            style={{
              background: 'none',
              border: 'none',
              padding: '0.85rem 1.25rem',
              fontSize: '1rem',
              fontWeight: activeTab === 'sent' ? 700 : 500,
              color: activeTab === 'sent' ? 'var(--primary)' : 'var(--text-muted)',
              borderBottom: activeTab === 'sent' ? '3px solid var(--primary)' : '3px solid transparent',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem'
            }}
          >
            <Send size={18} />
            <span>Sent Requests</span>
            <span
              style={{
                fontSize: '0.75rem',
                padding: '0.15rem 0.5rem',
                borderRadius: 'var(--radius-full)',
                backgroundColor: activeTab === 'sent' ? 'var(--primary-light)' : 'var(--bg-subtle)',
                color: activeTab === 'sent' ? 'var(--primary)' : 'var(--text-muted)'
              }}
            >
              {sentRequests.length}
            </span>
          </button>
        </div>

        {/* Requests List */}
        {displayedRequests.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon-wrap">
              <ArrowLeftRight size={30} />
            </div>
            <h3 className="empty-title">
              {activeTab === 'received' ? 'No received requests yet' : 'You have not sent any requests yet'}
            </h3>
            <p className="empty-desc">
              {activeTab === 'received'
                ? 'When campus students want to purchase or exchange your listed textbooks, proposals will appear here.'
                : 'Browse the textbook marketplace and send a purchase or swap request!'}
            </p>
            {activeTab === 'sent' && (
              <Link to="/explore" className="btn btn-primary btn-sm">
                Explore Marketplace
              </Link>
            )}
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {displayedRequests.map((req) => (
              <div
                key={req.id}
                className="card"
                style={{
                  padding: '1.5rem',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  gap: '1.5rem',
                  flexWrap: 'wrap'
                }}
              >
                {/* Book & Peer info */}
                <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem', minWidth: '300px', flex: 1 }}>
                  <img
                    src={req.bookCover}
                    alt={req.bookTitle}
                    style={{ width: '65px', height: '85px', objectFit: 'cover', borderRadius: 'var(--radius-md)' }}
                  />
                  <div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.35rem' }}>
                      <StatusBadge status={req.status} />
                      <span className={`badge ${req.requestType === 'Exchange' ? 'badge-exchange' : 'badge-sale'}`}>
                        {req.requestType === 'Exchange' ? <Repeat size={12} /> : <ShoppingBag size={12} />}
                        {req.requestType} Request
                      </span>
                    </div>

                    <h4 style={{ fontSize: '1.1rem', marginBottom: '0.2rem' }}>
                      <Link to={`/books/${req.bookId}`} style={{ color: 'var(--text-main)', textDecoration: 'none' }}>
                        {req.bookTitle}
                      </Link>
                    </h4>

                    <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                      {activeTab === 'received' ? (
                        <>From: <strong>{req.requesterName}</strong> ({req.requesterCollege})</>
                      ) : (
                        <>To Seller: <strong>{req.sellerName}</strong></>
                      )}
                      {' • '}Date: {req.date}
                    </p>

                    {req.requestType === 'Exchange' && req.offeredBookTitle && (
                      <div
                        style={{
                          marginTop: '0.4rem',
                          padding: '0.25rem 0.6rem',
                          backgroundColor: 'var(--bg-subtle)',
                          borderRadius: 'var(--radius-sm)',
                          fontSize: '0.8rem',
                          color: 'var(--secondary)'
                        }}
                      >
                        <strong>Offered in swap:</strong> {req.offeredBookTitle}
                      </div>
                    )}
                  </div>
                </div>

                {/* Status / Tracking preview */}
                <div style={{ textAlign: 'right', minWidth: '120px' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Timeline Stage</span>
                  <div style={{ fontSize: '0.9rem', fontWeight: 700, color: 'var(--primary)' }}>
                    Step {req.trackingStep || 1} of 5
                  </div>
                  <Link
                    to="/exchange-tracking"
                    style={{ fontSize: '0.75rem', color: 'var(--accent)', textDecoration: 'underline' }}
                  >
                    View Timeline
                  </Link>
                </div>

                {/* Action Buttons */}
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flexWrap: 'wrap' }}>
                  <button
                    onClick={() => setSelectedRequestDetails(req)}
                    className="btn btn-outline btn-sm"
                  >
                    Details
                  </button>

                  <button
                    onClick={() => handleChat(req)}
                    className="btn btn-ghost btn-sm"
                    title="Message student"
                  >
                    <MessageSquare size={16} /> Chat
                  </button>

                  {/* Actions for Received Requests */}
                  {activeTab === 'received' && req.status === 'Pending' && (
                    <>
                      <button
                        onClick={() => handleAccept(req.id)}
                        className="btn btn-primary btn-sm"
                        style={{ backgroundColor: 'var(--success)' }}
                      >
                        <Check size={16} /> Accept
                      </button>
                      <button
                        onClick={() => handleReject(req.id)}
                        className="btn btn-danger btn-sm"
                      >
                        <X size={16} /> Reject
                      </button>
                    </>
                  )}

                  {/* Actions for Sent Requests */}
                  {activeTab === 'sent' && req.status === 'Pending' && (
                    <button
                      onClick={() => handleCancel(req.id)}
                      className="btn btn-danger btn-sm"
                    >
                      <Ban size={15} /> Cancel Request
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </main>

      {/* Request Details Modal */}
      {selectedRequestDetails && (
        <Modal
          isOpen={!!selectedRequestDetails}
          onClose={() => setSelectedRequestDetails(null)}
          title="Proposal Details"
        >
          <div>
            <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.25rem' }}>
              <img
                src={selectedRequestDetails.bookCover}
                alt={selectedRequestDetails.bookTitle}
                style={{ width: '70px', height: '95px', objectFit: 'cover', borderRadius: 'var(--radius-sm)' }}
              />
              <div>
                <StatusBadge status={selectedRequestDetails.status} />
                <h4 style={{ fontSize: '1.1rem', margin: '0.3rem 0' }}>{selectedRequestDetails.bookTitle}</h4>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Author: {selectedRequestDetails.bookAuthor}</p>
                <p style={{ fontSize: '0.9rem', fontWeight: 700, color: 'var(--primary)', marginTop: '0.2rem' }}>
                  Value: ₹{selectedRequestDetails.bookPrice} ({selectedRequestDetails.requestType})
                </p>
              </div>
            </div>

            <div style={{ backgroundColor: 'var(--bg-subtle)', padding: '1rem', borderRadius: 'var(--radius-md)', marginBottom: '1.25rem', fontSize: '0.9rem' }}>
              <p style={{ fontWeight: 600, color: 'var(--text-main)', marginBottom: '0.25rem' }}>Student Message & Terms:</p>
              <p style={{ color: 'var(--text-muted)', fontStyle: 'italic' }}>
                "{selectedRequestDetails.notes || 'No custom note attached.'}"
              </p>
            </div>

            {selectedRequestDetails.offeredBookTitle && (
              <div style={{ backgroundColor: 'var(--secondary-light)', padding: '0.85rem 1rem', borderRadius: 'var(--radius-md)', marginBottom: '1.25rem', fontSize: '0.85rem', color: 'var(--secondary-hover)' }}>
                <strong>Offered Swap Item:</strong> {selectedRequestDetails.offeredBookTitle}
              </div>
            )}

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
              <button onClick={() => setSelectedRequestDetails(null)} className="btn btn-outline">
                Close
              </button>
              <button
                onClick={() => {
                  handleChat(selectedRequestDetails);
                  setSelectedRequestDetails(null);
                }}
                className="btn btn-primary"
              >
                <MessageSquare size={16} /> Open Chat
              </button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};

