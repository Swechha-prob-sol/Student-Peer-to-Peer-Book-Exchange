import React from 'react';
import { RotateCcw, Filter, Check, SlidersHorizontal } from 'lucide-react';
import {
  SUBJECT_OPTIONS,
  SEMESTER_OPTIONS,
  CONDITION_OPTIONS,
  LISTING_TYPE_OPTIONS
} from '../data/mockBooks';

export const FilterPanel = ({
  filters,
  onFilterChange,
  onResetFilters,
  totalResults = 0,
  isMobileModal = false,
  onCloseMobile
}) => {
  const hasActiveFilters =
    filters.subject !== 'All Subjects' ||
    filters.semester !== 'All Semesters' ||
    filters.condition !== 'All Conditions' ||
    filters.listingType !== 'All Types' ||
    filters.maxPrice < 1000 ||
    filters.minPrice > 0;

  return (
    <div
      style={{
        backgroundColor: '#ffffff',
        borderRadius: 'var(--radius-lg)',
        border: '1px solid var(--border-light)',
        padding: '1.5rem',
        boxShadow: 'var(--shadow-xs)'
      }}
    >
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: '1.25rem',
          paddingBottom: '0.75rem',
          borderBottom: '1px solid var(--border-light)'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <SlidersHorizontal size={18} color="var(--primary)" />
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Filters</h3>
        </div>

        {hasActiveFilters && (
          <button
            onClick={onResetFilters}
            className="btn btn-ghost btn-sm"
            style={{ color: 'var(--accent)', fontSize: '0.8rem', padding: '0.2rem 0.5rem' }}
          >
            <RotateCcw size={13} />
            Reset All
          </button>
        )}
      </div>

      {/* Subject Filter */}
      <div className="form-group">
        <label className="form-label">Subject</label>
        <select
          value={filters.subject}
          onChange={(e) => onFilterChange('subject', e.target.value)}
          className="form-select"
        >
          {SUBJECT_OPTIONS.map((subj) => (
            <option key={subj} value={subj}>
              {subj}
            </option>
          ))}
        </select>
      </div>

      {/* Semester Filter */}
      <div className="form-group">
        <label className="form-label">Semester</label>
        <select
          value={filters.semester}
          onChange={(e) => onFilterChange('semester', e.target.value)}
          className="form-select"
        >
          {SEMESTER_OPTIONS.map((sem) => (
            <option key={sem} value={sem}>
              {sem === 'All Semesters' ? sem : `Semester ${sem}`}
            </option>
          ))}
        </select>
      </div>

      {/* Listing Type Filter */}
      <div className="form-group">
        <label className="form-label">Listing Type</label>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
          {LISTING_TYPE_OPTIONS.map((type) => (
            <label
              key={type}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.6rem',
                fontSize: '0.875rem',
                cursor: 'pointer',
                color: filters.listingType === type ? 'var(--primary)' : 'var(--text-main)',
                fontWeight: filters.listingType === type ? 600 : 400
              }}
            >
              <input
                type="radio"
                name="listingType"
                value={type}
                checked={filters.listingType === type}
                onChange={() => onFilterChange('listingType', type)}
              />
              {type}
            </label>
          ))}
        </div>
      </div>

      {/* Condition Filter */}
      <div className="form-group">
        <label className="form-label">Book Condition</label>
        <select
          value={filters.condition}
          onChange={(e) => onFilterChange('condition', e.target.value)}
          className="form-select"
        >
          {CONDITION_OPTIONS.map((cond) => (
            <option key={cond} value={cond}>
              {cond}
            </option>
          ))}
        </select>
      </div>

      {/* Price Range Filter */}
      <div className="form-group" style={{ marginBottom: 0 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <label className="form-label">Max Price</label>
          <span style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--primary)' }}>
            ₹{filters.maxPrice}
          </span>
        </div>
        <input
          type="range"
          min="100"
          max="1200"
          step="50"
          value={filters.maxPrice}
          onChange={(e) => onFilterChange('maxPrice', Number(e.target.value))}
          style={{ width: '100%', accentColor: 'var(--secondary)', cursor: 'pointer', margin: '0.5rem 0' }}
        />
        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
          <span>₹100</span>
          <span>₹1,200+</span>
        </div>
      </div>

      {isMobileModal && (
        <div style={{ marginTop: '1.5rem', paddingTop: '1rem', borderTop: '1px solid var(--border-light)' }}>
          <button onClick={onCloseMobile} className="btn btn-primary btn-full">
            Apply Filters ({totalResults} Results)
          </button>
        </div>
      )}
    </div>
  );
};

