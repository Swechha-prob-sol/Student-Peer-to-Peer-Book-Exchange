import React from 'react';
import { Star } from 'lucide-react';

export const RatingStars = ({ rating = 5, max = 5, size = 16, interactive = false, onRate = null }) => {
  const stars = [];

  for (let i = 1; i <= max; i++) {
    const isFilled = i <= Math.round(rating);
    stars.push(
      <span
        key={i}
        className={interactive ? 'star-interactive' : ''}
        onClick={() => interactive && onRate && onRate(i)}
        style={{ display: 'inline-flex', cursor: interactive ? 'pointer' : 'default' }}
      >
        <Star
          size={size}
          fill={isFilled ? '#F59E0B' : 'transparent'}
          color={isFilled ? '#F59E0B' : '#CBD5E1'}
          strokeWidth={1.5}
        />
      </span>
    );
  }

  return (
    <div className="rating-stars" title={`${rating} out of ${max} stars`}>
      {stars}
    </div>
  );
};

