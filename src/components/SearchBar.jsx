import React from 'react';
import { Search, X } from 'lucide-react';

export const SearchBar = ({ value, onChange, onClear, placeholder = 'Search by book title, author, or subject...', onSubmit }) => {
  const handleSubmit = (e) => {
    e.preventDefault();
    if (onSubmit) onSubmit(value);
  };

  return (
    <form onSubmit={handleSubmit} style={{ width: '100%', position: 'relative' }}>
      <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
        <Search
          size={18}
          color="var(--text-muted)"
          style={{ position: 'absolute', left: '1rem', pointerEvents: 'none' }}
        />
        <input
          type="text"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
          className="form-input"
          style={{
            paddingLeft: '2.75rem',
            paddingRight: value ? '2.5rem' : '1rem',
            height: '48px',
            fontSize: '0.95rem',
            backgroundColor: '#ffffff'
          }}
        />
        {value && (
          <button
            type="button"
            onClick={onClear}
            style={{
              position: 'absolute',
              right: '0.85rem',
              background: 'none',
              border: 'none',
              cursor: 'pointer',
              color: 'var(--text-muted)',
              display: 'flex',
              padding: '4px'
            }}
            title="Clear search"
          >
            <X size={16} />
          </button>
        )}
      </div>
    </form>
  );
};

