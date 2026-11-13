// Form validation utility functions

export const validateEmail = (email) => {
  if (!email || !email.trim()) return 'Email address is required';
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email.trim())) {
    return 'Please enter a valid email address';
  }
  return null;
};

export const validateCollegeEmail = (email) => {
  const basicErr = validateEmail(email);
  if (basicErr) return basicErr;
  // Optional check if college email (e.g. .edu, .ac.in, or standard email)
  return null;
};

export const validatePassword = (password) => {
  if (!password) return 'Password is required';
  if (password.length < 6) {
    return 'Password must be at least 6 characters long';
  }
  return null;
};

export const validateConfirmPassword = (password, confirmPassword) => {
  if (!confirmPassword) return 'Please confirm your password';
  if (password !== confirmPassword) {
    return 'Passwords do not match';
  }
  return null;
};

export const validateRequired = (value, fieldName = 'Field') => {
  if (!value || (typeof value === 'string' && !value.trim())) {
    return `${fieldName} is required`;
  }
  return null;
};

export const validatePrice = (price, listingType) => {
  if (listingType === 'For Exchange') return null;
  if (price === undefined || price === null || price === '') {
    return 'Price is required for sale listings';
  }
  const num = Number(price);
  if (isNaN(num) || num < 0) {
    return 'Please enter a valid price in ₹ (greater than or equal to 0)';
  }
  return null;
};

