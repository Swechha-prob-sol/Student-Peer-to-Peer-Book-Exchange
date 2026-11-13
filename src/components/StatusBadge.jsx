import React from 'react';

export const StatusBadge = ({ status, type = 'status' }) => {
  if (!status) return null;

  const normalized = status.toLowerCase();

  let badgeClass = 'badge ';

  if (type === 'listingType') {
    if (normalized.includes('sale') && !normalized.includes('both')) {
      badgeClass += 'badge-sale';
    } else if (normalized.includes('exchange') && !normalized.includes('both')) {
      badgeClass += 'badge-exchange';
    } else {
      badgeClass += 'badge-both';
    }
  } else {
    // Normal transaction / book status
    switch (normalized) {
      case 'active':
        badgeClass += 'badge-active';
        break;
      case 'pending':
        badgeClass += 'badge-pending';
        break;
      case 'sold':
        badgeClass += 'badge-sold';
        break;
      case 'exchanged':
        badgeClass += 'badge-exchange';
        break;
      case 'draft':
        badgeClass += 'badge-draft';
        break;
      case 'accepted':
        badgeClass += 'badge-active';
        break;
      case 'completed':
        badgeClass += 'badge-completed';
        break;
      case 'rejected':
      case 'cancelled':
        badgeClass += 'badge-rejected';
        break;
      default:
        badgeClass += 'badge-draft';
    }
  }

  return <span className={badgeClass}>{status}</span>;
};

