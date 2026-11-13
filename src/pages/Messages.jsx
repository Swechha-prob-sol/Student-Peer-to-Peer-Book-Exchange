import React, { useState, useEffect, useRef } from 'react';
import {
  MessageSquare,
  Search,
  Send,
  User,
  CheckCheck,
  ArrowLeft,
  School,
  Smile,
  Paperclip
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useBooks } from '../context/BookContext';
import { Sidebar } from '../components/Sidebar';

export const Messages = () => {
  const { currentUser } = useAuth();
  const { conversations, sendMessage } = useBooks();

  const [selectedConvId, setSelectedConvId] = useState(conversations[0]?.conversationId || null);
  const [inputText, setInputText] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [showMobileChat, setShowMobileChat] = useState(false);

  const messagesEndRef = useRef(null);

  const selectedConv = conversations.find((c) => c.conversationId === selectedConvId) || conversations[0];

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [selectedConv?.messages]);

  const handleSend = (e) => {
    e.preventDefault();
    if (!inputText.trim() || !selectedConv) return;

    sendMessage(selectedConv.conversationId, inputText.trim());
    setInputText('');
  };

  const filteredConversations = conversations.filter((c) =>
    c.withUser.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    c.withUser.college.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="dashboard-layout">
      <Sidebar />

      <main className="dashboard-content" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column' }}>
        <div
          className="messaging-container card"
          style={{
            display: 'grid',
            gridTemplateColumns: '320px 1fr',
            height: 'calc(100vh - 120px)',
            overflow: 'hidden',
            boxShadow: 'var(--shadow-md)'
          }}
        >
          {/* Left Panel: Conversation List */}
          <div
            className={`chat-sidebar ${showMobileChat ? 'hidden-mobile' : ''}`}
            style={{
              borderRight: '1px solid var(--border-light)',
              display: 'flex',
              flexDirection: 'column',
              backgroundColor: '#ffffff'
            }}
          >
            {/* Search Header */}
            <div style={{ padding: '1.25rem', borderBottom: '1px solid var(--border-light)' }}>
              <h3 style={{ fontSize: '1.15rem', marginBottom: '0.85rem' }}>Messages</h3>
              <div style={{ position: 'relative' }}>
                <Search
                  size={16}
                  color="var(--text-muted)"
                  style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)' }}
                />
                <input
                  type="text"
                  placeholder="Search students or college..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="form-input"
                  style={{ paddingLeft: '2.4rem', height: '38px', fontSize: '0.85rem' }}
                />
              </div>
            </div>

            {/* Conversation List */}
            <div style={{ flex: 1, overflowY: 'auto' }}>
              {filteredConversations.length === 0 ? (
                <div style={{ padding: '2rem 1rem', textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.85rem' }}>
                  No chats found matching search.
                </div>
              ) : (
                filteredConversations.map((conv) => {
                  const isSelected = selectedConv?.conversationId === conv.conversationId;
                  return (
                    <div
                      key={conv.conversationId}
                      onClick={() => {
                        setSelectedConvId(conv.conversationId);
                        setShowMobileChat(true);
                      }}
                      style={{
                        padding: '1rem 1.25rem',
                        borderBottom: '1px solid var(--border-subtle)',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '0.85rem',
                        cursor: 'pointer',
                        backgroundColor: isSelected ? 'var(--primary-light)' : 'transparent',
                        transition: 'background-color 0.15s ease'
                      }}
                    >
                      <div style={{ position: 'relative' }}>
                        <img
                          src={conv.withUser.avatar}
                          alt={conv.withUser.name}
                          style={{ width: '44px', height: '44px', borderRadius: '50%', objectFit: 'cover' }}
                          onError={(e) => {
                            e.target.src = `https://api.dicebear.com/7.x/initials/svg?seed=${encodeURIComponent(conv.withUser.name)}`;
                          }}
                        />
                        {conv.withUser.online && (
                          <span
                            style={{
                              position: 'absolute',
                              bottom: '1px',
                              right: '1px',
                              width: '11px',
                              height: '11px',
                              borderRadius: '50%',
                              backgroundColor: '#10b981',
                              border: '2px solid #ffffff'
                            }}
                          />
                        )}
                      </div>

                      <div style={{ flex: 1, overflow: 'hidden' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                          <h4 style={{ fontSize: '0.92rem', color: isSelected ? 'var(--primary)' : 'var(--text-main)' }}>
                            {conv.withUser.name}
                          </h4>
                          <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>
                            {conv.lastMessageTime}
                          </span>
                        </div>
                        <p
                          style={{
                            fontSize: '0.78rem',
                            color: 'var(--text-muted)',
                            whiteSpace: 'nowrap',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            marginTop: '0.2rem'
                          }}
                        >
                          {conv.lastMessage}
                        </p>
                      </div>
                    </div>
                  );
                })
              )}
            </div>
          </div>

          {/* Right Panel: Chat Thread Window */}
          <div
            className={`chat-window ${!showMobileChat ? 'hidden-mobile' : ''}`}
            style={{
              display: 'flex',
              flexDirection: 'column',
              backgroundColor: '#ffffff'
            }}
          >
            {selectedConv ? (
              <>
                {/* Chat Header */}
                <div
                  style={{
                    padding: '1rem 1.5rem',
                    borderBottom: '1px solid var(--border-light)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between'
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                    <button
                      onClick={() => setShowMobileChat(false)}
                      className="btn btn-ghost btn-sm"
                      style={{ display: 'none', padding: '0.3rem' }}
                    >
                      <ArrowLeft size={18} />
                    </button>

                    <img
                      src={selectedConv.withUser.avatar}
                      alt={selectedConv.withUser.name}
                      style={{ width: '40px', height: '40px', borderRadius: '50%', objectFit: 'cover' }}
                    />
                    <div>
                      <h4 style={{ fontSize: '1rem', lineHeight: 1.2 }}>{selectedConv.withUser.name}</h4>
                      <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        {selectedConv.withUser.college} • <span style={{ color: '#10b981' }}>{selectedConv.withUser.lastSeen}</span>
                      </p>
                    </div>
                  </div>

                  <span
                    style={{
                      fontSize: '0.75rem',
                      padding: '0.2rem 0.6rem',
                      backgroundColor: 'var(--bg-subtle)',
                      borderRadius: 'var(--radius-full)',
                      color: 'var(--text-muted)'
                    }}
                  >
                    Encrypted Peer Chat
                  </span>
                </div>

                {/* Messages Body */}
                <div
                  style={{
                    flex: 1,
                    padding: '1.5rem',
                    overflowY: 'auto',
                    backgroundColor: '#fafbfc',
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '1rem'
                  }}
                >
                  {selectedConv.messages.map((msg) => {
                    const isMe = msg.senderId === currentUser.id;
                    return (
                      <div
                        key={msg.id}
                        style={{
                          display: 'flex',
                          flexDirection: 'column',
                          alignItems: isMe ? 'flex-end' : 'flex-start',
                          maxWidth: '75%',
                          alignSelf: isMe ? 'flex-end' : 'flex-start'
                        }}
                      >
                        <div
                          style={{
                            padding: '0.75rem 1rem',
                            borderRadius: 'var(--radius-lg)',
                            borderBottomRightRadius: isMe ? '2px' : 'var(--radius-lg)',
                            borderBottomLeftRadius: !isMe ? '2px' : 'var(--radius-lg)',
                            backgroundColor: isMe ? 'var(--primary)' : '#ffffff',
                            color: isMe ? '#ffffff' : 'var(--text-main)',
                            border: isMe ? 'none' : '1px solid var(--border-light)',
                            boxShadow: 'var(--shadow-xs)',
                            fontSize: '0.9rem',
                            lineHeight: 1.5
                          }}
                        >
                          {msg.text}
                        </div>
                        <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginTop: '0.25rem', padding: '0 0.25rem' }}>
                          {msg.time} {isMe && '• Sent'}
                        </span>
                      </div>
                    );
                  })}
                  <div ref={messagesEndRef} />
                </div>

                {/* Chat Input Bar */}
                <form
                  onSubmit={handleSend}
                  style={{
                    padding: '1rem 1.5rem',
                    borderTop: '1px solid var(--border-light)',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.75rem',
                    backgroundColor: '#ffffff'
                  }}
                >
                  <input
                    type="text"
                    placeholder="Type your message to plan the book handoff..."
                    value={inputText}
                    onChange={(e) => setInputText(e.target.value)}
                    className="form-input"
                    style={{ flex: 1, height: '44px' }}
                  />
                  <button type="submit" className="btn btn-primary" style={{ height: '44px', padding: '0 1.25rem' }}>
                    <Send size={16} />
                    <span>Send</span>
                  </button>
                </form>
              </>
            ) : (
              <div className="empty-state" style={{ height: '100%', border: 'none' }}>
                <MessageSquare size={40} color="var(--primary)" />
                <h3 className="empty-title">Select a conversation</h3>
                <p className="empty-desc">Choose a student on the left panel to begin discussing book swaps.</p>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
};

