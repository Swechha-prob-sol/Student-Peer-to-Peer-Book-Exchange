import React, { useState, useMemo, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Search, Filter, SlidersHorizontal, BookOpen, X, RotateCcw, ArrowDownUp } from 'lucide-react';
import { useBooks } from '../context/BookContext';
import { BookCard } from '../components/BookCard';
import { SearchBar } from '../components/SearchBar';
import { FilterPanel } from '../components/FilterPanel';
import { Modal } from '../components/Modal';

export const ExploreBooks = () => {
  const { books } = useBooks();
  const [searchParams, setSearchParams] = useSearchParams();

  // Search state initialized from URL if present
  const [searchTerm, setSearchTerm] = useState(searchParams.get('search') || '');

  // Filter state
  const [filters, setFilters] = useState({
    subject: searchParams.get('subject') || 'All Subjects',
    semester: searchParams.get('semester') || 'All Semesters',
    condition: 'All Conditions',
    listingType: 'All Types',
    maxPrice: 1200,
    minPrice: 0
  });

  // Sort state
  const [sortBy, setSortBy] = useState('newest');

  // Pagination / Load More
  const [displayCount, setDisplayCount] = useState(8);
  const [mobileFilterOpen, setMobileFilterOpen] = useState(false);

  // Update filters if searchParams change externally
  useEffect(() => {
    const s = searchParams.get('search');
    const subj = searchParams.get('subject');
    if (s !== null) setSearchTerm(s);
    if (subj !== null) setFilters((f) => ({ ...f, subject: subj }));
  }, [searchParams]);

  const handleFilterChange = (key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
    setDisplayCount(8); // Reset pagination on filter change
  };

  const handleResetFilters = () => {
    setSearchTerm('');
    setFilters({
      subject: 'All Subjects',
      semester: 'All Semesters',
      condition: 'All Conditions',
      listingType: 'All Types',
      maxPrice: 1200,
      minPrice: 0
    });
    setSortBy('newest');
    setSearchParams({});
    setDisplayCount(8);
  };

  // Filter & Search Logic
  const filteredBooks = useMemo(() => {
    return books.filter((book) => {
      // Must be active listing
      if (book.status && book.status !== 'Active') return false;

      // Search term matching title, author, subject, or description
      if (searchTerm.trim()) {
        const q = searchTerm.toLowerCase();
        const matchTitle = book.title?.toLowerCase().includes(q);
        const matchAuthor = book.author?.toLowerCase().includes(q);
        const matchSubject = book.subject?.toLowerCase().includes(q);
        const matchDesc = book.description?.toLowerCase().includes(q);
        if (!matchTitle && !matchAuthor && !matchSubject && !matchDesc) {
          return false;
        }
      }

      // Subject Filter
      if (filters.subject !== 'All Subjects' && book.subject !== filters.subject) {
        return false;
      }

      // Semester Filter
      if (filters.semester !== 'All Semesters' && String(book.semester) !== String(filters.semester)) {
        return false;
      }

      // Condition Filter
      if (filters.condition !== 'All Conditions' && book.condition !== filters.condition) {
        return false;
      }

      // Listing Type Filter
      if (filters.listingType !== 'All Types') {
        if (filters.listingType === 'Both') {
          if (book.listingType !== 'Both') return false;
        } else if (filters.listingType === 'For Sale') {
          if (book.listingType !== 'For Sale' && book.listingType !== 'Both') return false;
        } else if (filters.listingType === 'For Exchange') {
          if (book.listingType !== 'For Exchange' && book.listingType !== 'Both') return false;
        }
      }

      // Price Filter
      if (book.listingType !== 'For Exchange') {
        if (book.price > filters.maxPrice) {
          return false;
        }
      }

      return true;
    }).sort((a, b) => {
      switch (sortBy) {
        case 'newest':
          return new Date(b.dateListed || 0) - new Date(a.dateListed || 0);
        case 'oldest':
          return new Date(a.dateListed || 0) - new Date(b.dateListed || 0);
        case 'price-low':
          return (a.price || 0) - (b.price || 0);
        case 'price-high':
          return (b.price || 0) - (a.price || 0);
        case 'seller-rating':
          return (b.sellerRating || 0) - (a.sellerRating || 0);
        default:
          return 0;
      }
    });
  }, [books, searchTerm, filters, sortBy]);

  const visibleBooks = filteredBooks.slice(0, displayCount);

  // Active filter pills
  const activePills = [];
  if (searchTerm.trim()) {
    activePills.push({ label: `"${searchTerm}"`, onRemove: () => setSearchTerm('') });
  }
  if (filters.subject !== 'All Subjects') {
    activePills.push({ label: filters.subject, onRemove: () => handleFilterChange('subject', 'All Subjects') });
  }
  if (filters.semester !== 'All Semesters') {
    activePills.push({ label: `Sem ${filters.semester}`, onRemove: () => handleFilterChange('semester', 'All Semesters') });
  }
  if (filters.condition !== 'All Conditions') {
    activePills.push({ label: filters.condition, onRemove: () => handleFilterChange('condition', 'All Conditions') });
  }
  if (filters.listingType !== 'All Types') {
    activePills.push({ label: filters.listingType, onRemove: () => handleFilterChange('listingType', 'All Types') });
  }
  if (filters.maxPrice < 1200) {
    activePills.push({ label: `Under ₹${filters.maxPrice}`, onRemove: () => handleFilterChange('maxPrice', 1200) });
  }

  return (
    <div className="explore-page page-wrapper" style={{ padding: '2.5rem 0 5rem 0' }}>
      <div className="container">
        {/* Header */}
        <div style={{ marginBottom: '2rem' }}>
          <h1 style={{ fontSize: '2.2rem', marginBottom: '0.4rem' }}>Explore Academic Marketplace</h1>
          <p style={{ color: 'var(--text-muted)' }}>
            Find course textbooks, engineering lab manuals, and exam guides posted by students near you.
          </p>
        </div>

        {/* Top Search & Filter Bar */}
        <div
          style={{
            display: 'flex',
            gap: '1rem',
            marginBottom: '1.5rem',
            alignItems: 'center',
            flexWrap: 'wrap'
          }}
        >
          <div style={{ flex: '1', minWidth: '280px' }}>
            <SearchBar
              value={searchTerm}
              onChange={setSearchTerm}
              onClear={() => setSearchTerm('')}
              placeholder="Search by book title, author, or subject keyword..."
            />
          </div>

          {/* Sort By Dropdown */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', minWidth: '200px' }}>
            <ArrowDownUp size={16} color="var(--text-muted)" />
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="form-select"
              style={{ height: '48px', padding: '0.5rem 1rem' }}
            >
              <option value="newest">Newest Listed First</option>
              <option value="oldest">Oldest Listed First</option>
              <option value="price-low">Price: Low to High</option>
              <option value="price-high">Price: High to Low</option>
              <option value="seller-rating">Highest Rated Sellers</option>
            </select>
          </div>

          {/* Mobile Filter Toggle Button */}
          <button
            onClick={() => setMobileFilterOpen(true)}
            className="btn btn-outline filter-toggle-mobile-btn"
            style={{ display: 'none', height: '48px', alignItems: 'center', gap: '0.5rem' }}
          >
            <Filter size={18} />
            <span>Filters ({activePills.length})</span>
          </button>
        </div>

        {/* Active Filter Pills Bar */}
        {activePills.length > 0 && (
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              flexWrap: 'wrap',
              marginBottom: '1.5rem'
            }}
          >
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 600 }}>
              Active Filters:
            </span>
            {activePills.map((pill, idx) => (
              <span
                key={idx}
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '0.4rem',
                  padding: '0.25rem 0.65rem',
                  backgroundColor: 'var(--primary-light)',
                  color: 'var(--primary)',
                  borderRadius: 'var(--radius-full)',
                  fontSize: '0.8rem',
                  fontWeight: 600
                }}
              >
                {pill.label}
                <button
                  onClick={pill.onRemove}
                  style={{
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    color: 'var(--primary)',
                    display: 'flex',
                    padding: 0
                  }}
                  title="Remove filter"
                >
                  <X size={13} />
                </button>
              </span>
            ))}

            <button
              onClick={handleResetFilters}
              style={{
                background: 'none',
                border: 'none',
                cursor: 'pointer',
                color: 'var(--accent)',
                fontSize: '0.8rem',
                fontWeight: 600,
                textDecoration: 'underline',
                marginLeft: '0.5rem'
              }}
            >
              Clear All
            </button>
          </div>
        )}

        {/* Main Grid: Left Filter Panel + Right Book Cards Grid */}
        <div
          className="explore-grid-layout"
          style={{
            display: 'grid',
            gridTemplateColumns: '260px 1fr',
            gap: '2rem',
            alignItems: 'start'
          }}
        >
          {/* Desktop Left Sidebar Filter Panel */}
          <div className="filter-panel-desktop" style={{ position: 'sticky', top: '90px' }}>
            <FilterPanel
              filters={filters}
              onFilterChange={handleFilterChange}
              onResetFilters={handleResetFilters}
              totalResults={filteredBooks.length}
            />
          </div>

          {/* Right Results Column */}
          <div>
            {/* Results Count Bar */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                marginBottom: '1.25rem'
              }}
            >
              <span style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>
                Showing <strong>{visibleBooks.length}</strong> of <strong>{filteredBooks.length}</strong> books
              </span>
            </div>

            {/* Book Cards Grid */}
            {filteredBooks.length === 0 ? (
              <div className="empty-state">
                <div className="empty-icon-wrap">
                  <BookOpen size={30} />
                </div>
                <h3 className="empty-title">No books match your criteria</h3>
                <p className="empty-desc">
                  Try adjusting your search terms, changing the subject, or broadening your price filter range.
                </p>
                <button onClick={handleResetFilters} className="btn btn-primary btn-sm">
                  <RotateCcw size={14} />
                  Reset All Filters
                </button>
              </div>
            ) : (
              <>
                <div
                  style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
                    gap: '1.5rem'
                  }}
                >
                  {visibleBooks.map((book) => (
                    <BookCard key={book.id} book={book} />
                  ))}
                </div>

                {/* Load More Button */}
                {displayCount < filteredBooks.length && (
                  <div style={{ textAlign: 'center', marginTop: '3rem' }}>
                    <button
                      onClick={() => setDisplayCount((prev) => prev + 8)}
                      className="btn btn-outline btn-lg"
                      style={{ minWidth: '220px' }}
                    >
                      Load More Books ({filteredBooks.length - displayCount} remaining)
                    </button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>

      {/* Mobile Filters Modal */}
      {mobileFilterOpen && (
        <Modal
          isOpen={mobileFilterOpen}
          onClose={() => setMobileFilterOpen(false)}
          title="Filter Books"
        >
          <FilterPanel
            filters={filters}
            onFilterChange={handleFilterChange}
            onResetFilters={handleResetFilters}
            totalResults={filteredBooks.length}
            isMobileModal={true}
            onCloseMobile={() => setMobileFilterOpen(false)}
          />
        </Modal>
      )}
    </div>
  );
};

