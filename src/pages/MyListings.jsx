import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  BookOpen,
  PlusCircle,
  Eye,
  Edit,
  Trash2,
  CheckCheck,
  Repeat,
  AlertTriangle,
  RotateCcw,
  Sparkles
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useBooks } from '../context/BookContext';
import { Sidebar } from '../components/Sidebar';
import { StatusBadge } from '../components/StatusBadge';
import { Modal } from '../components/Modal';

export const MyListings = () => {
  const { currentUser } = useAuth();
  const { books, deleteBook, updateBookStatus } = useBooks();
  const navigate = useNavigate();

  // Tab filter: 'All', 'Active', 'Pending', 'Sold', 'Exchanged', 'Draft'
  const [statusFilter, setStatusFilter] = useState('All');

  // Confirmation Modals State
  const [deleteModal, setDeleteModal] = useState({ isOpen: false, book: null });
  const [statusModal, setStatusModal] = useState({ isOpen: false, book: null, newStatus: '' });

  if (!currentUser) return null;

  // Filter listings belonging to logged-in student
  const myListings = books.filter((b) => b.sellerId === currentUser.id);

  const filteredListings = myListings.filter((book) => {
    if (statusFilter === 'All') return true;
    return book.status === statusFilter;
  });

  const handleDeleteConfirm = () => {
    if (deleteModal.book) {
      deleteBook(deleteModal.book.id);
      setDeleteModal({ isOpen: false, book: null });
    }
  };

  const handleStatusChangeConfirm = () => {
    if (statusModal.book && statusModal.newStatus) {
      updateBookStatus(statusModal.book.id, statusModal.newStatus);
      setStatusModal({ isOpen: false, book: null, newStatus: '' });
    }
  };

  return (
    <div className="dashboard-layout">
      <Sidebar />

      <main className="dashboard-content">
        <div className="dashboard-header">
          <div>
            <h1 style={{ fontSize: '1.85rem', marginBottom: '0.2rem' }}>My Book Listings</h1>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Manage your academic textbooks, update availability, or mark items as sold/exchanged.
            </p>
          </div>

          <Link to="/add-book" className="btn btn-secondary">
            <PlusCircle size={16} />
            <span>List a New Textbook</span>
          </Link>
        </div>

        {/* Filter Tabs */}
        <div
          style={{
            display: 'flex',
            gap: '0.5rem',
            borderBottom: '1px solid var(--border-light)',
            marginBottom: '1.5rem',
            overflowX: 'auto',
            paddingBottom: '0.25rem'
          }}
        >
          {['All', 'Active', 'Pending', 'Sold', 'Exchanged', 'Draft'].map((tab) => {
            const count = tab === 'All' ? myListings.length : myListings.filter((b) => b.status === tab).length;
            const isActive = statusFilter === tab;
            return (
              <button
                key={tab}
                onClick={() => setStatusFilter(tab)}
                style={{
                  background: 'none',
                  border: 'none',
                  padding: '0.65rem 1rem',
                  fontSize: '0.9rem',
                  fontWeight: isActive ? 700 : 500,
                  color: isActive ? 'var(--primary)' : 'var(--text-muted)',
                  borderBottom: isActive ? '2px solid var(--primary)' : '2px solid transparent',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.4rem',
                  whiteSpace: 'nowrap'
                }}
              >
                <span>{tab}</span>
                <span
                  style={{
                    fontSize: '0.75rem',
                    padding: '0.1rem 0.45rem',
                    borderRadius: 'var(--radius-full)',
                    backgroundColor: isActive ? 'var(--primary-light)' : 'var(--bg-subtle)',
                    color: isActive ? 'var(--primary)' : 'var(--text-muted)'
                  }}
                >
                  {count}
                </span>
              </button>
            );
          })}
        </div>

        {/* Listings Table / Cards */}
        {filteredListings.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon-wrap">
              <BookOpen size={30} />
            </div>
            <h3 className="empty-title">No listings found in this tab</h3>
            <p className="empty-desc">
              {statusFilter === 'All'
                ? 'You have not listed any textbooks yet. Clear your shelf today!'
                : `No books currently marked as "${statusFilter}".`}
            </p>
            <Link to="/add-book" className="btn btn-primary btn-sm">
              <PlusCircle size={15} /> List a Textbook Now
            </Link>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {filteredListings.map((book) => (
              <div
                key={book.id}
                className="card"
                style={{
                  padding: '1.25rem 1.5rem',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  gap: '1.5rem',
                  flexWrap: 'wrap'
                }}
              >
                {/* Left: Cover & Details */}
                <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem', minWidth: '280px', flex: 1 }}>
                  <img
                    src={book.coverImage}
                    alt={book.title}
                    style={{
                      width: '60px',
                      height: '80px',
                      objectFit: 'cover',
                      borderRadius: 'var(--radius-md)',
                      boxShadow: 'var(--shadow-xs)'
                    }}
                  />
                  <div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                      <StatusBadge status={book.status} />
                      <StatusBadge status={book.listingType} type="listingType" />
                    </div>

                    <h4 style={{ fontSize: '1.05rem', marginBottom: '0.2rem' }}>
                      <Link to={`/books/${book.id}`} style={{ color: 'var(--text-main)', textDecoration: 'none' }}>
                        {book.title}
                      </Link>
                    </h4>

                    <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                      by {book.author} • {book.edition} ({book.condition})
                    </p>

                    <div style={{ display: 'flex', gap: '1rem', marginTop: '0.35rem', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                      <span>Subject: <strong>{book.subject}</strong></span>
                      <span>Listed: <strong>{book.dateListed}</strong></span>
                    </div>
                  </div>
                </div>

                {/* Middle: Price */}
                <div style={{ textAlign: 'right', minWidth: '100px' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>
                    Value
                  </span>
                  <div style={{ fontSize: '1.35rem', fontWeight: 800, color: 'var(--primary)' }}>
                    {book.listingType === 'For Exchange' ? 'Swap Only' : `₹${book.price}`}
                  </div>
                </div>

                {/* Right: Actions */}
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flexWrap: 'wrap' }}>
                  <Link
                    to={`/books/${book.id}`}
                    className="btn btn-outline btn-sm"
                    title="View public page"
                  >
                    <Eye size={15} /> View
                  </Link>

                  <button
                    onClick={() => navigate(`/add-book?edit=${book.id}`)}
                    className="btn btn-outline btn-sm"
                    title="Edit listing details"
                  >
                    <Edit size={15} /> Edit
                  </button>

                  {/* Status Change Buttons */}
                  {book.status === 'Active' && (
                    <>
                      <button
                        onClick={() => setStatusModal({ isOpen: true, book, newStatus: 'Sold' })}
                        className="btn btn-ghost btn-sm"
                        style={{ color: 'var(--secondary)' }}
                        title="Mark as Sold"
                      >
                        <CheckCheck size={15} /> Mark Sold
                      </button>
                      <button
                        onClick={() => setStatusModal({ isOpen: true, book, newStatus: 'Exchanged' })}
                        className="btn btn-ghost btn-sm"
                        style={{ color: 'var(--accent)' }}
                        title="Mark as Exchanged"
                      >
                        <Repeat size={15} /> Mark Exchanged
                      </button>
                    </>
                  )}

                  {(book.status === 'Sold' || book.status === 'Exchanged' || book.status === 'Draft') && (
                    <button
                      onClick={() => updateBookStatus(book.id, 'Active')}
                      className="btn btn-ghost btn-sm"
                      style={{ color: 'var(--success)' }}
                      title="Reactivate listing"
                    >
                      <RotateCcw size={15} /> Reactivate
                    </button>
                  )}

                  <button
                    onClick={() => setDeleteModal({ isOpen: true, book })}
                    className="btn btn-ghost btn-sm"
                    style={{ color: 'var(--error)' }}
                    title="Delete listing"
                  >
                    <Trash2 size={15} />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>

      {/* Delete Confirmation Modal */}
      {deleteModal.isOpen && (
        <Modal
          isOpen={deleteModal.isOpen}
          onClose={() => setDeleteModal({ isOpen: false, book: null })}
          title="Delete Book Listing"
        >
          <div style={{ textAlign: 'center', padding: '1rem 0' }}>
            <div
              style={{
                width: '48px',
                height: '48px',
                borderRadius: '50%',
                backgroundColor: 'var(--error-bg)',
                color: 'var(--error)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 1rem auto'
              }}
            >
              <AlertTriangle size={24} />
            </div>
            <h3 style={{ fontSize: '1.2rem', marginBottom: '0.5rem' }}>Are you sure?</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Are you sure you want to delete "<strong>{deleteModal.book?.title}</strong>"? This action cannot be undone.
            </p>
          </div>
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button
              onClick={() => setDeleteModal({ isOpen: false, book: null })}
              className="btn btn-outline"
            >
              Cancel
            </button>
            <button
              onClick={handleDeleteConfirm}
              className="btn btn-danger"
            >
              Yes, Delete Listing
            </button>
          </div>
        </Modal>
      )}

      {/* Status Change Confirmation Modal */}
      {statusModal.isOpen && (
        <Modal
          isOpen={statusModal.isOpen}
          onClose={() => setStatusModal({ isOpen: false, book: null, newStatus: '' })}
          title={`Mark Book as ${statusModal.newStatus}`}
        >
          <div style={{ padding: '0.5rem 0' }}>
            <p style={{ fontSize: '0.95rem', color: 'var(--text-main)', marginBottom: '1rem' }}>
              Do you want to update the status of "<strong>{statusModal.book?.title}</strong>" to{' '}
              <span style={{ fontWeight: 700, color: 'var(--primary)' }}>{statusModal.newStatus}</span>?
            </p>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              This will update public marketplace visibility so other students know the transaction has been completed.
            </p>
          </div>
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button
              onClick={() => setStatusModal({ isOpen: false, book: null, newStatus: '' })}
              className="btn btn-outline"
            >
              Cancel
            </button>
            <button
              onClick={handleStatusChangeConfirm}
              className="btn btn-primary"
            >
              Confirm Update
            </button>
          </div>
        </Modal>
      )}
    </div>
  );
};

