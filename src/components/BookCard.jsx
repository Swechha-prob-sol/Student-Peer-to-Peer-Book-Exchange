import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Heart, User, BookOpen } from 'lucide-react';
import { useBooks } from '../context/BookContext';
import { RatingStars } from './RatingStars';
import { StatusBadge } from './StatusBadge';
import { BookCover } from './BookCoverFallback';

export const BookCard = ({ book }) => {
  const { isInWishlist, toggleWishlist } = useBooks();
  const navigate = useNavigate();

  if (!book) return null;

  const inWishlist = isInWishlist(book.id);

  const handleWishlistClick = (e) => {
    e.preventDefault();
    e.stopPropagation();
    toggleWishlist(book.id);
  };

  return (
    <div className="book-card">
      <div className="book-card-image-wrap">
        <BookCover
          src={book.coverImage}
          alt={book.title}
          className="book-card-img"
          subject={book.subject}
          height="175px"
        />

        <div className="book-card-badge">
          <StatusBadge status={book.listingType} type="listingType" />
        </div>

        <button
          className={`book-wishlist-btn ${inWishlist ? 'active' : ''}`}
          onClick={handleWishlistClick}
          aria-label={inWishlist ? 'Remove from wishlist' : 'Add to wishlist'}
          title={inWishlist ? 'Remove from wishlist' : 'Save to wishlist'}
        >
          <Heart size={14} fill={inWishlist ? '#EF4444' : 'none'} color={inWishlist ? '#EF4444' : '#64748B'} />
        </button>
      </div>

      <div className="book-card-body">
        <div className="book-card-meta">
          <span>{book.subject}</span>
          <span>•</span>
          <span>Sem {book.semester}</span>
        </div>

        <Link to={`/books/${book.id}`} style={{ textDecoration: 'none' }}>
          <h4 className="book-card-title" title={book.title}>
            {book.title}
          </h4>
        </Link>

        <p className="book-card-author" title={book.author}>
          {book.author}
        </p>

        <div className="book-card-details-row">
          <span>{book.edition}</span>
          <span>•</span>
          <span style={{ fontWeight: 600, color: 'var(--text-main)' }}>{book.condition}</span>
        </div>

        <div className="book-card-seller">
          <Link
            to={`/seller/${book.sellerId}`}
            className="seller-info"
            style={{ textDecoration: 'none' }}
          >
            <User size={12} color="var(--text-muted)" />
            <span style={{ fontSize: '0.78rem' }}>{book.sellerName}</span>
          </Link>
          <span style={{ fontSize: '0.72rem', color: 'var(--text-muted)', maxWidth: '90px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }} title={book.sellerCollege}>
            {book.sellerCollege?.split(' ')[0]}
          </span>
        </div>

        <div className="book-card-footer">
          <div className="book-price-block">
            <span className="book-price-label">Price</span>
            {book.listingType === 'For Exchange' ? (
              <span className="book-price-free">Swap only</span>
            ) : (
              <span className="book-price-value">₹{book.price}</span>
            )}
          </div>

          <button
            onClick={() => navigate(`/books/${book.id}`)}
            className="btn btn-outline btn-sm"
          >
            View
          </button>
        </div>
      </div>
    </div>
  );
};
