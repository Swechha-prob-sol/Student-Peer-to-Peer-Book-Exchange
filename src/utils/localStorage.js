// Safe localStorage utility helpers

export const STORAGE_KEYS = {
  USERS: 'bookloop_users',
  CURRENT_USER: 'bookloop_current_user',
  BOOKS: 'bookloop_books',
  REQUESTS: 'bookloop_requests',
  WISHLIST: 'bookloop_wishlist',
  MESSAGES: 'bookloop_messages',
  REVIEWS: 'bookloop_reviews'
};

export const getItem = (key, defaultValue = null) => {
  try {
    const item = localStorage.getItem(key);
    return item ? JSON.parse(item) : defaultValue;
  } catch (error) {
    console.error(`Error reading ${key} from localStorage`, error);
    return defaultValue;
  }
};

export const setItem = (key, value) => {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (error) {
    console.error(`Error writing ${key} to localStorage`, error);
  }
};

export const removeItem = (key) => {
  try {
    localStorage.removeItem(key);
  } catch (error) {
    console.error(`Error removing ${key} from localStorage`, error);
  }
};

export const initializeStorageWithDefaults = (defaults) => {
  Object.entries(defaults).forEach(([key, defaultValue]) => {
    if (localStorage.getItem(key) === null) {
      setItem(key, defaultValue);
    }
  });
};

