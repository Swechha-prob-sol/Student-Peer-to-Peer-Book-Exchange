import React from 'react';
import { useBooks } from '../context/BookContext';
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';

export const Toast = () => {
  const { toast, hideToast } = useBooks();

  if (!toast) return null;

  const getIcon = () => {
    switch (toast.type) {
      case 'error':
        return <AlertCircle size={20} color="var(--error)" />;
      case 'info':
        return <Info size={20} color="var(--info)" />;
      case 'success':
      default:
        return <CheckCircle2 size={20} color="var(--success)" />;
    }
  };

  return (
    <div className="toast-container">
      <div className={`toast toast-${toast.type || 'success'}`}>
        {getIcon()}
        <span style={{ flex: 1 }}>{toast.message}</span>
        <button
          onClick={hideToast}
          style={{
            background: 'none',
            border: 'none',
            cursor: 'pointer',
            color: 'var(--text-muted)',
            display: 'flex',
            padding: '2px'
          }}
        >
          <X size={16} />
        </button>
      </div>
    </div>
  );
};

