import React, { useState } from 'react';
import { BookOpen } from 'lucide-react';

export const BookCover = ({ src, alt, className = '', height = '240px', subject = 'Academic' }) => {
  const [hasError, setHasError] = useState(false);

  // Derive gradient color from subject
  const getSubjectGradient = (subj = '') => {
    const s = subj.toLowerCase();
    if (s.includes('algorithm') || s.includes('data structure')) {
      return 'linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%)';
    }
    if (s.includes('network') || s.includes('operating')) {
      return 'linear-gradient(135deg, #0f766e 0%, #14b8a6 100%)';
    }
    if (s.includes('artificial') || s.includes('machine')) {
      return 'linear-gradient(135deg, #4338ca 0%, #6366f1 100%)';
    }
    if (s.includes('math') || s.includes('discrete')) {
      return 'linear-gradient(135deg, #9d174d 0%, #ec4899 100%)';
    }
    if (s.includes('programming') || s.includes('java') || s.includes('python')) {
      return 'linear-gradient(135deg, #c2410c 0%, #f97316 100%)';
    }
    return 'linear-gradient(135deg, #172554 0%, #0d9488 100%)';
  };

  if (!src || hasError) {
    return (
      <div
        className={className}
        style={{
          width: '100%',
          height: height,
          background: getSubjectGradient(subject),
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#ffffff',
          padding: '1.5rem',
          textAlign: 'center',
          position: 'relative',
          overflow: 'hidden'
        }}
      >
        <div
          style={{
            position: 'absolute',
            top: 0,
            left: 0,
            width: '18px',
            height: '100%',
            background: 'rgba(0,0,0,0.2)',
            borderRight: '1px solid rgba(255,255,255,0.15)'
          }}
        />
        <BookOpen size={42} style={{ opacity: 0.85, marginBottom: '0.75rem' }} />
        <span
          style={{
            fontSize: '0.9rem',
            fontWeight: '700',
            lineHeight: 1.3,
            maxWidth: '180px',
            textShadow: '0 2px 4px rgba(0,0,0,0.3)',
            display: '-webkit-box',
            WebkitLineClamp: 3,
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden'
          }}
        >
          {alt || 'Academic Book'}
        </span>
        <span
          style={{
            fontSize: '0.7rem',
            opacity: 0.8,
            marginTop: '0.5rem',
            textTransform: 'uppercase',
            letterSpacing: '0.05em'
          }}
        >
          {subject}
        </span>
      </div>
    );
  }

  return (
    <img
      src={src}
      alt={alt}
      className={className}
      onError={() => setHasError(true)}
      loading="lazy"
    />
  );
};

