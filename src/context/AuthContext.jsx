import React, { createContext, useContext, useState, useEffect } from 'react';
import { STORAGE_KEYS, getItem, setItem, removeItem } from '../utils/localStorage';
import { MOCK_USERS } from '../data/mockUsers';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [users, setUsers] = useState(() => {
    return getItem(STORAGE_KEYS.USERS, MOCK_USERS);
  });

  const [currentUser, setCurrentUser] = useState(() => {
    // Default to the first demo user (Aarav Sharma) if nothing stored, for an immediate seamless demo
    const stored = getItem(STORAGE_KEYS.CURRENT_USER, null);
    if (stored) return stored;
    return MOCK_USERS[0];
  });

  useEffect(() => {
    setItem(STORAGE_KEYS.USERS, users);
  }, [users]);

  useEffect(() => {
    if (currentUser) {
      setItem(STORAGE_KEYS.CURRENT_USER, currentUser);
    } else {
      removeItem(STORAGE_KEYS.CURRENT_USER);
    }
  }, [currentUser]);

  const login = (email, password, rememberMe = true) => {
    const user = users.find(
      (u) => u.email.toLowerCase() === email.trim().toLowerCase()
    );

    if (!user) {
      return { success: false, error: 'No account found with this email address.' };
    }

    if (user.password !== password) {
      return { success: false, error: 'Incorrect password. Please try again.' };
    }

    setCurrentUser(user);
    return { success: true, user };
  };

  const register = (userData) => {
    const existing = users.find(
      (u) => u.email.toLowerCase() === userData.email.trim().toLowerCase()
    );

    if (existing) {
      return { success: false, error: 'An account with this email already exists.' };
    }

    const newUser = {
      id: `user_${Date.now()}`,
      name: userData.name.trim(),
      email: userData.email.trim().toLowerCase(),
      password: userData.password,
      college: userData.college.trim(),
      department: userData.department.trim(),
      semester: userData.semester || '1',
      phone: userData.phone ? userData.phone.trim() : '+91 90000 00000',
      avatar: userData.avatar || `https://api.dicebear.com/7.x/initials/svg?seed=${encodeURIComponent(userData.name)}`,
      bio: userData.bio || `Student at ${userData.college}. Excited to buy and swap textbooks on BookLoop!`,
      rating: 5.0,
      reviewCount: 0,
      booksSold: 0,
      booksExchanged: 0,
      joinedDate: 'Just now'
    };

    const updatedUsers = [...users, newUser];
    setUsers(updatedUsers);
    setCurrentUser(newUser);

    return { success: true, user: newUser };
  };

  const logout = () => {
    setCurrentUser(null);
  };

  const updateProfile = (updatedFields) => {
    if (!currentUser) return;
    const updatedUser = { ...currentUser, ...updatedFields };
    setCurrentUser(updatedUser);

    setUsers((prevUsers) =>
      prevUsers.map((u) => (u.id === currentUser.id ? updatedUser : u))
    );
    return updatedUser;
  };

  const switchDemoUser = (userId) => {
    const user = users.find((u) => u.id === userId);
    if (user) {
      setCurrentUser(user);
    }
  };

  return (
    <AuthContext.Provider
      value={{
        currentUser,
        users,
        login,
        register,
        logout,
        updateProfile,
        switchDemoUser,
        isAuthenticated: !!currentUser
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

