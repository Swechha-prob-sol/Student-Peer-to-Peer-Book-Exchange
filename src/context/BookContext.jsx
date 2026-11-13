import React, { createContext, useContext, useState, useEffect } from 'react';
import { STORAGE_KEYS, getItem, setItem } from '../utils/localStorage';
import { MOCK_BOOKS } from '../data/mockBooks';
import { MOCK_REQUESTS } from '../data/mockRequests';
import { MOCK_MESSAGES } from '../data/mockMessages';
import { MOCK_REVIEWS } from '../data/mockReviews';
import { useAuth } from './AuthContext';

const BookContext = createContext();

export const BookProvider = ({ children }) => {
  const { currentUser } = useAuth();

  // Books State
  const [books, setBooks] = useState(() => {
    return getItem(STORAGE_KEYS.BOOKS, MOCK_BOOKS);
  });

  // Wishlist State (list of book IDs)
  const [wishlist, setWishlist] = useState(() => {
    return getItem(STORAGE_KEYS.WISHLIST, ['book_1', 'book_3']);
  });

  // Requests State
  const [requests, setRequests] = useState(() => {
    return getItem(STORAGE_KEYS.REQUESTS, MOCK_REQUESTS);
  });

  // Messages / Conversations State
  const [conversations, setConversations] = useState(() => {
    return getItem(STORAGE_KEYS.MESSAGES, MOCK_MESSAGES);
  });

  // Reviews State
  const [reviews, setReviews] = useState(() => {
    return getItem(STORAGE_KEYS.REVIEWS, MOCK_REVIEWS);
  });

  // Toast Notification state
  const [toast, setToast] = useState(null);

  const showToast = (message, type = 'success') => {
    setToast({ id: Date.now(), message, type });
    setTimeout(() => {
      setToast(null);
    }, 3800);
  };

  const hideToast = () => {
    setToast(null);
  };

  // Sync with LocalStorage
  useEffect(() => {
    setItem(STORAGE_KEYS.BOOKS, books);
  }, [books]);

  useEffect(() => {
    setItem(STORAGE_KEYS.WISHLIST, wishlist);
  }, [wishlist]);

  useEffect(() => {
    setItem(STORAGE_KEYS.REQUESTS, requests);
  }, [requests]);

  useEffect(() => {
    setItem(STORAGE_KEYS.MESSAGES, conversations);
  }, [conversations]);

  useEffect(() => {
    setItem(STORAGE_KEYS.REVIEWS, reviews);
  }, [reviews]);

  // Book operations
  const getBookById = (id) => {
    return books.find((b) => String(b.id) === String(id));
  };

  const addBook = (bookData) => {
    if (!currentUser) {
      showToast('You must be logged in to list a book.', 'error');
      return null;
    }

    const newBook = {
      id: `book_${Date.now()}`,
      title: bookData.title.trim(),
      author: bookData.author.trim(),
      isbn: bookData.isbn ? bookData.isbn.trim() : 'N/A',
      subject: bookData.subject,
      category: bookData.category || 'Computer Science',
      semester: String(bookData.semester),
      edition: bookData.edition.trim(),
      condition: bookData.condition,
      price: bookData.listingType === 'For Exchange' ? 0 : Number(bookData.price || 0),
      listingType: bookData.listingType,
      preferredExchange: bookData.preferredExchange ? bookData.preferredExchange.trim() : '',
      description: bookData.description.trim(),
      coverImage: bookData.coverImage || 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80',
      sellerId: currentUser.id,
      sellerName: currentUser.name,
      sellerCollege: currentUser.college,
      sellerRating: currentUser.rating || 5.0,
      sellerReviewsCount: currentUser.reviewCount || 0,
      status: bookData.status || 'Active', // Active or Draft
      dateListed: new Date().toISOString().split('T')[0],
      featured: false
    };

    setBooks((prev) => [newBook, ...prev]);
    showToast(
      bookData.status === 'Draft' 
        ? 'Listing saved as Draft!' 
        : 'Book successfully listed on BookLoop!', 
      'success'
    );
    return newBook;
  };

  const updateBook = (bookId, updatedData) => {
    setBooks((prev) =>
      prev.map((b) => (b.id === bookId ? { ...b, ...updatedData } : b))
    );
    showToast('Book listing updated successfully!', 'success');
  };

  const deleteBook = (bookId) => {
    setBooks((prev) => prev.filter((b) => b.id !== bookId));
    // Also remove from wishlist if present
    setWishlist((prev) => prev.filter((id) => id !== bookId));
    showToast('Book listing deleted.', 'info');
  };

  const updateBookStatus = (bookId, status) => {
    setBooks((prev) =>
      prev.map((b) => (b.id === bookId ? { ...b, status } : b))
    );
    showToast(`Book status marked as ${status}.`, 'success');
  };

  // Wishlist operations
  const toggleWishlist = (bookId) => {
    if (wishlist.includes(bookId)) {
      setWishlist((prev) => prev.filter((id) => id !== bookId));
      showToast('Removed from Wishlist', 'info');
    } else {
      setWishlist((prev) => [...prev, bookId]);
      showToast('Added to Wishlist!', 'success');
    }
  };

  const isInWishlist = (bookId) => {
    return wishlist.includes(bookId);
  };

  // Request operations
  const sendRequest = ({ bookId, requestType, offeredBookId = null, notes = '' }) => {
    if (!currentUser) {
      showToast('Please log in to send a request.', 'error');
      return null;
    }

    const targetBook = getBookById(bookId);
    if (!targetBook) {
      showToast('Book not found.', 'error');
      return null;
    }

    if (targetBook.sellerId === currentUser.id) {
      showToast('You cannot request your own book!', 'error');
      return null;
    }

    let offeredBookTitle = '';
    if (requestType === 'Exchange' && offeredBookId) {
      const offeredBook = getBookById(offeredBookId);
      if (offeredBook) {
        offeredBookTitle = `${offeredBook.title} (${offeredBook.edition})`;
      }
    }

    const newRequest = {
      id: `req_${Date.now()}`,
      bookId: targetBook.id,
      bookTitle: targetBook.title,
      bookAuthor: targetBook.author,
      bookCover: targetBook.coverImage,
      bookPrice: targetBook.price,
      sellerId: targetBook.sellerId,
      sellerName: targetBook.sellerName,
      requesterId: currentUser.id,
      requesterName: currentUser.name,
      requesterCollege: currentUser.college,
      requesterAvatar: currentUser.avatar,
      requestType,
      offeredBookTitle,
      offeredBookId,
      date: new Date().toISOString().split('T')[0],
      status: 'Pending',
      trackingStep: 1, // 1: Request Sent
      notes: notes || `Interested in your ${targetBook.title}. Let me know!`
    };

    setRequests((prev) => [newRequest, ...prev]);
    showToast(`${requestType} request sent successfully to ${targetBook.sellerName}!`, 'success');
    return newRequest;
  };

  const updateRequestStatus = (requestId, newStatus, newTrackingStep = null) => {
    setRequests((prev) =>
      prev.map((req) => {
        if (req.id === requestId) {
          let step = newTrackingStep;
          if (!step) {
            if (newStatus === 'Accepted') step = 2; // Request Accepted
            else if (newStatus === 'Completed') step = 5; // Review Submitted / Completed
            else if (newStatus === 'Rejected') step = 1;
            else if (newStatus === 'Cancelled') step = 1;
            else step = req.trackingStep;
          }
          return {
            ...req,
            status: newStatus,
            trackingStep: step
          };
        }
        return req;
      })
    );

    // If accepted or completed, optionally update book status
    if (newStatus === 'Completed') {
      const targetReq = requests.find((r) => r.id === requestId);
      if (targetReq) {
        updateBookStatus(targetReq.bookId, targetReq.requestType === 'Exchange' ? 'Exchanged' : 'Sold');
      }
    }

    showToast(`Request marked as ${newStatus}.`, 'success');
  };

  const advanceTrackingStep = (requestId, nextStep) => {
    setRequests((prev) =>
      prev.map((req) => {
        if (req.id === requestId) {
          const updated = { ...req, trackingStep: nextStep };
          if (nextStep === 4) {
            updated.status = 'Completed';
          }
          return updated;
        }
        return req;
      })
    );
    showToast(`Exchange timeline advanced to Step ${nextStep}!`, 'success');
  };

  const cancelRequest = (requestId) => {
    setRequests((prev) =>
      prev.map((req) =>
        req.id === requestId ? { ...req, status: 'Cancelled' } : req
      )
    );
    showToast('Request has been cancelled.', 'info');
  };

  // Messaging operations
  const sendMessage = (conversationId, text) => {
    if (!text.trim() || !currentUser) return;

    const newMessage = {
      id: `m_${Date.now()}`,
      senderId: currentUser.id,
      senderName: currentUser.name,
      text: text.trim(),
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      date: new Date().toISOString().split('T')[0]
    };

    setConversations((prev) =>
      prev.map((conv) => {
        if (conv.conversationId === conversationId) {
          return {
            ...conv,
            messages: [...conv.messages, newMessage],
            lastMessage: text.trim(),
            lastMessageTime: 'Just now'
          };
        }
        return conv;
      })
    );
  };

  const startConversationWithUser = (seller) => {
    if (!currentUser) {
      showToast('Please log in to message this seller.', 'error');
      return null;
    }

    // Check if conversation already exists
    const existing = conversations.find(
      (c) =>
        c.participants.includes(currentUser.id) &&
        c.participants.includes(seller.id)
    );

    if (existing) {
      return existing.conversationId;
    }

    const newConvId = `conv_${currentUser.id}_${seller.id}`;
    const newConv = {
      conversationId: newConvId,
      participants: [currentUser.id, seller.id],
      withUser: {
        id: seller.id,
        name: seller.name,
        college: seller.college,
        avatar: seller.avatar || `https://api.dicebear.com/7.x/initials/svg?seed=${encodeURIComponent(seller.name)}`,
        online: true,
        lastSeen: 'Online'
      },
      lastMessage: 'Started a new conversation',
      lastMessageTime: 'Just now',
      unreadCount: 0,
      messages: [
        {
          id: `m_start_${Date.now()}`,
          senderId: currentUser.id,
          senderName: currentUser.name,
          text: `Hi ${seller.name}, I am interested in exchanging or purchasing books with you!`,
          time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          date: new Date().toISOString().split('T')[0]
        }
      ]
    };

    setConversations((prev) => [newConv, ...prev]);
    return newConvId;
  };

  // Review operations
  const addReview = ({ sellerId, bookTitle, rating, comment }) => {
    if (!currentUser) return;

    const newRev = {
      id: `rev_${Date.now()}`,
      sellerId,
      reviewerId: currentUser.id,
      reviewerName: currentUser.name,
      reviewerCollege: currentUser.college,
      reviewerAvatar: currentUser.avatar,
      bookTitle,
      rating: Number(rating),
      comment: comment.trim(),
      date: new Date().toISOString().split('T')[0]
    };

    setReviews((prev) => [newRev, ...prev]);
    showToast('Your rating and review were submitted! Thank you.', 'success');
    return newRev;
  };

  return (
    <BookContext.Provider
      value={{
        books,
        wishlist,
        requests,
        conversations,
        reviews,
        toast,
        showToast,
        hideToast,
        getBookById,
        addBook,
        updateBook,
        deleteBook,
        updateBookStatus,
        toggleWishlist,
        isInWishlist,
        sendRequest,
        updateRequestStatus,
        advanceTrackingStep,
        cancelRequest,
        sendMessage,
        startConversationWithUser,
        addReview
      }}
    >
      {children}
    </BookContext.Provider>
  );
};

export const useBooks = () => {
  const context = useContext(BookContext);
  if (!context) {
    throw new Error('useBooks must be used within a BookProvider');
  }
  return context;
};

